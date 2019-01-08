package cn.heima.myutilslibrary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.heima.myutilslibrary.contacts.Contacts2Activity;
import cn.heima.myutilslibrary.popupWindow.PopupWindowActivity;
import cn.heima.myutilslibrary.tts.IatDemo;
import cn.krvision.toolmodule.base.ARouterPath;
import cn.krvision.toolmodule.base.BaseActivity;

@Route(path=ARouterPath.MainActivity)
public class MainActivity extends BaseActivity {

    private String TAG = " MainActivity=";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;

//        startActivity(new Intent(context, ReflectActivity.class));
    }

    @OnClick({R.id.tv_bluetooth, R.id.tv_contact,R.id.tv_video, R.id.tv_pop,R.id.tv_tts})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_bluetooth:
//                startActivity(new Intent(context, BluetoothActivity.class));

                //使用Arouter路由跳转界面并传递参数
                ARouter.getInstance().build(ARouterPath.BluetoothActivity)
                        .withString("key1","value1")
                        .withInt("key2",2)
                        .withTransition(R.anim.dialog_enter_anim,R.anim.dialog_exit_anim)//添加动画
                        .navigation();
                break;
            case R.id.tv_contact:
                startActivity(new Intent(context, Contacts2Activity.class));
                break;
            case R.id.tv_video:
//                startActivity(new Intent(context, MediaPlayActivity.class));
                ARouter.getInstance().build(ARouterPath.MediaPlayActivity).navigation();
                break;
            case R.id.tv_pop:
                startActivity(new Intent(context, PopupWindowActivity.class));
//                ARouter.getInstance().build(ARouterPath.PopupWindowActivity).navigation();
                break;
            case R.id.tv_tts:
                startActivity(new Intent(context, IatDemo.class));
//                ARouter.getInstance().build(ARouterPath.MainActivitySpeech).navigation();
                break;
        }
    }

}