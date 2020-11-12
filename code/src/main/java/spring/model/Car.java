package spring.model;

import java.util.Calendar;

/**
 * @Description: 测试对象的生命周期
 * @Author: GuoChangYu
 * @Date: Created in 23:57 2020/11/12
 **/
public class Car {

    /**
     * 无参构造器
     */
    public Car() {
        System.out.println("car constructor ---");
    }


    public void init() {
        System.out.println("car --- init ----");
    }

    public void destroy() {
        System.out.println("cat ---- destroy ---");
    }


}
