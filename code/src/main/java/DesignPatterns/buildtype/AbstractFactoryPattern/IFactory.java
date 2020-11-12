package DesignPatterns.buildtype.AbstractFactoryPattern;

import DesignPatterns.buildtype.model.Fruit;

/**
 * @Description: 提取出工厂接口
 * @Author: GuoChangYu
 * @Date: Created in 19:35 2020/11/11
 **/
public interface IFactory {
    public Fruit create();

}
