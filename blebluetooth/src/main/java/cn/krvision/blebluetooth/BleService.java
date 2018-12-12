package cn.krvision.blebluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gaoqiong on 2018/10/10 9:47
 * Description:$description$
 */
public class BleService extends Service {

    private String TAG = "BleService= ";

    //蓝牙初始化
    private static BluetoothManager mBluetoothManager;
    private static BluetoothAdapter mBluetoothAdapter;

    //蓝牙链接状态
    public static int mConnectionState = Constants.BLUETOOTH_DISCONNECTED;
    public static boolean mScaning = false;
    private static BluetoothGatt mBluetoothGatt;

    //读写特征值
    private static BluetoothGattCharacteristic testCharacteristic;
    //通知类型特征
    private static BluetoothGattCharacteristic notifyCharateristic;

    //特征值对应的值
    private String characteristicValue;
    private boolean isBluetoothConnecteAouto;

    //GATT连接
    public final static String ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
    //GATT断开
    public final static String ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";
    //GATT服务被发现
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";
    //BLE通讯成功
    public final static String ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE";

    private Context mContext;
    private Handler mHandler;


    //蓝牙扫描.
    private static final long SCAN_PERIOD = 30000;
    //正在连接的眼镜 地址
    private String mBluetoothDeviceAddress;
    //正在连接的眼镜 名字
    private String mScanDeviceName;

    private MyBinder mBinder;
    private MyServiceConnection mServiceConnection;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 获取单例
     */
    private static BleService instance = null;

    private BleService() {
    }

    public static BleService getInstance() {
        if (instance == null) {
            synchronized (BleService.class) {
                if (instance == null) {
                    instance = new BleService();
                }
            }
        }
        return instance;
    }


    /**
     * 回调接口初始化
     */
    private BleServiceInterface bleServiceInterface;

    public void initBleServiceInterface(BleServiceInterface bleServiceInterface) {
        this.bleServiceInterface = bleServiceInterface;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mHandler = new Handler();

        if (mBinder == null) {
            mBinder = new MyBinder();
            mServiceConnection = new MyServiceConnection();
        }

        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(Constants.BROADCAST_FROM_ACTIVITY);
        registerReceiver(mStatusReceive, intentFilter);

        this.bindService(new Intent(BleService.this, BLEServiceCopy.class), mServiceConnection, Context.BIND_IMPORTANT);

    }

    public class MyBinder extends IMyAidlInterface.Stub {

    }

