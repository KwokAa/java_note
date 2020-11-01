## HashMap与ConcurrentHashMap

### HashMap

#### 为什么不是线程安全的？

因为HashMap的get和set函数都没有使用任何同步机制，多线程访问下，可能出现一个线程刚刚put完一个key之后，紧接着再取出这个key的值已经不再是刚才设置的那个值啦。

#### 为什么重写了equals方法之后一定要重写hashcode方法？

get值的时候是根据key的hashcode计算相应的index，再在链表中使用equals方法比对各个key是否等于要查找的key。put值得时候也是根据key得hashcode计算相应得index，再在链表中通过使用equals方法比较插入得key是否与链表中得key相等，相等则覆盖，不相等则新增。**重写了equals方法之后，就要重写hashcode方法，要保证相等的对象有相同的hashcode，不相等的元素的hashcode不相同。**假设相等的两个对象的hashcode的值不相同，那么两个相同的值就很有可能在计算index时，会计算出来不同的index，就会存放到HashMap的不同位置上，这就造成HashMap中有两个相同的值。不相等的元素有不相同的hashcode可以让不同的对象更松散的分布在HashMap中，考虑一种极端的情况，所有的对象的hashcode都相同，那么HashMap就退化成了一个链表啦。

#### 有什么线程安全的类可以替代吗？

Hashtable、ConcurrentHashMap。

Hashtable是线程安全的，它是使用synchronized进行的同步，并发度比较低。ConcurrentHashMap还可以。

Collections.synchronizedMap(Map)创建线程安全的map集合

#### HashMap底层数据结构是什么？

java7之前HashMap使用的是数组+单向链表，java8使用的是数组+单项链表+红黑树

#### Java7和Java8中的区别？

java7中使用的是数组+单项链表，链表的节点类是entry，链表使用的是头插法，java8中使用的是数组+单项链表/红黑树，链表节点类是node，链表使用的是尾插法。java7时，多线程环境下使用HashMap有可能造成循环链表死循环，java8中，多线程环境下已经不会出现这种问题。

java7中HashMap在扩容的时候对于每个哈希桶内的每个key都会重新计算它在新的哈希桶数组中的位置，java8优化成了对于每个哈希桶将形成两个链表，一个链表内的key还在原位置，另一个链表在原位置+oldCapacity的新位置，这样就不用对每个key重新计算新的位置。

#### 初始值多少？为什么是2的幂大小？

初始值大小是16，负载因子是0.75。index的计算规则是key的hashcode和容量-1进行按位与，在容量是2的幂次方时，这种按位运算就相当于求余，并且位运算比求余运算高效。

#### HashMap的扩容机制

如果哈希桶数组很大，即使再差的Hash算法，也能将key均匀分散开来，但是比较占内存；如果哈希桶数组过小，冲突的概率就比较大，导致哈希桶数组中的链表比较长，存取时效率不高。为了平衡空间和时间效率，HashMap引入了动态扩容机制。HashMap中有几个比较重要的属性，threshold记录HashMap扩容的阈值，loadFacroty记录的是负载因子，size记录的是整个HashMap中key的数量，当HashMap中key的数量大于threshold时，HashMap就开始扩容，哈希桶数组的大小变成原来的2倍，旧的哈希桶数组中的key重新映射到新的哈希桶数组，映射完成之后，用新的哈希桶数组替换旧的哈希桶数组。

#### HashMap的存取过程

#### 计算hashcode的规则

如果key等于null，直接返回0；如果key不等于0，先调用key的hashcode方法得到哈希码，将哈希码的逻辑右移16位再与哈希码进行按位异或。

### Collections.synchronizedMap(map)

调用Collections.synchronizedMap(map)，就会创建一个SynchronizedMap对象并返回，SynchronizedMap对象有一个属性map指向了你传递进来的map，一个mutex指向新建的SynchronizedMap对象作为访问map的锁，每次调用SynchronizedMap对象的方法时都会加锁。

### Hashtable

Hashtable是线程安全的，但是效率比较低，因为它在对数据进行操作的时候都会加上监视器锁synchronized。

