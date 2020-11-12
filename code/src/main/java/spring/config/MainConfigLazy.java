package spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import spring.model.Person;

/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 0:56 2020/11/12
 **/
@Configuration
public class MainConfigLazy {

    /**
     *
     * 懒加载
     *      单实例bean,默认在容器启动时候创建对象
     *       懒加载：容器启动时不创建对象，第一次使用（获取）bean创建对象并且初始化
     *       第二次使用bean创建对象
     * @return
     */

    @Lazy
    @Bean("person")
    public Person person() {
        System.out.println("给容器中添加Person---");
        return new Person("zhansan", 23,"");
    }
}
