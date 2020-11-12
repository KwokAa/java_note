package DesignPatterns.buildtype.BuilderPattern;

import org.junit.Test;

/**
 * @Description: 建造者模式 调用者
 * @Author: GuoChangYu
 * @Date: Created in 23:12 2020/11/11
 **/
public class BuilderPatternUser {
    @Test
    public void buyMilkTea() {
        MilkTea milkTea = new MilkTea.Builder("原味").build();
        show(milkTea);

        MilkTea milkTea1 = new MilkTea.Builder("奶香").size("大杯").ice(true).pearl(true).build();
        show(milkTea1);

    }

    private void show(MilkTea milkTea) {
        System.out.println("点了一份奶茶，size:"+milkTea.getSize()+"--type"+milkTea.getType()+"---ice:"+milkTea.isIce()+"--pearl:"+milkTea.isPearl());
    }


}
