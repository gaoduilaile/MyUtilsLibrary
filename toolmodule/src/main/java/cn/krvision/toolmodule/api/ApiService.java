package cn.krvision.toolmodule.api;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * 创建日期：2018/11/12
 * 描述: 接口service
 * 作者: gaoqing
 */
public interface ApiService {

    @POST("downloadfirstclassmenu/")
    Observable<ResponseBody> downloadfirstclassmenu(@Body RequestBody requestBody);


    @POST("uploadclassbutton/")
    Observable<ResponseBody> uploadclassbutton(@Body RequestBody requestBody);

    /**
     *天气 token
     */
    @GET("{url}")
    Observable<ResponseBody> getWeather(@Path("url") String url);


}
