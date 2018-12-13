package cn.krvision.toolmodule;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.danikula.videocache.HttpProxyCacheServer;

import java.util.Timer;

/**
 * Created by gaoqiong on 2018/12/11 14:42
 * Description:$description$
 */
public class MediaPlayerUtils {

    private boolean voicePlaying = false;
    private MediaPlayer mediaPlayer;
    private int currentPosition;
    private Timer timer;
    private MediaPlayerUtilsFunc func;
    private Activity context;
    private HttpProxyCacheServer httpProxyCacheServer;

    public static MediaPlayerUtils getInstance() {

        return MediaPlayerUtils.SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final MediaPlayerUtils instance = new MediaPlayerUtils();
    }


    public interface MediaPlayerUtilsFunc {
        void onStart();

        void onCompletion();
    }

    private   HttpProxyCacheServer getHttpProxyCacheServer(Context context) {
        return new HttpProxyCacheServer(context);
    }

    public void setMediaPlayerUtilsFunc(MediaPlayerUtilsFunc func) {
        this.func = func;
    }

    public void startVoice(String voiceUrl) {
        mediaStop();
        String proxyUrl = httpProxyCacheServer.getProxyUrl(voiceUrl);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(proxyUrl); // 设置数据源
            mediaPlayer.prepare(); // prepare自动播放
            mediaPlayer.seekTo(currentPosition);// 移动到上次播放位置
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        voicePlaying = true;
        func.onStart();
    }

    public void getCurrentPosition() {
        if (mediaPlayer != null && mediaPlayer.isPlaying() && voicePlaying) {
            currentPosition = mediaPlayer.getCurrentPosition();
        }
    }

    public void playOrPause() {
        if (mediaPlayer == null) return;

        if (mediaPlayer.isPlaying() && voicePlaying) {
            mediaPlayer.pause();//暂停播放
            voicePlaying = false;
            currentPosition = mediaPlayer.getCurrentPosition();
        } else {
            mediaPlayer.start();//继续播放
            voicePlaying = true;
            mediaPlayer.seekTo(currentPosition);
        }
    }

    public void intiMediaPlayer(Activity context) {
        httpProxyCacheServer = getHttpProxyCacheServer(context);
        this.context = context;
        if (mediaPlayer == null) {
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        return false;
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        voicePlaying = false;
                        currentPosition = 0;
                        func.onCompletion();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void mediaStop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.stop();
            mediaPlayer.release();
            currentPosition = 0;
            mediaPlayer = null;
            voicePlaying = false;
        }
    }
}
