package DesignPatterns.structuretype.adapter;

import org.junit.Test;

/**
 *   适配器模式不推荐多用，未雨绸缪好过亡羊补牢，事先做好处理方式
 *   只有当源接口无法修改时候才考虑用适配器
 *
 * @Description: 使用家用电源和USB 数据线充电的用户
 * @Author: GuoChangYu
 * @Date: Created in 14:39 2020/11/12
 **/
public class BatterUser {

    @Test
    public void chargeForPhone() {
        //获取家用电压
        HomeBattery homeBattery = new HomeBattery();
        int homeVolt  = homeBattery.supply();
        System.out.println("家用电源提供的电压是：" + homeVolt);

        //添加适配器，将家用电压适配USB
        VoltAdapter voltAdapter = new VoltAdapter();
        int chargeVolt = voltAdapter.convert(homeVolt);
        System.out.println("适配器添加后，充电电压为：" + chargeVolt);

        //USB 数据线
        USBLine usbLine = new USBLine();
        usbLine.charge(chargeVolt);
    }

}


