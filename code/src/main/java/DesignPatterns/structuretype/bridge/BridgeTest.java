package DesignPatterns.structuretype.bridge;

import org.junit.Test;

/**
 * @Description:  通过组合方式，桥接 颜色和形状，
 * 桥接模式主要用于两个或者多个同等级的接口 ， 比如本例中的IColor 和 IShape
 * @Author: GuoChangYu
 * @Date: Created in 15:01 2020/11/12
 **/
public class BridgeTest {

    @Test
    public void drawTest() {
        Rectangle rectangle = new Rectangle();
        rectangle.setColor(new Red());
        rectangle.draw();

        Round round = new Round();
        round.setColor(new Blue());
        round.draw();


        Triangle triangle = new Triangle();
        triangle.setColor(new Green());
        triangle.draw();
    }
}
