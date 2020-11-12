package DesignPatterns.buildtype.FactoryMethodPattern;

import DesignPatterns.buildtype.model.Fruit;
import DesignPatterns.buildtype.model.Pear;

/**
 * @Description: 梨工厂
 * @Author: GuoChangYu
 * @Date: Created in 19:26 2020/11/11
 **/
public class PearFactory {
    public Fruit create() {
        return new Pear();
    }
}