    class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//            Toast.makeText(ServiceB.this, name + "连接成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
//            Toast.makeText(ServiceB.this, TAG + "断开连接", Toast.LENGTH_SHORT).show();
            BleService.this.bindService(new Intent(BleService.this, BLEServiceCopy.class), mServiceConnection, Context.BIND_IMPORTANT);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mStatusReceive);
        unbindService(mServiceConnection);
        close();
    }


    private BroadcastReceiver mStatusReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) {
                return;
            }
            switch (intent.getAction()) {
                //监听手机蓝牙打开和关闭
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            break;
                        case BluetoothAdapter.STATE_ON:
                            mConnectionState = Constants.BLUETOOTH_SWITCH_OPEN;
                            //开始扫描
                            scanLeDevice(true);
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            disconnectBluetooth(1);
                            mConnectionState = Constants.BLUETOOTH_SWITCH_CLOSE;
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            break;
                    }
                    break;
                //接受activity界面传过来的指令
                case Constants.BROADCAST_FROM_ACTIVITY:
                    int code = intent.getIntExtra("code", 0);
                    switch (code) {
                        case Constants.ACTION_SCAN:
                            initialize();
                            break;
                        case Constants.ACTION_CONNECTED:
                        case Constants.ACTION_CONNECTED_AUTO:
                            String name = intent.getStringExtra("name");
                            String address = intent.getStringExtra("address");
                            connect(name, address);
                            break;
                        case Constants.ACTION_DISCONNECTED:
                            disconnect();
                            break;
                        case Constants.ACTION_UNBIND:
                            disconnect();
                            break;
                    }
                    break;
            }
        }
    };

    /**
     * 初始化蓝牙适配管理器
     *
     * @return
     */
    public boolean initialize() {
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBluetoothManager == null) {
            return false;
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            return false;
        }
        return true;
    }


    /**
     * 连接蓝牙
     *
     * @param scanDeviceName 蓝牙设备名字  address 蓝牙设备地址值
     * @return
     */
    public boolean connect(String scanDeviceName, final String address) {

        if (mBluetoothAdapter == null || address == null || scanDeviceName == null) {
            return false;
        }

        // Previously connected device.  Try to reconnect.
//        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
//            if (mBluetoothGatt.connect()) {
//                mConnectionState = Constants.BLUETOOTH_CONNECTING;
//                LogUtils.e(TAG, "Trying to use an existing mBluetoothGatt for connection. true");
//                return true;
//            } else {
//                LogUtils.e(TAG, "Trying to use an existing mBluetoothGatt for connection.  false");
//                return false;
//            }
//        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            return false;
        }

        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        mBluetoothDeviceAddress = address;
        mScanDeviceName = scanDeviceName;
        mConnectionState = Constants.BLUETOOTH_CONNECTING;
        return true;

    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            mBluetoothGatt.disconnect();
            isBluetoothConnecteAouto = false;
            testCharacteristic = null;
            notifyCharateristic = null;
        }
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    /**
     * 关闭手机蓝牙开关
     */
    public static void closePhotoBluetoothSwitch() {
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.disable();  //关闭蓝牙
            }
        }
    }

    /**
     * 开启手机蓝牙开关
     */
    public static void openPhotoBluetoothSwitch() {
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }
        }
    }

    /**
     * 2018/6/25  断开眼镜
     */
    public void disconnectBluetooth(int type) {
        scanLeDevice(false);
        disconnect();
    }

    /**
     * 扫描蓝牙
     *
     * @param enable
     */
    private void scanLeDevice(final boolean enable) {
        if (mBluetoothAdapter != null) {
            if (enable) {
                if (mScaning) {
                    return;
                }
                mScaning = true;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mBluetoothAdapter != null) {
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                            mConnectionState = Constants.BLUETOOTH_DISCONNECTED;
                            mScaning = false;
                        }
                    }
                }, SCAN_PERIOD);
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                mScaning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
    }

    private Map<String, String> mapBluetoothDevice = new HashMap<>();
    /**
     * 2018/8/15 蓝牙设备扫描结果回调
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device.getName() != null && device.getName().length() == 18) {
                mapBluetoothDevice.put(device.getName(), device.getAddress());
                String scanDeviceName = device.getName();

                if (device.getName() != null && device.getAddress() != null) {
                    bleServiceInterface.onLeScanSuccess(device);
//                if (scanDeviceName.contains("KR")) {
//                    scanLeDevice(false);
//                    connect(scanDeviceName, mapBluetoothDevice.get(scanDeviceName));
//                }
                }

            }
        }
    };

    /**
     * 2018/8/15 GATT连接状态  连接成功、 断开连接
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                bluetoothStateUpdate(ACTION_GATT_CONNECTED);
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mBluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                if (mConnectionState == Constants.ACTION_DISCONNECTED || mConnectionState == Constants.ACTION_UNBIND) {
                    // 主动断开
                    close();
                } else {
                    connect(mScanDeviceName, mBluetoothDeviceAddress);
                    bluetoothStateUpdate(ACTION_GATT_DISCONNECTED);
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.e(TAG, "onServicesDiscovered: mBluetoothGatt=" + gatt);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                bluetoothStateUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "onCharacteristicRead");
                characteristicValue = getCharacteristicValue(characteristic);
                bluetoothStateUpdate(ACTION_DATA_AVAILABLE);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.e(TAG, "onCharacteristicChanged");
            characteristicValue = getCharacteristicValue(characteristic);
            bluetoothStateUpdate(ACTION_DATA_AVAILABLE);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "OnCharacteristicWrite " + status);
//                aCharacteristicWriteInterface.OnCharacteristicWriteSuccess();
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor bd, int status) {
            Log.e(TAG, "onDescriptorRead");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor bd, int status) {
            Log.e(TAG, "onDescriptorWrite");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int a, int b) {
            Log.e(TAG, "onReadRemoteRssi");
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int a) {
            Log.e(TAG, "onReliableWriteCompleted");
        }
    };

    /**
     * 获取特征值 value
     *
     * @param characteristic 特征值的value
     * @return
     */
    private String getCharacteristicValue(BluetoothGattCharacteristic characteristic) {
        final byte[] data = characteristic.getValue();
        return new String(data);
    }

    /**
     * 蓝牙状态变化处理
     *
     * @param action
     */
    private void bluetoothStateUpdate(final String action) {
        Log.e("mGattUpdateReceiver1 ", action);
        switch (action) {
            case ACTION_GATT_DISCONNECTED://连接断开
                mConnectionState = Constants.BLUETOOTH_DISCONNECTED;
                break;
            case ACTION_GATT_CONNECTED://连接成功
                mConnectionState = Constants.BLUETOOTH_CONNECTED;
                break;
            case ACTION_GATT_SERVICES_DISCOVERED://获取服务、特征
                mConnectionState = Constants.BLUETOOTH_SERVICES_DISCOVERED;
                //特性值
                boolean characteristicData = getCharacteristicData();
                if (!characteristicData) {
                    return;
                }
                break;
            case ACTION_DATA_AVAILABLE://读取响应的蓝牙数据
                mConnectionState = Constants.BLUETOOTH_AVAILABLE;

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(characteristicValue);
                    int code = jsonObject.getInt("code");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    private boolean getCharacteristicData() {

        if (mBluetoothGatt == null) {
            return false;
        }

        List<BluetoothGattService> gattServices = mBluetoothGatt.getServices();

        if (gattServices == null) {
            return false;
        }

        List<List<BluetoothGattCharacteristic>> mDeviceServices = new ArrayList<>();

        // Loops through available GATT Services from the connected device
        for (BluetoothGattService gattService : gattServices) {
            List<BluetoothGattCharacteristic> characteristics = gattService.getCharacteristics();
            mDeviceServices.add(characteristics);// each GATT Service can have multiple characteristic
        }

        for (List<BluetoothGattCharacteristic> service : mDeviceServices) {//循环service列表
            for (BluetoothGattCharacteristic serviceCharacteristic : service) {//循环特征值
                if (serviceCharacteristic.getService().getUuid().equals(Constants.SERVICE_UUID)) {
                    if (serviceCharacteristic.getUuid().equals(Constants.CHARACTERISTIC_UUID)) {
                        testCharacteristic = serviceCharacteristic;
                        setCharacteristicNotification(testCharacteristic, true);
                        Log.e(TAG, "registerCharacteristic: testCharacteristic=" + testCharacteristic);
                    } else if (serviceCharacteristic.getUuid().equals(Constants.NOTIFY_CHARACTERISTIC_UUID)) {
                        notifyCharateristic = serviceCharacteristic;
                        setCharacteristicNotification(notifyCharateristic, true);
                        Log.e(TAG, "registerCharacteristic: notifyCharateristic=" + notifyCharateristic);
                    }
                }
            }
        }

        //请求修改传输字节数
        requestMtu(300);

        if (notifyCharateristic == null || testCharacteristic == null) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * 只有设置该值才会收到BLE设备发送过来的信息
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        }
    }

    /**
     * 修改传输协议字节长度
     */
    public void requestMtu(int size) {
        if (mBluetoothGatt != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBluetoothGatt.requestMtu(size);
            }
        }
    }

}
