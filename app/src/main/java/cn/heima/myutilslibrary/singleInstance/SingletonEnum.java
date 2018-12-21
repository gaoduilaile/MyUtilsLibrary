package cn.heima.myutilslibrary.singleInstance;

/**
 * Created by gaoqiong on 2018/1/31  枚举单例
 *
 *
 * 上面的类Resource是我们要应用单例模式的资源，具体可以表现为网络连接，数据库连接，线程池等等。
 */

public enum SingletonEnum {
    INSTANCE;
    private Resource instance;

    SingletonEnum() {
        instance = new Resource();
    }

    public Resource getInstance() {
        return instance;
    }

}
