package cn.heima.myutilslibrary.mediaPlayer;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.heima.myutilslibrary.R;

public class MediaPlayActivity extends AppCompatActivity {


    @BindView(R.id.simpleExoPlayerView)
    SimpleExoPlayerView simpleExoPlayerView;
    private TextToSpeech tts;
    private String TAG = " MainActivity=";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediaplay);
        ButterKnife.bind(this);

        ExoplayerUtils.getInstance().init(this, simpleExoPlayerView);
        String url = "http://visioncircle.file.alimmdn.com/%E8%A7%86%E5%8F%8B%E5%9C%88/%E5%90%AF%E6%98%8E%E6%98%9F%E7%9C%BC%E9%95%9C%E4%BD%BF%E7%94%A8%E4%BA%A4%E4%BA%92%E5%8D%87%E7%BA%A7.mp4?t=1543827230000";
        ExoplayerUtils.getInstance().setMediaSource(url);

        ExoplayerUtils.getInstance().play();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ExoplayerUtils.getInstance().stop();
        ExoplayerUtils.getInstance().release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ExoplayerUtils.getInstance().stop();
        ExoplayerUtils.getInstance().release();
    }

}
