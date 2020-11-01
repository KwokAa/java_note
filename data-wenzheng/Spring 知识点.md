## spring 知识点

### @ConditionalOnMissingBean 注解

仅当 BeanFactory 中不包含指定的 bean class 和/或 name 时条件匹配

该条件只能匹配到目前为止 application context 已经处理的 bean 定义，因此强烈建议仅在自动配置类上使用此条件。

spring boot中与@ConditionalOnMissingBean意义相同的注解的解释：https://my.oschina.net/u/566591/blog/2250291

相关知识点：spring 条件化bean

### Spring IoC容器

spring容器是spring框架的核心。spring容器通过DI（依赖注入）来管理组成一个应用程序的各个组件并负责创建对象、连接对象、配置对象、管理对象的整个声明周期（从对象的创建到销毁）。

IOC容器是具有依赖注入功能的容器。IOC容器负责实例化、定位、配置应用程序中的对象并建立这些对象之间的依赖。通常情况下new一个实例对象是由程序员完成的，而控制反转是指new实例不由程序员来做而是交给IOC容器。

spring中有两类容器，分别是Spring BeanFactory容器和Spring ApplicationContext容器。

BeanFactory容器默认采取延迟加载策略。只有当需要某个Bean实例时才会对其进行初始化和依赖注入。

ApplicationContext构建在BeanFactory基础之上。在ApplicationContext容器启动之后，默认会初始化并绑定其所管控的所有对象。

### Spring  Bean

#### spring bean的作用域

spring bean有5中作用域，分别是singleton、prototype、request、session、global-session。

```xml
<bean class="" id="" scope></bean>
```

| 作用域         | 描述                                                         |
| -------------- | ------------------------------------------------------------ |
| singleton      | 在Spring IoC容器中仅存在一个实例。Bean实例以单例模式存在，这是默认的值。 |
| prototype      | 每次从Spring IoC容器中调用Bean实例时，都会返回一个新的实例。 |
| request        | 每次Http请求都会创建一个新的Bean实例，这个作用域仅适用于WebApplicationContext容器。 |
| session        | 同一个Http Session共享一个Bean实例，不同的Session使用不同的Bean实例，这个作用域仅适用于WebApplicationContext容器。 |
| global-session | 整个应用程序使用一个Bean实例，这个作用域仅适用于WebApplicationContext容器。 |
| application    | 将单个bean定义的作用域限定为ServletContext的生命周期。这个作用域仅适用于WebApplicationContext容器。 |
| websocket      | 将单个bean定义的作用域限定为WebSocket的生命周期。这个作用域仅适用于WebApplicationContext容器。 |

如果一个bean具有singleton作用域，那么在Spring IoC容器创建时就会自动创建这样一个Bean，而不管你以后用不用的到。

具有prototype作用域的Bean在Spring IoC容器创建时并不会实例化，而是当尝试获取Bean实例时（包括将Bean实例注入到另外一个Bean中或者使用程序调用容器的getBean方法）才会创建，并且每次获取的都是一个新的Bean实例。这种作用域比较适合有状态Bean，无状态Bean比较适合使用singleton作用域。对于任意作用域的bean，Ioc容器都会调用其初始化回调函数（如果配置了的话）。对于具有prototype作用域的Bean，Ioc容器并不管理这种Bean的整个的声明周期，Ioc容器并不会调用这种Bean的销毁回调（即使配置了），这就需要在这种Bean不需要时手动释放一些持有的资源，可以使用BeanPostProcessor来实现这一点。

#### spring bean的生命周期回调

spring bean的声明周期大致可以描述为：Bean的定义->Bean的创建->Bean的使用->Bean的销毁。

##### spring bean的初始化回调

定义Bean的初始化方法有三种方式，实现InitializingBean 接口并实现afterPropertiesSet方法或者在XML中配置\<bean\>元素的init-method属性。初始化方法会在Bean的所有依赖都设置完成后才会调用。这时Spring的AOP拦截器还没有应用到Bean上。如果使用下面三种方法定义了Bean初始化方法并且三种方法指定的类方法都不相同，那么调用的顺序将是使用@PostConstruct注解、实现InitializingBean接口的afterPropertiesSet方法、指定init方法（方法二）。如果通过这三种方法指定的类方法有重复的，那么重复的方法将只会调用一次。

方法一：

