package spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import spring.model.Person;

/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 0:56 2020/11/12
 **/
@Configuration
public class MainConfigScope {

    /**
     * 默认单实例
     * prototype 多实例  多实例情况下，ioc容器启动并不会调用方法创建对象放在容器中，
     *                   每次获取的时候才会调用方法创建对象放入容器中，每次生成新的对象
     * singleton(默认)  单实例情况下，ioc容器启动时就会调用方法将bean加入到容器中，
     *                  以后每次获取都只从容器中拿（可以理解为map.get()），所以对象只有一个，多次获取都相同
     * request （同一次请求创建一个实例）
     * session （同一个session创建一个实例）
     *
     *
     * 懒加载
     *      单实例bean,默认在容器启动时候创建对象
     *       懒加载：容器启动时不创建对象，第一次使用（获取）bean创建对象并且初始化
     *

     * @return
     */
    @Scope("singleton")   // 调整作用域
    @Bean("person")
    public Person person() {
        System.out.println("给容器中添加Person---");
        return new Person("zhansan", 23,"3");
    }
}
