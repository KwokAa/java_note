package DesignPatterns.structuretype.adapter;

/**
 * @Description:  适配器
 * @Author: GuoChangYu
 * @Date: Created in 14:42 2020/11/12
 **/
public class VoltAdapter {

    //适配过程 ，操作可以很复杂，此处简化
    int convert(int homeVolt) {
        int chargeVolt = homeVolt - 215;
        return chargeVolt;
    }

}
