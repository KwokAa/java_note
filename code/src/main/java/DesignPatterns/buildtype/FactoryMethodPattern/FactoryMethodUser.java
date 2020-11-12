package DesignPatterns.buildtype.FactoryMethodPattern;

import DesignPatterns.buildtype.FactoryMethodPattern.AppleFactory;
import DesignPatterns.buildtype.FactoryMethodPattern.PearFactory;
import DesignPatterns.buildtype.model.Fruit;
import org.junit.Test;

/**
 * @Description: 调用者---调用工厂方法
 * @Author: GuoChangYu
 * @Date: Created in 19:27 2020/11/11
 **/
public class FactoryMethodUser {
    @Test
    public void eat() {
        AppleFactory appleFactory = new AppleFactory();
        Fruit apple = appleFactory.create();
        PearFactory pearFactory = new PearFactory();
        Fruit pear = pearFactory.create();
        apple.eat();
        pear.eat();
    }
}

