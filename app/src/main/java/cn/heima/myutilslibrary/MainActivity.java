package cn.heima.myutilslibrary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.heima.myutilslibrary.mediaPlayer.MediaPlayActivity;
import cn.heima.myutilslibrary.popupWindow.PopupWindowActivity;
import cn.krvision.blebluetooth.BluetoothActivity;

public class MainActivity extends AppCompatActivity {

    private String TAG = " MainActivity=";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;
    }

    @OnClick({R.id.tv_bluetooth, R.id.tv_contact,R.id.tv_video, R.id.tv_pop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_bluetooth:
                startActivity(new Intent(context, BluetoothActivity.class));
                break;
            case R.id.tv_contact:
//                startActivity(new Intent(context, ContactsActivity.class));
                break;
            case R.id.tv_video:
                startActivity(new Intent(context, MediaPlayActivity.class));
                break;
            case R.id.tv_pop:
                startActivity(new Intent(context, PopupWindowActivity.class));
                break;
        }
    }

}