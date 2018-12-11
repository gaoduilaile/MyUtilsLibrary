package cn.krvision.toolmodule;

import android.util.Log;

/**
 * 创建日期：2018/11/18
 * 描述: 日志工具类
 * 作者: gaoqiong
 */
public class LogUtils {
    private static boolean isDebug= true;

    //信息太长,分段打印
    public static void e(String tag, String msg) {
        if (isDebug){
            if (msg.length() > 4000) {
                for (int i = 0; i < msg.length(); i += 4000) {
                    if (i + 4000 < msg.length())
                        Log.e(tag, msg.substring(i, i + 4000));
                    else Log.e(tag, msg.substring(i, msg.length()));
                }
            } else {
                Log.e(tag, msg);
            }
        }
    }
}