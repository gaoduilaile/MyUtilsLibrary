package cn.krvision.voip;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        try {
//            SkypeApi skypeApi = new SkypeApi(getApplicationContext());
//
//            skypeApi.startConversation(skypeName.toString(), Modality.AudioCall);
//        } catch (SkypeSdkException e) {
//            // Exception handling logic here
//        }
//
//        try {
//            SkypeApi skypeApi = new SkypeApi(getApplicationContext());
//            //Get the app id of your bot and prepend 28:
//            skypeApi.startConversation('28: b4914413-f8dd-4cb3-b2db-9b715ecfe26e', Modality.Chat);
//        } catch (SkypeSdkException e) {
//            // Exception handling logic here
//        }
    }
}
