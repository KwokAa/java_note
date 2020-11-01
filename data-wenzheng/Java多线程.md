## Java多线程

### 线程安全

运行一个Java程序的实质就是启动一个Java虚拟机**进程**。进程是程序向操作系统申请资源的基本单位，线程是进程内的可以独立执行的最小单元。一个进程内可以包含多个线程，**一个进程内的多个线程**可以共享该进程的资源。线程所要完成的计算被称为任务。

#### 原子性

Java中对基本类型（除了long和double类型之外的byte、boolean、int、short、float、char）和引用类型的赋值都是原子操作。但是如果使用**volatile**关键字进行修饰，可以实现对long、double类型变量的写原子性。Java对任何类型的变量的读操作都是原子性的。

原子性的不可分割性应该包含以下两层含义：

- 读写某个共享变量的操作从其执行线程之外的其他线程来看，这个操作要么已经执行结束要么还未开始，即其他线程不会看到该操作执行的中间结果。
- 访问同一组共享变量的操作不能交替执行。

#### 可见性

在多线程环境下，一个线程更新了一个共享变量的值，后续其他的线程可能没办法立即看到该共享变量最新的值，这就是可见性问题。

Java语言规范保证在父线程启动子线程之前对共享变量的修改对子线程都是可见的，一个线程终止后该线程对共享变量的修改对调用该线程join方法的线程来说是可见的。

#### 顺序性

- 源代码顺序。

  就是平时写代码时的语句顺序。

- 程序顺序。

  java虚拟机执行代码分为两种：解释执行（被执行的是字节码）、编译执行（被执行的是机器码）。程序顺序是指经过编译器编译之后生成的字节码或者机器码的顺序。

- 执行顺序。

  代码在处理器上实际执行时的顺序。

- 感知顺序。

  给定处理器感知到的该处理器或其他处理器的内存访问操作的顺序。

##### 指令重排序

在源代码顺序和程序顺序不一致、程序顺序与执行顺序不一致时发生了**指令重排序**。指令重排序的来源主要是**编译器**、**处理器**。Java平台主要包括两种编译器：静态编译器、JIT编译器。静态编译器的作用是在程序运行之前，将java源代码编译成字节码。JIT编译器的作用是在程序运行过程中将字节码动态的编译成机器码。在java平台中，静态编译器基本上不会执行指令重排序，而JIT编译器可能导致指令重排序。

##### 存储子系统重排序

存储子系统重排序也叫做内存重排序。即使在源代码顺序、程序顺序、执行顺序一致的情况下，感知顺序与执行顺序也可能不一致，这就是内存重排序。内存重排序是由高速缓存、写缓冲区引起的。

### 线程属性

#### ID(ID)

线程ID是类型为long的只读属性，用于标识不同的线程，不同的线程有不同的编号。当一个线程运行结束之后，它的ID还可以被后来创建的线程所重复使用。

#### 名称(Name)

线程名称是类型为String的可读写属性，用于标识不同的线程。线程名称主要是为了方便开发者以人类可读的方式标识不同线程，默认情况下（也就是不显示的设置线程名称），线程名称的格式是“Thread-线程ID”。可以将两个不同的线程设置同一个名称，但是这样不利于区分这两个线程，会为调试带来麻烦。

#### 类别(Daemon)

线程类别是一个boolean类型的属性，当值为true时表示相应的线程是一个守护线程，当值为false时表示这是一个用户级线程。默认情况下，这个属性的值与父线程的该属性的值相同。可以通过在线程实例上调用setDaemon()方法来设置线程的类别，但是对setDaemon()方法的调用必须先于对start()方法的调用，否则会抛出IllegalThreadStateException异常。

用户线程会阻止Java虚拟机正常结束，也就是说Java虚拟机必须等待其所有的用户线程运行结束后才能结束。而守护线程不会阻止Java虚拟机的正常结束，应用程序中有守护进程也不影响Java虚拟机的正常结束。所有，守护进程常常用于执行一些不太重要的任务，比如，监控其他进程的状况。

**默认情况下，如果父线程是守护线程，子线程也是守护线程；如果子线程是用户线程，子线程也是用户线程。**

#### 优先级

线程优先级是一个**可读写**的int类型的属性，实质上是给线程调度器的提示，用于表示应用程序希望优先调用哪个线程。java设置了1~10个线程优先级。这个属性的默认值一般是**5**（表示普通优先级），**具体到一个线程上，该线程的优先级默认是父线程（创建该线程的线程）的优先级**。

### 线程创建的三种方式

1. 直接继承Thread类创建线程类

   Thread类或者Thread子类的一个实例就是一个线程。线程的任务处理逻辑就是**run方法**的具体实现，run方法可以理解为线程的任务处理逻辑的入口方法。run方法由Java虚拟机在运行相应线程时直接调用，而不是由应用程序代码调用。Thread类的**start方法**用于启动一个线程，启动一个线程的实质是请求Java虚拟机运行该线程，但是这个线程具体何时执行取决于**线程调度器**。运行结束的线程所占用的资源会被Java虚拟机垃圾收集器回收。

   线程A调用线程B的**join()**方法，将导致线程A被阻塞，直到线程B运行结束。

   当在当前线程中运行**Thread.yield()**时，将使当前线程主动放弃对处理器的占用，会导致当前线程暂停。但是，这个方法并不是可靠的，该方法被调用后当前线程依然可能还在运行（视当前系统情况而定）。

2. 实现Runnable接口创建线程类

3. 使用Callable和Future创建线程

```
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class ThirfThread {
	public static void main(String[] args) {
		FutureTask<Integer> task = new FutureTask<Integer>(
				(Callable<Integer>)()->{
					int j = 0;
					for (; j < 100; j++) {
						System.out.println(Thread.currentThread().getName()
								+ " 的循环变量i的值：" + j);
					}
					return j;
				});
		for (int i = 0; i < 100; i++) {
			System.out.println(Thread.currentThread().getName()
					+ " 的循环变量i的值： " + i);
			if (i == 20) {
				new Thread(task,"有返回值的线程").start();;
			}
		}
		
		try {
			//获取线程的返回值
			System.out.println("子线程的返回值：" + task.get());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
```

Callable接口不是Runnable接口的子接口，所以Callable不能直接作为Thread的target。Java5提供了Future接口来代表Callable接口里call()方法的返回值，并为Future接口提供了一个FutureTask实现类，该实现类实现了Future接口和Runnable接口，可以作为Thread的target。在Future接口里定义了如下几个公共方法来控制与它关联的Callable任务：

- boolean cancel(boolean mayInterruptIfRunning) 试图取消该Future里关联的Callable任务。
- V get(): 返回Callable任务里call()方法的返回值。***调用该方法将导致程序阻塞***，必须等到子线程结束后才会得到返回值。
- V get(long timeout, TimeUnit unit)：返回Callable任务里call方法的返回值。该方法让程序最多阻塞timeout和unit指定的时间，如果经过指定时间后Callable任务依然没有返回值，将会抛出TimeoutException异常。
- boolean isCancelled(): 如果在Callable任务正常完成前被取消，则返回true。
- boolean isDone(): 如果Callable任务已经完成，则返回true。

实现Runnable接口与实现Callable接口来创建多线程，差别不大。不同的地方只是Callable接口内的call方法可以返回值并且可以抛出异常。

采用实现Runnable、Callable接口的方式创建多线程的优缺点：

- 线程类只是实现了Runnable接口或Callable接口，还可以继承其他类。
- 多个线程可以共享同一个target，非常适合多个相同线程处理同一份资源的情况。从而将CPU、代码、数据分开。
- 从面向对象编程的角度来看，这是采用组合的方式，类和类之间的耦合度更低，更加灵活。
- 编程复杂，***如果需要访问当前线程，则必须使用Thread.currentThread()方法***。

采用继承Thread类的方法创建多线程的优缺点：

- 因为线程继承了Thread类，所以不能够再继承其他类。
- 编程简单，***如果需要访问当前线程，直接使用this即可获得当前线程***。

### 线程的生命周期

新建（New）、就绪（Runnable）、运行（Running）、阻塞（Blocked）和死亡（Dead）。

当程序使用new关键字创建一个线程之后，该线程就处于新建状态，这时它和其他的Java对象一样，仅仅由Java虚拟机为其分配内存，并初始化其成员变量。

当线程对象调用了start()方法后，该线程处于就绪状态。Java虚拟机会为其**创建方法调用栈和程序计数器**。处于这个状态的线程还没开始执行，只是表示可以开始运行了。至于何时开始运行，取决于Java虚拟机里线程调度器的调度。

