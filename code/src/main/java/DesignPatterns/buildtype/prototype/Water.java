package DesignPatterns.buildtype.prototype;

/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 13:54 2020/11/12
 **/
public class Water implements Cloneable {
    private boolean heat;

    public boolean isHeat() {
        return heat;
    }

    public void setHeat(boolean heat) {
        this.heat = heat;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