```java
public class MyBean implements InitializingBean {
   public void afterPropertiesSet() {
      //做一些初始化工作
   }
}
```

方法二：

这个init方法必须是无参且返回值为void的方法。

```java
public class MyBean {
   public void init() {
      //做一些初始化工作
   }
}
```

```xml
<bean id="path.to.MyBean" class="" init-method="init"></bean>
```

方法三：

使用@PostConstruct注解

##### spring bean的销毁回调

同理，定义Bean的销毁回调也有三种方法。如果使用下面三种方法定义了Bean销毁回调并且三种方法指定的类方法都不相同，那么调用的顺序将是使用@PreDestroy注解、实现DisposableBean接口的destroy方法、指定的destroy方法（方法二）。如果通过这三种方法指定的类方法有重复的，那么重复的方法将只会调用一次。

方法一：

```java
public class MyBean implements DisposableBean {
   public void destroy() {
      // 做一些销毁工作
   }
}
```

方法二：

这个destroy方法必须是无参且返回值为void的方法

```java
public class MyBean {
   public void destroy() {
      // 做一些销毁工作
   }
}
```

```xml
<bean class="path.to.MyBean" id="MyBean" destroy-method="destroy"></bean>
```

方法三：

使用@PreDestroy注解

##### startup和shutdown回调

看不懂！

### Spring 依赖注入

##### 构造函数注入

构造函数参数解析匹配是通过使用参数的类型来完成的。

```java
public class Foo {
   public Foo(Bar bar, Baz baz) {
      // ...
   }
}
```

```xml
<bean id="foo" class="path.to.Foo">
	<constructor-arg ref="bar"/>
	<constructor-arg ref="baz"/>
</bean>
<bean id="bar" class="path.to.Bar"></bean>
<bean id="baz" class="path.to.Baz"></bean>
```

上面的示例中，通过构造函数的方式注入了两个Bean，这里需要注意的是在XML配置文件中的\<constructor-arg\>元素的顺序必须和Foo的构造函数的形参的顺序一致。如果要想不让他们必须保持一致，就必须在XML中为\<constructor-arg\>元素指定index属性（属性值从0开始）来指定其在Bean的构造函数形参中的顺序，类似于：

```xml
<bean id="foo" class="path.to.Foo">
    <constructor-arg index="1" ref="baz"/>
	<constructor-arg index="0" ref="bar"/>
</bean>
<bean id="bar" class="path.to.Bar"></bean>
<bean id="baz" class="path.to.Baz"></bean>
```

如果在\<constructor-arg\>元素上使用 type 属性显式的指定了构造函数参数的类型，也可以向Bean中注入简单类型，例如：

```java
public class Foo {
   public Foo(inr number, String name) {
      // ...
   }
}
```

```xml
<bean id="foo" class="path.to.Foo">
	<constructor-arg type="int" value="1"/>
	<constructor-arg type="java.lang.String" value="jack"/>
</bean>
```

还可以通过指定构造函数形参的名称来消除歧义，就像下面这样：

```xml
<bean id="foo" class="path.to.Foo">
	<constructor-arg name="number" value="1"/>
	<constructor-arg name="name" value="jack"/>
</bean>
```

##### setter方法注入

基于setter方法的依赖注入是通过在Ioc容器上调用bean的setter方法来实现的。而这个bean，是通过调用无参构造函数或者无参工厂函数来实例化的。

```java
public class Foo {
	private Bar bar;
    private String name;
    public void setBar(Bar bar){
    	this.bar = bar;
    }
    public void setName(String name) {
    	this.name = name;
    }
    // 省略了getter方法
   public Foo() {
      // ...
   }
}
```

通过setter方法注入：

```xml
<bean id="foo" class="path.to.Foo">
    <!-- 注入一个对象时，需要指定ref属性 -->
	<property name="bar" ref="bar"/>
    <!--  注入一个原始值时，只需直接指定值value -->
	<property name="name" value="jack"/>
<bean/>
<bean id="bar" class="path.to.Bar"><bean/>
```

使用p-namespace可以简化相关的配置：

```xml
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:p="http://www.springframework.org/schema/p"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-3.0.xsd">

	<bean id="foo" class="path.to.Foo" p:name="jack" p:bar-ref="bar">
	<bean/>
	<bean id="bar" class="path.to.Bar"><bean/>
</beans>
```