如果希望调用子线程的start方法后子线程立即开始执行，程序可以使用Thread.sleep(1)来让当前运行的线程（主线程）睡眠1ms。在这1ms内CPU会去执行另一个处于就绪状态的线程，这样就可以让子线程立即开始执行。

当发生如下情况时，线程将会进入阻塞状态：

- 线程调用sleep()方法主动放弃所占用的处理器资源。
- 线程调用一个阻塞式IO方法，在该方法返回之前，该线程被阻塞。
- 线程试图获得一个同步监视器，但该同步监视器正被其他线程所持有。
- 线程正在等待某个通知。
- 程序调用了线程的suspend方法将线程挂起。

相对于上面线程的阻塞原因，也有相应的线程从阻塞到重新就绪的原因。

线程会以如下三种方式结束，结束后就处于死亡状态:

- run()或call()方法执行完成，线程正常结束。
- 线程抛出一个未捕获的Exception或Error。
- 直接调用该线程的stop方法来结束该线程，该方法容易导致死锁。

当主线程结束后，其他线程不受任何影响，并不会随之结束，一旦子线程启动起来后，它就拥有和主线程相同的地位，它不受主线程的影响。Java中没有方法来获取线程的父线程和其全部的子线程。可以调用线程对象的isAlive()方法来测试某个线程是否死亡，如果线程处于就绪、运行、阻塞三种状态时，该方法将返回true；当线程处于新建、死亡两种状态时，该方法将返回false。不可对已经死亡的线程调用start()方法，否则将会引发IllegalThreadStateException异常。

### 线程同步机制

线程同步的原理是内存屏障。按照可见性分析，分为加载屏障和存储屏障。按照有序性分析，可以分为获取屏障和释放屏障。

#### 锁

锁会引起上下文切换，从而带来开销。

##### 内部锁

就是使用synchronized关键字时使用的锁。

##### 显式锁

Lock接口定义的锁。

#### volatile关键字

volatile关键字的作用包括：保障可见性、保障有序性、保障对double、long型变量的读写的原子性。对于数组和引用类型的共享变量，volatile关键字并不能保证读写相应对象的属性（实例属性、静态属性）和元素的原子性。

举个例子：假设有如下所示的赋值操作

```
count1 = count2 + 1
```

假如count2是共享变量，那么这个操作实际上是read-modify-write操作。在操作过程中，其他线程可能已经修改了count2的值，因此这不是一个原子操作。如果count2是一个局部变量，这就是一个原子操作。所以可以总结，对volatile关键字进行赋值的操作，只要赋值号右边的表达式中包含共享可变变量（包括这个被赋值的volatile变量本身），这个赋值操作就不是原子操作。

volatile关键字也被称为**轻量级锁**。volatile关键字用于修饰共享可变变量。被volatile关键字修饰的变量是volatile变量，volatile变量不会被编译器分配到寄存器中存储，对volatile变量的读写操作都是直接访问内存的。volatile关键字与锁的相似性在于，它也可以保证可见性和有序性。volatile关键字仅能保证写和读volatile变量的原子性，但是没有锁的排他性。**volatile关键字不会引起上下文切换**。

#### 原子变量类

原子变量类是基于CAS、volatile变量实现的。

#### static和final关键字

对于引用型静态变量，static关键字可以保证一个线程可以读取到该变量的初始值，这个初始值所指向的对象已经被正确初始化。

### 线程间协作机制

#### Object.wait()/Object.notify()

Object.wait()的用法，模板代码如下：

```
synchronized(lock){
    while(保护条件不成立) {
        lock.wait();
    }
}
```

Object.notify()的用法，模板代码如下：

```
synchronized(lock) {
    // 更新等待线程涉及的共享变量
    updateShareState();
    lock.notify();
}
```

调用lock.notify()会唤醒lock对象等待集上的任意一个线程，而调用lock.notifyAll()会唤醒lock对象等待集上的所有线程。

缺点：

- 过早唤醒

  假设有一组线程同步在lock对象上。等待线程T1、T2在等待保护条件C1成立，等待线程T3、T4在等待保护条件C2成立，现在通知线程修改了共享变量使得保护条件C2成立，通知线程为了唤醒所有等待保护条件C2的线程而调用了lock.notifyAll()方法，因为调用lock.notifyAll()会唤醒lock对象等待集上所有线程，所以线程T1、T2也会被唤醒，但是保护条件C1并不成立，此时线程T1、T2被过早唤醒了。

- 信号丢失

  假设等待线程在调用lock.wait()方法之前并没有判断保护条件是否成立，就有可能出现一下情况。通知线程在等待线程进入临界区之前，修改了共享变量使得保护条件成立并对等待线程进行了通知，下一个时间点等待线程进入临界区并直接执行了lock.wait(),这就使得等待线程错过了一个通知，如果自此之后通知线程不在执行，那么等待线程就会一直等待下去。只要按照上诉模板那样使用lock.wait()/lock.notify()就不会造成信号丢失问题。

- 上下文切换开销问题

  锁的申请和释放可能导致上下文切换。等待线程对lock.wait()方法的调用会导致两次对lock对象内部锁的申请和释放（第一次发生在等待线程获取到lock对象的内部锁并检查到保护条件不成立，然后调用lock.wait(), 之后释放了lock对象的内部锁；第二次发生在等待线程被通知线程唤醒之后，要重新获取lock对象的内部锁，检查到保护条件成立，然后执行相应业务逻辑，最后退出临界区释放lock对象的内部锁）。通知线程在执行lock.notify()或者lock.notifyAll()方法之前需要获取lock对象上的内部锁，所以调用lock.notify()或者lock.notifyAll()方法都会有一次lock对象内部锁的申请和释放。

#### Condition条件变量

条件变量Condition的用法：

- 等待线程

```
 public class ConditionTest{
  	private final Lock lock = new ReentrantLock();
      private final Condition condition = lock.newCondition();
      
      public void waitMethod() throws InterruptedException {
          // 获取显示锁
          lock.lock();
          try{
              while(保护条件不成立) {
                  condition.await();
              }
              // 等待条件成立后执行具体的逻辑
          } finally {
              lock.unlock();
          }
      }
  }
```

- 通知线程

```
 public class ConditionTest{
  	private final Lock lock = new ReentrantLock();
      private final Condition condition = lock.newCondition();
      
      public void notifyMethod() throws InterruptedException {
          // 获取显示锁
          lock.lock();
          try{
          	// 修改共享变量
              updateState();
              // 让通知尽可能靠近释放显示锁
              condition.signal();
          } finally {
              lock.unlock();
          }
      }
  }
```

观察条件变量的使用模板，每次在显式锁实例上调用lock.newCondition()方法都会创建一个新的条件变量实例，每个条件变量内部都维护了一个用于存储等待线程的队列。使用条件变量与使用Object.wait()和Object.notify()/Object.notifyAll()一样都需要执行线程持有创建相应条件变量时使用的显示锁。

条件变量解决了一下两个问题：

1. 过早唤醒

   假设现有等待线程T1、T2、T3，其中T1、T2等待条件M1成立，T3等待条件M2成立。可以通过调用显式锁的lock.newCondition()方法来生成两个不同的条件变量的实例C1、C2。等待线程T1、T2在发现条件M1不成立时调用C1的wait()方法，让T1、T2在C1上等待；T3发现条件M2不成立时调用C2的wait()方法，让T3在C2上等待。

   而在通知线程里面，当改变共享变量使得等待条件M1成立时就调用C1的signal()方法来唤醒任意一个线程，当M2等待条件成立时就调用C2的signal()方法，这样就不会出现过早唤醒。

2. Object.wait(long)是由于超时返回还是被通知线程通知

   condition.awaitUntil(Date deadLine)方法有一个boolean返回时，当为true时代表是被通知线程唤醒的，当为false时代表是超时。

   其用法如下：

```
public void waitMethod() throws InterruptedException {
             boolean timeout = false;
             // 获取显示锁
             lock.lock();
             try{
                 while(保护条件不成立) {
                 	if(!timeout) {
                 		//在这里处理超时逻辑
                 	}
                     timeout = condition.awaitUntil(deadLine);
                 }
                 // 等待条件成立后执行具体的逻辑
             } finally {
                 lock.unlock();
             }
         }
```

#### CountDownLatch(倒计时协调器)

