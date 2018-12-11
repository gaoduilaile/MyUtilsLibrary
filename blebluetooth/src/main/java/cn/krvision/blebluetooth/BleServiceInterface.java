package cn.krvision.blebluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * Created by gaoqiong on 2018/10/10 11:48
 * Description:$description$
 */
public interface  BleServiceInterface {
    void OnCharacteristicWriteSuccess();
    void onLeScanSuccess(BluetoothDevice device);
}
