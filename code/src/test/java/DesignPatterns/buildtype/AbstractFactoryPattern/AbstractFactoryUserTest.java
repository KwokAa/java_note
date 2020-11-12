package DesignPatterns.buildtype.AbstractFactoryPattern;

import DesignPatterns.buildtype.model.Fruit;
import org.junit.Test;

/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 19:42 2020/11/11
 **/

public class AbstractFactoryUserTest {

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