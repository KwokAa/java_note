package DesignPatterns.singleton;

/**
 * @Description: 懒汉式 单例模式  只有当调用getInstance的时候，才回去初始化这个单例。
 *  1.定义构造函数为私有 2.定义一个私有静态实例  3.向外提供静态方法用户创建或者获取私有静态实例
 * @Author: GuoChangYu
 * @Date: Created in 16:19 2020/11/2
 **/
public class LazySingleton {
    // volatile保证线程安全
    private static volatile LazySingleton instance = null;

    /**
     *构造方法私有化，避免类在外部被实例化
     */
    private LazySingleton() {
        System.out.println("执行构造方法");
    }

    /**
     *  向外提供静态方法获取实例
     *  使用的同步锁，降低了效率，但是在多线程可以防止创建多个实例
     * @return
     */
    public static synchronized LazySingleton getInstance(){
        if(instance == null){
            instance = new LazySingleton();
        }
        return instance;
    }

    public void show(){
        System.out.println("我是singleton:"+instance.toString());
    }



    public static void main(String[] args) {

//        System.out.println(new LazySingleton().toString());
        LazySingleton lazySingleton = LazySingleton.getInstance();
        lazySingleton.show();

    }


}
