package spring.model;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @Description:  测试JSR250    @PostConstruct 和  @PreDestroy
 * @Author: GuoChangYu
 * @Date: Created in 0:34 2020/11/13
 **/

@Component
public class Dog {

    public Dog() {
        System.out.println("dog ---  construct ---");
    }

    /**
     * 对象创建成功并且赋值以后调用
     */
    @PostConstruct
    public void init() {
        System.out.println("dog --- PostConstruct ");
    }

    /**
     * 在容器移除对象之前调用
     */
    @PreDestroy
    public void destroy() {
        System.out.println("dog ---  PreDestroy");
    }
}
