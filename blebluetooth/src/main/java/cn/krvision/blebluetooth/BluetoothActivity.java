package cn.krvision.blebluetooth;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import cn.krvision.toolmodule.ARouterPath;
import cn.krvision.toolmodule.BaseActivity;
import cn.krvision.toolmodule.LogUtils;
import cn.krvision.toolmodule.bean.WeatherBean;

@Route(path= ARouterPath.BluetoothActivity)
public class BluetoothActivity extends BaseActivity {

    @Autowired()
    String key1;
    @Autowired()
    int key2;
    @Autowired(name="WeatherinfoBean")
    WeatherBean.WeatherinfoBean weatherinfoBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

//        ARouter.init(getApplication());

        LogUtils.e("BluetoothActivity ","key1="+key1+" key2="+key2+" WeatherinfoBean="+weatherinfoBean);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ARouter.getInstance().build(ARouterPath.MediaPlayActivity).navigation();
            }
        }, 2000);

    }


    public void primaryDevice(View view) {

    }

    public void slaveDevice(View view) {
    }
}
