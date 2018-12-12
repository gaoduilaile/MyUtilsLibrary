package cn.heima.myutilslibrary.mediaPlayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import cn.heima.myutilslibrary.MainApplication;
import cn.heima.myutilslibrary.R;

/**
 * SoundPool 铃声尽量不要超过1M
 * 在不同的系统下 SoundPool 表现可能存在不一致
 */
public class AVChatSoundPlayer {

    private static final String TAG = "AVChatSoundPlayer";

    private Context context;

    private SoundPool soundPool;
    private AudioManager audioManager;
    private int streamId;
    private int soundId;

    private static AVChatSoundPlayer instance = null;

    public static AVChatSoundPlayer instance() {
        if (instance == null) {
            synchronized (AVChatSoundPlayer.class) {
                if (instance == null) {
                    instance = new AVChatSoundPlayer();
                }
            }
        }
        return instance;
    }

    public AVChatSoundPlayer() {
        this.context = MainApplication.mContext;
    }

    public void stop() {
        Log.e(TAG, "stop");
        if (soundPool != null) {
            if (streamId != 0) {
                soundPool.stop(streamId);
                streamId = 0;
            }
            if (soundId != 0) {
                soundPool.unload(soundId);
                soundId = 0;
            }
        }
    }

    /**
     * 播放本地铃声
     */
    public void play() {
        initSoundPool();
        soundId = soundPool.load(context, R.raw.helpsound, 1);
    }

    /**
     * 初始化SoundPool
     */
    private void initSoundPool() {
        stop();
        if (soundPool == null) {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    streamId = soundPool.play(soundId, 1, 1, 1, 0, 1f);
                    Log.e("11111111 ","streamId= "+streamId+"   soundId= "+soundId);
                }
            });
        }
    }


}
