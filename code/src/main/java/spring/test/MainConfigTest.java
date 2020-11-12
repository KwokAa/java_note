package spring.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import spring.config.MainConfigComponentScan;
import spring.model.Person;

/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 23:38 2020/11/11
 **/
public class MainConfigTest {
    public static void main(String[] args) {
        //通过注解 读取配置文件方式获取上下文
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigComponentScan.class);
        Person person = applicationContext.getBean(Person.class);
        System.out.println(person);

        //按类型找到bean的名字
        String[] beanNamesForType = applicationContext.getBeanNamesForType(Person.class);
        //快捷键iter 自动生成for循环
        for (String name : beanNamesForType) {
            System.out.println(name);
        }

    }
}
