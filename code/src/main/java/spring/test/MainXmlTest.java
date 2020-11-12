package spring.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import spring.model.Person;

/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 23:31 2020/11/11
 **/
public class MainXmlTest {
    public static void main(String[] args) {

        //通过配置的xml文件获取上下文
        ApplicationContext context =  new ClassPathXmlApplicationContext("beans.xml");
        Person person = (Person) context.getBean("person");
        System.out.println(person);
    }
}
