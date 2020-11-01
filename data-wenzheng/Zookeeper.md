### ZAB协议

#### 崩溃恢复

进入崩溃恢复模式的时机

- leader节点崩溃
- leader失去与过半follower的联系

#### 原子广播

1. leader收到事务请求，并为事物请求分配一个全局的zxid(64位)，zxid是全局严格递增的。
2. leader为每个follwer维护了一个FIFO队列，将带有zxid的消息作为一个proposal放在队列中，发送给follwer。
3. follwer收到来自leader的proposal后，先把proposal持久化到磁盘，然后向leader返回一个ACK。
4. leader在收到**超过半数**的follwer的ACK后，leader就会向这些follwer发送commit命令，同时在本地执行消息。
5. 当follwer收到来自leader的commit后，本地提交消息。

ZAB协议的原子广播模式有些类似于2PC。

### 服务器的状态

- LOOKING
- FOLLOWING
- LEADING
- OBSERVING

### ZooKeeper特性

- 顺序一致性
  同一个Client发起的事务请求，严格按照发起顺序执行
- 原子性
  事务请求，要么应用到所有节点，要么一个节点都不应用
- 单一视图
  Client无论连接到那个节点，看到的服务端数据都是**最终一致的**。
- 可靠性
  事务一旦执行成功，状态永久保留。
- 实时性
  事务一旦执行成功，Client并不能立即看到最新数据，但ZooKeeper保证最终一致性。

### ZooKeeper应用场景

- 服务发现
- 分布式锁
- 统一配置管理
- 集群管理

### Leader选举

整个选举过程中，选票的格式是(myid, zxid)。zxid的高32位是epoch，每经过一次Leader选举并成功选举出Leader后，epoch就加一。zxid的低32位是事务id，每次选举出新的Leader后都会自动清零。

#### 启动时leader选举

以3台机器组成的集群为例，集群中有Server1、Server2、Server3共台机器。

1. 启动时Leader还未选出，所以集群中所有的机器处于Looking状态。开始时，每台服务器都投出一个初始的选票，Server1的选票是(1, 0)、Server2的选票是(2,0)。**每个服务器**都将自己的选票发送给集群中的其他服务器。
2. 每个服务器收到来自于其他服务器的选票后要判断选票的合法性。比如检查是否是本轮投票(epoch), 是否来自于Looking状态的服务器。
3. 每个服务器对于**接收到的每张选票进行PK**，PK的规则是zxid大的优先，其次是myid。在这里以Server1接收到的选票(2,0)为例，因为zxid都为0，所以比较myid，因为选票的myid是2，所以胜出。Server1更新它的选票为(2,0), 在这里Server1只收到了一张选票，如果集群服务器比较多时会收到多张选票。整个PK过程完成之后，Server1再将它的选票重新发送给**集群中的其他的服务器**。
4. 每次投票结束后，各个服务器都会统计选票信息，如果发现某个服务器赢得了半数以上的选票( >= n/2 + 1)，那么统计选票信息的服务器就会将这个服务器认定为Leader。
5. 如果选出了Leader，每个服务器就更新自己的状态，Leader或者Follower。

#### leader崩溃时选举

zk分布式锁：https://juejin.im/post/5c01532ef265da61362232ed
https://www.cnblogs.com/leeSmall/p/9614601.html