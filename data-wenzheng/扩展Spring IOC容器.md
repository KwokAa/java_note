## 扩展Spring IOC容器

### Spring BeanPostProcessor

使用Spring后置处理器可以在调用Bean的初始化函数前后对Bean做一些处理。BeanPostProcessor接口定义了后置处理器的方法。

注意：在Bean实例化时，对每一个Bean都会执行一遍BeanPostProcessor

```java
package spittr.web;

public interface Person {
    // 定义person接口的自我介绍方法
    String selfIntroduction();
}
```

```java
package spittr.web;

public class Student implements Person{
    private String name;

    @Override
    public String selfIntroduction() {
        return "My name is " + name;
    }

    public void init(){
        System.out.println("初始化方法被调用啦！");
    }

    public void destroy() {
        System.out.println("Bean 被销毁啦！");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

```java
package spittr.web;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class StudentPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("在初始化bean之后调用啦！");
        System.out.println("bean的名称是：" + beanName);
        Class beanClass = bean.getClass();
        if (beanClass == Student.class) {
            Object proxy = Proxy.newProxyInstance(bean.getClass().getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println("被动态代理拦截的方法是：" + method.getName());
                    String result = (String) method.invoke(bean, args);
                    return result.toUpperCase();
                }
            });
            return proxy;
        }
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("在bean的初始化回调之前调用啦！");
        System.out.println("bean的名字是：" + beanName);
        return bean;
    }
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:c="http://www.springframework.org/schema/c"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-3.0.xsd http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd">

   <bean id="student" class="spittr.web.Student" init-method="init" destroy-method="destroy">
      <property name="name" value="jack"/>
   </bean>
   <bean class="spittr.web.StudentPostProcessor"/>
</beans>
```

```java
package spittr.test;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import spittr.web.Person;
import spittr.web.Student;

public class Test {
    public static void main(String[] args) {
        AbstractApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
        Person student = (Person) context.getBean("student");
        System.out.println(student.selfIntroduction());
        context.registerShutdownHook();
    }
}
```

最后测试程序的输出是：

```
在bean的初始化回调之前调用啦！
bean的名字是：student
初始化方法被调用啦！
在初始化bean之后调用啦！
bean的名称是：student
被动态代理拦截的方法是：selfIntroduction
MY NAME IS JACK
Bean 被销毁啦！
```

可以看到BeanPostProcessor接口定义的两个方法postProcessBeforeInitialization、postProcessAfterInitialization分别在名为student的Bean实例的初始化回调init方法前后被调用，并且这两个方法都有两个参数分别表示已经初始化的Bean实例和Bean实例的名字，这说明在调用这两个方法时Spring IoC容器已经实例化了对应的Bean。

定义Spring的后置处理器只需要实现BeanPostProcessor接口并配置为普通的Bean即可，Spring IoC容器会自动检测出实现了BeanPostProcessor的Bean并在适当的时机调用它们。可以配置多个 BeanPostProcessor 接口，通过设置 BeanPostProcessor 实现的 **Ordered** 接口提供的 **order** 属性来控制这些 BeanPostProcessor 接口的执行顺序。

从测试程序的输出上可以看到，在postProcessAfterInitialization方法中对名为student的Bean的selfIntroduction()方法调用进行了一定程度的改变（使用的是动态代理方法），使其返回值转变成大写形式。

### Spring BeanFactoryPostProcessor

BeanFactoryPostProcessor可以在**Spring的IoC容器启动之后、实例化任何Bean之前**读取**Bean的配置元数据**（就是通常XML中的配置信息），并根据需要进行相应的修改。例如：可以修改Bean的作用域、修改Bean的属性值。一个Spring IoC容器可以配置多个BeanFactoryPostProcessor的实现类，如果对这些类的调用顺序有要求，这些实现类就必须实现org.springframework.core.Ordered接口来指定它们之间的调用顺序。 

如果使用的是BeanFactory容器，则需要手动设置BeanFactoryPostProcessor。如果使用的是ApplicationContext容器，则只需要在XML中配置BeanFactoryPostProcessor的实现类，就像下面自定义BeanFactoryPostProcessor的实现类一样。因为ApplicationContext容器会自动识别配置文件中的BeanFactoryPostProcessor。

#### 自定义BeanFactoryPostProcessor

```java
public interface Person {
    String selfIntroduction();
}
```

```java
public class Teacher implements Person {
    private String name;
    private float salary;

    @Override
    public String selfIntroduction() {
        return "name: " + name  + " salary: " + salary;
    }
   // 省略getter、setter
}

