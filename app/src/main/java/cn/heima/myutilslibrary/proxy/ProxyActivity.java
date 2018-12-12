package cn.heima.myutilslibrary.proxy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cn.heima.myutilslibrary.R;
import cn.heima.myutilslibrary.proxy.staticProxy.PersonProxy;

public class ProxyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy);

        SendInterface person = new Person();
        SendInterface sendInterface = new PersonProxy(person);
        sendInterface.sendMessage();
        sendInterface.submit();



    }
}
