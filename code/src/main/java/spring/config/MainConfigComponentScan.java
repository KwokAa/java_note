package spring.config;

import org.springframework.context.annotation.*;
import spring.filter.MyTypeFilter;
import spring.model.Person;

/**
 * @Description: 配置类 == 以前的配置文件
 * @Author: GuoChangYu
 * @Date: Created in 23:33 2020/11/11
 **/
@Configuration //告诉spring这是一个配置类
//@ComponentScan(value = "spring",excludeFilters = {
//        @ComponentScan.Filter(type = FilterType.ANNOTATION,classes = {Controller.class, Service.class} )  //type选择排除规则,type = FilterType.ANNOTATION,classes = Controller.class表示排除@Controler标注的类
//})
//@ComponentScan 包扫描  value:指定要扫描的包
// excludeFilters :Filter[]  包扫描的时候排除什么组件
// includeFilters :Filter[]  包扫描的时候只需要包含什么组件, 注意useDefaultFilters = false

//Filter 规则
//FilterType.ANNOTATION：按照注解
//FilterType.ASSIGNABLE_TYPE:按照指定类型，例如下例BookDao，不管他的子类还是实现类都会加入到容器中
//FilterType.ASPECTJ, 使用ASPECTJ表达式  不常用
//FilterType.REGEX, 使用正则表达式
//FilterType.CUSTOM; 使用自定义规则
@ComponentScan(value = "spring", includeFilters = {
//        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {Controller.class, Service.class}),
//        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = {BookDao.class}),
        @ComponentScan.Filter(type = FilterType.CUSTOM, classes = {MyTypeFilter.class})

},useDefaultFilters = false)

//注意@ComponentScan  中有@Repeatable 标识@ComponentScan可以写多个
// 或者使用 @ComponentScans(value = {@ComponentScan(),@ComponentScan()}) 指定多个ComponentScan规则
public class MainConfigComponentScan {

    //给容器中注册一个bean,类型为方法返回值，id默认用方法名作为id，可以指定value字段确定bean的名字
    @Bean("person")
    public Person person01() {
        return new Person("lisi", 20,"");
    }
}
