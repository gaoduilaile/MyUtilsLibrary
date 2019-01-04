package cn.krvision.toolmodule.api;

import android.content.Context;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.krvision.toolmodule.utils.LogUtils;
import cn.krvision.toolmodule.utils.MyUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 创建日期：2018/11/12
 * 描述:rx + retrofit封装网络请求框架
 * 作者: gaoqing
 */
public class RetrofitClient {

    public static String TAG = "HttpUtils";
    private static final int DEFAULT_TIMEOUT = 10;
    private static Context mContext;

    public static ApiService apiService;

    //单例
    public static ApiService getApiServiceInstance() {
        if (apiService == null) {
            synchronized (RetrofitClient.class) {
                if (apiService == null) {
                    getApiService();
                }
            }
        }
        return apiService;
    }

    private static void getApiService() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        //连接失败后是否重新连接,并设置超时时间,读写时间
        httpClientBuilder.retryOnConnectionFailure(true)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        //添加拦截器 设置公参
        OkHttpClient client = httpClientBuilder.addInterceptor(new RetrofitInterceptor()).build();
//        OkHttpClient client = httpClientBuilder.build();

        //获取Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpConstant.BASE_URL)  //设置baseUrl,注意，baseUrl必须后缀"/"
                .addConverterFactory(GsonConverterFactory.create())   //添加Gson转换器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
        //获取ApiService
        apiService = retrofit.create(ApiService.class);
    }

    /**
     * 封装线程管理和订阅的过程
     */
    public static <T> Observable<T> ApiSubscribe(Observable<T> observable) {
        Observable<T> tObservable = observable
                .subscribeOn(Schedulers.newThread())//这里需要注意的是，网络请求在非ui线程。如果返回结果是依赖于Rxjava的，则需要变换线程
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                .observeOn(AndroidSchedulers.mainThread());
        return tObservable;
    }

    public static RequestBody getRequestBody(Object t) {
        Gson gson = new Gson();
        String toJson = gson.toJson(t);////通过Gson将Bean转化为Json字符串形式
        LogUtils.e(TAG, "getRequestBody: " + toJson);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), toJson);
        return requestBody;
    }


    public static RequestBody getRequestBodyFromMap(Map<String, Object> map, Context mContext) {
        String version = MyUtils.getAppVersionName(mContext);
        int timestamp = MyUtils.getTimetemp();
        String md5 = RetrofitClient.md5(version + String.valueOf(timestamp) + "Android");
        map.put("access_token", md5);
        map.put("timestamp", timestamp);
        map.put("version", version);
        map.put("mobile_type", "Android");
        Gson gson = new Gson();
        String toJson = gson.toJson(map);////通过Gson将Bean转化为Json字符串形式
        LogUtils.e(TAG, "getRequestBody: " + toJson);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), toJson);
        return requestBody;
    }

    /**
     * 计算字符串MD5值
     *
     * @param string 拼接字符串
     * @return
     */
    public static String md5(String string) {
        String result;
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update((string).getBytes("UTF-8"));
            byte b[] = md5.digest();

            int i;
            StringBuffer buf = new StringBuffer("");

            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString().toUpperCase();
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 解析json数据
     */
    public static Object responseBodyToJavaBean(ResponseBody responseBody, Class<? extends Object> obj) {
        String string;
        try {
            string = responseBody.string();
            LogUtils.e("responseBodyToJavaBean:", string);
            Gson gson = new Gson();
            Object data = gson.fromJson(string, obj);
//            Log.e("getResponseBody:", data.toString());
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
