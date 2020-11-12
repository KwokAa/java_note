package DesignPatterns.buildtype.singleton;

/**
 * @Description: 饿汉 单例模式
 *   1.静态私有单例对象，保证在调用 getInstance 方法之前单例已经存在
 *   2.构造函数私有
 *   3.外部调用 public getInstance 获取对象
 *
 * @Author: GuoChangYu
 * @Date: Created in 16:37 2020/11/2
 **/
public class HungrySingleton {
    //类一旦加载就创建一个单例，保证在调用 getInstance 方法之前单例已经存在了。
    private static HungrySingleton instange = new HungrySingleton();

    private HungrySingleton() {

    }


    public static HungrySingleton getInstance() {
        return instange;
    }

    public static void main(String[] args) {
        System.out.println("hungrySingleton:" + HungrySingleton.getInstance());
    }

}
