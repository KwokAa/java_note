### 内存管理机制

Slab Allocation机制。
将分配给memcached的内存切分成一个个的大小不同的chunk。相同大小的chunk分成一个组，在将数据存储到memcached时，找到第一个合适的一个组内的一个chunk。分配给memcached的内存是不会释放的，会重复利用。
缺点：内存利用率比较低。
优点：解决了直接使用Allocate、Free导致的内存碎片问题。

### Consistent Hashing

首先求出memecached服务器的哈希值，根据哈希值映射到0～2的32次方的圆上。在向memcached服务器上设置key-value时，选择服务器时，先计算出key的哈希指，同样的根据key的哈希值映射到0～2的32次方的圆上，然后顺时针找到第一个服务器。找到的这个服务器就是要存放这个key-value的服务器。
<img src="images/Memcached/memcache%E4%B8%80%E8%87%B4%E6%80%A7hash1.png" alt="img" style="zoom:50%;" />
<img src="images/Memcached/memcache%E4%B8%80%E8%87%B4%E6%80%A7hash2.png" alt="img" style="zoom:50%;" />

### memcached与redis比较

1. redis支持多种数据结构，包括：String、List、Set、Zset、Hash；memcached只支持简单的key-value，如果想存储复杂的数据类型，需要在客户端自己处理。
2. redis支持持久化数据到磁盘，有RDB和AOF两种持久化机制，宕机之后恢复可以从磁盘载入数据；memcached不支持持久化，宕机之后数据就丢失了。
3. memcached是在客户端使用一致性hash做分布式；redis支持在服务器端做分布式(Twemproxy、Redis Cluster)。
4. redis使用的是单线程模型；memcached使用cas保证多线程下数据一致性。

### memcached多线程

### 二进制协议