在使用构造函数注入与setter方法注入时，比较推荐的一种做法是。对，必须注入的依赖使用构造函数注入，对于非必须的依赖使用setter方法注入，同时需要注意的是对非必须的依赖要提供一个合理的默认值，以免整个应用中布满非空检查的代码。

##### 接口注入

比较繁琐，不常用。

### 方法注入

当一个Bean所依赖的Bean与自己的声明周期不相同时(比如A依赖B，而A的作用域是singleton，B的作用域是prototype)，由于A只会创建一次，IoC容器就没有办法每次在A需要B的实例时，为A提供一个全新的B的实例。Spring提供了以下3种方法解决上诉问题。方法注入主要用于在作用域为singleton的Bean中注入作用域为prototype的Bean。

为了说明问题，有以下程序：

```java
public class Command {
    private Object state;

    public Object getState() {
        return state;
    }

    public void setState(Object state) {
        System.out.println("设置命令状态....." + state);
        this.state = state;
    }

    public Object execute() {
        System.out.println("执行方法体" + state);
        return null;
    }
}
```

#### 放弃控制反转

这种方法必须实现ApplicationContextAware接口

```java
public class CommandManager1 implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    public Object process(Object commandState) {
        // 获取Command类的一个全新的实例
        Command command = createCommand();
        System.out.println("创建的command：" + command);
        command.setState(commandState);
        return command.execute();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    protected Command createCommand() {
        return this.applicationContext.getBean("command", Command.class);
    }
}
```

XML配置：

```xml
<bean id="command" class="spittr.web.Command" scope="prototype"/>
<bean id="commandManager1" class="spittr.web.CommandManager1"/>
```

测试程序：

```java
  ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
        CommandManager1 commandManager = (CommandManager1)context.getBean("commandManager1");
        commandManager.process("A");
        commandManager.process("B");
// ========== 本机上的输出 ===========
// 创建的command：spittr.web.Command@7276c8cd
// 设置命令状态.....A
// 执行方法体A
// 创建的command：spittr.web.Command@544a2ea6
// 设置命令状态.....B
// 执行方法体B
```

并不提倡使用这种方法，因为这样使业务代码与spring框架的代码紧密耦合在一块。

#### 方法注入

Spring实现方法注入是通过CGLIB在运行时动态产生字节码来生成一个子类（这里是动态生成CommandManager类的一个子类）并覆盖掉要进行方法注入的方法（这里是createCommand方法）。需要注意的是，要被继承的类（这里是CommandManager类）不能由final修饰，被覆盖掉的方法（这里是CommandManager类的createCommand方法）也不能由final修饰。被覆盖掉的方法的签名要符合下面的格式：

<public|protected> [abstract] \<return-type\> theMethodName(no-arguments); 

```java
public abstract class CommandManager {
    public Object process(Object commandState) {
        Command command = createCommand();
        System.out.println("生成的对象：" + command);
        command.setState(commandState);
        return command.execute();
    }
    protected abstract Command createCommand();
}
```

```xml
 <bean id="myCommand" class="spittr.web.Command" scope="prototype"></bean>
    <bean id="commandManager" class="spittr.web.CommandManager">
        <lookup-method name="createCommand" bean="myCommand"/>
    </bean>
```

测试程序：

```java
 ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
        CommandManager commandManager = (CommandManager)context.getBean("commandManager");
        commandManager.process("A");
        commandManager.process("B");
// ============ 本机上的输出结果 ===============
// 生成的对象：spittr.web.Command@4f9a3314
// 设置命令状态.....A
// 执行方法体A
// 生成的对象：spittr.web.Command@3b2c72c2
// 设置命令状态.....B
// 执行方法体B
```

在基于注解的配置中，方法注入也可以写成：

```java
public abstract class CommandManager {
    public Object process(Object commandState) {
        Command command = createCommand();
        System.out.println("生成的对象：" + command);
        command.setState(commandState);
        return command.execute();
    }
    @Lookup("myCommand")
    protected abstract Command createCommand();
}
```



#### 方法替换





### spring 自动注入

#### 自动注入的一些优点

1. 自动注入可以减轻手动注入的工作量。
2. 当代码改变时，bean的配置信息不用跟着变化。比如，现在要求在原来的基础上，又给一个bean添加了一个新的依赖，如果使用手动注入，这时就需要修改bean的配置信息，如果使用了自动注入，就不需要主动修改bean的配置信息。

