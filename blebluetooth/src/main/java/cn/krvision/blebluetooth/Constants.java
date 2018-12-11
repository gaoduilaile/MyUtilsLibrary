package cn.krvision.blebluetooth;

import java.util.UUID;

/**
 * Created by gaoqiong on 2018/8/16
 */

public class Constants {

    //蓝牙服务的UUID
    public static final UUID SERVICE_UUID = UUID.fromString("0000190D-0000-1000-8000-00805f9b34fb");
    //写特征值的UUID
    public static final UUID CHARACTERISTIC_UUID = UUID.fromString("0000191D-0000-1000-8000-00805f9b34fb");
    //通知特征值的UUID
    public static final UUID NOTIFY_CHARACTERISTIC_UUID = UUID.fromString("0000192D-0000-1000-8000-00805f9b34fb");
    public final static String BROADCAST_FROM_SERVICE = "broadcast.from.service";
    //ctivity向眼镜端发送广播
    public final static String BROADCAST_FROM_ACTIVITY = "broadcast.from.activity";


    /*蓝牙连接状态  服务向activity发送内容*/
    public final static int BLUETOOTH_DISCONNECTED = 101;//断开蓝牙
    public final static int BLUETOOTH_CONNECTED = 102;//连接上蓝牙
    public final static int BLUETOOTH_SERVICES_DISCOVERED = 103;//发现蓝牙服务
    public final static int BLUETOOTH_AVAILABLE = 104;//蓝牙通讯成功
    public final static int BLUETOOTH_CONNECTING = 105;
    public final static int BLUETOOTH_SWITCH_OPEN = 106;//手机蓝牙开关打开
    public final static int BLUETOOTH_SWITCH_CLOSE = 107;//手机蓝牙开关关闭
    public final static int BLUETOOTH_BIND_FIRST = 108;//首次绑定蓝牙


    public final static int ACTION_SCAN = 110;//去扫描
    public final static int ACTION_CONNECTED = 111;//去连接蓝牙
    public final static int ACTION_CONNECTED_AUTO = 112;//自动连接蓝牙
    public final static int ACTION_DISCONNECTED =113;//去断开蓝牙
    public final static int ACTION_UNBIND = 114;//解绑蓝牙


    public final static String CN_VISION_NEED_WIFI = "cn.vision.need.wifi";//wifi弹窗

}
