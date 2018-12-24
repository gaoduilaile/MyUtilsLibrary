package cn.krvision.toolmodule.api;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Observer;

/**
 * 创建日期：2018/11/12
 * 描述: 接口工具类
 * 作者: gaoqing
 */
public class ModelUtils {

    /**
     * 下载一级菜单
     */
    public static void downloadfirstclassmenu(Context context,Observer<ResponseBody> observer) {
        Map<String, Object> map = new HashMap<>();
        Observable<ResponseBody> observable = RetrofitClient.getApiServiceInstance().downloadfirstclassmenu(RetrofitClient.getRequestBodyFromMap(map,context));
        RetrofitClient.ApiSubscribe(observable).subscribe(observer);
    }

    public static void uploadclassbutton(Context context,String last_class_name,Observer<ResponseBody> observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("class_name", last_class_name);
        Observable<ResponseBody> observable = RetrofitClient.getApiServiceInstance().uploadclassbutton(RetrofitClient.getRequestBodyFromMap(map,context));
        RetrofitClient.ApiSubscribe(observable).subscribe(observer);
    }



}
