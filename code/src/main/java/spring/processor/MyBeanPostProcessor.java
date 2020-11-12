package spring.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;


/**
 * @Description: 后置处理器，在bean初始化前后工作
 * @Author: GuoChangYu
 * @Date: Created in 0:57 2020/11/13
 **/
@Component //将后置处理器加入容器
public class MyBeanPostProcessor  implements BeanPostProcessor {
    /**
     *
     * @param bean 容器中刚创建的bean实例
     * @param beanName  bean的名字
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("postProcessBeforeInitialization----"+beanName+"--->>"+bean);
        return bean;
    }

    /**
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("postProcessAfterInitialization----"+beanName+"--->>"+bean);
        return bean;
    }

}
