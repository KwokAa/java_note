package DesignPatterns.buildtype.AbstractFactoryPattern;

import DesignPatterns.buildtype.model.Apple;
import DesignPatterns.buildtype.model.Fruit;

/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 19:37 2020/11/11
 **/
public class AppleFactory implements IFactory {
    @Override
    public Fruit create() {
        System.out.println("apple---create---");
       return new Apple();
    }
}
