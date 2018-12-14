package cn.heima.myutilslibrary;

import android.app.Application;
import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;

import cn.krvision.toolmodule.CrashHandler;

public class MainApplication extends Application {

    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        CrashHandler.getInstance().init();

        //注册路由
        ARouter.init(MainApplication.this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //销毁路由
        ARouter.getInstance().destroy();
    }
}
