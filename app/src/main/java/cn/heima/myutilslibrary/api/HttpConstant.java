package cn.heima.myutilslibrary.api;

/**
 * 创建日期：2018/11/12
 * 描述: BaseUrl
 * 作者: gaoqing
 */
public class HttpConstant {

    //是不是测试环境
    public static boolean isDebug = false;

    private static String BASE_URL_RELEASE = "http://baidu.app.com/";
    private static String BASE_URL_DEBUG = "http://testbaidu.app.com/";

    public static String BASE_URL = isDebug ? BASE_URL_DEBUG : BASE_URL_RELEASE;
}