一个或者多个线程等待其他线程完成一组特定的操作之后才能执行，这一组特定的操作就叫做**先决操作**。CountDownLatch可以完成上诉逻辑。CountDownLatch完成上诉逻辑的原理是，等待先决操作完成的线程调用CountDownLatch实例的await()方法进行等待（线程阻塞并转为WAITTING状态），CountDownLatch的一个实例内部维护一个表示还**未完成**的先决操作的个数的计数器，当一个先决操作完成后调用CountDownLatch实例的countDown()方法时这个计数器就减1，直到这个计数器变为0时，就会唤醒所有的等待线程。

当CountDownLatch实例的内部计数器减小到0时，再调用countDown()方法就不会有任何效果（既不会使计数器减1也不会重新唤醒所有的等待线程），所以说CountDownLatch的使用是一次性的；调用await()方法也不会有任何效果（既不会阻塞当前线程也不会报异常）。CountDownLatch实例内部封装了对“全部操作已经完成”这个保护条件的等待、通知逻辑，所以在调用实例的await()、countDown()方法时不用加锁。

创建CountDownLatch的实例时先决操作的个数可以通过其构造函数指定：

```
public CountDownLatch(int count)
```

使用CountDownLatch时，如果由于程序错误使得其内部的计数器永远无法达到0，那么等待线程就会无限期的等待下去，可以通过一下两点来避免这种情况：

1. 将对countDown()方法的调用放在finally子句中，这样无论程序正确与否都不会出现内部计数器无法到0的情况。
2. 在等待线程中使用超时等待方法

```
public boolean await(long timeout, TimeUnit unit) throws InterruptedException
```

如果在指定时间内CountDownLatch实例的内部计数器没有变为0，所有的等待线程就会被唤醒，可以通过方法的返回值来区分是超时返回还是被唤醒后返回的。

#### CyclicBarrier(栅栏)

CyclicBarrier的一个构造器接受一个被成为barrierAction的任务（Runnable接口实例），barrierAction会被最后一个线程在执行CyclicBarrier.await()时执行，该任务执行结束后其他线程才会被唤醒。

#### 生产者、消费者模式

##### 阻塞队列

LinkedBlockingQueue 适合在消费者和生产者之间并发度比较大的情况下使用。

ArrayBlockingQueue 适合在消费者和生产者之间并发度较低的情况下使用。

SynchronousQueue 适合在生产者和消费者处理能力相差不大的情况下使用。

##### 信号量（Semaphore）

```
// limit用于指定信号量可用的配额数目，isFair用于指定是否使用公平的调度策略
public Semaphore(int limit, boolean isFair)
```

semaphore.acquire()/release()分别用于获取和释放配额。semaphore.acquire()在成功获取一个配额后会使总的配额减少1并直接返回，如果配额不足，semaphore.acquire()的调用线程会被阻塞，semaphore内部会维护一个等待队列用于存储这些被暂停的线程。semaphore.release()会使配额总数增加1并在等待队列中唤醒任意一个线程，但是信号量默认采用的是非公平调度策略，这个被唤醒的线程和新到的线程会同时竞争semaphore的配额。

##### 管道（Channel）

PipedOutputStream和PipedInputStream适合单生产者-单消费者模式。

##### 双缓冲和Exchanger

Exchanger是双缓冲区的实现

### 线程中断与停止

#### 线程中断

中断可以看成一个线程（发起线程，originator）给另一个线程（目标线程，target）的指示，该指示表示发起线程希望目标线程终止当前的工作。但是，在目标线程发现这个指示时，目标线程具体采用什么动作，由目标线程决定。

方法Thread.currentThread().isInterrupted()用来获取当前线程的中断标志值。方法thread.interrupted()用来获取线程的中断标志值并重置中断标志（将中断标志值设置为false）。方法thread.interrupt()用于将线程的中断标志值设置为true。

目标线程检查到中断标志值为true之后所采取的行动被称为中断响应。

***给目标线程发送中断指示会唤醒目标线程***。

#### 线程停止

设置一个共享变量来作为线程停止的标志。但是这样还不够，因为当发送线程设置线程停止标志为true时，目标线程可能因为执行了一些阻塞操作而被暂停，所以在这种情况下，设置的线程停止标志不会起作用。解决办法是在设置线程停止标志后再向目标线程发送中断指示，使目标线程得以判断线程停止标识。

### 线程安全设计技术

#### 无状态对象

对象所包含的数据就被称为该对象的状态，对象的状态包括实例变量和静态变量。无状态对象不包含任何实例变量和静态变量或者包含的静态变量是只读的。

#### 不可变对象

不可变对象：

- 类本身使用final修饰：这是为了防止通过创建子类来改变其默认行为。
- 所有字段都使用final修饰。
- 任何字段，如果引用了任何状态可变的对象（例如，数组、集合等），这些字段必须使用private修饰。如果需要有方法返回这些字段，必须进行防御性复制或者使用迭代器模式返回一个只读的迭代器。

### 线程控制

#### join线程

当某个程序执行流中调用了其他线程的join()方法时，调用线程将阻塞，直到被join的线程执行完毕。join方法通常由使用线程的程序调用，以将大问题切分成多个小问题，每个小问题分配给一个线程，当所有的小问题得到处理后，再调用主线程来进一步操作。join的方法由如下两种常用的重载方式：

- join()
- join(long millis) 等待被join的线程的时间最长为millis毫秒。如果millis毫秒内被join的线程还没结束，则不在等待。

```
public class JoinThread extends Thread{
	public JoinThread(String name) {
		super(name);
	}
	
	@Override
	public void run() {
		for (int i = 0; i < 100; i++) {
			System.out.println(getName() + "===" + i);
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		new JoinThread("新线程").start();
		
		for (int i = 0; i < 100; i++) {
			if (i==20) {
				JoinThread jThread = new JoinThread("被Join的线程");
				jThread.start();
				/**
				 * main线程中调用了jThread的join方法，
				 * main线程必须等jThread执行结束才会向下执行
				 */
				jThread.join();
				System.out.println(Thread.currentThread().getName() + "==" + i);
			}
		}
	}
}
```

#### 后台进程

后台进程（Daemon Thread）又称为“守护进程”、“精灵进程”。

如果所有的前台线程都死亡，后台线程也会死亡。主线程默认是前台线程，前台线程创建的子线程默认是前台线程，后台线程创建的子线程默认是后台线程。

```
public class DaemonThread extends Thread{
	@Override
	public void run() {
		for (int i = 0; i < 1000; i++) {
			System.out.println(getName() + "===" + i);
		}
	}
	
	public static void main(String[] args) {
		DaemonThread daemonThread = new DaemonThread();
		//设置成后台进程
		daemonThread.setDaemon(true);
		daemonThread.start();
		
		//测试进程是否时后台守护进程
		if (daemonThread.isDaemon()) {
			System.out.println("后台守护进程");
		}
		
		for (int i = 0; i < 10; i++) {
			System.out.println(Thread.currentThread().getName() + "===" + i);
		}
	}
}
```

#### 线程睡眠：sleep

调用Thread类的静态sleep()方法让当前线程进入阻塞状态，并暂停一段时间。当当前线程调用sleep()方法进入阻塞状态后，在其睡眠时间短内，该线程不会获得执行的机会，即使系统中没有其他可执行的线程。

#### 线程让步：yield

yield()方法也是Thread类的一个静态方法，它可以让当前正在执行的线程暂停，但是它不会阻塞该线程，只是将该线程转入就绪状态。yield只是让当前线程暂停一下，让系统的线程调度器重新调度一次，完全可能的情况是：当某个线程调用了yield()方法暂停之后，线程调度器又将其调度出来重新执行。

在只有一个CPU的机器上，如果某个线程调用了yield()方法暂停后，只有优先级与当前线程相同或者优先级比当前线程更高的处于就绪状态的线程才会获得执行的机会。

#### 改变线程优先级

每个线程默认的优先级都与创建它的父进程的优先级相同。例如，默认情况下，main线程具有普通优先级，由main线程创建的子线程也具有普通优先级。

Thread提供setPriority(int newPriority)、getPriority()方法来设置和返回指定线程的优先级。setPriority的的参数可以是1~之间的整数，也可以是Thread类的如下三个静态常量。MAX_PRIORITY(其值是10)、MIN_PRIORITY(其值是1)、NORMAL_PRIORITY(其值是5)。

### 线程同步

类Account的代码如下：

```
public class Account {
	private String accountNo;
	private double balance;
	public Account() {
	}
	public Account(String accountNo, double balance) {
		this.accountNo = accountNo;
		this.balance = balance;
	}
	
	public int hashCode()
	{
		return accountNo.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && obj.getClass() == Account.class) {
			Account target = (Account)obj;
			return target.getAccountNo().equals(accountNo);
		}
		return false;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
}
```

#### 同步代码块

