package spring.condition;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import spring.model.RainBow;

/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 23:16 2020/11/12
 **/
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    /**
     *
     * @param importingClassMetadata 当前类的注解信息
     * @param registry
     * @param importBeanNameGenerator    BeanDefinition注册类
     *     把所有需要添加进容器中的bean;
     *     调用BeanDefinitionRegistry.registerBeanDefinition 手工注册进来
     *
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {

//        spring.model.Blue
//        spring.model.Yellow
        //注意import之前导入的bean 为全类名
//        boolean red = registry.containsBeanDefinition("red");
//        boolean blue = registry.containsBeanDefinition("blue");
        boolean red = registry.containsBeanDefinition("spring.model.Yellow");
        boolean blue = registry.containsBeanDefinition("spring.model.Blue");
        if (red && blue) {
            //如果容器中有red 和blue

            // 指定 beanDefinition  bean的定义信息（Bean的类型，作用域scope等等）
            BeanDefinition beanDefinition = new RootBeanDefinition(RainBow.class);
            //注册一个bean 并且 指定bean 名
            registry.registerBeanDefinition("rainBow", beanDefinition);
        }
    }

}
