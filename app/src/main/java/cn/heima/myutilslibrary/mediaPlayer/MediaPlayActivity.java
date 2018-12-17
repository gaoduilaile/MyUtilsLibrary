package cn.heima.myutilslibrary.mediaPlayer;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.heima.myutilslibrary.R;
import cn.krvision.toolmodule.ARouterPath;
import cn.krvision.toolmodule.BaseActivity;
import cn.krvision.toolmodule.ExoplayerUtils;
import cn.krvision.toolmodule.LogUtils;
import cn.krvision.toolmodule.MediaPlayerUtils;
@Route(path= ARouterPath.MediaPlayActivity)
public class MediaPlayActivity extends BaseActivity {


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

        initExoPlayer();

//        initMediaPlayer();

    }

    private void initMediaPlayer() {

        String url = "http://visioncircle.file.alimmdn.com/Sighthearing/audio/%E6%B7%B1%E6%9E%97%E9%87%8C%E7%9A%84%E9%B8%9F%E5%8F%AB%E5%A3%B0.m4a?t=1542777495000";

        MediaPlayerUtils.getInstance().intiMediaPlayer(MediaPlayActivity.this);
        MediaPlayerUtils.getInstance().startVoice(url);
        MediaPlayerUtils.getInstance().setMediaPlayerUtilsFunc(new MediaPlayerUtils.MediaPlayerUtilsFunc() {
            @Override
            public void onStart() {

                LogUtils.e("1111111 ","onStart");

            }

            @Override
            public void onCompletion() {
                LogUtils.e("1111111 ","onCompletion");
            }
        });

    }

    private void initExoPlayer() {
        ExoplayerUtils.getInstance().init(this, simpleExoPlayerView);
        String url = "http://visioncircle.file.alimmdn.com/%E8%A7%86%E5%8F%8B%E5%9C%88/%E5%90%AF%E6%98%8E%E6%98%9F%E7%9C%BC%E9%95%9C%E4%BD%BF%E7%94%A8%E4%BA%A4%E4%BA%92%E5%8D%87%E7%BA%A7.mp4?t=1543827230000";
        ExoplayerUtils.getInstance().setMediaSource(url);

        ExoplayerUtils.getInstance().play();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        ExoplayerUtils.getInstance().stop();
        ExoplayerUtils.getInstance().release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ExoplayerUtils.getInstance().stop();
        ExoplayerUtils.getInstance().release();
    }

    private void musicClick(View view){
        MediaPlayerUtils.getInstance().playOrPause();
    }

}
