package cn.krvision.toolmodule.api;

import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.krvision.toolmodule.utils.LogUtils;
import cn.krvision.toolmodule.utils.MyUtils;
import cn.krvision.toolmodule.bean.BaseBean;
import cn.krvision.toolmodule.bean.ConmentStandardBean;
import cn.krvision.toolmodule.bean.WeatherBean;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by gaoqiong on 2018/12/12 11:29
 * Description:$description$
 */
public class HttpModel {
    public interface HttpModelFunc {
        void downloadSuccess();

        void downloadFailure(String message);
    }

    public void downloadfirstclassmenu(Context context) {
        ModelUtils.downloadfirstclassmenu(context,new Observer<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                BaseBean bean = (BaseBean) RetrofitClient.responseBodyToJavaBean(responseBody, BaseBean.class);
            }
        });
    }

    public void uploadclassbutton(Context context,String last_class_name) {
        ModelUtils.uploadclassbutton(context,last_class_name, new Observer<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {

                ConmentStandardBean bean = (ConmentStandardBean) RetrofitClient.responseBodyToJavaBean(responseBody, ConmentStandardBean.class);

            }
        });
    }

    public void getWeather(Activity mContext) {

        String weatherString = MyUtils.readAssetsTxt(mContext, "weather.txt");

        String locationCity = MyUtils.beginLocation(mContext);
        String locationCity2 = null;
        String codeCity = null;

        if (locationCity.endsWith("市")) {
            locationCity2 = locationCity.replace("市", "");
        } else if (locationCity.endsWith("区")) {
            locationCity2 = locationCity.replace("区", "");
        } else {
            locationCity2 = locationCity;
        }

        if (weatherString.contains(locationCity2)) {
            int i = weatherString.indexOf(locationCity2);
            codeCity = weatherString.substring(i + locationCity2.length() + 1, i + locationCity2.length() + 1 + 9);
        }

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.retryOnConnectionFailure(true)
                .connectTimeout(3000, TimeUnit.SECONDS);
        OkHttpClient client = httpClientBuilder.addInterceptor(new RetrofitInterceptor()).build();

        //获取Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())   //添加Gson转换器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl("http://www.weather.com.cn/data/cityinfo/")
                .client(client)
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        Observable<ResponseBody> observable = apiService.getWeather(codeCity + ".html");

        Observable<ResponseBody> responseBodyObservable = observable
                .subscribeOn(Schedulers.newThread())//这里需要注意的是，网络请求在非ui线程。如果返回结果是依赖于Rxjava的，则需要变换线程
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                .observeOn(AndroidSchedulers.mainThread());

        responseBodyObservable.subscribe(new Observer<ResponseBody>() {


            @Override
            public void onCompleted() {
                LogUtils.e("onCompleted", "");
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.e("onError", e.toString());
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String string = responseBody.string();

                    LogUtils.e("weatherUrl", string);
                    Gson gson = new Gson();
                    WeatherBean weatherBean = gson.fromJson(string, WeatherBean.class);
                    WeatherBean.WeatherinfoBean weatherinfo = weatherBean.getWeatherinfo();
                    String weather = weatherinfo.getWeather();
                    String temp1 = weatherinfo.getTemp1();
                    String timeString = MyUtils.getTimeString();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
