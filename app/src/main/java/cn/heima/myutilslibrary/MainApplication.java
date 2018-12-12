package cn.heima.myutilslibrary;

import android.app.Application;
import android.content.Context;

import cn.krvision.toolmodule.CrashHandler;

public class MainApplication extends Application {

    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        CrashHandler.getInstance().init();
    }
}
