package DesignPatterns.buildtype.FactoryMethodPattern;

import DesignPatterns.buildtype.model.Fruit;
import org.junit.Test;


/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 19:32 2020/11/11
 **/
public class UserTest {
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