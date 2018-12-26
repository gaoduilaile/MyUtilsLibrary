package cn.heima.myutilslibrary.tts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.sunflower.FlowerCollector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import cn.heima.myutilslibrary.R;
import cn.heima.myutilslibrary.tts.speech.util.JsonParser;

public class IatDemo extends Activity implements OnClickListener {
    private static String TAG = IatDemo.class.getSimpleName();
    // 语音听写对象
    private SpeechRecognizer mIat;

    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    private EditText mResultText;
    private Toast mToast;
    //	private SharedPreferences mSharedPreferences;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

//	private boolean mTranslateEnable = false;

    private boolean cyclic = true;//音频流识别是否循环调用
    private int ret;


    @SuppressLint("ShowToast")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.iatdemo);

        SpeechUtility.createUtility(IatDemo.this, "appid=5c22e0e9");

        initLayout();
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(IatDemo.this, mInitListener);

        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源

//		mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME,
//        Activity.MODE_PRIVATE);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        mResultText = ((EditText) findViewById(R.id.iat_text));

    }

    /**
     * 初始化Layout。
     */
    private void initLayout() {
        findViewById(R.id.iat_recognize).setOnClickListener(IatDemo.this);
        findViewById(R.id.iat_stop).setOnClickListener(IatDemo.this);
    }


    @Override
    public void onClick(View view) {
        if (null == mIat) {
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            this.showTip("创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化");
            return;
        }

        int i = view.getId();
        if (i == R.id.iat_recognize) {
//            executeStream();
            init();

            // 停止听写
        } else if (i == R.id.iat_stop) {
            mIat.stopListening();
            flagAutoTrack = false;
            flagAutoRecord = false;

            stop();
            // 取消听写
        }
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.e(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code);
            }
        }
    };

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            Log.e(TAG, "开始说话 ");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            if (error.getErrorCode() == 14002) {
                showTip(error.getPlainDescription(true) + "\n请确认是否已开通翻译功能");
            } else {
                showTip(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            Log.e(TAG, "结束说话 ");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.e(TAG, "onResult: " + results.getResultString());

            flagAutoTrack = false;

            printResult(results);
            if (isLast & cyclic) {
                // TODO 最后的结果
//                Message message = Message.obtain();
//                message.what = 0x001;
//                han.sendMessageDelayed(message, 100);
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            Log.e(TAG, "返回音频数据：" + data.length + " 当前正在说话，音量大小：" + volume);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }


        mResultText.setText(resultBuffer.toString());
        mResultText.setSelection(mResultText.length());
    }


    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    /**
     * 参数设置
     *
     * @return
     */
    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
//        mIat.setParameter(SpeechConstant.ACCENT, lag);
//
//        if (mTranslateEnable) {
//            mIat.setParameter(SpeechConstant.ORI_LANG, "cn");
//            mIat.setParameter(SpeechConstant.TRANS_LANG, "en");
//        }
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        //此处用于设置dialog中不显示错误码信息
        //mIat.setParameter("view_tips_plain","false");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != mIat) {
            // 退出时释放连接
            mIat.cancel();
            mIat.destroy();
        }

        stop();
    }

    @Override
    protected void onResume() {
        // 开放统计 移动数据统计分析
        FlowerCollector.onResume(IatDemo.this);
        FlowerCollector.onPageStart(TAG);
        super.onResume();
    }

    @Override
    protected void onPause() {
        // 开放统计 移动数据统计分析
        FlowerCollector.onPageEnd(TAG);
        FlowerCollector.onPause(IatDemo.this);
        super.onPause();
    }

    //执行音频流识别操作
    private void executeStream() {
        mResultText.setText(null);// 清空显示内容
        mIatResults.clear();
        // 设置参数
        setParam();
        // 设置音频来源为外部文件
        mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
        // 也可以像以下这样直接设置音频文件路径识别（要求设置文件在sdcard上的全路径）：
        // mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-2");
        //mIat.setParameter(SpeechConstant.ASR_SOURCE_PATH, "sdcard/XXX/XXX.pcm");
        // 函数调用返回值
//        ret = mIat.startListening(mRecognizerListener);
//        Log.e("executeStream ", "ret=" + ret);

    }


    /**
     * 播放音频对象
     */
    private AudioTrack audioTrack;
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
    public final static int AUDIO_SAMPLE_RATE = 16000;  //44.1KHz,普遍使用的频率

    //音频输入-麦克风
    public final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
//    @SuppressLint("HandlerLeak")
//    Handler han = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == 0x001) {
//                executeStream();
//            }
//        }
//    };


    /**
     * 初始化
     */
    public void init() {
//        executeStream();
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
//                Log.e(TAG, "........recordSound bytes_pkg==" + bytes_pkg.length + "  bufferSizeInBytesWrite=" + bytesAudioRecord.length);
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

            while (flagAutoTrack) {
                try {
                    bytesAudioTrack = bytesAudioRecordArray.getFirst();
                    bytes_pkg = bytesAudioTrack.clone();

//                    ret = mIat.startListening(mRecognizerListener);

                    // 一次（也可以分多次）写入音频文件数据，数据格式必须是采样率为8KHz或16KHz（本地识别只支持16K采样率，云端都支持），
                    // 位长16bit，单声道的wav或者pcm
                    // 写入8KHz采样的音频时，必须先调用setParameter(SpeechConstant.SAMPLE_RATE, "8000")设置正确的采样率
                    // 注：当音频过长，静音部分时长超过VAD_EOS将导致静音后面部分不能识别。
                    // 音频切分方法：FucUtil.splitBuffer(byte[] buffer,int length,int spsize);

//                    mIat.writeAudio(bytes_pkg, 0, bytes_pkg.length);
//                    mIat.stopListening();

                    Log.e("executeStream ", " bytes_pkg.length=" + bytes_pkg.length);


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
}
