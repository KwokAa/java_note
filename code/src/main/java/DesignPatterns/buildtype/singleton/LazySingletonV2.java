package DesignPatterns.buildtype.singleton;

/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 19:48 2020/11/11
 **/
public class LazySingletonV2 {

    /**
     * 私有静态实例
     */
    private static LazySingletonV2 singleton = null;

    /**
     * 私有构造方法
     */
    private LazySingletonV2() {

    }

    public static LazySingletonV2 getSingleton() {
        //下述代码的懒汉式单例乍一看没什么问题，但其实它不是线程安全的。
        // 如果有多个线程同一时间调用 getInstance 方法，instance 变量可能会被实例化多次。为了保证线程安全，我们需要给判空过程加上锁
        //同步化之前再加一层验证，避免当多个线程调用 getInstance 时，每次都需要执行 synchronized 同步化方法，严重影响程序的执行效率
        if (singleton == null) {
            synchronized (LazySingletonV2.class) {
                if (singleton == null) {
                    singleton = new LazySingletonV2();
                }
            }
        }
        return singleton;
    }


}
