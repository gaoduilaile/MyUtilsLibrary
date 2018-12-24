package cn.krvision.toolmodule.api;

import java.io.IOException;

import cn.krvision.toolmodule.LogUtils;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by gaoqiong on 2018/11/12
 */

public class RetrofitInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request oldRequest = chain.request();
        // 添加新的参数
        HttpUrl.Builder authorizedUrlBuilder = oldRequest.url()
                .newBuilder()
                .scheme(oldRequest.url().scheme())
                .host(oldRequest.url().host());
        // 新的请求
        Request newRequest = oldRequest.newBuilder()
                .method(oldRequest.method(), oldRequest.body())
                .url(authorizedUrlBuilder.build())
                .addHeader("Content-Type", "application/json")
                .build();
        LogUtils.e("----------", newRequest.url()+" ");
        return chain.proceed(newRequest);
    }
}
