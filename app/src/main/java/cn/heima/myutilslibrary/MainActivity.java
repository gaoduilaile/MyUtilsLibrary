package cn.heima.myutilslibrary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.exoplayer2.SimpleExoPlayer;

import cn.heima.myutilslibrary.contacts.ContactsActivity;
import cn.heima.myutilslibrary.mediaPlayer.MediaPlayActivity;
import cn.heima.myutilslibrary.popupWindow.PopupWindowActivity;
import cn.krvision.blebluetooth.BluetoothActivity;

public class MainActivity extends AppCompatActivity {

    private SimpleExoPlayer player;
    private String TAG = " MainActivity=";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
    }


    public void blebluetoothClick(View view) {
        startActivity(new Intent(context, BluetoothActivity.class));
    }

    public void contactClick(View view) {
        startActivity(new Intent(context, ContactsActivity.class));
    }

    public void pwsClick(View view) {
        startActivity(new Intent(context, ContactsActivity.class));
    }

    public void viedPlay(View view) {
        startActivity(new Intent(context, MediaPlayActivity.class));
    }

    public void pop(View view) {
        startActivity(new Intent(context, PopupWindowActivity.class));
    }
}