```

通过实现BeanFactoryPostProcessor接口自定义MyBeanFactoryPostProcessor

```java
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("调用自定义的BeanFactoryPostProcessor开始================================");
        // 根据XML配置中Bean的id获取Bean定义元数据BeanDefinition对象
        BeanDefinition bd = beanFactory.getBeanDefinition("teacher");
        System.out.println("在XML中配置的属性值：" + bd.getPropertyValues().toString());
        MutablePropertyValues mp = bd.getPropertyValues();
        if (mp.contains("name")) {
            mp.addPropertyValue("name", "这是在BeanFactoryPostProcessor中配置的新的name属性的值");
        }
        bd.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        System.out.println("调用自定义的BeanFactoryPostProcessor结束================================");
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
```

在XML中进行配置：

```xml
<bean id="teacher" class="spittr.web.Teacher">
      <property name="name" value="我是老师"/>
      <property name="salary" value="28888.8"/>
</bean>
<bean id="MyBeanFactoryPostProcessor" class="spittr.web.MyBeanFactoryPostProcessor"/>
```

测试程序：

```java
public class SpringTest {
    @Test
    public void TestBeanFactoryPostProcessor() {
        AbstractApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
        Person teacher = (Person) context.getBean("teacher");
        System.out.println(teacher.selfIntroduction());
        context.registerShutdownHook();
    }
}

```

程序输出：

```
调用自定义的BeanFactoryPostProcessor开始================================
在XML中配置的属性值：PropertyValues: length=2; bean property 'name'; bean property 'salary'
调用自定义的BeanFactoryPostProcessor结束================================
name: 这是在BeanFactoryPostProcessor中配置的新的name属性的值 salary: 28888.8
```

#### spring中自带的BeanFactoryPostProcessor

spring中几个比较常用的BeanFactoryPostProcessor的实现类都在org.springframework.beans.
factory.config包下面，它们是：

1、PropertyPlaceholderConfigurer 

2、PropertyOverrideConfigurer 

3、CustomEditorConfigurer 

##### PropertyPlaceholderConfigurer 

PropertyPlaceholderConfigurer 允许我们在XML配置文件中使用占位符，并将这些占位符所代表的实际值放到properties文件中。常用于数据源配置、邮件服务器配置。

先看一下XML中的配置：

```xml
<bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
    <property name="locations">
        <list>
            <value>conf/jdbc.properties</value>
        </list>
    </property>
</bean>
<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
    <property name="url" value="${jdbc.url}"/>
    <property name="driverClassName" value="${jdbc.driver}" />
    <property name="username" value="${jdbc.username}" />
	<property name="password" value="${jdbc.password}" />
</bean>
```

jdbc.properties文件内容：

```properties
jdbc.url=jdbc:mysql://server/MAIN?useUnicode=true&characterEncoding=ms932&failOverReadOnly=false&roundRobinLoadBalance=true
jdbc.driver=com.mysql.jdbc.Driver
jdbc.username=your username
jdbc.password=your password
```

当spring IoC容器加载完成所有配置信息时， 容器中保存的对象的属性信息还只是以占位符的形式存在，如\${jdbc.url}、 \${jdbc.driver}。当PropertyPlaceholderConfigurer作为BeanFactoryPostProcessor被应用时，它会使用properties配置文件中的配置信息来替换相应BeanDefinition中占位符所表示的属性值。这样，当容器实例化bean时， bean定义中的属性值就是最终替换完成的了。

从Spring 2.5开始，提供了一个专门的元素来指定properties文件的位置，如果想同时指定多个properties文件，可以直接在location属性上写明，但是各个properties文件的位置要使用逗号分隔开。

```xml
<context:property-placeholder location="classpath:com/something/jdbc.properties"/>
```

PropertyPlaceholderConfigurer不只会从properties配置文件中加载配置信息，还会从System类的Properties中加载。 PropertyPlaceholderConfigurer有三种模式，SYSTEM_PROPERTIES_MODE_FALLBACK、SYSTEM_PROPERTIES_MODE_NEVER、SYSTEM_PROPERTIES_MODE_OVERRIDE，分别代表如果properties配置文件中没有配置项就去System类的properties中查找、只在properties配置文件中查找配置项、properties配置文件中的配置项会覆盖掉System类的properties中的配置项。默认的是SYSTEM_PROPERTIES_MODE_FALLBACK模式。可以通过调用PropertyPlaceholderConfigurer的setSystemPropertiesMode()方法改变默认的模式。

##### PropertyOverrideConfigurer 

PropertyOverrideConfigurer 所使用的properties文件的格式如下：

```properties
beanName.property=value
```

其中的beanName是XML配置文件中指定的Bean的id，property就是这个Bean的属性，value是要覆盖掉的值。可以看到PropertyOverrideConfigurer所使用的properties中的文件的key(这里是beanName.property)是与XML配置文件紧密耦合在一块的，如果不查看PropertyOverrideConfigurer所使用的properties文件是看不出来Bean的属性值是如何被替换的。相反，PropertyPlaceholderConfigurer所使用的properties文件中key只是起到了一个占位符的作用，并不会与XML配置耦合在一块。

如果一个Bean的某个属性在PropertyOverrideConfigurer所使用的properties文件中指定了一个值，这个值将覆盖掉XML中为这个属性配置的值。如果一个Bean的某个属性在PropertyOverrideConfigurer所使用的properties文件中没为其指定值，就还是使用XML文件中配置的值。可以指定多个properties文件，如果一个属性在多个properties文件中都指定了值，那么将使用最后一个properties文件中的值。

与PropertyPlaceholderConfigurer 类似，从Spring 2.5开始，也为PropertyOverrideConfigurer提供了一个专门的元素来指定它所使用的properties文件，使用多个properties文件时，要在location属性中指定并使用逗号分隔。

```xml
<context:property-override location="classpath:override.properties"/>
```

### Spring FactoryBean

#### 使用工厂方法注册Bean

为了说明问题，现有以下程序：

```java
public class Foo{
	private Bar bar;
	// 省略getter和setter
}
```

```java
public interface Bar{
	// 接口Bar的相关代码
}
```

可以看到Foo依赖一个接口Bar的具体实现。

##### 使用静态工厂方法注册Bean

```java
public class StaticBarFactory{
	public static Bar getInstance() {
		Bar instance = new BarImpl();
		// 对Bar的具体实现实例instance做一些操作
		return instance;
	}
}
```

在XML中需要配置成：

```xml
<bean id="foo" class="path.to.Foo">
	<property name="bar" ref="bar"></property>
