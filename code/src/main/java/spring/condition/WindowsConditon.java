package spring.condition;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @Description: 是否windows 系统
 * @Author: GuoChangYu
 * @Date: Created in 1:46 2020/11/12
 **/
public class WindowsConditon implements Condition {

    /**
     *
     * @param conditionContext  判断条件能使用的上下文环境
     * @param annotatedTypeMetadata 注释信息
     * @return
     */
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        // 是否是Linux环境

        //1.能获取到IOC使用的bean工厂 beanFactory（用于创建对象以及分配的）
        ConfigurableListableBeanFactory beanFactory = conditionContext.getBeanFactory();

        //2. 也能获得类加载器
        ClassLoader classLoader = conditionContext.getClassLoader();

        //3.获取环境变量
        Environment environment = conditionContext.getEnvironment();

        // 获取bean定义的注册类 可以注册，查询，移除bean的定义
        BeanDefinitionRegistry registry = conditionContext.getRegistry();


        String property = environment.getProperty("os.name");
        if (property.contains("Windows")) {
            return true;
        }

        return false;
    }
}
