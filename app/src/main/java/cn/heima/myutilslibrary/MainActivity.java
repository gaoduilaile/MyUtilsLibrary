package cn.heima.myutilslibrary;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.Locale;

import cn.heima.myutilslibrary.contacts.ContactsActivity;
import cn.krvision.blebluetooth.BluetoothActivity;

public class MainActivity extends AppCompatActivity {

    private SimpleExoPlayer player;
    private TextToSpeech tts;
    private String TAG = " MainActivity=";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        initMediaPlayer();

//        initTTS();

    }

    private void initTTS() {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                //初始化成功的话，设置语音
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.ENGLISH);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(MainActivity.this, "抱歉!不支持语音播报功能...", Toast.LENGTH_SHORT).show();
                    } else {
                        tts.speak("123", TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            }
        });
    }


    private void initMediaPlayer() {

        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        // 1.创建一个默认TrackSelector
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        // 2.创建一个默认的LoadControl
        LoadControl loadControl = new DefaultLoadControl();

        // 3.创建播放器
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

        //4、创建一个显示视频的控件View
        SimpleExoPlayerView simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.simpleExoPlayerView);
        // 5、将player关联到View上
        simpleExoPlayerView.setPlayer(player);


        // 5、创建一个DataSource 资源
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "yourApplicationName"), bandwidthMeter);
        //6、创建一个 Extractor 资源
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        //6、创建一个 MediaSource 资源
        String url = "http://visioncircle.file.alimmdn.com/video/file_8e577efaa0ae4eae8e53938dfb279634?t=1522652671328";
        ExtractorMediaSource videoSource = new ExtractorMediaSource(Uri.parse(url),
                dataSourceFactory, extractorsFactory, null, null);

        // 5、播放
        player.prepare(videoSource);


        //6、讲一个视频资源和字母资源 整合到一起
//        MediaSource subtitleSource = new SingleSampleMediaSource(Uri.parse(url), dataSourceFactory, );
//        MergingMediaSource mergedSource = new MergingMediaSource(videoSource, subtitleSource);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }

    public void blebluetoothClick(View view) {
        startActivity(new Intent(context, BluetoothActivity.class));
    }

    public void contactClick(View view) {
        startActivity(new Intent(context, ContactsActivity.class));
    }

    public void pwsClick(View view) {
//        startActivity(new Intent(context, ContactsActivity.class));
    }
}