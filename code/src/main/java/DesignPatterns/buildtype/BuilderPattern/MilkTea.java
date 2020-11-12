package DesignPatterns.buildtype.BuilderPattern;

/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 23:01 2020/11/11
 **/
public class MilkTea {

    private final String type;
    private final String size;
    private final boolean pearl;
    private final boolean ice;

    //设置构造方法为私有,外部不能通过MilkTea获取实例
    private MilkTea(Builder builder) {

        this.type = builder.type;
        this.size = builder.size;
        this.pearl = builder.pearl;
        this.ice = builder.ice;
    }

    public String getType() {
        return type;
    }

    public String getSize() {
        return size;
    }

    public boolean isPearl() {
        return pearl;
    }

    public boolean isIce() {
        return ice;
    }

    public static class Builder {
        private final String type;
        private String size = "中杯";
        private boolean pearl = true;
        private boolean ice = true;

        //对于必填的属性，通过Builder的构造方法传入
        public Builder(String type) {
            this.type = type;
        }

        //可选的属性，通过Builder的链式调用方法传入，如果不配置则选择默认
         public Builder size(String size) {
            this.size = size;
            return this;
        }

        public Builder pearl(boolean pearl) {
            this.pearl = pearl;
            return this;
        }

        public Builder ice(boolean ice) {
            this.ice = ice;
            return this;
        }

        //只能通过builde()获取MilkTea实例
        public MilkTea build() {
            return new MilkTea(this);
        }

    }

}