```
public class DrawThread extends Thread{
	private Account account;
	private double drawAmount;
	public DrawThread(String name,
			Account account, double drawAmount) {
		super(name);
		this.account = account;
		this.drawAmount = drawAmount;
	}
	@Override
	public void run() {
		/**
		 * 使用account作为同步监视器，任何线程进入下面同步代码块之前
		 * 必须先获得对account账户的锁定--其他线程无法获取锁。
		 */
		synchronized (account) {
			if (account.getBalance() >= drawAmount) {
				System.out.println(getName() + "取钱成功！吐出彩票：" + drawAmount);
				try {
					Thread.sleep(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
				account.setBalance(account.getBalance() - drawAmount);
				System.out.println("余额为：" + account.getBalance());
			}else {
				System.out.println(getName() + " 取钱失败！余额不足");
			}
		}
	}
	public static void main(String[] args) {
		Account acct = new Account("123456",1000);
		new DrawThread("甲", acct, 800).start();
		new DrawThread("乙", acct, 800).start();
	}
}
```

#### 同步方法

同步方法就是使用synchronized关键字类修饰某个方法，则该方法称为同步方法。对于synchronizeed修饰的实例方法（非static方法）而言，无需显示指定同步监视器，同步方法的同步监视器是this，也就是调用该方法的对象。

线程安全的类具有如下特征：

- 该类的对象可以被多个线程安全的访问
- 每个线程调用该对象的任意方法之后将得到正确的结果
- 每个线程调用该对象的任意方法之后，该对象状态依然保存合理状态

不可变类不存在线程安全问题，可变类存在线程安全问题。可变对象的线程安全是以降低程序的运行效率为代价的。synchronized关键字可以修饰方法、代码块，但不能修饰构造器、成员变量等。

```
public class Account {
	private String accountNo;
	private double balance;
	public Account() {
	}
	public Account(String accountNo, double balance) {
		this.accountNo = accountNo;
		this.balance = balance;
	}
	
	public int hashCode()
	{
		return accountNo.hashCode();
	}
	
	public synchronized void draw(double drawAmount) {
		if (balance >= drawAmount) {
			System.out.println(Thread.currentThread().getName()
					+ " 取钱成功！：" + drawAmount);
			try {
				Thread.sleep(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			balance = balance - drawAmount;
			System.out.println("余额为：" + balance);
		}else {
			System.out.println(Thread.currentThread().getName() + 
					" 取钱失败！余额不足！");
		}
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && obj.getClass() == Account.class) {
			Account target = (Account)obj;
			return target.getAccountNo().equals(accountNo);
		}
		return false;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public double getBalance() {
		return balance;
	}
}
public class DrawThread extends Thread{
	private Account account;
	private double drawAmount;
	public DrawThread(String name,
			Account account, double drawAmount) {
		super(name);
		this.account = account;
		this.drawAmount = drawAmount;
	}
	@Override
	public void run() {
		account.draw(drawAmount);
	}
	public static void main(String[] args) {
		Account acct = new Account("123456",1000);
		new DrawThread("甲", acct, 800).start();
		new DrawThread("乙", acct, 800).start();
	}
}
```

#### 释放同步监视器的锁定

任何线程进入同步代码块、同步方法之前，必须先获得对同步监视器的锁定。程序无法显示释放对同步监视器的锁定，线程会在如下情况下释放对同步监视器的锁定。

- 当前线程的同步方法、同步代码块执行结束，当前线程即释放同步监视器。
- 当前线程在同步代码块、同步方法中遇到break、return终止了该代码块、该方法的继续执行，当前线程将会释放同步监视器。
- 当前线程在同步代码块、同步方法中遇到了未处理的Error或Exception，导致了该代码块、该方法异常结束时，当前线程将会释放同步监视器。
- 当前线程在同步代码块、同步方法中时，程序执行了同步监视器的wait()方法，则当前线程暂停，并释放同步监视器。

如下情况，线程不会释放同步监视器：

- 线程执行同步代码块或同步方法时，程序调用了Thread.sleep()、Thread.yield()方法来暂停当前线程的执行，当前线程不会释放同步监视器。
- 线程执行同步代码块时，其他线程调用了该线程的suspend()方法将该线程挂起，该线程不会释放同步监视器。

#### 同步锁

Lock是控制多个线程对共享资源进行访问的工具。Lock、ReadWriteLock是Java5提供的两个根接口，并为Lock提供了ReentranLock（可重入锁）实现类，为ReadWriteLock提供了ReentrantReadWriteLock实现；类。Java8新增了新型的StampedLock类，在大多数场景中它可以替代传统的ReentrantReadWriteLock。ReentrantReadWriteLock为读写操作提供了三种锁模式：Writing、ReadingOptimistic、Reading。在实现线程安全的控制中，最常用的是ReentrantLock（可重入锁）。

可以使用同步锁机制来实现对Account类的同步：

```
import java.util.concurrent.locks.ReentrantLock;

public class Account {
	
	//定义锁对象
	private final ReentrantLock lock = new ReentrantLock();
	
	private String accountNo;
	private double balance;
	public Account() {
	}
	public Account(String accountNo, double balance) {
		this.accountNo = accountNo;
		this.balance = balance;
	}
	
	public int hashCode()
	{
		return accountNo.hashCode();
	}
	
	public void draw(double drawAmount) {
		
		//加锁
		lock.lock();
		try {
			if (balance >= drawAmount) {
				System.out.println(Thread.currentThread().getName()
						+ " 取钱成功！：" + drawAmount);
				try {
					Thread.sleep(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
				balance = balance - drawAmount;
				System.out.println("余额为：" + balance);
			}else {
				System.out.println(Thread.currentThread().getName() + 
						" 取钱失败！余额不足！");
			}
		} finally {
			//释放锁
			lock.unlock();
		}
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && obj.getClass() == Account.class) {
			Account target = (Account)obj;
			return target.getAccountNo().equals(accountNo);
		}
		return false;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public double getBalance() {
		return balance;
	}
}
```

同步方法或同步代码块使用于竞争资源相关的、隐式的同步监视器，并且强制要求加锁和释放锁要出现在一个块结构中，而且当获取多个锁时，它们必须以相反的顺序释放，且必须在与所有锁被获取时相同的范围内释放所有锁。

### 线程通信

#### 传统的线程通信

使用Object类的wait()、notify()、notifyAll()方法。这三个方法必须由同步监视器对象来调用。这种方式只适用于使用代码块、方法进行同步，不适合使用Lock进行同步。

- wait()：导致当前线程等待，直到其他线程调用该同步监视器的notify()方法或notifyAll()方法来唤醒该线程。
- notify()：唤醒在此同步监视器上等待的单个线程。如果所有线程都在此同步监视器上等待，则会选择唤醒其中一个线程。选择是任意性的。只有当前线程放弃对该同步监视器的锁定后（使用wait()方法），才可以执行被唤醒的线程。
- notifyAll()：唤醒在此同步监视器上等待的所有线程。只有当前线程放弃对该同步监视器的锁定后，擦可以执行被唤醒的线程。

```
public class Account {
	//存款账号
	private String accountNo;
	//账户余额
	private double balance;
	
	//标识账号中是否已有存款
	private boolean flag = false;
	
	public Account() {
	}
	public Account(String accountNo, double balance) {
		this.accountNo = accountNo;
		this.balance = balance;
	}
	
	public synchronized void draw(double drawAmount)
	{
		try {
			//如果flag为假，表明账户中还没有人存钱进去
			if (!flag) {
				this.wait();
			}else {
				System.out.println(Thread.currentThread().getName()
						+ " 取钱：" + drawAmount);
				balance -= drawAmount;
				System.out.println("账户的余额是：" + balance);
				//标识已经取款完，存款进程可以进行存款啦
				flag = false;
				//通知存款进程可以进行存款
				this.notifyAll();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void deposit(double depositAmount) {
		try {
			if (flag) {//已经存完款啦，等待取款线程取款
				wait();
			}else {
				System.out.println(Thread.currentThread().getName()
						+ " 存款：" + depositAmount);
				balance += depositAmount;
				System.out.println("账户余额为：" + balance);
				//已经存款完成，取款者可以来取钱啦
				flag = true;
				//唤醒其他线程
				this.notifyAll();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public double getBalance() {
		return balance;
	}
	
}
public class DrawThread extends Thread{
	private Account account;
	private double drawAmount;
	public DrawThread(String name,
			Account account, double drawAmount) {
		super(name);
		this.account = account;
		this.drawAmount = drawAmount;
	}
	@Override
	public void run() {
		for (int i = 0; i < 5; i++) {
			account.draw(drawAmount);
		}
	}
}

public class DepositThread extends Thread{
	private Account account;
	private double depositAmount;
	public DepositThread(String name,
			Account account, double depositAmount) {
		super(name);
		this.account = account;
		this.depositAmount = depositAmount;
	}
	
	@Override
	public void run() {
		for (int i = 0; i < 5; i++) {
			account.deposit(depositAmount);
		}
	}
}

public class DrawTest {
	public static void main(String[] args) {
		Account account = new Account("1234567",0);
		new DrawThread("取款者", account, 10).start();
		new DepositThread("存款者1", account, 10).start();
	}
}
```

