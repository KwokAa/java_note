package DesignPatterns.buildtype.AbstractFactoryPattern;

import DesignPatterns.buildtype.model.Fruit;
import org.junit.Test;

/**
 * @Description: 调用抽象工厂方法
 * @Author: GuoChangYu
 * @Date: Created in 19:40 2020/11/11
 **/
public class AbstractFactoryUser {

    @Test
    public void eat() {
        IFactory appleFactory = new AppleFactory();
        Fruit apple = appleFactory.create();
        IFactory pearFactory = new PearFactory();
        Fruit pear = pearFactory.create();
        apple.eat();
        pear.eat();

    }
}
