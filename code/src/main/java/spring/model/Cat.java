package spring.model;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @Description:   测试实现 InitializingBean 和 DisposableBean两个接口来控制对象的初始化和销毁过程
 * @Author: GuoChangYu
 * @Date: Created in 0:15 2020/11/13
 **/

@Component
public class Cat implements InitializingBean, DisposableBean {

    public Cat() {
        System.out.println("cat --- constructor ---");
    }

    /**
     * 在bean销毁后调用
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        System.out.println("cat --- destroy ---");

    }

    /**
     *
     * 在对象创建完成后，并且赋好值（就是调用过构造器的意思）后调用
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("cat --- afterPropertiesSet ---");

    }
}