#### 使用Condition控制线程通信

如果程序不使用synchronized关键字来保证同步，而是直接使用Lock对象来保证同步，则系统中不存在隐式的同步监视器，也就不能使用wait()、notify()、notifyAll()方法进行线程通信了。这时，可以使用Condition类来保持协调，使用Condition可以让那些已经得到Lock对象却无法继续执行的线程释放Lock对象，Condition对象也可以唤醒其他处于等待的线程。

Condition实例被绑定在一个Lock对象上，要获得特定Lock实例的Condition实例，调用Lock对象的newCondition()方法即可。Condition类提供了如下三个方法：

- await()：类似于隐式同步监视器上的wait()方法，导致当前线程等待，直到其他线程调用该Condition的signal()方法或者signalAll()方法来唤醒该线程。
- signal()：唤醒在此Lock对象上等待的单个线程。如果所有线程都在该Lock对象上等待，则会选择唤醒其中一个线程。选择是任意性的。只用当前线程放弃对该Lock对象的锁定后（使用await()方法），才可以执行被唤醒的进程。
- signalAll()：唤醒在此Lock对象上等待的所有线程。只用当前线程放弃对该Lock对象的锁定后（使用await()方法），才可以执行被唤醒的进程。

```
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
	
	//显示定义Lock对象
	private final Lock lock = new ReentrantLock();
	//获取指定Lock对象的Condition
	private final Condition con = lock.newCondition();
	
	
	
	//存款账号
	private String accountNo;
	//账户余额
	private double balance;
	
	//标识账号中是否已有存款
	private boolean flag = false;
	
	public Account() {
	}
	public Account(String accountNo, double balance) {
		this.accountNo = accountNo;
		this.balance = balance;
	}
	
	public void draw(double drawAmount)
	{
		//上锁
		lock.lock();
		try {
			//如果flag为假，表明账户中还没有人存钱进去
			if (!flag) {
				con.await();
			}else {
				System.out.println(Thread.currentThread().getName()
						+ " 取钱：" + drawAmount);
				balance -= drawAmount;
				System.out.println("账户的余额是：" + balance);
				//标识已经取款完，存款进程可以进行存款啦
				flag = false;
				//通知存款进程可以进行存款
				con.signalAll();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			//释放锁
			lock.unlock();
		}
	}
	
	public void deposit(double depositAmount) {
		
		lock.lock();
		
		try {
			if (flag) {//已经存完款啦，等待取款线程取款
				con.await();
			}else {
				System.out.println(Thread.currentThread().getName()
						+ " 存款：" + depositAmount);
				balance += depositAmount;
				System.out.println("账户余额为：" + balance);
				//已经存款完成，取款者可以来取钱啦
				flag = true;
				//唤醒其他线程
				con.signalAll();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public double getBalance() {
		return balance;
	}
	
}
```

#### 使用阻塞队列（BlockingQueue）控制线程通信

虽然BlockingQueue是Queue的子接口，但是它的主要用途并不是作为容器，而是作为线程同步的工具。BlockngQueue具有一个特征：当生产者线程试图向BlockingQueue中放入元素时，如果该队列已满，则该线程被阻塞；当消费者线程试图从BlockingQueue中取出元素时，如果队列已空，则该线程被阻塞。

BlockingQueue提供如下两个支持阻塞的方法：

- put(E e)：尝试把E元素放入BlockingQueue中，如果该队列的元素已满，则阻塞该线程。
- take()：尝试从BlockingQueue的头部取出元素，如果该队列已空，则阻塞该线程。

BlockingQueue继承可Queue接口，可以使用Queue接口的方法。这些方法归纳如下:

- 在队列尾部插入元素：包括add(E e)、offer(E e)和put(E e)方法，当队列已满时，这三个方法分别抛出异常、返回false、阻塞队列。
- 在队列头部删除并返回删除的元素：包括remove()、poll()和take()方法。当该队列已空时，这三个方法分别会抛出异常、返回false、阻塞队列。
- 在队列头部取出但不删除元素。包括element()和peek()方法，当队列已空时，这两个方法分别抛出异常、返回false。

BlockingQueue有如下5个实现类：

- ArrayBlockingQueue：基于数组实现的BlockingQueue队列。
- LinkedBlockingQueue：基于链表实现的BlockingQuue队列。
- PriorityBlockingQueue：这不是标准的阻塞队列。该队列调用remove()、poll()、take()等方法取出元素时，并不是取出队列中存在时间最长的元素，而是队列中最小的元素。PriorityBlockingQueue判断元素的大小既可以根据元素的本身大小来自然排序，也可以使用Comparator进行定制排序。
- SychronousQueue：同步队列。对该队列的存、取操作必须交替进行。
- DelayQueue：

```
import java.util.concurrent.BlockingQueue;

public class Producer extends Thread{
	private BlockingQueue<String> bg;
	public Producer(BlockingQueue<String> bq) {
		this.bg = bq;
	}
	@Override
	public void run() {
		String[] strArr = new String[]
				{
						"Java",
						"Struts",
						"Spring"
				};
		for (int i = 0; i < 4; i++) {
			System.out.println(getName() + " 生产者准备生产数据");
			try {
				Thread.sleep(200);
				bg.put(strArr[i % 3]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(getName() + "生产完成：" + bg);
		}
	}
}
import java.util.concurrent.BlockingQueue;

public class Consumer extends Thread{
	private BlockingQueue<String> bq;
	public Consumer(BlockingQueue<String> bq) {
		this.bq = bq;
	}
	@Override
	public void run() {
		while (true) {
			System.out.println(getName() + "消费者准备消费集合元素！");
			try {
				Thread.sleep(200);
				bq.take();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(getName() + "消费完成：" + bq);
		}
	}
}
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueTest {
	public static void main(String[] args) {
		BlockingQueue<String> bq = new ArrayBlockingQueue<>(1);
		//启动3个生产者线程
		new Producer(bq).start();
		new Producer(bq).start();
		//启动一个消费者线程
		new Consumer(bq).start();
	}
}
```

### 线程组和未处理的异常

Java使用ThreadGroup来表示线程组。如果没有显示指定线程组，用户创建的线程属于默认线程组。默认情况下，子线程与父线程处于同一个线程组。一旦某个线程加入了指定线程组之后，该线程将一直属于该线程组，直到该线程死亡。线程运行中途不能改变它所属的线程组。

### 线程池

使用线程池来执行线程任务的步骤：

1. 调用Executors类的静态工厂方法创建一个ExecutorService对象，该对象代表一个线程池。
2. 创建Runable实现类或Callable实现类的实例，作为线程执行任务。
3. 调用ExecutorService对象的submit()方法来提交Runable实例或Callable实例。
4. 当不想提交任何任务时，调用ExecutorService对象的shutdown()方法来关闭线程池。、

```
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolTest {
	public static void main(String[] args) {
		//1、创建线程池
		ExecutorService pool = Executors.newFixedThreadPool(6);
		//2、创建Runable对象
		Runnable target = () -> {
			for (int i = 0; i < 10; i++) {
				System.out.println(Thread.currentThread().getName() 
						+ " 的i值为：" + i);
			}
		};
		//3、将执行任务提交到进程池
		pool.submit(target);
		pool.submit(target);
		//4、关闭线程池
		pool.shutdown();
		
	}
}
```

ForkJoinPool支持将一个任务拆分成多个”小任务“并行计算，再把多个”小任务“的结果合并成总的计算结果。

