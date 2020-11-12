package spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import spring.condition.LinuxCondition;
import spring.condition.WindowsConditon;
import spring.model.Person;

/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 0:56 2020/11/12
 **/
@Configuration
public class MainConfigConditional {

    /**
     * @Conditional 按照一定条件进行判断，满足条件给容器中注册bean
     * 要求： 如果环境是windows 给容器中注入bill
     *        如果环境是Linux,给容器中注入linus
     *
     *
     *  可以标注在类上， 类中组件统一设置 标注在配置类上面表示只有满足条件，这个配置类里面的所有bean才会生效
     *  也可以标注在方法上，
     *  需要传Condition的实现类 数组
     *
     *   idea  run configure -> VM Options 设置  ->    -Dos.name=linux  模拟linux
     * @return
     */


    @Conditional({WindowsConditon.class})
    @Bean("bill")
    public Person person01() {
        System.out.println("给容器中添加bill---");
        return new Person("Bill gates", 23,"");
    }

    @Conditional({LinuxCondition.class})
    @Bean("linus")
    public Person person02() {
        System.out.println("给容器中添加Person---");
        return new Person("linus",232,"");
    }
}
