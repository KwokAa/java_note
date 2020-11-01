#### AOP

AOP是面向切面编程，用于处理系统中**分布在各个模块的横切关注点**，比如：事务、日志、缓存等。

AOP分为**编译时织入**，这需要特殊的编译器来完成。**类加载时织入**，需要特殊的类加载器。**运行时织入**，在运行时**为目标对象动态生成一个代理对象**，SpringAop就是采用这种方式。

Spring AOP有两种使用方式，一种是**使用AspectJ注解**，一种是在**XML中进行配置**。

Spring Aop有5种通知类型：**@Before** 在连接点之前执行 **@After** 无论连接点是正常执行完毕还是抛出异常都会在执行结束时执行这个通知 **@Around** 环绕通知是最强大的通知类型，它可以让编写的逻辑把通知方法包围起来。**@AfterReturning**在连接点正常结束时执行通知 **@AfterThrowing**在连接点抛出异常时执行通知。

Spring Aop中还有两个注解：**@Aspect**、**@Pointcut**(用于定义切点)。Spring Aop只支持**方法级别的连接点**。

原理：SpringAop基于动态代理，当目标对象实现了接口时就是用jdk自带的动态代理，如果目标对象没有实现任何接口，就是用CGLIB生成目标对象的一个子类进行代理。

**为什么对实现了接口的类使用jdk动态代理？**
jdk动态代理生成的代理对象继承了Proxy类，因为java是单继承的，所以不能再继承另外一个父类，但是对于接口，java可以继承多个接口，所以jdk的动态代理只能实现对接口的代理。如果目标类没实现接口，那只能使用CGLIB，通过字节码技术为一个类创建子类，并在子类中采用方法拦截的技术拦截所有父类方法的调用。

静态代理和动态代理？
静态代理在编译之前就可以明确代理类、目标类、代理类和目标类的共同接口确定下来，在程序运行之前代理类的class文件就已经生成。动态代理是在运行时通过**反射**+**Proxy类**+**InvocationHandler接口**动态生成代理类的类文件并缓存在JVM中，再通过这个缓存的类文件创建动态代理对象。

#### IOC

**Spring Bean的生命周期？**
Spring初始化BeanFactory实例后，加载Bean的配置文件，根据配置文件初步生成BeanDefinition对象实例，再在BeanFactoryPostProcessor中调整BeanDefinition。接下来就是Bean的实例化，根据IOC容器的不同和Bean作用域的不同，Bean的初始化时机也各不相同。**如果IOC容器是BeanFactory，则所有的Bean都在第一次使用时实例化。如果IOC容器是ApplicationContext，如果Bean的作用域是singleton并且lazy设置为false，则ApplicationContext实例化完成后就立即实例化这些Bean并缓存器起来。如果Bean的作用域是singleton并且lazy设置为true，则在第一次使用这个Bean是才实例化这个Bean。如果Bean的作用域是prototype，则总是在第一次使用这个Bean是才实例化这个Bean**。

**实例化Bean的过程**
创建单例Bean实例-> Bean属性注入 -> 如果Bean实现了一些Aware接口，调用相应的方法设置属性 -> 调用BeanPostProcessor的前置处理器 -> 调用@PostConstruct注解的方法 -> 如果Bean实现了InitializingBean接口，就调用afterPropertiesSet方法 -> 调用自定义的初始化方法 -> 调用BeanPostProcessor的后置处理器 -> **现在的Bean可以正常使用** -> @PreDestroy注解的方法 -> 如果Bean实现了DisposableBean接口，就调用destroy方法 -> 调用自定义的销毁方法。

[参考](https://blog.csdn.net/qq_39632561/article/details/83070140)

#### Spring为什么把Bean默认设置成单例的？

单例Bean在第一次创建之后会被缓存下来，这样做的好处是：

- 减少了Bean的创建次数。spring会通过**反射**或者**cglib**来生成Bean实例，这些是比较耗时的操作。
- 减少jvm垃圾回收的压力。如果每次使用Bean都要创建，这中间就涉及了对象的创建，在Bean不用时还会释放Bean的内存空间，这些都会对jvm造成压力。
- 提高响应时间。单例Bean本身是被缓存起来的，在使用时直接从缓存里面取出来就可以啦。

#### Spring事务

[参考](https://www.cnblogs.com/mseddl/p/11577846.html)