```
import java.util.concurrent.RecursiveAction;


/**
 * 继承RecursiveAction来实现“可分解”的任务
 * @author qlxazm
 *
 */
public class PrintTask extends RecursiveAction{
	private static final int THRESHOLD = 50;
	private int start;
	private int end;
	
	public PrintTask(int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	@Override
	protected void compute() {
		if (end - start < THRESHOLD) {
			for (int i = start; i < end; i++) {
				System.out.println(
						Thread.currentThread().getName()
						+ "的i值：" + i);
			}
		}else {
			//切分任务
			int middle = (start + end) / 2;
			PrintTask left = new PrintTask(start, middle);
			PrintTask right = new PrintTask(middle, end);
			//并行执行两个“小任务”
			left.fork();
			right.fork();
		}
		
	}
	
}

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class ForkJoinPoolTest {
	public static void main(String[] args) throws InterruptedException {
		ForkJoinPool pool = new ForkJoinPool();
		//提交可分解的PrintTask任务
		pool.submit(new PrintTask(0, 300));
		pool.awaitTermination(2, TimeUnit.SECONDS);
		//关闭线程池
		pool.shutdown();
	}
}
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RecursiveTask;


class CalTask extends RecursiveTask<Integer> {
	//每个“小任务”最多只累加20个数
	private static final int THRESHOLD = 20;
	private int arr[];
	private int start;
	private int end;
	
	public CalTask(int[] arr, int start, int end) {
		this.arr = arr;
		this.start = start;
		this.end = end;
	}
	@Override
	protected Integer compute() {
		int sum = 0;
		if (end - start < THRESHOLD) {
			for (int i = start; i < end; i++) {
				sum += arr[i];
			}
			return sum;
		}else {
			int middle = (start + end) / 2;
			CalTask left = new CalTask(arr, start, middle);
			CalTask right = new CalTask(arr, middle, end);
			//并行指向两个小任务
			left.fork();
			right.fork();
			//把两个“小任务”累加的结果结合起来
			return left.join() + right.join();
		}
	}
	
}
public class Sum {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		int[] arr = new int[100];
		Random random = new Random();
		int total = 0;
		for (int i = 0; i < arr.length; i++) {
			int tmp = random.nextInt(20);
			total += tmp;
			arr[i] = tmp;
		}
		System.out.println("100个数的总和是：" + total);
		//创建线程池
		ForkJoinPool pool = ForkJoinPool.commonPool();
		//提交可分解得CalTask任务
		Future<Integer> future = pool.submit(new CalTask(arr, 0, arr.length));
		System.out.println("多线程执行结果是：" + future.get());
		//关闭线程池
		pool.shutdown();
	}
}
```

#### 线程池的好处？

1. 避免重复创建和销毁线程的开销。
2. 提高响应速度。如果有新任务到达，可以直接从线程池里面获取一个空闲线程来执行任务，省去了创建新线程的开销。

#### ThreadPoolExecutor

##### 参数说明

```
public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize,long keepAliveTime,TimeUnit unit,
   BlockingQueue<Runnable> workQueue,
   ThreadFactory threadFactory,
   RejectedExecutionHandler handler)
```

- corePoolSize: 核心线程的数量

- maximumPoolSize: 最大线程数

- keepAliveTime: 非核心线程的存活时间

- unit: 时间单位

- workQueue: 存放任务的阻塞队列

  1. ArrayBlockingQueue

     基于数组的一个有界阻塞队列，FIFO

  2. LinkedBlockingQueue

     基于链表的阻塞队列，FIFO。可以设置最大容量，如果不指定就是无界队列，队列容量将达到Integer.MAX_VALUE

  3. DelayQueue

  4. PriorityBlockingQueue

     具有优先级的无界阻塞队列

  5. SynchronousQueue

     相当于一个容量为1的阻塞队列

- threadFactory: 生成新线程的工厂方法

- handler: 线程池饱和时的处理策略

  1. AbortPolicy：直接抛出异常，这也是默认值
  2. DiscardPolicy：直接丢弃新任务
  3. DiscardOldestPolicy：丢弃阻塞队列中队头的任务，将新任务加入阻塞队列
  4. CallerRunsPolicy：利用提交任务的线程进行处理

##### 提交过程

1. 核心线程数量是否达到了corePoolSize，如果未达到就创建新的核心线程执行任务；否则，转向2
2. 阻塞队列是否已经满了？如果未满，就将任务加入阻塞队列；否则，转向3
3. 当前线程数量是否达到了了maximumPoolSize，如果未达到，就创建一个新的线程来执行任务；否则，转向4
4. 执行饱和时的拒绝策略

##### 异常处理

1. 在任务代码中使用try/catch进行捕获

```
ExecutorService threadPool = Executors.newFixedThreadPool(5);
   
           for (int i = 0; i < 5; i++) {
               threadPool.submit(() -> {
                   System.out.println(Thread.currentThread().getName());
                   try{
                       Object o = null;
                       System.out.println("result=#" + o.toString());
                   }catch (Exception e) {
                       System.out.println(Thread.currentThread().getName() + "出错啦！");
                   }
               });
           }
```

1. 通过Future对象的get方法进行捕获

```
ExecutorService threadPool = new ThreadPoolExecutor(5, 10,
                  10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20),
                  new ThreadFactory() {
                      @Override
                      public Thread newThread(Runnable r) {
                          Thread t = new Thread(r);
                          return t;
                      }
                  });
   
           for (int i = 0; i < 5; i++) {
               Future<Integer> future = threadPool.submit(() -> {
                   System.out.println(Thread.currentThread().getName());
                   Object o = null;
                   System.out.println(o.toString());
                   return 0;
               });
   
               try {
                   future.get();
               } catch (Exception e) {
                   System.out.println("捕获到了异常！");
               }
           }
           threadPool.shutdown();
```

1. 在线程构造工厂内，调用线程的setUncaughtExceptionHandler方法

```
ExecutorService threadPool = new ThreadPoolExecutor(5, 10,
                  10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20),
                  new ThreadFactory() {
                      @Override
                      public Thread newThread(Runnable r) {
                          Thread t = new Thread(r);
                          t.setUncaughtExceptionHandler((t1, e) -> {
                              System.out.println(t1.getName() + e);
                          });
                          return t;
                      }
                  });
   
           for (int i = 0; i < 5; i++) {
               threadPool.execute(() -> {
                   System.out.println(Thread.currentThread().getName());
                   Object o = null;
                   System.out.println(o.toString());
               });
           }
           threadPool.shutdown();
```

1. 重写ThreadPoolExecutor的afterExecute方法

##### 常见的几种常量池

- newFixedThreadPool

```
 public static ExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
          return new ThreadPoolExecutor(nThreads, nThreads,
                                        0L, TimeUnit.MILLISECONDS,
                                        new LinkedBlockingQueue<Runnable>(),
                                        threadFactory);
      }
```

1. 核心线程数和最大线程数相同
2. 使用LinkedBlockingQueue作为一个无界阻塞队列
   适用于CPU密集型的任务。

- newCachedThreadPool

```
public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
          return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                        60L, TimeUnit.SECONDS,
                                        new SynchronousQueue<Runnable>(),
                                        threadFactory);
      }
```

1. 核心线程数为0
2. 最大线程数为Integer.MAX_VALUE
3. 非核心线程存活时间是60S
   适用并发执行大量短期小任务

- newSingleThreadExecutor

```
 public static ExecutorService newSingleThreadExecutor(ThreadFactory threadFactory) {
          return new FinalizableDelegatedExecutorService
              (new ThreadPoolExecutor(1, 1,
                                      0L, TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>(),
                                      threadFactory));
      }
```

用于串行执行任务

- newScheduledThreadPool

```
 public ScheduledThreadPoolExecutor(int corePoolSize) {
          super(corePoolSize, Integer.MAX_VALUE, 0, NANOSECONDS,
                new DelayedWorkQueue());
      }
```

