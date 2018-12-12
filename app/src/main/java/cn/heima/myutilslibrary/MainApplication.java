package cn.heima.myutilslibrary;

import android.app.Application;
import android.content.Context;

public class MainApplication extends Application {

    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        CrashHandler.getInstance().init();
    }
}
