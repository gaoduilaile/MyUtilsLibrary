package cn.heima.myutilslibrary.singleInstance;

/**
 * Created by gaoqiong on 2018/7/11  静态内部类单例设计模式
 */

public class Singleton {
    private Singleton() {
    }
    /**
     *
     *
     *
     *
     * @return 静态内部类
     */
    public static Singleton getInstance() {

        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final Singleton instance = new Singleton();
    }
}
