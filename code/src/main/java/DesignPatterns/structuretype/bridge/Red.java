package DesignPatterns.structuretype.bridge;

import javax.swing.*;

/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 14:54 2020/11/12
 **/
public class Red implements IColor {

    @Override
    public String getColor() {
        return "red";
    }
}
