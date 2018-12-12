package cn.heima.myutilslibrary.tts;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Locale;

import cn.heima.myutilslibrary.R;

public class TTSActivity extends AppCompatActivity {

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts);
        initTTS();
    }
    private void initTTS() {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                //初始化成功的话，设置语音
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.ENGLISH);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(TTSActivity.this, "抱歉!不支持语音播报功能...", Toast.LENGTH_SHORT).show();
                    } else {
                        tts.speak("123", TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            }
        });
    }
}