package cn.krvision.toolmodule.mediaPlayer;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

/**
 * Created by gaoqiong on 2018/12/11 11:27
 * Description:$description$
 */
public class HttpProxyCacheUtil {


    private static class SingletonHodler {
        public static HttpProxyCacheUtil instance = new HttpProxyCacheUtil();
    }

    public static HttpProxyCacheUtil getInstance() {
        return SingletonHodler.instance;
    }

    public static HttpProxyCacheServer getHttpProxyCacheServer(Context context) {
        return new HttpProxyCacheServer(context);
    }






}
