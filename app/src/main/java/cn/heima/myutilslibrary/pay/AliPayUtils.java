package cn.heima.myutilslibrary.pay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import java.util.HashMap;


/**
 * 创建日期：2018/8/22
 * 描述: 支付：支付宝支付工具类
 * 作者: gaoqiong
 */
public class AliPayUtils {

    Activity context;
    private AliPlayInterface aliPlaylisten;
    IWXAPI api;

    public interface AliPlayInterface {
        void fail();

        void success(String result, HashMap<String, String> map);

        void ensureing();
    }

    public AliPayUtils(Activity context, AliPlayInterface aliPlaylisten) {
        this.context = context;
        this.aliPlaylisten = aliPlaylisten;
    }


    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_PAY_FLAG_WEIXIN = 2;
    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG:
                    PayResult payResult = new PayResult((String) msg.obj);

//                    LogUtils.e("payResult ", payResult.toString());

                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    if (TextUtils.equals(resultStatus, "9000")) {
                        String[] strs = resultInfo.split("&");
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("str", resultInfo);
                        for (int i = 0; i < strs.length; i++) {
                            map.put(strs[i].split("=")[0], strs[i].split("=")[1]);
                        }
                        //Toast.makeText(context, "pay_success", Toast.LENGTH_SHORT).show();
                        aliPlaylisten.success(resultStatus, map);
                    } else {
                        if (TextUtils.equals(resultStatus, "8000")) {
//                            Toast.makeText(context, "支付结果确认中", Toast.LENGTH_SHORT).show();
                            aliPlaylisten.ensureing();
                        } else {
//                            Toast.makeText(context, "pay_failure", Toast.LENGTH_SHORT).show();
                            aliPlaylisten.fail();
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    };


    public void aliPayGood(final Activity context, final String orderInfo) {
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(context);
                String result = alipay.pay(orderInfo, true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
}

