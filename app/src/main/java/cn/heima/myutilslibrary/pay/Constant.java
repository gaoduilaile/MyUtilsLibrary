package cn.heima.myutilslibrary.pay;

import android.os.Environment;

/**
 * Created by gaoqiong on 2018/11/12 16:58
 * Description:$description$
 */
public class Constant {

    //友盟
    public static final String UMENG_MESSAGE_SECRE = "4532b5a039038473d03ebcba25dc7be9";
    public static final String APPKEY = "5a010718a40fa36407000757";
    public static final String UMENG_APP_MASTER_SECRET = "1v6357fy3afbm2gcb3vygy7utnyomvfk";

    //微信
    public static final String WEIXIN_APPID = "wxc5cffcd929280839";
    public static final String WEIXIN_SECRET = "30ea2fd60e31e033b6c2bdab36d1dfd3";

    //QQ
    public static final String QQ_APPID = "1106446409";
    public static final String QQ_KEY = "MvB7l1XT4xcdDpb6";


    /**
     * 拍照或相册
     */
    public static final String EXTERNAL_STORAGE_DIRECTORY = Environment.getExternalStorageDirectory().getPath();
    /**
     * 裁剪图片地址,图片在Intent之间传输有大小限制
     */
    public static final String FILENAME = EXTERNAL_STORAGE_DIRECTORY + "/crop.png";
    /**
     * 拍照的图片路径
     */
    public static final String PHOTONAME = EXTERNAL_STORAGE_DIRECTORY + "/youwo.png";


}