Hashtable不允许key和value为null，HashTable允许key和value为null。Hashtable进行put时会检查value是否为null，如果为null，就报错，并且如果key为null，在调用key.hashcode()函数时也会报错。HashMap在key为null时将key的哈希码变成0来处理。

Hashtable继承了Dictionary类，HashMap继承的是AbstractMap类

Hashtable的初始容量是11，HashMap的初始容量是16，但是他们的负载因子都是0.75，Hashtable的底层结构是数组+单向链表

Hashtable扩容的大小是原来的两倍加1，HashMap的扩容后的大小是原来的两倍

Hashtable的迭代器是fail-safe的，而HashMap的迭代器是fail-fast的

### ConcurrentHashMap

#### 为什么快？

java7时，其底层实现是数组+链表，其中数组是Segment类型，Segment对象中有包括一个HashEntry类型的对象，这是真正存放数据的地方。HashEntry对象的key和next 都使用volatile关键字，保障了可见性。Segment继承了ReentrantLock，当一个线程获取了一个Segment对象上的锁之后，并不影响其他的线程处理其他的Segment对象，理论上CocurrentHashMap支持Segment数组大小个线程并发。

java8中，底层采用了数组+链表/红黑树，放弃了使用Segment分段锁，使用CAS+synchronized来同步。

#### put的过程

java8中put的过程是：

1. 根据key计算出hashcode
2. table如果为空或者长度等于0，就需要进行初始化
3. 根据hashcode计算出key的index，如果table[index]为空，则使用CAS进行插入，失败则自旋
4. 如果hashcode == MOVED == -1就需要进行扩容
5. 如果上面都不满足，则通过synchronized对table[index]加锁，如果是红黑树，就在红黑树上插入，否者就遍历链表进行插入。
6. 最后判断table[index]这个链表是否超过阈值，超过就转换成红黑树

java7中的put过程是：

1. 根据key计算出来hashcode
2. 根据hashcode计算出key在Segments数组里面的index
3. 尝试获取Segment上的锁，如果获取所不成功就自选获取锁，自选到一定的次数就阻塞
4. 获取到锁后，根据hashcode计算出在HashEntry数组中的index
5. 遍历HashEntry数组索引index上的链表，进行插入，如果超过阈值，进行扩容
6. 释放锁

#### 如何计算哈希桶的位置？

(hash ^ (hash >>> 16)) & 0x7FFFFFFF

#### 为什么key、value不能等于null？

key为null是，无法区分出来是不存在，还是就是为null值

### fail-fast(快速失败)

在遍历容器的过程中，一旦发现容器的数据被修改啦，立即抛出ConcurrentModificationException异常来终止遍历，java.utils包下的集合类都是快速失败机制的

```java
List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        int i = 0;
        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (i == 3) {
                list.remove(3);
            }
            i++;
            System.out.println(iterator.next());
        }

HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            map.put(i, i);
        }
        int i = 0;
        Iterator<Map.Entry<Integer, Integer>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            if (i == 3) {
                map.remove(i);
            }
            Map.Entry<Integer, Integer> next = iterator.next();
            i++;
        }
```

容器类里面有一个整型modCount属性，当有涉及到容器元素个数变动的操作时，比如对于HashMap，put一个原本不存在的key，remove一个key等，都会造成modCount增加1。再使用迭代器遍历的时候，容器类内部的迭代器类在实例化时会拷贝一个modCount值到expectedModCount 属性。然后在每次调用next方法时，都会检查modCount==expectedModCount是否成立，如果不成立就抛出ConcurrentModificationException异常。

### fail-safe(安全失败)

采用安全失败的集合在遍历时都是复制一份原有集合的数组，在这个副本上进行遍历。由于遍历的是原有集合类的一个副本，所以即使在遍历的过程中并发修改集合的内容也不会抛出ConcurrentModificationException异常。java.util.concurrent包下的容器都是安全失败的，比如常见的CocurrenHashMap和CopyOnWriteArrayList。

### HashSet为什么可以去重？

HashSet内部基于HashMap来实现，HashSet对象内部有一个HashMap的实例，当调用HashSet对象的add方法时其实是间接调用HashMap的put方法，调用put方法时的key是调用add方法传递过来的值，value则只是一个占位符。因为HashMap的key是不可以重复的，所以HashSet可以去重。