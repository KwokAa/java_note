package DesignPatterns.buildtype.AbstractFactoryPattern;

import DesignPatterns.buildtype.model.Fruit;
import DesignPatterns.buildtype.model.Pear;

/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 19:39 2020/11/11
 **/
public class PearFactory implements IFactory {
    @Override
    public Fruit create() {
        System.out.println("pear---create----");
        return new Pear();
    }
}
