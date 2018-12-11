package cn.krvision.blebluetooth;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Created by gaoqiong on 2018/10/10 9:56
 * Description:$description$
 */
public class BLEServiceCopy extends Service {

    private MyBinder mBinder;
    private MyServiceConnection mServiceConnection;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.bindService(new Intent(BLEServiceCopy.this, BleService.class), mServiceConnection, Context.BIND_IMPORTANT);
        if (mBinder == null) {
            mBinder = new MyBinder();
        }
        mServiceConnection = new MyServiceConnection();
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
            BLEServiceCopy.this.bindService(new Intent(BLEServiceCopy.this, BleService.class), mServiceConnection, Context.BIND_IMPORTANT);
        }
    }
}
