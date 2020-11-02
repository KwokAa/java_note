package DesignPatterns.singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 单例模式扩展多例模式
 * @Author: GuoChangYu
 * @Date: Created in 16:40 2020/11/2
 **/
public class MultiSingleton {

    private static volatile List<MultiSingleton> singletons = new ArrayList<MultiSingleton>();

    private static final int n = 10; //多例数

    static {
        for (int i = 0; i < n; i++) {
            singletons.add(new MultiSingleton(i));
        }
    }
    private MultiSingleton(int n) {

    }

    public static synchronized MultiSingleton getInstance() {
        int value = (int) (Math.random() * n);
        return singletons.get(value);
    }

    public static void main(String[] args) {
        System.out.println("multiSingleton:"+getInstance());
    }

}