spring 支持5种自动注入模式，分别是no、byName、byType、constructor、autodetect。

现在有下面的示例程序：

```java
public class A{
	private B b;
	private C c;
    public A(B b, C c){
        this.b = b;
        this.c = c;
    }
	// 省略getter、setter
}
public class B{}
public class C{}
```

#### no

```xml
<bean id="a" class="path.to.A" autowire="no"/>
```

autowire="no"表示不支持任何形式的自动注入，这是默认的配置值，同时这也意味着必须进行手动注入才能正常的让程序工作，就像下面这样：

```xml
<bean id="a" class="path.to.A" autowire="no">
	<property name="b" ref="b"/>
    <property name="c" ref="c"/>
</bean>
<bean id="b" class="path.to.B"/>
<bean id="c" class="path.to.C"/>
```

#### byName

顾名思义，这是按照名称进行自动注入。这里的名字是指Bean中属性的名字，例如这里类A中的b、c属性。当对A进行自动注入时，会在IoC容器已注册的所有Bean中寻找名称是b、c的Bean，注入到属性b、c上。可以看到这只适用于setter方法注入。

```xml
<bean id="a" class="path.to.A" autowire="byName"/>
<bean id="b" class="path.to.B"/>
<bean id="c" class="path.to.C"/>
```

这种自动注入方式是有缺陷的，它让XML配置中Bean的名称和Bean定义中的属性名紧密耦合在一块。

#### byType

在上面的示例程序中，对象b、c都是A的依赖对象。当IoC容器对当前的Bean（这里是a）进行依赖注入时，IoC容器发现名为a的Bean依赖了类型为B、C的Bean，IoC容器就会在它所管理的所有Bean中寻找B、C类型的Bean，找到符合条件的Bean后就将这些Bean注入到a中。如果找不到符合条件的Bean，则对应的属性将不会设置，也就是null。如果找到了不止一个符合条件的Bean，就会产生冲突。这种类型的自动注入使用的是setter注入。

```xml
<bean id="a" class="path.to.A" autowire="byType"/>
<bean id="b" class="path.to.B"/>
<bean id="c" class="path.to.C"/>
```

#### constructor

constructor类型的自动注入是针对构造函数的参数的类型进行的自动注入，它只是byType的构造函数形式。如果在整个Ioc容器中找不到一个类型匹配的bean，就会报错。

```xml
<bean id="a" class="path.to.A" autowire="constructor"/>
<bean id="b" class="path.to.B"/>
<bean id="c" class="path.to.C"/>
```

#### autodetect

autodetect模式是byType和constructor模式的结合体。如果对象有默认的无参构造函数，IoC容器会优先考虑byType模式的自动注入。否则，会使用constructor模式的自动注入。如果使用constructor进行自动注入完后，对象还有属性没注入，这时就会对剩余的属性采用byType模式的自动注入。

#### 自动注入的一些缺点

1. 手动注入的值会覆盖掉自动注入的值。
2. 自动注入不适用于原生类型、String类型、Classes类型以及由这些类型组成的数组。
3. 对于单个值的依赖，如果在容器中发现多个符合条件的候选bean，将造成歧义。

#### 克服缺点的办法

1. 直接放弃使用自动注入（废话）。

2. 将某个\<bean\>的autowire-candidate属性设置为false，来让这个bean不参与自动注入。比如，现在有一个Person接口，Person接口有两个实现类Student、Teacher，又有一个Clazz类，Clazz类中有一个属性为monitor（类型为Person），但是现在要求这个属性只能自动注入Student类型的bean实例，这就要将Teacher类的bean实例排除在外，可以在XML中这样写：

   ```xml
   <bean id="student" class="path.to.Student"/>
   <bean id="teacher" class="path.to.Teacher" autowire-candidate="false"/>
   ```

   autowire-candidate属性只对byType类型的自动注入有影响。也就是说，id为teacher的bean只在使用byType模式的自动注入时会被排除在外，而在使用byName模式的自动注入时依然有可能被注入。

3. 改变bean在自动注入中的优先级，这可以通过设置\<bean\>的primary属性为true来实现。还是利用2中的例子，为了能在Clazz中优先注入Student类的bean实例，可以在XML中这样配置:

   ```xml
   <bean id="student" class="path.to.Student" primary="true"/>
   <bean id="teacher" class="path.to.Teacher"/>
   ```

