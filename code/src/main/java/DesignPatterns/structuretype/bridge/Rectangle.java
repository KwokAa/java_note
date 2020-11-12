package DesignPatterns.structuretype.bridge;

import javax.swing.*;

/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 14:56 2020/11/12
 **/
public class Rectangle implements IShape {

    //形状类中桥接 IColor 接口
    private IColor color;

    void setColor(IColor color) {
        this.color = color;
    }

    @Override
    public void draw() {
        System.out.println("绘制矩形" + "颜色："+color.getColor());
    }
}
