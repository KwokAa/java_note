##### show processlist

##### 什么时候采用分库？什么时候采用分表？

https://juejin.im/entry/5b5eb7f2e51d4519700f7d3c

#### MySql大表Join危害

Left Join使用左表作为驱动表。
Right Join使用右表作为驱动表。
Join由MySql选择数据量比较小的表作为驱动表，数据量比较大的作为被驱动表。
在使用Join时要时刻记住使用小表作为驱动表，小表就是经过where条件过滤并且取出的列比较少，也就是查询后整体数据量比较小的表。
多个表进行Join时，先选择前两个进行Join，Join完成后再跟第三张表进行Join，知道所有的表都Join完。

假设驱动表上有N条数据，被驱动表上有M条数据。MySql在进行Join时，驱动表始终是走全表扫描，不会使用索引。但是被驱动表会尝试使用索引，根据被驱动表在Join列上是否使用到了索引分为两种算法。如果使用到了索引就用**Index Nested-Loop Join**算法，算法执行过程是先从驱动表上拿到一条数据，然后根据这条数据的Join列上的指到被驱动表上查询，由于被驱动表在Join列上有索引，所以这里是一个树上查找的过程，将在被驱动表查找到的数据与驱动表上的数据和起来形成结果放到结果集。重复上诉过程，直到把驱动表全查询完。这是一个时间复杂度为N+N*logM的过程。如果在被驱动表的Join列上没有索引，那就执行**Block Nested-Loop Join**算法，算法先将驱动表全部查询出来放到**Join Buffer**中(如果Join Buffer不够用，就分批，多次把驱动表中数据读到Join Buffer，相应的算法也执行多次)，全表扫描被驱动表拿到一行记录，就去Join Buffer中做匹配把符合条件的结果放到结果集，直到全部遍历完被驱动表。这是一个内存中时间复杂度为N*M的比较过程，期间还查询了N+M条数据。

##### BNL算法主要危害

1. 可能多次扫描被驱动表，占用大量磁盘IO。
2. 进行Join需要M*N次内存比较操作，耗费CPU资源。
3. **导致Buffer Pool的old区充满被驱动表的数据页，导致命中率下降。**(重要)
   为什么导致命中率下降？
   假设Buffer Pool的old区可以放下被驱动表的数据，那么可能在多次扫描被驱动表之后，被驱动表更新到young区，但是在join之后这些数据页又不会再用，占用了young区空间。
   假设Buffer Pool的old区不可以放下被驱动表的数据，那么可能在扫描被驱动表的过程中，其他的业务使用的数据页不断的被淘汰，根本没有机会驻留在Buffer Pool中，更别提young区，这会导致其他缓存命中率下降，其他业务的查询时延变大。

解决办法：给**被驱动表的Join列加上索引**，让它使用NLJ算法

#### MySql delete大表

直接删除一张大表的危害：所有与mysql相关的进程都会停止，直到drop结束，mysql才会恢复执行。出现这个情况的原因是因为在drop table的时候，innodb维护了一个全局锁，drop完后锁才释放。那么在整个drop期间，QPS会严重下滑。

##### 文件系统基础知识

FCB是文件控制块，主要存储以下信息。基础信息：文件名、文件的物理地址、文件的逻辑结构、文件的物理结构等。文件存取控制信息：文件的存取控制权限等。使用信息：文件的创建时间、文件的修改时间等。
Inode节点，在目录下检索文件时，只用到了文件名。只需要检索到某个目录项与要检索的文件名相同时，才需要查出文件的物理地址。所以，在整个检索过程中，除了文件名之外的其他信息都没起到作用。基于以上考虑，将文件名和文件的其他信息分离，目录项中只包括文件名和一个指向Inode节点的指针，Inode节点中存储除了文件名之外的其他信息。这样，同一个磁盘块就能存放更多文件项，检索起文件来就更快。

