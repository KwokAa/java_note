package spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import spring.condition.MyImportBeanDefinitionRegistrar;
import spring.condition.MyImportSelector;
import spring.model.Color;
import spring.model.Person;
import spring.model.Red;

/**
 * @Description: 测试@import
 *
 * 给容器中加入组件的方式：
 * 1.包扫描+组件注解标注（@Controller,@Service,@Repository,@Component）自己写的类，可以随便加
 * 2.@Bean[导入的第三方包的组件]
 * 3.@Import[快速的给容器中导入一个组件]
 *    1).@Import(要导入到容器中的组件)，容器中就会自动注册这个组件，id默认是全类名
 *    2）.ImportSelector:返回需要导入的组件的全类名数组
 *    3).ImportBeanDefinitionRegistrar: 手动注册bean到容器中
 *
 *  4.使用Spring提供的FactoryBean（工厂Bean）
 *     1).默认获取得到的是工厂bean调用getObject创建的对象
 *     2）.要获取工厂Bean本身，我们需要给id前面加一个&
 *          &colorFactoryBean
 *
 *
 *
 * @Author: GuoChangYu
 * @Date: Created in 22:49 2020/11/12
 **/
@Configuration
@Import({Color.class, Red.class, MyImportSelector.class, MyImportBeanDefinitionRegistrar.class})   //导入组件，id默认是组件的全类名 spring.model.Color   spring.model.Red
public class MainConfigImport {

//    @Bean("person")
//    public Person person() {
//        System.out.println("给容器中添加Person---");
//        return new Person("zhansan", 23);
//    }


}
