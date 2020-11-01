#### kafka的作用

- 缓存和消峰
- 解耦
- 异步通信

#### kafka controller

kafka集群由多个broker组成，每个集群都由一个broker充当controller的角色。controller除了具有一般broker具有的功能之外，还要负责topic的partition leader的选举、管理topic的partition及其副本状态、执行partition的rebalance、topic的创建删除。
如果在kafka集群运行过程中，controlller变的不可用，就会从还活跃的broker中选举出一个新的controller。

#### 如何选举产生controller

zookeer中有一个/controller**临时节点**，/controller节点的内容是：`{"version": 1, "brokerid": 0, "timestamp": "12345678"}`。brokerid表示成为controller的那个broker的id编号，timestamp表示竞选成为controller时的时间戳，version固定为1。
每个broker启动时都会去zookeeper上注册/controller节点，如果发现zookeeper上已经存在/controller节点，就放弃竞选controller，否则，就创建/controlller节点。多个broker竞选controller是奉行先到先得的规则。没能成为controller的broker会watch zookeeper上的/controller节点，当controller因为某些原因变的不可用时，broker就会得到通知，就会开启新的一轮选举controller的过程。

#### controller如何感知到broker的下线

zookeeper上有一个节点/brokers/ids，每个broker上线后都要创建一个/brokers/ids/[brokerid]临时节点，其中brokerid是全局唯一的。controller会watch zookeeper中/brokers/ids节点下的临时子节点的变化，一旦broker变为不可用时，controller就会得到通知。

#### kafka的副本机制

topic的partition有多个副本，其中有一个副本会被选举为leader，这是由controller进行选举产生的，其余的副本都是follower。由**leader负责partition的读写请求**，**follower只负责从leader fetch消息，同步到它所在的broker**，**follower并不负责读请求**。
kafka的复制机制既不是同步复制，也不是异步复制。同步复制要求在写入一条消息时，所有的follower都复制完成之后才能commit这条消息，这条消息才算完成，这样虽然能够最大程度的满足一致性，但是也会影响吞吐量；异步复制，是follower异步的从leader上fetch消息，在写入消息时，只要leader写成功了就表示这条消息可以commit啦，不用等待follower。如果follower异步复制leader比较落后，leader这时宕机啦，那么新写入leader的消息就丢失啦，不满足一致性的要求。kafka采用的是一种ISR复制机制。
ISR（In-Sync-Replicas），对于每个partition，ISR中保存了与这个partition的leader同步的副本，leader肯定在ISR中，这里的同步的判断规则是**replica.lag.time.max.ms**参数决定，假如参数值是10秒（这也是默认值），只要follower落后于leader的持续时间不超过10秒，那么这个follower就是与leader同步的。ISR列表存放在zookeeper的/brokers/topics/[topic]/partitions/[partition]/state节点下面，由leader负责动态维护。
LEO（last end offset）日志末端偏移量，记录的是partition的副本的日志文件中**下一条消息的offset**。每个partition副本都有一个，其实LEO就表示副本复制到了什么位置，下一次将要复制那条消息。
HW（High Watermark），整个parititon只有一个HW，HW取ISR中多个同步副本的LEO的最小值。所以HW一定小于等于leader的LEO，offset小于等于HW的消息才能被consumer消费，大于HW的消息还没同步完成，不能提供给consumer消费。
**notice：** 一种极端情况，假如leader宕机时ISR列表为空，这时候有两种选择：1、等待leader自己上线 2、从不在ISR列表中的broker中选择一个作为leader，这种选举被称为Unclean Leader Election，因为这些broker落后于leader，所以有数据丢失的危险。

#### kafka消息的写入过程

kafka写入消息的过程根据是否指定partitioner分为两种：

- 如果指定了partitioner，就按照指定的partitioner进行处理
- 如果没指定partitoner，又分为两种情况
  - 指定了key，那就按照key进行hash
  - 如果没有指定key，那就按照round-robin算法来轮询选择partition

在确定发送到哪个partition之后，消息就被放到对应的队列中，队列由topic+partition标识，当达到一定的batch_size之后再发送到broker。
1、producer首先到zookeeper的/brokers/topics/[topic]/partitions/[partition]/state节点下找一下，这个partition的leader在哪个broker
2、producer发送消息到对应leader上
3、leader将消息写入到本地的log
4、所有的follower从leader那里pull消息，写入到本地的log后给leader发送ACK
5、leader收到所有ISR中的follower的ACK后，增加HW并向producer返回ACK。
**notice:** producer需不需要等待leader的ACK？leader什么情况下返回ACK都是由参数**request.required.acks**决定的，request.required.acks参数的可能取值：

