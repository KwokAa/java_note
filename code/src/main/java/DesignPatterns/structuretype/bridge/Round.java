package DesignPatterns.structuretype.bridge;

/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 14:57 2020/11/12
 **/
public class Round implements IShape {

    private IColor color;

    public void setColor(IColor color) {
        this.color = color;
    }

    @Override
    public void draw() {
        System.out.println("绘制圆形" + "颜色："+color.getColor());
    }
}
