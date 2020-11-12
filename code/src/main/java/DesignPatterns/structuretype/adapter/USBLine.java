package DesignPatterns.structuretype.adapter;

/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 14:36 2020/11/12
 **/
public class USBLine {
    void charge(int volt) {
        if (volt != 5) {
            throw new IllegalArgumentException("只能接受5V电压");
        }
        //如果电压是5V,正常充电
        System.out.println("正常充电");
    }

}
