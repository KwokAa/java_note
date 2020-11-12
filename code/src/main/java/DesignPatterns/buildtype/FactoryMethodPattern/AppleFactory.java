package DesignPatterns.buildtype.FactoryMethodPattern;

import DesignPatterns.buildtype.model.Apple;
import DesignPatterns.buildtype.model.Fruit;

/**
 * @Description: 苹果工厂  模拟工厂方法模式
 * @Author: GuoChangYu
 * @Date: Created in 19:20 2020/11/11
 **/
public class AppleFactory {
    public Fruit create() {
        return new Apple();
    }
}