4. 使用限定符消除歧义。

   什么是限定符？

   在XML形式的配置文件中：

   ```xml
   <bean class="path.to.Student">
       <!-- qualifier元素定义的就是限定符 -->
   	<qualifier value="student"/>
   </bean>
   <bean class="path.to.Teacher">
   	<qualifier value="teacher"/>
   </bean>
   ```

   如果是使用java代码进行配置：

   ```java
   // 方式1：
   @Component
   @Qualifier("student")
   public class Student implements Person{
       private String name;
       // getter、setter
   }
   // 这里@Qualifier就定义了一个限定符
   ```

   ```java
   // 方式2：
   @Configuration
   public class JavaConfig{
       @Bean
       @Qualifier("clazz")
       public Clazz clazz() {
           return new Clazz();
       }
   }
   ```

   其实如果不显式的指定一个限定符（在XML中通过\<qualifier\>元素指定，在java配置类中通过@Qualifier注解指定），bean也会有一个默认的限定符，那就是bean的id。

   如何使用限定符？

   在自动注入的属性上使用：

   ```java
   @Component
   public class Clazz{
       @Autowire
       @Qualifier("student")
       private Person monitor;
       // getter、setter
   }
   ```

   在构造函数参数上使用：

   ```java
   @Component
   public class Clazz{
   	private Person monitor;
       public Clazz(@Qualifier("student") Person monitor) {
           this.monitor = monitor;
       }
   }
   ```

   实现自定义的限定符注解

   ```java
   @Target({ElementType.FIELD, ElementType.PARAMETER})
   @Retention(RetentionPolicy.RUNTIME)
   @Qualifier
   public @interface Student {}
   ```

   ```java
   @Target({ElementType.FIELD, ElementType.PARAMETER})
   @Retention(RetentionPolicy.RUNTIME)
   @Qualifier
   public @interface Teacher {}
   ```

   在Student、Teacher类的定义中可以这样使用：

   ```java
   @Component
   @Student
   public class Student implements Person{
       private String name;
       // getter、setter
   }
   ```

   ```java
   @Component
   @Teacher
   public class Teacher implements Person{
       private String name;
       // getter、setter
   }
   ```

   在Clazz中可以这样限定注入的属性类型：

   ```java
   @Component
   public class Clazz{
       @Autowire
      @Student
       private Person monitor;
       // getter、setter
   }
   ```

### Spring属性注入值

#### 基本类型和String

```xml
<bean id="beanID" class=".....">
    <property name="propertyName" value="propertyValue"/>
</bean>
```

还可以配置java.util.Properties的实例

```xml
<bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
      <property name="properties">
         <value>
            value1=value11111
            value2=value2
         </value>
      </property>
   </bean>
<bean id="beanID" class=".....">
    <property name="propertyName" value="${value1}"/>
</bean>
```

为java.util.Properties的实例进行配置之后，就可以在其他的Bean的配置中使用java.util.Properties的实例中的属性，就像上面的${value1}。

#### 注入内部Bean

如果一个Bean的作用只是当作某个Bean的属性而在别的地方不在使用，就可以将前者注入为一个内部Bean。

```java
public class Customer {
    private Goods goods;

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }
}
```

```java
public class Goods {
    private String name;
    private Integer price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
```

```xml
<bean id = "customer" class="spittr.web.Customer">
      <property name="goods">
         <bean class="spittr.web.Goods">
            <property name="name" value="商品名称"/>
            <property name="price" value="100" />
         </bean>
      </property>
   </bean>
```

注意内部Bean没有指定id、name或者scope属性，即使指定了，spring也会忽略这些值。内部Bean只能在定义它的Bean中使用，其他的地方没办法使用。内部bean的作用域与外部包含它的bean作用域是一样的。

定义内部Bean与使用内部类定义Bean不同，现在为Customer类定义一个静态内部类：

```java
public class Customer {
    private Goods goods;

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public static class Address{
        private String streetName;

        public String getStreetName() {
            return streetName;
        }

        public void setStreetName(String streetName) {
            this.streetName = streetName;
        }
    }
}
```

XML中要将\<bean\>的class属性指定成path.to.Customer$Address:

```xml
 <bean id="address" class="spittr.web.Customer$Address">
      <property name="streetName" value="大街"/>
   </bean>
```

