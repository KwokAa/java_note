package spring.test;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import spring.config.*;
import spring.model.Blue;
import spring.model.Person;

import java.util.Map;

/**
 * @Description:  测试IOC 容器bean 注入相关
 * @Author: GuoChangYu
 * @Date: Created in 0:02 2020/11/12
 **/
public class IOCTest {
    /**
     * 测试包扫描规则 @ComponentScan
     */
    @Test
    public void testComponentScan() {
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(MainConfigComponentScan.class);
        //容器中所有的bean names
        String[] beanDefinitionNames = annotationConfigApplicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }
    }


    /**
     * 测试@scope 作用域
     */
    @Test
    public void testScope() {
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(MainConfigScope.class);

        System.out.println("ioc 容器创建完成----");
        //容器中所有的bean names
        String[] beanDefinitionNames = annotationConfigApplicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }
        Object person = annotationConfigApplicationContext.getBean("person");
        Object person2 = annotationConfigApplicationContext.getBean("person");
        //单实例模式 获取的bean相同 返回true
        System.out.println(person==person2);
    }


    /**
     * 测试@Lazy 懒加载
     */
    @Test
    public void testLazy() {
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(MainConfigLazy.class);
        System.out.println("ioc 容器创建完成----");
        //容器中所有的bean names
//        String[] beanDefinitionNames = annotationConfigApplicationContext.getBeanDefinitionNames();
//        for (String beanDefinitionName : beanDefinitionNames) {
//            System.out.println(beanDefinitionName);
//        }
        Object person = annotationConfigApplicationContext.getBean("person");
        Object person2 = annotationConfigApplicationContext.getBean("person");
        //单实例模式 获取的bean相同 返回true
        System.out.println(person==person2);
    }


    /**
     * 测试 @Conditional
     */
    @Test
    public void testConditional() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainConfigConditional.class);
        System.out.println("ioc 容器创建完成----");

        //获取ioc容器的运行环境
        ConfigurableEnvironment environment = context.getEnvironment();
        //动态获取环境变量的值   Windows 10
        String property = environment.getProperty("os.name");
        System.out.println(property);


        //根据类型取容器中的bean name
        String[] beanNamesForType = context.getBeanNamesForType(Person.class);
        for (String name : beanNamesForType) {
            System.out.println(name);
        }

        //获取所有 容器内bean 的对象  ，key 为bean name
        Map<String, Person> beansOfType = context.getBeansOfType(Person.class);
        System.out.println(beansOfType);

    }

    @Test
    public void testImport() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainConfigImport.class);
        printBeans(context);
        Blue blue = context.getBean(Blue.class);
        System.out.println(blue);
    }


    @Test
    public void testFactoryBean() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainConfigFactoryBean.class);

        //工厂bean 获取的是调用getObject创建的对象  bean的类型：class spring.model.Color
        Object colorFactoryBean = context.getBean("colorFactoryBean");
        Object colorFactoryBean2 = context.getBean("colorFactoryBean");

        Object colorFactoryBean3 = context.getBean("colorFactoryBean");


        System.out.println("bean的类型："+colorFactoryBean.getClass());
        System.out.println("bean的类型："+colorFactoryBean2.getClass());
        System.out.println("bean的类型："+colorFactoryBean3.getClass());

        //产生多个bean,改动 ColorFactoryBean 中 isSingleton（）的返回值控制单例和多例
        //单例生成的多个对象地址一样，多例下面打印的对象地址不一样
        System.out.println("colorFactoryBean："+colorFactoryBean);
        System.out.println("colorFactoryBean2:"+colorFactoryBean2);
        System.out.println("colorFactoryBean3："+colorFactoryBean3);


        //如果只想要 ColorFactoryBean的对象，而不是getObject（）的返回值，对象名前面加  &
        // 为什么加&  查看BeanFactory 源码
        Object colorFactoryBean4 = context.getBean("&colorFactoryBean");
        //打印结果 class spring.factory.ColorFactoryBean
        System.out.println(colorFactoryBean4.getClass());



    }


    /**
     * 打印容器中所有的 beans
     * @param context
     */
    private void printBeans(AnnotationConfigApplicationContext context) {
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }
    }


}
