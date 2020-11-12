package spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import spring.factory.ColorFactoryBean;

/**
 * @Description: 测试通过 使用Spring提供的FactoryBean（工厂Bean） 添加实例到容器中
 * @Author: GuoChangYu
 * @Date: Created in 23:36 2020/11/12
 **/
@Configuration
public class MainConfigFactoryBean {


    /**
     * 实际注册的是 colorFactoryBean中getObject()方法的返回对象
     * @return
     */
    @Bean
    public ColorFactoryBean colorFactoryBean() {
        return new ColorFactoryBean();
    }
}
