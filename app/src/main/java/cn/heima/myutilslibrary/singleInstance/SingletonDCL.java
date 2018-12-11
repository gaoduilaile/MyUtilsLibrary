package cn.heima.myutilslibrary.singleInstance;

/**
 * Created by gaoqiong on 2018/7/11 双重检查锁定
 */

public class SingletonDCL {
    private static SingletonDCL instance = null;

    private SingletonDCL() {
    }

    public static SingletonDCL getInstance() {
        if (instance == null) {
            synchronized (SingletonDCL.class) {
                if (instance == null) {
                    instance = new SingletonDCL();
                }
            }
        }

        return instance;
    }

}
