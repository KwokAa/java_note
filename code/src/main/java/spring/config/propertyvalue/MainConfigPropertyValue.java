package spring.config.propertyvalue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import spring.model.Person;

/**
 * @Description: 属性赋值
 *
 *
 *
 *
 *
 * @Author: GuoChangYu
 * @Date: Created in 1:14 2020/11/13
 **/

//使用 @PropertySource 读取外部配置文件中的k/v map 保存到运行的环境变量中;加载完外部的配置文件后使用${}取出配置文件中的值
    //可以指定 @PropertySources 里面写多个@PropertySource
    //@PropertySource 中是数组可以写多个配置文件
//@PropertySources(value = {@PropertySource(value = ""),@PropertySource(value = "")})
@PropertySource(value = {"classpath:/person.properties"})

@Configuration
public class MainConfigPropertyValue {

    @Bean
    public Person person() {
        return new Person();
    }

}
