package cn.krvision.blebluetooth;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import cn.krvision.toolmodule.base.ARouterPath;
import cn.krvision.toolmodule.base.BaseActivity;
import cn.krvision.toolmodule.utils.LogUtils;

@Route(path= ARouterPath.BluetoothActivity)
public class BluetoothActivity extends BaseActivity {

    @Autowired
    String key1;
    @Autowired
    int key2;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        ARouter.getInstance().inject(this);

//        ARouter.init(getApplication());

        LogUtils.e("BluetoothActivity ","key1="+key1+" key2="+key2);
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