#### 注入集合

##### \<props\>

\<props\>用于向java.util.Properties中注入值。

```java
public class InjectProperties {
    private Properties properties;

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
```

```xml
<bean class="spittr.web.InjectProperties" id="injectProperties">
    <!--  相当于调用方法setProperties(java.util.Properties) -->
      <property name="properties">
         <props>
            <prop key="driver">com.mysql.jdbc.Driver</prop>
            <prop key="url">jdbc:mysql://localhost:3306</prop>
            <prop key="username">username</prop>
         </props>
      </property>
   </bean>
```

测试程序：

```java
ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
        InjectProperties pros = (InjectProperties) context.getBean("injectProperties");
        Properties properties = pros.getProperties();
        System.out.println(properties.getProperty("driver"));
        System.out.println(properties.getProperty("url"));
        System.out.println(properties.getProperty("username"));
```

##### set、map、list

```java
public class InjectCollection {
    private List<String> list;
    private Map<String, String> map;
    private Set set;

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public Set getSet() {
        return set;
    }

    public void setSet(Set set) {
        this.set = set;
    }
}
```

```xml
<bean class="spittr.web.InjectCollection" id="injectCollection">
      <property name="list">
         <list>
            <value>value1</value>
            <value>value2</value>
            <value>value3</value>
             <!-- 
				还可以引用其他bean，就像
				<ref bean="beanName">
				-->
         </list>
      </property>
      <property name="map">
         <map>
            <entry key="key1" value="map-value-1"/>
            <entry key="key2" value="map-value-2"/>
            <entry key="key3" value="map-value-3"/>
             <!--
			 	还可以引用其他的bean，就像
				<entry key="key4" value-ref="beanName"/>
			-->
         </map>
      </property>
      <property name="set">
         <set>
            <value>set-value1</value>
            <value>set-value2</value>
            <value>set-value3</value>
             <!-- 还可以引用其他的bean -->
            <ref bean="injectProperties"/>
         </set>
      </property>
   </bean>

   <bean class="spittr.web.InjectProperties" id="injectProperties">
      <property name="properties">
         <props>
            <prop key="driver">com.mysql.jdbc.Driver</prop>
            <prop key="url">jdbc:mysql://localhost:3306</prop>
            <prop key="username">username</prop>
         </props>
      </property>
   </bean>
```

测试程序：

```java
@Test
    public void collectionTest() {
        ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
        InjectCollection injectCollection = (InjectCollection) context.getBean("injectCollection");

        List<String> list = injectCollection.getList();
        Map<String, String> map = injectCollection.getMap();
        Set<String> set = injectCollection.getSet();

        // 循环list
        for (int i = 0, len = list.size(); i < len; i++) {
            System.out.println(list.get(i));
        }
        // 循环map
        Iterator<Map.Entry<String, String>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println("key=" + key + " ,value=" + value);
        }
        // 循环set
        Iterator iterator = set.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }
```

在注入Set类型的属性时，还使用\<ref\>注入了名为injectProperties的Bean，不过需要注意的是InjectCollection中的set属性未使用泛型参数，否则是不能这样混合注入String、和自定义Bean的。

##### 集合合并

继续沿用InjectProperties类的代码，但是其XML配置将换成：

```xml
<bean id="parent" abstract="true">
      <property name="properties">
         <props>
            <prop key="username">username111</prop>
            <prop key="password">password11</prop>
         </props>
      </property>
   </bean>
   <bean class="spittr.web.InjectProperties" id="injectProperties" parent="parent">
      <property name="properties">
         <props merge="true">
            <prop key="driver">com.mysql.jdbc.Driver</prop>
            <prop key="url">jdbc:mysql://localhost:3306</prop>
            <prop key="username">username</prop>
         </props>
      </property>
   </bean>
```

再有以下测试程序：

```java
ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
        InjectProperties pros = (InjectProperties) context.getBean("injectProperties");
        Properties properties = pros.getProperties();
        System.out.println(properties.getProperty("driver"));
        System.out.println(properties.getProperty("url"));
        System.out.println(properties.getProperty("username"));
        System.out.println(properties.getProperty("password"));
```

最终username的输出值将是username，因为值“username”将覆盖掉“username111”，同时password的值将是"password11"。需要注意的是这里在\<props\>上设置merge属性为true。