需要周期性的执行任务
参考链接：[线程池](https://www.zhihu.com/search?type=content&q=java线程池)

##### 线程池状态

- RUNNING
  1. 这个状态的线程池会接收新任务，并处理阻塞队列中的任务
  2. 调用shutdown()方法，可以切换到SHUTDOWN状态
  3. 调用shutdownNow()方法，可以切换到STOP状态
- SHUTDOWN
  1. 这个状态的线程池不会接收新任务，会处理阻塞队列中的任务
  2. 阻塞队列中的任务被执行完毕后，就会进入TIDYING状态
- STOP
  1. 线程池不会接收新任务，也不会处理阻塞队列中的任务
  2. 线程池中的任务为空，就进入TIDYING状态
- TIDYING
  1. 该状态表示所有的任务已经执行完毕
  2. 调用terminated()方法后，线程进入TERMINATED状态
- TERMINATED
  该状态表示线程池彻底终止

### 进程间进行同步的方式

1. 共享内存
2. 消息队列
3. 管道
4. 信号
5. 信号量
6. Socket

### 线程间进行同步的方式

1. 锁
2. 信号
3. 信号量

### Synchronized在1.6的改进

https://www.cnblogs.com/paddix/p/5405678.html

### Synchronized与ReentrantLock的区别

https://mp.weixin.qq.com/s/7JnYqTCqtM7kePqrTB8ZSQ

### Java线程模型

#### Java线程的实现方式

在Sun JDK中，windows、Linux版都是一个Java线程映射到一个操作系统线程上。而操作系统又有三种线程模型：

1. 1对1
   一个内核线程映射到一个用户线程上
2. 1对多
   一个内核线程映射到多个用户线程上
3. 多对多
   n个内核线程映射到m个用户线程上

#### Java线程调度方式

- 协同式调度

  协同式调度中，当线程执行完自己的工作后，通知操作系统进行线程切换，线程的执行时间由它自己来控制。

  1. 好处： 实现简单，并且由线程进行线程切换，不存在线程同步问题。
  2. 坏处：一个线程出现问题而迟迟不能通知操作系统进行线程切换，可能导致整个系统崩溃。

- 抢占式调度

  抢占式调度中，由操作系统给线程分配执行时间，并且也是由操作系统决定什么时候切换线程。

  1. 好处：线程的执行时间是操作系统可控的，并且也不会出现一个线程出现问题，导致整个系统不可用。
  2. 坏处：需要进行线程之间的同步。

java使用的是抢占式调度方式，可通通过修改java线程的优先级来改变分配给线程的执行时间。java线程有10个优先级，优先级越大优先级越高，分配的处理器执行时间越多。由于Java线程是映射到操作系统的原生线程上来实现的，所以说java线程的优先级可能不准确。比如：windows中线程优先级只有7个，将java线程的10个优先级映射到这7个上面，可能就会有多个Java线程优先级映射的操作系统线程优先级相同的情况。

#### Java线程的几种状态

- 新建(New): 创建后还没有启动的线程处于这个状态

- 运行(Runable): 这个状态包括了操作系统中的两个线程状态,包括Running、Read，处于这个状态的线程可能正在执行，也可能正在等待CPU资源

- 无限等待(waiting)：处于这个状态的线程需要其他线程显式的唤醒才能重新执行，否则，一直等待。导致无限等待的情况包括：

  1. 调用Object.wait()而没有使用指定Timeout参数
  2. 调用Thread.join()而没有指定Timeout参数
  3. LockSupport.park()

- 有限等待

  处于这种状态的线程可以由其他线程显式唤醒，如果其他线程迟迟没有唤醒，过了一段时间之后也会被系统自动唤醒。

  1. Thread.sleep()
  2. 提供了Timeout参数的Object.wait()
  3. 提供了Timeout参数的Thread.join()
  4. LockSupport.parkNanos()
  5. LockSupport.parkUntil()

- 阻塞
  线程等待获取一个排他锁时，线程会进入这个状态。阻塞态和等待态的区别是，**阻塞态的线程正在等待获得一个排他锁**，在其他线程放弃这个排他锁时线程会结束阻塞态。**等待状态是线程正在等待一个事件的出现**，这个事件可以是某个线程唤醒当前线程、或者Timeout事件到达。

- 结束
  已经终止的线程，处于这个状态。

### Java内存模型

Java内存模型定义了程序中各个变量的访问规则，也就是说在JVM中将变量存储到内存和从内存中取出变量这样的底层细节。这里的变量指的是多个线程之间的共享变量，而不是线程私有的局部变量和方法参数等。

Java内存模型规定了所有的变量都存储在主内存中，每条线程还有自己的工作内存，线程的工作内存中保存了该线程中用到的变量的主内存副本拷贝，线程对变量的所有操作都必须在工作内存中进行，而不能直接读写主内存。不同的线程之间也无法直接访问对方工作内存中的变量，线程间变量的传递均需要自己的工作内存和主存之间进行数据同步。

### Java指令重排序的例子

```
// 线程共享的变量，用于设置配置文件
Map configOptions = null;
// 表示是否设置完成了configOptions
boolean initialized = false;

// 线程A
configOptions = new HashMap();
// 在这里读取配置文件并设置进configOptions
// 设置标志位，表示已经读取完了配置文件
initialized = true;

// 线程B
while(!initialized) {
    sleep();
}
// 配置设置成功后再执行一些其他操作
doSomeThing();
```

### Runable和Callable区别

1. Runable的Run方法不可返回值，不可以抛出异常
2. Callable的call()方法可以返回值，结合Future和FutureTask使用，可以抛出异常

### 直接调用线程的run方法和start方法

调用run方法在当前线程中执行代码, 调用start方法, 新开启一个线程,在新线程中执行代码.

### 开闭原则

开闭原则明确的告诉我们：软件实现应该**对扩展开放**，**对修改关闭**，其含义是说**一个软件实体应该通过扩展来实现变化，而不是通过修改已有的代码来实现变化**的。

### 里式原则

里氏代换原则中说，任何基类可以出现的地方，子类一定可以出现。

### 门闩

就是CountDownLatch

## Java多线程

### 什么是线程安全？

线程安全是指不论多个线程以什么顺序访问共享可变变量，都能保证最后结果的正确性。

### 线程安全的实现方式？、

1. 互斥同步，Java中的互斥同步手段是synchronized关键字、JUC包下面的ReentrantLock。
2. 非阻塞同步，CAS+自旋实现的乐观锁。、

**synchronized与ReentrantLock的区别？**
synchronized是在**jvm层**实现的，其**底层原理是monitor对象+Java对象头**来实现。synchronized只支持**非公平锁**，**可重入**、**不可被中断**。**synchronized关键字可以保证原子性、有序性、可见性。**

ReentrantLock是在**jdk层**面实现的，其**底层是AQS**。ReentrantLock支持**公平锁**、**非公平锁**，**默认是非公平锁**，**可重入**、**可被中断**、可以**通过newCondition()方法绑定多个Condition条件变量**。

**synchronized实现原理？**
当synchronized使用在同步代码块中时，在进入临界区之前会插入一条**monitorenter**字节码指令，在退出临界区之后会插入一条**monitorexit**字节码指令。**monitorenter字节码指令会尝试去获取锁对象的monitor对象**，**monitorexit字节码指令会释放获取到的monitor对象**。
当synchronized使用在同步方法中时，当方法被调用时，**调用指令**会检查方法的**方法表**的**访问控制字段**中**是否设置了ACC_SYNCHRONIZED标志**，如果设置了，那说明这是一个同步方法，**执行线程就要去获取monitor**，获取成功后才能执行方法体，方法执行完成后释放monitor。如果一个同步方法执行期间抛出了异常，并且在方法内部无法处理这个异常，那么这个同步方法持有的monitor对象将在异常抛出到同步方法之外时自动释放。

**Monitor对象如何实现的？**
在Java虚拟机(HotSpot)中，monitor是由**ObjectMonitor**实现的，ObjectMonitor中有几个关键属性：

1. **_count**用来记录重入次数
2. **_waitSet**是一个队列，用来记录处于wait状态的线程
3. **_EntryList**是一个队列，用来记录处于block状态的线程
4. **_owner**用来记录是那个线程持有了monitor对象

当多个线程同时访问一段同步代码时，会先进入**_EntryList**队列中，当某个线程获取到了锁对象的monitor对象时，这个线程就会被设置到_owner，并且_count设置为1，以后这个线程每次重入时都会把_count加1。如果获取到了monitor对象的线程调用锁对象的**wait方法**，就会将_owner置为空并将_count设置为0，线程进入_waitSet队列。

**Java 6对synchronized关键字的优化？**
synchronized在java6之前只支持重量级锁，**重量级锁在线程获取不到锁时需要将线程阻塞**、**在锁被释放时还要唤醒线程**，**由于java的线程是映射到操作系统本地线程上来实现的**，如果要**阻塞和唤醒线程都需要操作系统来处理**，这就要**在用户态和和心态之间来回切换**，这在没有锁竞争时非常耗时也显得没有必要。

**什么是锁竞争？**如果线程每次获取锁都比较顺利，没有其他线程在获取锁，这不叫锁竞争，只有在获取锁的同时其他线程也在获取锁，这才叫做锁的争用。为了提高效率，避免不必要的加锁操作，在java6对synchronized关键字进行了优化，引入了偏向锁、轻量级锁。

根据锁的争用程度，锁会从偏向锁升级为轻量级锁再升级为重量级锁。synchronized关键字的这些锁都依赖于java的对象头，java对象头对于非数组对象存储两方面的内容，第一个部分是mark word，其中存储了对象的hashcode、gc分带年龄、锁标志等信息。第二部分是对象的类元数据指针，用于表示对象是哪个类的实例。

**偏向锁？**
为什么有偏向锁？
在大多数情况下，锁都不存在竞争，而是经常由同一个线程持有，这样锁的释放和获取过程就会浪费时间。引入偏向锁之后，在第一次获取偏向锁时会获取锁，以后再由这个线程执行时，就直接跳过获取锁的步骤，直接执行临界区代码。
当一个线程初次执行synchronized关键字引导的代码块时，会首先根据所对象对象头的mark word判断是否已经有其他的线程获得了锁，**如果没有，就将mark word置为偏向锁状态**，**并使用cas操作将当前线程的id设置到makword中**，这样这个锁就偏向了当前的这个线程，并且在当前线程退出同步代码块的时候也不会释放锁，这样下一次这个线程再次尝试进入同步代码块的时候只需要根据锁对象的对象头中的markword判断一下是自己持有这个偏向锁，就直接进入同步代码块，省略了加锁的步骤。**如果在A线程已经获取到锁对象的偏向锁的情况下，线程B这个时候想获取锁**，这个时候B就需要判断线程A是否存活，如果不存活，就直接由B来获得偏向锁；否则，锁就要升级为轻量级锁，并且这个轻量级锁由A持有，**B进行自旋等待获取轻量级锁**。

**轻量级锁？**
为什么有轻量级锁？
**多个线程之间可能不存在锁的争用，而是一个线程一个线程的交替获得锁并执行。**轻量级线程可以在没有线程争用的情况下，避免重量级锁需要借助操作系统来实现同步的开销。

如何获取轻量级锁？
首先，线程在栈帧中创建一个**lock** **record**，并且将锁对象的对象头中的markword复制到栈帧中的这个lock record中，再使用cas操作将markword指向lock record ，如果成功，线程就获取到了锁，并且将markword中锁的状态置为轻量级锁状态。如果cas操作失败，就再判断锁对象的markword是否指向当前线程栈帧中的lock record，如果是就直接进入同步块，否则，就自旋等待锁。**用自旋操作的忙等待来换取用户态和和心态切换的开销**。如果有**两个以上线程**争用同一个轻量级锁，锁就升级为重量级锁。

自旋操作也不是无限制的，当某个线程的自选次数达到一定的次数时，这个线程使用cas修改锁对象头上的锁标志位就把锁对象升级为重量级锁，后续争用锁的线程发现锁对象处于重量级锁状态，**就会阻塞等待**。

轻量级锁解锁时，会使用原子的CAS操作将Mark Word替换回到对象头，如果成功，则表示没有竞争发生。如果失败，表示当前锁存在竞争，锁就会膨胀成重量级锁(多个线程在相同时刻竞争同一把锁)。

[参考](https://blog.csdn.net/zhao_miao/article/details/84500771)

### 什么是锁的升级？

偏向锁 -> 轻量级锁 ->重量级锁

### 什么是锁的消除？

假如有下面这样一组代码：

```
 public String concatString(String s1, String s2, String s3) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(s1);
        stringBuffer.append(s2);
        stringBuffer.append(s3);
        return stringBuffer.toString();
    }
```

由于这里的stringBuffer变量是局部变量，所以肯定不会存在多线程同步问题，所以在编译阶段，会将这些不必要的加锁同步给清除掉。

### 什么是锁的粗化？

一系列的操作对对象频繁加锁、解锁，更夸张的是加锁、解锁直接出现在循环中，这种频繁的加解锁效率比较低。

```
 public String concatString(String s1, String s2, String s3) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(s1);
        stringBuffer.append(s2);
        stringBuffer.append(s3);
        return stringBuffer.toString();
    }
```

JVM如果观察到连续的操作都对一个对象进行频繁的加解锁，就会进行锁的粗化，减少加锁解锁的次数。比如上面代码中，会把加锁操作扩展到第一句，解锁操作扩展到最后一句。

### CAS是什么？

**cas是一个操作系统的原语，具有原子性**。cas需要提供三个参数，V表示变量对内存地址，A表示变量的旧的值，也就是在当前线程看来变量如果没有被修改的情况下应该有的值，B代表新值。cas执行时，如果变量的值还是旧值A，就将新值B设置为变量的值，否则，失败。
cas在java中的实现是unsafe类，unsafe类的getSafe()方法只允许BootstrapClassLoader加载器加载的类访问，所以如果想使用它，就要利用反射。
cas可能遇到ABA问题，可以通过增加版本号来避免。

## IO模型

1. 阻塞式IO
2. 非阻塞式IO
3. IO多路复用
4. 信号驱动IO
5. 异步IO

[参考](https://blog.csdn.net/ZWE7616175/article/details/80591587)

### IO多路复用为什么比多线程快？

当计算成本相对于IO成本来说可以忽略不计时，IO多路复用是比多线程更加高效的。因为，在这种情况下，一个线程可以负责多个IO流，并且可以很快处理完IO流产生的IO事件，没有线程的开销。如果使用多线程，一个线程在短暂处理完IO事件后，又会因为等待IO事件而阻塞，阻塞就会产生上下文切换，这样很多处理机资源都浪费在了线程切换上。IO多路复用十分适合IO高并发但是处理事件比较短暂的应用。
当计算成本大于IO成本时，IO多路复用就不一定比多线程高效。

### select、poll、epoll？

IO多路复用利用了select、poll、epoll可以同时监听多个流的IO事件的能力，当没有IO事件时**当前线程阻塞**，当有IO事件时，当前线程被唤醒，通过轮询各个流，来依次处理IO事件。

区别：

1. select进行的是无差别轮询，select只知道有IO事件产生，但是具体是哪个流产生的并不知道，所以要遍历所有的流。能够同时监听的流的数量有限。select只能监听1024个连接。
2. poll对监听的流的数量没有了限制，其他的与select相同。
3. epoll可以知道产生IO事件到底是哪个流，它监听的流的数量也是没有限制的。

[参考](https://blog.csdn.net/mashaokang1314/article/details/88636371)

[参考]

### 同步与异步

同步与异步指的是**消息通信机制**，侧重的是被调用者。
同步：调用一个函数，这个函数在没有计算出最终结果之前不会返回结果。只有完成了整个函数的执行之后，才会返回最终结果。
异步：调用一个函数，这个函数会立即返回，但是没有返回最终结果，当函数执行完成之后，才会将结果通知给调用者，比如使用回调函数。

### 阻塞与非阻塞

阻塞和非阻塞指的是**调用者所在线程的状态**，侧重的是调用者。
阻塞：调用一个函数，在返回结果之前，调用线程**会被挂起**直到最终结果返回才被唤醒。
非阻塞：调用一个函数，在没有返回结果之前并不会阻塞调用线程，调用线程可以去做其他事情，但是要时常过来检查调用结果是否返回。
你打电话问书店老板有没有《分布式系统》这本书，你如果是阻塞式调用，你会一直把自己“挂起”，直到得到这本书有没有的结果，如果是非阻塞式调用，你不管老板有没有告诉你，你自己先一边去玩了， 当然你也要偶尔过几分钟check一下老板有没有返回结果。

## 协程

协程是**编译器级别**的，协程是**一个运行在用户态**的轻量级线程。协程之间的调度是**非抢占式调度**，协程的调度由协程自己来完成。**一个线程对应多个协程**，当一个协程发现自己无法执行下去的时候，会通知**协程调度器**，由调度器来选择接下来让那个协程占有CPU。
而对于进程、线程，进程、线程是操作系统级别的，它们什么时候进行上下文切换开发者无法干预，完全由操作系统决定，而不是由进程、线程决定。进程、线程的切换还要涉及用户态和内核态之间的切换。

### 优点

1. 协程的切换不涉及用户态和内核态的切换，协程什么时候进行切换由开发者决定，因此切换的代价小、切换的次数也少，所以协程比进程、线程更加高效。

### 缺点

1. 协程是属于单个线程的，而单个线程只能在一个CPU内核上运行，**无法利用多核**。一旦一个线程内的协程出现阻塞，将会**阻塞整个线程**。

### 适用场景

1. 协程属于单个线程，因此协程适合IO密集型的程序，不适合CPU密集型的程序。

## 为什么分内核态和用户态？

假如没有内核态和用户态之分，程序随随便便就可以访问系统的任何资源，比如说内存分配，如果程序员不小心将不合适的内容写到不合适的位置，就可能导致整个系统崩溃。所以，操作系统将各种指令分为特权指令和非特权指令，特权指令运行在内核态，非特权指令运行在用户态。非特权指令想要使用操作系统资源，必须调用特权指令，特权指令是操作系统内置的，这样既可以保证安全性。