文件系统的软连接和硬链接：软链接其实就是创建了新的文件，只不过文件中的内容存放的是目标文件的路径。硬链接是指多个目录项共享一个Inode节点，Inode节点中有一个引用计数，表示有多少目录项指向了这个Inode节点。当有目录项指向了这个Inode时就加1，当有指向这个Inode的目录项被删除时就减1，当引用计数为0时，由系统负责删除这个文件。

##### 如何正确删除一张大表

独立表空间：每个表都有一个.frm文件和一个.ibd文件，其中.frm文件存放的是表的元数据，包括表的结构定义，这个文件与存储引擎无关，.ibd文件保存了每个表的数据和索引。

1. 找到数据存储目录，可以查看my.cnf文件中的datadir配置项。
2. 假设现在我们有一个mytest数据库，在其下有一个erp表。这里假设数据库数据存放路径就是datadir，那么我们到datadir/mytest/文件夹下可以看到erp.frm和erp.ibd两个文件。我们先为erp.ibd建立硬链接，使用命令`ln ./erp.ibd ./erp.ibd.hdlk`
3. 使用SQL语句`drop table erp`删除表，这时会发现删除的很快。因为只是删除一个目录项，并修改Inode中的引用计数，真实的文件还没删除。
4. 如何删除文件erp.ibd.hdlk。不能使用rm命令，因为使用rm命令删除一个大文件将导致磁盘IO飙升，CPU负载过大，影响同一机器上其他程序的运行。需要单独安装coreutils工具集，使用它下的truncate命令逐步删除：可以使用如下脚本：

```
TRUNCATE=/usr/local/bin/truncate
for i in `seq 2194 -10 10 `; 
do 
  sleep 2
  $TRUNCATE -s ${i}G /data/mysql/mytest/erp.ibd.hdlk 
done
rm -rf /data/mysql/mytest/erp.ibd.hdlk ;
```

从2194G开始，每次缩减10G，停2秒，继续，直到文件只剩10G，最后使用rm命令删除剩余的部分。
上诉总结参考链接：https://blog.csdn.net/weixin_34187822/article/details/92139397

#### MySql读写分离

#### MySql全表扫描

##### 大查询会不会耗费尽MySql的内存

MySql是如何将查询结果发送给客户端的？

1. MySql计算出一行，放到net_buffer中，net_buffer的大小是由参数net_buffer_length配置的，默认是16K。
2. 重复1过程，直到net_buffer满了，调用网络接口发送这个部分结果集。
3. 如果发送成功，就清空net_buffer，然后再重复执行过程1和2。
4. 如果发送函数返回 EAGAIN 或 WSAEWOULDBLOCK，就表示本地网络栈（socket send buffer）写满了，进入等待。直到网络栈重新可写，再继续发送。

MySql采用的是边算边发的策略来处理查询语句的，如果客户端处理不过来，来不及从socket receive buffer中获取结果集并处理，那么MySql就卡在那里，等着可以发送了再发送。

##### MySql的Buffer Pool中的LRU列表

Buffer Pool的作用：

1. 加速数据更新。不用直接更新到磁盘，只需要更新Buffer Pool中对应数据页的对应记录行就行。
2. 加速查询。Buffer Pool中的数据都是最新的，所以查询时如果可以在Buffer Pool中直接查找到记录，就可以直接返回。

LRU列表的改进型LRU算法：
Buffer Pool中的LRU列表，被分成了young区域和old区域两个部分，其中young区域占5/8，old区域占3/8。

1. 如果访问的记录所在的数据页在young区域，那就把对应的数据页放到young区域的开头。
2. 如果访问的记录所在的数据页根本就不在Buffer Pool中，就从磁盘上加载对应的数据页到内存，把新加载的数据页放在old区的开头位置，根据Buffer Pool是否已满，来决定是否淘汰old区的结尾的那个数据页。
3. 如果访问的记录在old区域，那就要判断如果这个数据页已经在old区呆了超过1秒钟，就把这个数据页放到young区的开头。如果在old区未呆够了1秒钟，那就位置不变。
   这个1秒钟，是通过参数innodb_old_blocks_time控制的。