集合合并也适用于其他的集合类型（List、Map、Set）。继续沿用InjectCollection类和其测试程序，但是XML配置变化如下：

```xml
<bean id="collectParent" abstract="true">
      <property name="list">
         <list>
            <value>parent-value1</value>
            <value>parent-value2</value>
         </list>
      </property>
      <property name="map">
         <map>
            <entry key="key4" value="parent-map-value-4"/>
         </map>
      </property>
      <property name="set">
         <set>
            <value>parent-set-value1</value>
            <value>parent-set-value2</value>
            <value>parent-set-value3</value>
         </set>
      </property>
   </bean>

   <bean class="spittr.web.InjectCollection" id="injectCollection" parent="collectParent">
      <property name="list">
         <list merge="true">
            <value>value1</value>
            <value>value2</value>
            <value>value3</value>
         </list>
      </property>
      <property name="map">
         <map merge="true">
            <entry key="key1" value="map-value-1"/>
            <entry key="key2" value="map-value-2"/>
            <entry key="key3" value="map-value-3"/>
         </map>
      </property>
      <property name="set">
         <set merge="true">
            <value>set-value1</value>
            <value>set-value2</value>
            <value>set-value3</value>
         </set>
      </property>
   </bean>
```

这样配置之后，对于List，parent中配置的值将会在child中的值之前。对于Map、Set、Properties则不会保持这种顺序。注意，Map会覆盖掉parent中key值相同的value。

#### depend-on

depend-on用来指定Bean初始化及销毁时的顺序。depend-on常用来确定bean定义中依赖关系不明确或者没有直接依赖关系时，指定bean初始化和销毁的顺序。

```xml
<bean id="a" class="path.to.A"></bean>
<bean id="b" class="path.to.B" depend-on="a"></bean>
```

上诉配置的意思是a在b之前实例化，b在a销毁之后才销毁。depend-on属性中可以指定多个bean（使用逗号或者分号或者空格隔开）。

#### 懒初始化bean

默认情况下，Ioc容器会在初始化时实例化所有作用域为scope的bean，如果想改变默认的行为，可以通过制定lazy-init属性来实现。

``` xml
<bean id="beanName" class="path.to.beanClass" lazy-init="true"/>
```

如果一个非懒加载的bean依赖一个懒加载的bean，那么这个定义为懒加载的bean并不会真正的懒加载而是会表现的如非懒加载的bean一样。还可以通过以下方式全局开启懒加载：

```xml
<beans default-lazy-init="true" />
```

### Spring IOC

#### 什么是IOC？

IOC即控制反转，是一种设计思想。以前程序员写代码时，需要自己创建对象并解决对象之间的依赖、管理对象的生命周期。在引入了控制反转之后，就由IOC容器负责新对象的创建、对象之间依赖关系的解决、管理对象的生命周的各个阶段。IOC在Spring中的实现就是依赖注入，由IOC容器管理Bean的创建、Bean之间的自动装配。

#### IOC容器的类型？

- BeanFactory
- ApplicationContext

#### Bean的注入方式？

- 构造函数注入
- 属性注入
- 接口注入

#### Bean的配置方式？

- xml配置
- java配置类
- 基于注解

#### IOC的好处

- 解耦合
- 方便单元测试

### AOP

#### 应用场景

1. 事务控制
2. 权限控制
3. 出错控制
4. 日志

#### 什么是AOP?

AOP是面向切面编程, AOP的关注单元是切面。切面是指

#### AOP的概念

1. 切点

   切点是匹配连接点的表达式

2. 切面

   切点和通知组成了切面

3. 连接点

   连接点是指程序中可以插入切面的一个点，比如方法的执行、出错的处理。

4. 通知

   定义了切面要真正执行的代码，以及什么时候执行。比如在连接点之前执行、在连接点之后执行。

5. 织入

   织入是把切面应用到目标对象并创建代理对象的过程。

   - 编译期织入

     切面在**目标对象编译时**织入，这需要特殊的编译器来支持。AspectJ的织入编译器就是以这种方式织入切面的。

   - 运行时织入

     切面在**应用运行时**织入，一般需要为目标对象创建一个动态代理对象，Spring AOP就是运行时织入。

   - 类加载期织入

     切面在**目标对象加载时**织入，需要特殊的类加载器。

6. 引入

#### Spring AOP

