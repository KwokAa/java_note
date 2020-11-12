package spring.test;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import spring.config.lifecycle.MainConfigJSR250;
import spring.config.lifecycle.MainConfigLifeCycle;
import spring.config.lifecycle.MainConfigLifeCycleIniBeanAndDisBean;
import spring.config.lifecycle.MainConfigProcessor;

/**
 * @Description: 测试IOC 对象生命周期相关
 * @Author: GuoChangYu
 * @Date: Created in 0:01 2020/11/13
 **/
public class IOCTest_LifeCycle {


    @Test
    public void test01() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainConfigLifeCycle.class);
        System.out.println("容器创建完成---");
        Object car = context.getBean("car");
        System.out.println(car);
        //关闭容器
        context.close();
    }

    /**
     *  测试 InitializingBean(定义初始化逻辑) 和 DisposableBean(定义销毁逻辑)
     *
     */
    @Test
    public void test02() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainConfigLifeCycleIniBeanAndDisBean.class);
        System.out.println("容器创建完成---");
//        Object cat = context.getBean("cat");
//        System.out.println(cat);
        //关闭容器
        context.close();
    }

    /**
     * 测试 JSR250
     */
    @Test
    public void test03() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainConfigJSR250.class);
        System.out.println("容器创建完成---");
//        printBeans(context);
        context.close();
    }


    /**
     * 测试 BeanPostProcessor
     */
    @Test
    public void test04() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainConfigProcessor.class);
        System.out.println("容器创建完成---");
//        printBeans(context);
        context.close();
    }



    private void printBeans(AnnotationConfigApplicationContext configApplicationContext) {
        String[] beanDefinitionNames = configApplicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }
    }

}
