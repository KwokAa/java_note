package DesignPatterns.buildtype.prototype;

import java.util.Arrays;

/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 13:36 2020/11/12
 **/
public class MilkTeaPrototype implements Cloneable {
    private String type;
    private boolean ice;
    private int[] others;
    private Water water;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isIce() {
        return ice;
    }

    public void setIce(boolean ice) {
        this.ice = ice;
    }

    public int[] getOthers() {
        return others;
    }

    public void setOthers(int[] others) {
        this.others = others;
    }

    public Water getWater() {
        return water;
    }

    public void setWater(Water water) {
        this.water = water;
    }

    /**
     * 手写clone方法实现 prototype 模式
     *
     * @return
     */
    protected MilkTeaPrototype cloneMaunal() {
        MilkTeaPrototype milkTeaPrototype = new MilkTeaPrototype();
        milkTeaPrototype.ice = this.ice;
        milkTeaPrototype.type = this.type;
        //初始化数组
        int[] others = new int[this.others.length];
        //复制数组
        System.arraycopy(this.others, 0, others,0, others.length);
        milkTeaPrototype.setOthers(others);

        // model
        Water water = new Water();
        water.setHeat(this.water.isHeat());
        milkTeaPrototype.setWater(water);

        return milkTeaPrototype;
    }

    /**
     * 实现Object类的clone方法  implentments Cloneable
     * 是浅拷贝的，只有基本类型对象的参数会被拷贝一份，而非基本类型对象仍然使用的是值传递引用方式
     * 如果 需要深拷贝，需要在clone方法内重写，例如 int[] others
     *
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    protected MilkTeaPrototype clone() throws CloneNotSupportedException {
        //默认浅拷贝直接返回即可
//        return (MilkTeaPrototype) super.clone();
        //实现深拷贝
        MilkTeaPrototype milkTeaPrototype = (MilkTeaPrototype) super.clone();
        //初始化数组
        int[] others = new int[this.others.length];
        //复制数组
        System.arraycopy(this.others, 0, others,0, others.length);
        milkTeaPrototype.setOthers(others);

        // model 深拷贝
        Water water = (Water) this.water.clone();
        milkTeaPrototype.setWater(water);
        return milkTeaPrototype;
    }

    @Override
    public String toString() {
        return "MilkTeaPrototype{" +
                "type='" + type + '\'' +
                ", ice=" + ice +
                ", others=" + Arrays.toString(others) +
                ", water=" + water +
                '}';
    }
}
