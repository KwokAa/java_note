package DesignPatterns.buildtype.SimpleFactoryPattern;

/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 2:15 2020/11/2
 **/
public class ColorSimpleFactory {

    public static Color makeColor(String type) {
        if ("Red".equals(type)) {
            Color color = new Red();
            return color;
        }else if ("Blue".equals(type)){
            Color color = new Blue();
            return color;
        }else {
            return null;
        }
    }
    public static void main(String[] args) {
        ColorSimpleFactory.makeColor("Red").display();
        ColorSimpleFactory.makeColor("Blue").display();
//        ColorSimpleFactory.makeColor("dd").display();
    }
}
