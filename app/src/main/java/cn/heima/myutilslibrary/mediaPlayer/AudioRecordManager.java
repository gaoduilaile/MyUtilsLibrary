package cn.heima.myutilslibrary.mediaPlayer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by gaoqiong on 2018/11/7 16:43
 * Description:边录音边播放
 */
public class AudioRecordManager {

    private String TAG = "AudioRecordManager:";

    /**
     * AudioRecord 写入缓冲区大小
     */
    protected int bufferSizeInBytesWrite;
    /**
     * 录制音频对象
     */
    private AudioRecord audioRecord;
    /**
     * 录入的字节数组
     */
    private byte[] bytesAudioRecord;
    /**
     * 存放录入字节数组的大小
     */
    private LinkedList<byte[]> bytesAudioRecordArray;
    /**
     * AudioTrack 播放缓冲大小
     */
    private int bufferSizeInBytesPlay;
    /**
     * 播放音频对象
     */
    private AudioTrack audioTrack;
    /**
     * 播放的字节数组
     */
    private byte[] bytesAudioTrack;
    /**
     * 录制音频线程
     */
    private Thread record;
    /**
     * 播放音频线程
     */
    private Thread play;
    /**
     * 让线程停止的标志
     */
    private boolean flagAutoRecord = true;
    private boolean flagAutoTrack = true;

    //采用频率
    //44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    public final static int AUDIO_SAMPLE_RATE = 44100;  //44.1KHz,普遍使用的频率

    //音频输入-麦克风
    public final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;


    /**
     * 初始化
     */
    public void init() {
        initAudioRecord();
        initAudioTrack();
    }

    /**
     * AudioRecord初始化 录音
     */
    private void initAudioRecord() {
        // 获得缓冲区字节大小

        bufferSizeInBytesWrite = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);

        // 创建AudioRecord对象
        audioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytesWrite);

        // 实例化一个字节数组，长度为最小缓冲区的长度
        bytesAudioRecord = new byte[bufferSizeInBytesWrite];
        // 实例化一个链表，用来存放字节组数
        bytesAudioRecordArray = new LinkedList<byte[]>();

        // 启动录制线程
        record = new Thread(new recordSound());
        record.start();
    }

    /**
     * AudioTrack初始化 播放
     */
    private void initAudioTrack() {
        // AudioTrack 得到播放最小缓冲区的大小
        bufferSizeInBytesPlay = AudioTrack.getMinBufferSize(AUDIO_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        // 实例化播放音频对象
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, AUDIO_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytesPlay,
                AudioTrack.MODE_STREAM);
        // 实例化一个长度为播放最小缓冲大小的字节数组
        bytesAudioTrack = new byte[bufferSizeInBytesPlay];

        // 启动播放线程
        play = new Thread(new playRecord());
        play.start();
    }


    //  录音线程
    class recordSound implements Runnable {
        @Override
        public void run() {
            Log.e(TAG, "........recordSound run()......");
            byte[] bytes_pkg;
            // 开始录音
            audioRecord.startRecording();
//
            while (flagAutoRecord) {
                audioRecord.read(bytesAudioRecord, 0, bufferSizeInBytesWrite);
                bytes_pkg = bytesAudioRecord.clone();
                Log.e(TAG, "........recordSound bytes_pkg==" + bytes_pkg.length + "  bufferSizeInBytesWrite=" + bytesAudioRecord.length);
                if (bytesAudioRecordArray.size() >= 2) {
                    bytesAudioRecordArray.removeFirst();
                }

                //将最新的一个数组存入
                bytesAudioRecordArray.add(bytes_pkg);
            }

//            writeDateTOFile();//往文件中写入裸数据
        }
    }

    //播放线程
    class playRecord implements Runnable {
        @Override
        public void run() {
            Log.e(TAG, "........playRecord run()......");
            byte[] bytes_pkg = null;
            // 开始播放
            audioTrack.play();

            while (flagAutoTrack) {
                try {
                    bytesAudioTrack = bytesAudioRecordArray.getFirst();
                    bytes_pkg = bytesAudioTrack.clone();

                    Log.e(TAG, "........trackSound bytes_pkg==" + bytes_pkg.length + "  bytesAudioTrack=" + bytesAudioTrack.length);

                    audioTrack.write(bytes_pkg, 0, bytes_pkg.length);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * AudioRecord停止
     */
    public void stop() {
        flagAutoRecord = false;
        flagAutoTrack = false;
        if (audioTrack != null) {
            audioTrack.stop();
        }
        if (audioRecord != null) {
            audioRecord.stop();
        }
    }


    /**
     * 将录音文件存到本地
     */
    private void writeDateTOFile() {
        // new一个byte数组用来存一些字节数据，大小为缓冲区大小
        byte[] audiodata = new byte[bufferSizeInBytesWrite];
        FileOutputStream fos = null;
        int readsize = 0;

        String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String mAudioAMRPath = fileBasePath + "/" + "glassRecordTest.wav";
        try {
            File file = new File(mAudioAMRPath);
            if (file.exists()) {
                file.delete();
            }
            fos = new FileOutputStream(file);// 建立一个可存取字节的文件
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (flagAutoRecord) {
            readsize = audioRecord.read(audiodata, 0, bufferSizeInBytesWrite);
            if (AudioRecord.ERROR_INVALID_OPERATION != readsize && fos != null) {
                try {
                    fos.write(audiodata);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            if (fos != null)
                fos.close();// 关闭写入流
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
