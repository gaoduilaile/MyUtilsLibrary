package cn.heima.myutilslibrary;


import cn.krvision.toolmodule.LogUtils;
import cn.krvision.toolmodule.MyUtils;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static CrashHandler sInstance = null;
    public static CrashHandler getInstance() {
        if (sInstance == null) {
            synchronized (CrashHandler.class) {
                if (sInstance == null) {
                    synchronized (CrashHandler.class) {
                        sInstance = new CrashHandler();
                    }
                }
            }
        }
        return sInstance;
    }

    private CrashHandler() {
    }

    /**
     * 初始化默认异常捕获
     */
    public void init() {
        LogUtils.intAppLog(MyUtils.getTimeString());
        // 将此类设为默认异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        LogUtils.saveAppLog(t.toString() + "  " + e.toString());
    }


}