- 0 表示producer不需要等待leader的ACK确认，直接发送下一条消息。在这种情况下，如果leader宕机，消息就会丢失。
- 1 表示producer需要等待leader发送ACK确认，当收到ACK确认之后才能发送下一条消息。在这种情况下，可以保证消息一定写入了leader，但是不保证已经在follower进行了同步。如果在消息已经写入到leader但是还没来得及同步到follower时，leader宕机啦，消息就会丢失。***默认值, 体现了At Most Once语义***
- -1 表示producer需要等待leader发送ACK确认，之后才能发送下一条消息。leader只有在接收到ISR中所有follower的ACK后才能向producer发送ACK，这样不会丢失消息，但是吞吐量也下来啦。

#### kafka consumer的partition分配策略

consumer有三种partition分配策略

- RangeAssignor策略（**默认值**）
  对**每一个topic**，RangeAssignor策略将消费者组内订阅这个topic的所有consumer的名字按照字典序排序，然后用partition的总数除以consumer的数目计算出一个Range，为每个consumer分配Range个partition，如果不够除，那么字典序靠前的consumer可能分配的比较多。
  假设现在在一个consumer group中有c0、c1两个consumer，这两个consumer都订阅t0、t1这两个topic，每个topic都有4个partition，所以总的partition是t0p0、t0p1、t0p2、t0p3、t1p0、t1p1、t1p2、t1p3。对于topic t0，将partition t0p0、t0p1分配给c0，将partition t0p2、t0p3分配给c1。topic t1同理。

- RoundRobinAssignor策略

  将同一个consumer group内的所有consumer以及consumer所订阅的所有topic的partition按照字典序进行排序，然后通过轮询的方式将partition一个个的分配给consumer。

  1. 假设consumer group中所有的consumer都订阅了相同的topic。现在在一个consumer group中有consumer c0、c1，他们都订阅了topic t0、t1，每个topic有3个partition，所以总的partition的情况就是t0p0、t0p1、t0p2、t1p0、t1p1、t1p2，那么c0将分配到t0p0、t0p2、t1p1，c1将分配到t0p1、t1p0、t1p2。
  2. 假设consumer group中的consumer订阅了不同的topic。现在在一个consumer group中有consumer c0、c1、c2，其中c0、c1、c2订阅了topic t0，c1、c2订阅了topic t1，c2订阅了topic t2，其中topic t0有一个partition、topic t1有两个partition、topic t2有三个partition，这样总的partition就是t0p0、t1p0、t1p1、t2p0、t2p1、t2p2，那么consumer c0将分配到t0p0，consumer c1将分配到t1p0，consumer c2将分配到t1p1、t2p0、t2p1、t2p2，分配的不均匀。

- StickyAssignor策略
  参考链接：https://www.cnblogs.com/felixzh/p/11935693.html

#### partition leader的选举

- 如果ISR中存在存活的broker，就从ISR中选择一个broker作为新的leader
- 如果ISR是空的，就要尝试进行Unclean Leader Election。如果配置参数unclean.leader.election.enable为true，就在replica存活的节点选择一个作为leader。如果为false，就抛异常。

#### partition rebalance

#### kafka为什么这么快？

- 一个topic有多个partition，可以并行同时写多个partition。
- producer在向partition写数据时，是使用的批量写，当message的数据量达到一定大小或者是有一段时间未向partition写入时，才写入。
- kafka采用的顺序写，磁盘的顺序写比随机写快很多。
- 使用了zero-copy零拷贝的写入技术，少了一次数据复制和两次用户态和核心态的切换。
- 在consumer端采用pull的方式消费数据，比较适合consumer的消费速度。
  什么是零拷贝技术？[参考](https://my.oschina.net/u/3990817/blog/3045359)

#### kafka为什么不支持读写分离？

- 数据不一致性问题。因为主从复制存在一定延迟，所以可能导致leader和follwer之间的数据不一致的情况。
- 延迟过大。不同于redis，redis只需要写内存。kafka需要将消息落盘，这就使得主从复制的延迟比较大。

[参考1](https://blog.csdn.net/miss1181248983/article/details/90724870)

[参考2](https://matt33.com/2015/11/14/The-Introduce-of-Kafka/)

#### 如何保证数据