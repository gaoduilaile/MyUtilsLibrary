package cn.krvision.blebluetooth;

import android.content.Context;
import android.content.Intent;

/**
 * Created by gaoqiong on 2018/10/10 12:01
 * Description:$description$
 */
public class SendBroadcastDataUtils {

    public static void sendData(Context context, int code) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.setAction(Constants.BROADCAST_FROM_ACTIVITY);
        intent.putExtra("code", code);
        context.sendBroadcast(intent);
    }

    public static void sendData(Context context, int code, String name, String address) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.setAction(Constants.BROADCAST_FROM_ACTIVITY);
        intent.putExtra("code", code);
        intent.putExtra("name", name);
        intent.putExtra("address", address);
        context.sendBroadcast(intent);
    }
}