这种改进型LRU算法的好处？
在处理大表的全表扫描时会获得好处。假设依然采用传统的LRU算法，在全表扫描一个以前未处理的大表时，数据页会被大量的载入到Buffer Pool，在访问完数据页里面的记录后就不会再访问，这样会导致短时间内Buffer Pool中全是这个大表的数据页。**其他查询的数据页会很快被逐出LUR列表，导致其他查询的缓存命中率下降，其他查询的时延明显增加，影响其他业务**。
采用了改进的LRU算法之后，新访问到的数据页会被先放在old区域，一个数据页里面有多条记录，但是从第一条记录被访问到最后一条记录被访问时间不会超过1秒，这样这个数据页还是留在old区域，再访问后面的数据页时这个数据页会被逐渐移动到old区的末尾，直到被淘汰，反正访问完之后也不会再访问。

#### 幻读是什么？如何解决？

幻读是一个事务在前后两次查询时，后一次查询查找到了前一次查询没有看到的数据行。
在可重复读隔离级别下，普通的查询是快照读，是不会查找到其他事务插入的数据的。幻读只会出现在当前读中，因为当前读总会事务提交的最新的数据。
**幻读特指新插入数据行**
幻读的危害：binlog中的数据修改记录和数据库中实际的数据状态不一致。
解决办法：引入间隙锁，加锁时不仅加行锁还要加间隙锁。间隙锁之间不冲突。

1. MySQL引擎有哪几种，InnoDB和MyIsam的区别，mvcc介绍

   MySQL存储引擎有InnoDB、MyIsam、Memory。

   InnoDB与MyIsam的区别：InnoDB存储引擎**支持事务**、**支持外键**、**支持行锁和表锁**。InnoDB存储引擎实现了4种事务隔离级别，默认是RR级别，在RR级别下它使用next-key lock策略防止幻读。默认情况下，所有的读操作都不会加锁，而是使用**MVCC来提高并发性**。InnoDB使用的是**聚簇索引**，按照主键生成了一个索引，B+树叶子节点直接存放了数据。辅助索引上存放的是主键的值，根据辅助索引进行查找时，如果不是覆盖索引，那就还需要去主键索引上再查找一次，InnoDB存储引擎适用于需要支持事务、需要进行大量删除的应用。 MyIsam存储引擎**不支持事务**，**不支持外键**、**只支持表锁**，MyIsam是**非聚簇索引**，主键索引的B+树叶子节点上存放的是执行数据文件的指针，辅助索引也是。MyIsam存储引擎适用于需要进行大量读操作的场景。

   Memory存储引擎将表的数据存放到内存，如果数据库重启，数据就会丢失，Memory存储引擎默认使用哈希索引。Mysql在查询时使用Memory存储引擎作为临时表来存放中间结果。

   MVCC叫做**多版本并发控制**，可以实现**一致性非锁定读**，提高读取的并发度。在使用MVCC时，如果读取的数据行正在执行DELETE、UPDATE操作，读取操作并不会等待行上的锁释放掉，而是读取这一行数据的上一个版本。**因为没有事务会修改一行数据的上一个版本，所以这里可以实现多个线程并发读取，这样就提高了并发性。**读取这一行数据的上一个版本是通过undo log来实现的，innoDB的数据行中有隐藏的两列，一个是事务ID列，表示最近修改这行记录的事务的ID，另一列是回滚指针，一行数据的多个版本之间就是通过这个回滚指针进行连接起来的，MVCC正是通过这个隐藏的回滚指针列来读取一行数据的上一个版本。