Spring AOP只支持方法级别的拦截。

#### Spring AOP两种使用方式

1. 使用AspectJ注解方式

   @Aspect @Pointcut

2. 使用XML配置方式

#### Spring AOP的通知类型

1. 前置通知 @Before
2. 后置通知 @After
3. 环绕通知 @Around
4. 返回通知 @AfterReturning
5. 出错通知 @AfterThrowing

#### Spring AOP使用的代理

1. Java动态代理，代理实现类某些接口的类。生成一个动态代理对象
2. CGLIB，代理没实现接口的类。生成一个子类。

### Spring 事务

#### Spring事务的隔离级别

- ISOLATION_DEFAULT: 这个是默认值, 表示使用底层数据库的默认的隔离级别
- ISOLATION_READ_UNCOMMIT
- ISOLATION_READ_COMMIT
- ISOLATION_REPEATABLE_READ
- ISOLATION_SERIALIZATION

#### Spring事务的传播行为

- PROPAGATION_REQUIRED: 如果当前存在事务, 就加入当前事务; 否则, 创建一个新的事务. 这个是默认值
- PROPAGATION_REQUIRES_NEW: 创建一个新的事务, 如果当前事务存在, 就将当前事务挂起
- PROPAGATION_SUPPORTS: 如果当前存在事务, 就加入当前事务; 否则, 就以非事务方式运行
- PROPAGATION_NOT_SUPPORTS: 以非事务方式运行,如果当前存在, 如果当前存在事务, 就将当前事务挂起
- PROPAGATION_NEVER: 以非事务方式运行, 如果当前存在事务,就报错.
- PROPAGATION_MANDATORY: 如果当前存在事务, 则加入当前事务;否则,报错
- PROPAGATION_NESTED: 如果当前存在事务,就创建一个新的事务作为当前事务的嵌套事务来执行，如果没有当前事务，就等于PROPAGATION_REQUIRED

#### 事务超时

所谓事务超时，就是指一个事务所允许执行的最长时间，如果超过该时间限制但事务还没有完成，则自动回滚事务。

#### Spring事务回滚规则

指示spring事务管理器回滚一个事务的推荐方法是在当前事务的上下文内抛出异常。spring事务管理器会捕捉任何未处理的异常，然后依据规则决定是否回滚抛出异常的事务。

默认配置下，Spring只有在抛出的异常是非检查异常时才会回滚该事务, 也就是说抛出的异常时RuntimeException的子类以及Errors或其子类, 抛出检查型异常不会导致事务回滚.

可以通过配置, 执行可以回滚事务的异常类型

#### Spring事务管理器

- DataSourceTransactionManager 数据源事务管理器
- JpaTransactionManager Jpa事务管理器
- 还有很多其他事务管理器

#### Spring支持编程式事务和声明式事务

- 编程式事务

  使用TransactionTemplate

- 声明式事务

  1. 基于tx和aop命名空间的xml配置

  2. 基于@Transactional注解

     value　可选，配置事务管理器

     propagation　可选，配置事务船舶级别

     isolation　可选，配置事务隔离级别

     readOnly　可选，读写或只读事务，默认是读写

     timeout　事务的超时时间

     rollbackFor 导致事务回滚的异常类数组

     rollbackForClassName 导致事务回滚的异常类类名数组

     noRollbackFor 不会导致事务回滚的异常类数组

     noRollbackForClassName 不会导致事务回滚的异常类类名数组

＠Transactional可以作用于接口、接口方法、类、类方法，当作用于类时，类中的所有public方法都将被这个注解影响。

**Spring中Bean的循环依赖**

### Spring拦截器、过滤器、AOP

- 拦截器

  实现**HandlerInterceptor** 接口

  1. preHandler方法

     在controller层之前调用

  2. postHandler方法

     controller执行完，视图渲染之前调用

  3. afterCompletion方法

     视图渲染之后调用

  两个拦截器时的调用顺序，preHandler1、preHandler2、postHandler2、postHandler1、afterCompletion2、afterCompletion1。

  **拦截的是URL**

- 过滤器

  只有一个doFilterInternal方法，如果继续执行，调用filterChain的doFilter()方法

  **拦截的是URL**

- AOP

  拦截的是类的元数据，相对于拦截器更加细致、灵活、能够实现更复杂的逻辑。

三者的调用顺序：过滤器、拦截器、AOP。