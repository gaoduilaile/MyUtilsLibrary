package cn.heima.myutilslibrary.mediaPlayer;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class ExoplayerUtils {

    private SimpleExoPlayer player;
    private ExtractorsFactory extractorsFactory;
    private DataSource.Factory dataSourceFactory;

    /**
     * @return 静态内部类
     */

    public ExoplayerUtils() {

    }

    public static ExoplayerUtils getInstance() {

        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final ExoplayerUtils instance = new ExoplayerUtils();
    }


    public void init(Context context,SimpleExoPlayerView simpleExoPlayerView ) {
        pause();
        stop();
        release();
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        // 1.创建一个默认TrackSelector
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        // 2.创建一个默认的LoadControl
        LoadControl loadControl = new DefaultLoadControl();

        // 3.创建播放器
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);

        //4、创建一个显示视频的控件View
//        SimpleExoPlayerView simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.simpleExoPlayerView);
        // 5、将player关联到View上
        simpleExoPlayerView.setPlayer(player);


        // 5、创建一个DataSource 资源
        dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "yourApplicationName"), bandwidthMeter);
        //6、创建一个 Extractor 资源
        extractorsFactory = new DefaultExtractorsFactory();


        //6、讲一个视频资源和字母资源 整合到一起
//        MediaSource subtitleSource = new SingleSampleMediaSource(Uri.parse(url), dataSourceFactory, );
//        MergingMediaSource mergedSource = new MergingMediaSource(videoSource, subtitleSource);
    }

    public void setMediaSource(String url) {

        //6、创建一个 MediaSource 资源
//        String url = "http://visioncircle.file.alimmdn.com/video/file_8e577efaa0ae4eae8e53938dfb279634?t=1522652671328";
        ExtractorMediaSource videoSource = new ExtractorMediaSource(Uri.parse(url),
                dataSourceFactory, extractorsFactory, null, null);
        // 播放
        player.prepare(videoSource);

        player.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {
                Log.e("11111111 ", " onTimelineChanged ");
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.e("11111111 ", " onTracksChanged ");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.e("11111111 ", " onLoadingChanged "+player.getBufferedPosition() / 1000);
                System.out.println("播放: onLoadingChanged  " + player.getBufferedPosition() / 1000);

                if (seekBar!=null){
                    seekBar.setSecondaryProgress((int) (player.getBufferedPosition() / 1000));
                }

                if (tv1!=null){
                    tv1.setText((int) (player.getBufferedPosition() / 1000)+" ");
                }
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.e("11111111 ", " onPlayerStateChanged ");
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.e("11111111 ", " onPlayerError ");
            }

            @Override
            public void onPositionDiscontinuity() {
                Log.e("11111111 ", " onPositionDiscontinuity ");
            }
        });


    }

    public void play() {
        if (player != null)
            player.setPlayWhenReady(true);
    }

    public void pause() {
        if (player != null)
            player.setPlayWhenReady(false);
    }

    public void release() {
        if (player != null) {
            player.release();
        }
    }

    public void stop() {
        if (player != null) {
            player.stop();
//            if (player.isLoading())
        }
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    private SeekBar seekBar;
    private TextView tv1;

    public void setSeekBar(SeekBar seekBar, final TextView tv1, TextView tv2) {

        this.seekBar = seekBar;
        this.tv1 = tv1;
        seekBar.setEnabled(true);
        tv2.setText((int) (player.getDuration() / 1000)+" "); //设置总时长
        seekBar.setMax((int) (player.getDuration() / 1000));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.e("2222222 ", " onProgressChanged " + i + "  " + b);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e("2222222 ", " onStartTrackingTouch ");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e("2222222 ", " onStopTrackingTouch ");

                //是暂停的开始播放
                if (!player.getPlayWhenReady()) {
                    player.setPlayWhenReady(true);
                    // 继续播放
                }
                player.seekTo(seekBar.getProgress() * 1000);

                tv1.setText((int) (player.getBufferedPosition() / 1000)+" ");
            }
        });

    }

}