</bean>
<bean id="bar" class="path.to.StaticBarFactory" factory-method="getInstance"></bean>
```

更通用的情况是需要给接口Bar的具体实现类传递参数，就像下面这样：

```java
public class StaticBarFactory{
	public static Bar getInstance(FooBar foobar) {
		Bar instance = new BarImpl(foobar);
		// 对Bar的具体实现实例instance做一些操作
		return instance;
	}
}
```

这时可以用\<constructor-arg\>来为静态工厂方法传递参数，就像下面这样：

```xml
<bean id="foo" class="path.to.Foo">
	<property name="bar" ref="bar"></property>
</bean>
<bean id="bar" class="path.to.StaticBarFactory" factory-method="getInstance">
	<constructor-arg ref="foobar"/>
</bean>
<bean id="foobar" class="path.to.FooBar"/>
```

##### 使用非静态工厂方法注册Bean

```java
public class NonStaticBarFactory{
	public Bar getInstance(FooBar foobar){
		Bar instance = new BarImpl(foobar);
		// 对Bar接口的实现类实例做一些其他工作
		return instance;
	}
}
```

在XML中要配置成：

```xml
<bean id="foo" class="path.to.Foo">
    <property name="bar" ref="bar"/>
</bean>
<bean id="foobar" class="path.to.FooBar"></bean>
<bean id="barFactory" class="path.to.NonStaticBarFactory"></bean>
<bean id="bar" factory-bean="barFactory" factory-method="getInstance">
	<construcotr-arg ref="foobar"/>
</bean>
```

#### 使用FactoryBean接口注册Bean

当某些Bean的实例化过程过于复杂，以至于使用XML配置显得过于复杂或者是某些第三方库不能直接注册到Spring容器中时，可以考虑使用FactoryBean接口来注入这些Bean。

FactoryBean接口定义如下：

```java
public interface FactoryBean{
    // 返回Bean实例
    Object getObject() throws Exception;
    // 返回Bean实例的Class对象
    Class getObjectType();
    // true代表Bean的作用域是singleton
    boolean isSingleton();
}
```

```java
public class Tomorrow{
	private Date tomorrow;
    // 省略getter和setter
}
```

创建FactoryBean：

```java
public class TomorrowFactoryBean implements FactoryBean{
    @override
    public Object getObject() {
     	Date current = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(current);
        calendar.add(calendar.DATE, 1);
        current = calendar.getTime();   
        return current;
    }
    @override
    public Class getObjectType() {
        return Date.class;
    }
    @override
    public boolean isSingleton() {
        return false;
    }
}
```

在XML中配置成：

```xml
<bean id="tomorrow" class="path.to.Tomorrow">
    <property name="tomorrow" ref="tomorrowFactoryBean"/>
</bean>
<bean id="tomorrowFactoryBean" class="path.to.TomorrowFactoryBean"></bean>
```

从XML的配置上看不出与普通的Bean配置有什么区别。如果在ApplicationContext上调用getBean("tomorrowFactoryBean")，得到的将是TomorrowFactoryBean生产的Date类型的一个对象，如果想真正获取到TomorrowFactoryBean实例化后的Bean，**应该写成getBean("&tomorrowFactoryBean")**，即应该加上&号。