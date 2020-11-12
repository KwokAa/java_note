package spring.test;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import spring.config.lifecycle.MainConfigJSR250;
import spring.config.lifecycle.MainConfigLifeCycle;
import spring.config.lifecycle.MainConfigLifeCycleIniBeanAndDisBean;
import spring.config.lifecycle.MainConfigProcessor;
import spring.config.propertyvalue.MainConfigPropertyValue;
import spring.model.Person;

/**
 * @Description: 测试IOC 属性赋值相关
 * @Author: GuoChangYu
 * @Date: Created in 0:01 2020/11/13
 **/
public class IOCTest_PropertyValue {


    @Test
    public void test01() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainConfigPropertyValue.class);
        System.out.println("容器创建完成---");
        Person person = (Person) context.getBean("person");
        System.out.println(person);

        //取出容器中环境变量的值
        ConfigurableEnvironment environment = context.getEnvironment();
        String property = environment.getProperty("person.nickName");
        System.out.println(property);


//        printBeans(context);
        //关闭容器
        context.close();
    }


    private void printBeans(AnnotationConfigApplicationContext configApplicationContext) {
        String[] beanDefinitionNames = configApplicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }
    }

}