2. B树和B+树的区别

   在B树中，**非叶子节点也需要保存数据**，关键字在树中只出现一次，查找时如果查找到了关键字就停止，不用管是在叶子节点还是在非叶子节点查找到的。B+树中，只有**叶子节点保存数据**，**非叶子节点只起到索引的作用**，关键字在整个树中不只出现一次，查找时必须查找到叶子节点才算结束，**叶子节点之间会链接成双向链表**，而且**叶子节点之间是有序的**，进行范围查询时，只需要遍历这个双向链表就可以，B树进行范围查询时，需要进行中序遍历。**B树中节点的关键字个数总是比子树的数目少1，B+树中节点的关键字个数与子树的数目相同。**

   为什么使用B+树作为索引，而不是B树？

   B+树非叶子节点只存放关键字，非叶子节点只起到索引的作用。B树中非叶子不仅存放了关键字还存放了具体的数据。相对来说，一个磁盘块在B+树中存放的关键字个数更多，这样一次读入内存的关键字数目就比较多，一次可以过滤掉更多关键字，这样IO次数比B树更少。B+树对范围查询的支持比B树好。

   1. 分库分表？

   [https://mp.weixin.qq.com/s?__biz=MjM5ODYxMDA5OQ==&mid=2651960212&;idx=1&sn=ab4c52ab0309f7380f7e0207fa357128&pass_ticket=G8v3RrpK9Is7NJZH0fOShUfY8lp5oz9un8K5L24LeGGVtiBTXkBMc9UKkTMdQeDS](https://mp.weixin.qq.com/s?__biz=MjM5ODYxMDA5OQ==&mid=2651960212&idx=1&sn=ab4c52ab0309f7380f7e0207fa357128&pass_ticket=G8v3RrpK9Is7NJZH0fOShUfY8lp5oz9un8K5L24LeGGVtiBTXkBMc9UKkTMdQeDS)

   https://www.cnblogs.com/butterfly100/p/9034281.html

3. 数据库范式

   第一范式：第一范式就是说数据库的每一列都是不可分割的基本数据项

   第二范式：第二范式就是主键约束，要求表中要有主键，其他字段要依赖于主键。

   第三范式：第三范式是外键约束，要求表中不能存放其他表中已经有的数据，如果需要其他表的数据，要使用外键进行关联。

   1. SQL中on和where的区别

   **inner join先产生笛卡尔积，on条件过滤出符合条件的笛卡尔积，最后形成一个临时表。再在临时表上根据where条件过滤。**

   写SQL时的一些注意事项：

   **EXISTS和IN的区别？**

   假如现在有SQL语句：

   ```
   SELECT * FROM A a WHERE a.id IN(SELECT id FROM B);
   SELECT * FROM A a WHERE EXISTS(SELECT 1 FROM B b WHERE a.id=b.id);
   ```

   IN的执行过程是，先查询子句SELECT id FROM B，将**结果排序**并缓存为临时表（**临时表在内存**）。然后循环表A中的每个数据，对临时表进行**二分查找**。过程就像：

   ```
   List results = new List();
   List aData = (select * from A);
   List bData = (select id from B);
   // 循环
   for(int i = 0; i < aData.length; i++) {
       // 二分查找
       if(binarySearch(bData, aData.get(i))) {
           results.add(aData.get(i));
       }
   }
   ```

   EXISTS的执行过程是，循环表A中的每条数据，每条数据再去执行SQL语句SELECT 1 FROM B b WHERE a.id=b.id。过程就像：

   ```
   List results = new List();
   List aData = (select * from A);
   // 循环
   for(int i = 0; i < aData.length; i++) {
       if(exists(aData.get(i))) {
           results.add(aData.get(i));
       }
   }
   ```

   总结：这两条语句的执行结果相同，但是内存中的二分查找是比查询一次数据库快的。所以，**IN**比较适合B表较小的情况，只有B表比较小才能放在内存中；如果B表比较大，就要选用**EXISTS**。

   **GROUP BY语句？**

   有索引时，使用索引进行GROUP BY，否则使用临时表或者文件排序。

   **UNION的效率比较低？**

   MySQL使用临时表的方式执行UNION操作，MySQL会对临时表作唯一性检查，代价非常高。使用UNION ALL就不会，可以在应用端去重。

   **为什么不让使用DISTINCT？**

4. 最左前缀原则的含义？

   最左前缀原则指的是，**联合索引的最左N个字段**或者**字符串索引的最左M个字符**。

5. Multi-Range Read优化和索引下推

   Multi-Range Read优化也叫作MRR优化。在使用辅助索引查找到主键之后，需要回表再查一次，但是这次的回表查询是随机访问，硬盘的随机访问效率不高。MRR就是将这个随机访问改造成相对有序的访问，对于磁盘来说顺序访问效率比较高。MRR优化的运行过程是：首先将辅助索引上查找到的辅助索引缓存下来，在根据主键进行排序，然后根据主键的排序顺序来进行回表。

   现在有表T，T上有(name, age)的联合索引，有查询语句**SELECT \* FROM T WHERE name LIKE '张%' AND age = 10 AND gender = 1**。如果不使用索引下推，这个SQL的执行过程是：存储引擎先通过索引取出符合条件**name LIKE '张%'\**的所有数据行，再交给SERVER层根据\**age = 10 AND gender = 1**条件进行过滤。如果使用了索引下推，SQL的执行过程是：存储引擎先通过索引取出符合条件**name LIKE '张%'\**的所有数据行，再根据\**age=10**进行过滤，将过滤后的数据行交给SERVER层，SERVER层再根据**gender=1**进行过滤。索引下推减少了SERVER层获取数据的条数，提高了数据库的整体性能。可以通过在执行计划的Extra列看是否有Using index condition来查看是否使用了索引下推。

6. MySQL两阶段锁

   两阶段锁：InnoDB存储引擎中，行锁只在需要的时候才会加上，但是并不是在使用完之后立即释放掉，而是在事务提交时才统一释放掉。两阶段锁提示我们，要将最可能造成冲突的锁放在后面靠近事务提交的位置，这样的话才能让被阻塞的事务等待的时间最短。

   **行锁带来的问题：热点行更新导致性能问题**

   比如现在有1000个线程要同时更新一行，那么**那些后来的被阻塞在这行记录上的线程就会进行死锁检测**。具体的做法是：假设A、B、C同时修改一行，B、C被阻塞住，这是来了事务D，D要依次检查B、C看他们是否形成了循环等待，是否形成了死锁，这是O(n)的操作，在这里的话时间复杂度就是百万级别，要消耗掉大量的CPU资源。

   解决办法是：将热点行的数据拆分成多行，这样多个线程就会竞争多行上的行锁。

   **需要注意的是只有阻塞在同一行记录上的线程才会相互进行死锁检测。**

7. 事务的Repeatable Read和Read Commit隔离级别的实现？

   在RR隔离级别下，事务不可以读取到在它之后提交的事务的更改。这是通过MVCC来实现的，MVCC又是通过undo log来实现的。每个事务都有一个事务ID，这个事务ID是在事务开始的时候向系统申请的，事务ID是严格递增的。每次数据行被修改时就会记录到undo log中，同时也会记录下执行这次修改的事务的事务ID。在RR隔离级别下，每次事务开始运行时，都会创建一个Read View，在Read View中会有一个数组，用来记录当前系统中还没提交的事务的ID。当使用SELECT读取一行数据时，就会根据undo log读取数据行，如果当前版本的事务ID在Read View中，就说明这个版本对事务不可见，再根据undo log读取上一个版本，如果上一个版本的事务ID也在Read View中，就再找上上个版本，直到找到一个版本的事务ID不在Read View中为止。因为RR隔离级别下的Read View是在事务开始时生成的，在整个事务期间不会再重新生成，所以不会产生**不可重复读问题**。这种读是快照读。在RR隔离级别下，对数据行的修改必须用当前读，当前读是要读取数据的最新值，这要通过行锁来实现。

   RC隔离级别和RR隔离级别的不同就在Read View的生成上，在RR隔离级别下，是在事务开始时生成一个Read View，整个事务期间不会再生成新的Read View。而RC隔离级别下，每次读请求都会生成一个新的Read View，所以RC隔离级别会产生不可重复读的问题。

8. change buffer

   change buffer提高了**非唯一索引**的插入和更新效率。通常应用程序中行记录的插入顺序是按照主键自增插入的，这是一个磁盘的循序读取，磁盘的顺序读取比较快。如果这个表上还有一个普通索引的话，在插入主键索引的同时，还要维护普通索引，一般情况下，对于普通索引的插入不是有序的，这会导致磁盘的随机读取，磁盘的随机读取比较耗时。

   有了change buffer之后，当插入或者更新一行记录时，先查看数据页是否在内存中，如果在直接更新内存中的数据页；否则，现将更新记录到change buffer中，当下次查询将这个数据页加载到内存时，再将change buffer中的修改merge到数据页，这样可以将多个修改操作造成的多次读磁盘降低为读一次磁盘，提高了非唯一索引的更新效率。**为什么一定是非唯一索引？**因为唯一索引要检查唯一性约束，在更新唯一性索引时必然要将数据页读入内存，change buffer的使用价值不大。

   **什么样的场景适合change buffer？**

   更新频繁但是查询并不频繁的场景比较适合change buffer。读取频繁的场景不适合！

9. MySQL中的ORDER BY算法？

   ```
   CREATE TABLE `t` (
     `id` int(11) NOT NULL,
     `city` varchar(16) NOT NULL,
     `name` varchar(16) NOT NULL,
     `age` int(11) NOT NULL,
     `addr` varchar(128) DEFAULT NULL,
     PRIMARY KEY (`id`),
     KEY `city` (`city`)
   ) ENGINE=InnoDB;
   SELECT city, name, age FROM t WHERE city = '北京' ORDER BY name LIMIT 1000
   ```

   - name字段上没有索引

     **全字段排序**

     1. 为每个查询线程初始化一个sort_buffer，sort_buffer中要放入city、name、age这三个字段。
     2. 查询辅助索引city，找到第一个满足条件city=‘北京’的数据行，取出主键id。
     3. 根据主键id回表查找整行记录，取出city、name、age放到sort_buffer中。
     4. 再在辅助索引sort_buffer上顺序取出下一行记录的id。
     5. 重复步骤3、4，直到取到的记录不再满足city='北京'为止。
     6. 将sort_buffer中的所有记录按照name字段进行排序。
     7. 在排序后的数据集中取出top 1000行，返回。

     这个sort_buffer的大小可以通过sort_buffer_size参数设置。如果满足条件的数据不是很多，可以都放在sort_buffer中，那就只需要内存排序；否则，就要使用临时文件进行外部排序。可以通过EXPLAIN的EXTRA列中是否有Using filesort来查看是否使用外部排序。

     外部排序使用的是归并排序，当数据装满sort_buffer后排序形成一个局部有序的临时文件。所以，同一个数据集，sort_buffer约大，使用的临时文件数目越小，排序的时间越快。

     这里查询只有city、name、age这3个字段，如果有某一个查询有许多字段，这样sort_buffer中也放不下几行数据，这就会导致使用的临时文件数目增多，这时考虑使用rowid排序。

     **rowid排序**

     1. 为每个查询线程初始化一个sort_buffer，现在由于要查询的字段数目很多，sort_buffer中只放主键和排序字段。在这里就是id和name。
     2. 查询辅助索引city，找到第一个满足条件city='北京'的记录，取出主键id
     3. 根据主键id回表查询整行记录，取出name、id放到sort_buffer中
     4. 再在辅助索引上**顺序**取出下一行满足条件的记录，并取出主键id
     5. 重复步骤3、4知道取到的记录不再满足条件city=‘北京’为止
     6. 将sort_buffer按照name字段进行排序
     7. 从排序完的数据集中取出top 1000行，再根据这1000行的主键id回表查询出来city、name、age返回给客户端。

     可以看到rowid排序在最后比全字段排序多了一个回表查询的步骤。

     什么时候使用rowid排序？

     如果MySQL觉得sort_buffer还够用，就使用全字段排序，否则，使用rowid排序。**max_length_for_sort_data**，是 MySQL 中专门控制用于排序的行数据的长度的一个参数。它的意思是，如果单行的长度超过这个值，MySQL 就认为单行太大，要换一个算法。

   - name字段上有索引

     如果现在有联合索引(city, name)，在条件city='北京'的条件下，name已经是有序的。

     1. 根据联合索引(city, name)查找到第一个满足条件city=‘北京’的记录，并取出主键id
     2. 根据主键id回表查找到city、name、age，作为结果集的一部分返回
     3. 在取出满足条件city=‘北京’的下一行记录，取出主键id
     4. 重复步骤2、3直到取出了1000行记录或者是已经没有了满足条件city=‘北京’的记录，就停止

     可以看到这个过程并不需要排序，因为数据已经是按照city、name排好序的。这个过程还可以优化，通过MRR和覆盖索引。

     什么情况下会用到**Index排序**？

     1. 当order by子句中使用的是索引的最左前缀列
     2. 当where子句和order by子句组合起来使用的是最左前缀列，这个例子就是

10. MySQL中的JOIN算法？、

    ```
    CREATE TABLE `t2` (
      `id` int(11) NOT NULL,
      `a` int(11) DEFAULT NULL,
      `b` int(11) DEFAULT NULL,
      PRIMARY KEY (`id`),
      KEY `a` (`a`)
    ) ENGINE=InnoDB;
    
    drop procedure idata;
    delimiter ;;
    create procedure idata()
    begin
      declare i int;
      set i=1;
      while(i<=1000)do
        insert into t2 values(i, i, i);
        set i=i+1;
      end while;
    end;;
    delimiter ;
    call idata();
    
    create table t1 like t2;
    insert into t1 (select * from t2 where id<=100)
    ```

    - **Index Nested-Loop Join**

      ```
      select * from t1 straight_join t2 on (t1.a=t2.a);
      ```

      这条语句使用了t1作为驱动表，t2作为被驱动表。

      join的执行过程是：

      1. 从驱动表t1中读取一行数据
      2. 取出数据行的a属性，根据a属性到t2中查找
      3. 取出t2中满足条件的行，跟t1中的数据行拼接成最终结果的一部分
      4. 重复1~3，直到将t1中的数据取完

      分析：假设t1有N行，t2有M行，并且对于t1中的每一行在t2中有且仅有一行与之对应。

      这样整个执行过程对t1扫描N行，对于t1中的每一行都要先到t2的a索引上查找再回表查询，所以对于t1的每一行，t2需要的时间复杂度接近于2logM。整个查询的时间复杂度是：N + N * 2logM。可以看出驱动表越小，时间复杂度越小。

    - **Block Nested-Loop Join**

      如果在被驱动表t2的join列上没有索引，就要用到Block Nested-Loop Join算法。

      join的执行过程是：

      1. 将驱动表t1中的数据加载到join_buffer中，因为这里使用的是select * 所以将整个驱动表t1加载到join_buffer（如果join_buffer够用的情况下）
      2. 全表扫描t2，取出被驱动表t2中的一行记录，看是否与join_buffer中的数据行匹配，如果匹配就放到结果集中

      分析：假设t1有N行，t2有M行，并且对于t1中的每一行在t2中有且仅有一行与之对应。

      整个执行过程，需要扫描驱动表N行记录，被驱动表M行记录，总计扫描N + M行记录，执行内存判断次数是M * N

      join_buffer的大小是由join_buffer_size参数配置的，如果驱动表大小大于join_buffer。这时就需要将驱动表分成多个块放到join_buffer中，执行算法。比如：join_buffer只能存放80条t1的数据行，第一次执行算法，将80行放到join_buffer，执行**Block Nested-Loop Join**。完成之后，再将剩下的20行放到join_buffer中，再执行**Block Nested-Loop Join**算法。所以整个执行过程扫描了80 + 1000 + 20 + 1000，内存比较次数还是100 * 1000。更通用一些，假设t1有N行，t2有M行，N行被分成K块，K=λN，扫描行数就是 N + λN*M，内存比较次数是N*M,，可见驱动表越小，时间复杂度越小。

    总结：JOIN时要使用“小表”作为驱动表，那什么是“小表”？

    在决定哪个表做驱动表的时候，应该是两个表按照各自的条件过滤，过滤完成之后，计算参与 join 的各个字段的总数据量，数据量小的那个表，就是“小表”，应该作为驱动表。

11. MySQL中一条语句为什么很慢？

    - 经常慢

      那是SQL语句写得有问题

    - 偶尔慢

      **可能是MySQL在刷脏页**

      MySQL并不是直接更新磁盘上的数据页的，而是先将磁盘上的数据页读到内存中，再更新内存中的数据页。随着数据库的运行，就可能造成内存中的数据页比较新，磁盘上的数据页比较旧的局面。内存中的数据页就是“脏页”，将“脏页”同步到磁盘上的数据页的过程称为flush。

      引起flush的原因有4个：

      1. redo log文件写满了，必须将checkpoint向前移动一些，腾出redo log文件的一部分空间给InnoDB使用。在移动checkpoint期间，要将移动的这一部分redo log涉及的内存中的“脏页”flush到磁盘，移动checkpoint期间会**停掉所有更新操作**。
      2. 是buffer pool中内存不够啦，需要淘汰掉一些数据页空出一些内存来给新数据页使用。如果被淘汰掉的数据页是“干净页”那就直接淘汰掉，否则，需要将“脏页”flush到磁盘，而且如果flush的“脏页”的邻居是“脏页”，也会把邻居页给flush到磁盘，这更进一步延长了淘汰数据页的时间。**如果一次淘汰的“脏页”过多，会影响查询性能**。
      3. MySQL在觉得系统空闲时，flush一些“脏页”到磁盘。
      4. MySQL正常关机时，会将buffer bool中的“脏页”flush到磁盘。

      出现1、2两种情况，应该是刷新“脏页”的频率过慢导致的。

      在MySQL中刷脏的速度是**由脏页比例（记为M）**和**当前写入的序号跟 checkpoint 对应的序号之间的差值（记为N）**两个因素决定的。其中脏页比例通过以下SQL语句计算得出：

      ```
      mysql> select VARIABLE_VALUE into @a from global_status where VARIABLE_NAME = 'Innodb_buffer_pool_pages_dirty';
      select VARIABLE_VALUE into @b from global_status where VARIABLE_NAME = 'Innodb_buffer_pool_pages_total';
      select @a/@b;
      ```

      首先根据M计算出一个在0~100之间的值，F(M)，计算的伪代码是：

      ```
      // innodb_max_dirty_pages_pct是脏页比例上限，默认是75%
      F1(M)
      {
        if M>=innodb_max_dirty_pages_pct then
            return 100;
        return 100*M/innodb_max_dirty_pages_pct;
      }
      ```

      在根据N计算出一个0~100之间的数，F(N)。取F(M)和F(N)的最大者为R，按照 innodb_io_capacity 定义的能力乘以 R% 来控制刷脏页的速度。 innodb_io_capacity 数据库落盘脏页个数 ,配置压力和磁盘的性能相关，如果过大，IO能力不足，则出现卡顿。innodb_io_capacity默认是200，单位是页，该参数的设置大小取决于硬盘的IOPS，即每秒的输入输出量(或读写次数)。

12. Mysql如何解决脏读、不可重复读问题？
    当前读下(insert、update、delete、select ..... for update、select ... in shard mode)使用锁来实现。
    在快照读下使用