package DesignPatterns.structuretype.composite;

/**
 * @Description: 将员工和领导作为同一个组件整体对待，抽象出一个组件类，
 * @Author: GuoChangYu
 * @Date: Created in 16:03 2020/11/12
 **/
public abstract class Component {
    //职位
    private String position;
    //工作内容
    private String job;


    /**
     * 构造函数
     * @param position
     * @param job
     */
    public Component(String position, String job) {
        this.position = position;
        this.job = job;
    }

    /**
     * 做自己的本职工作
     */
    public void work() {
        System.out.println("我是+" + position + ",我正在" + job);
    }

    abstract void addComponent(Component component);

    abstract void removeComponent(Component component);

    abstract void check();




}
