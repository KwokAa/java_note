package spring.config.lifecycle;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import spring.model.Car;

/**
 * @Description:
 * bean的生命周期
 *        bean的创建 --- 初始化 --- 销毁的过程
 *  容器管理Bean的生命周期；
 *  我们可以自定义初始化和销毁方法；容器在bean进行到当前生命周期的时候来调用我们自定义的初始化和销毁方法
 *
 *  构造（对象创建）
 *       单实例：在容器启动时候创建对象
 *       多实例：在每次获取时候创建对象
 *
 *   初始化
 *        对象创建完成后，并赋值好以后，调用初始化方法
 *
 *    销毁
 *         单实例：容器关闭的时候
 *         多实例：容器不会管理这个bean,容器不会调用销毁方法
 *
 *
 *  1.指定初始化和销毁方法：
 *      以前xml: 指定 init-method="" destroy-method=""
 *      现在@Bean 通用指定 @Bean(initMethod = "init", destroyMethod = "destroy")
 *
 *  2. 通过让Bean 实现 InitializingBean(定义初始化逻辑)
 *                     DisposableBean(定义销毁逻辑)
 *
 *
 *
 *
 *
 *
 * @Author: GuoChangYu
 * @Date: Created in 23:53 2020/11/12
 **/
@ComponentScan(value = "spring.model")
@Configuration
public class MainConfigLifeCycleIniBeanAndDisBean {

}
