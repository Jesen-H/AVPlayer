package com.hgeson.avplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hgeson.avplayer.base.BaseActivity;
import com.hgeson.avplayer.contract.AudioRecorderContract;
import com.hgeson.avplayer.utils.TimeUtil;
import com.hgeson.avplayer.utils.ToastUtils;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Describe：
 * @Date：2018/10/5
 * @Author：hgeson
 */
public class AudioRecordActivity extends BaseActivity {

    @BindView(R.id.record_time)
    TextView recordTime;
    @BindView(R.id.record_content)
    TextView recordContent;
    @BindView(R.id.audio_record)
    RelativeLayout audioRecord;
    @BindView(R.id.delete)
    ImageView delete;
    @BindView(R.id.submit)
    ImageView submit;
    @BindView(R.id.record_complete)
    LinearLayout recordComplete;

    private ExecutorService mExecutorService;
    //录音API
    private MediaRecorder mMediaRecorder;
    //录音开始时间与结束时间
    private long startTime, endTime;
    //录音所保存的文件
    private File mAudioFile;
    //录音文件保存位置
    private String mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio/";
    //当前是否正在播放
    private volatile boolean isPlaying;
    //播放音频文件API
    private MediaPlayer mediaPlayer;
    private String file;

    private File files;

    private int count = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AudioRecorderContract.RECORD_SUCCESS:
                    recordComplete.setVisibility(View.VISIBLE);
                    audioRecord.setVisibility(View.GONE);
                    recordContent.setText("录制完成");
                    break;
                case AudioRecorderContract.RECORD_FAIL:
                    ToastUtils.showToast(AudioRecordActivity.this, getString(R.string.record_fail));
                    break;
                case AudioRecorderContract.RECORD_TOO_SHORT:
                    ToastUtils.showToast(AudioRecordActivity.this, getString(R.string.time_too_short));
                    break;
                case AudioRecorderContract.PLAY_TIME:
                    if (count == 60) {
                        ToastUtils.showToast(AudioRecordActivity.this, "录制不能超过60秒哦~");
                        count = 0;
                        recordTime.setText("00:01:00");
                        handler.removeMessages(AudioRecorderContract.PLAY_TIME);
                        handler.sendEmptyMessage(AudioRecorderContract.RECORD_SUCCESS);
                        return;
                    }
                    String str = TimeUtil.showTimeCount((long) count);
                    count++;
                    recordTime.setText(str);
                    handler.sendEmptyMessageDelayed(AudioRecorderContract.PLAY_TIME, 1000);
                    break;
            }
        }
    };

    @Override
    protected int setContentLayout() {
        return R.layout.activity_audio_record;
    }

    @Override
    protected void initView() {
        files = new File(mFilePath);
        deleteFiles(files);

        final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //录音及播放要使用单线程操作
        mExecutorService = Executors.newSingleThreadExecutor();
        //按住说话 OnTouch事件
        audioRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    //按下操作
                    case MotionEvent.ACTION_DOWN:
                        vibrator.vibrate(30);//震动处理
                        //安卓6.0以上录音权限处理
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            startRecord();
                        } else {
                            startRecord();
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        stopRecord();
                        break;
                }
                //返回true
                return true;
            }
        });
    }

    @Override
    protected void setListener() {

    }

    public void deleteFiles(File file) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete();
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                if (files != null) {
                    for (File file1 : files) { // 遍历目录下所有的文件
                        this.deleteFiles(file1); // 把每个文件 用这个方法进行迭代
                    }
                }
            }
        }
    }

    /*开始录音*/
    private void startRecord() {
        recordContent.setText("正在录音中...");
        handler.sendEmptyMessage(AudioRecorderContract.PLAY_TIME);
        //异步任务执行录音操作
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                //播放前释放资源
//                releaseRecorder();
                //执行录音操作
                recordOperation();
            }
        });
    }

    /*释放资源*/
    private void releaseRecorder() {
        if (null != mMediaRecorder) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    /**
     * @description 录音操作
     * @author ldm
     * @time 2017/2/9 9:34
     */
    private void recordOperation() {
        //创建MediaRecorder对象
        mMediaRecorder = new MediaRecorder();
        //创建录音文件
        mAudioFile = new File(mFilePath + System.currentTimeMillis() + ".mp3");
        //创建父文件夹
        mAudioFile.getParentFile().mkdirs();

        try {
            //创建文件
            mAudioFile.createNewFile();
            //配置mMediaRecorder相应参数
            //从麦克风采集声音数据
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置保存文件格式为MP4
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            //设置采样频率,44100是所有安卓设备都支持的频率,频率越高，音质越好，当然文件越大
            mMediaRecorder.setAudioSamplingRate(44100);
            //设置声音数据编码格式,音频通用格式是AAC
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            //设置编码频率
            mMediaRecorder.setAudioEncodingBitRate(96000);
            //设置录音保存的文件
            mMediaRecorder.setOutputFile(mAudioFile.getAbsolutePath());
            //开始录音
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            //记录开始录音时间
            startTime = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
            recordFail();
        }
    }

    /*停止录音*/
    private void stopRecord() {
        //记录停止时间
        endTime = System.currentTimeMillis();
        //录音时间处理，比如只有大于10秒的录音才算成功
        int time = (int) ((endTime - startTime) / 1000);
        if (time >= 10 && time <= 60) {
            //录音成功,发Message
            handler.sendEmptyMessageDelayed(AudioRecorderContract.RECORD_SUCCESS, 500);
        } else {
            //停止录音
            mAudioFile = null;
            recordContent.setText("请重新录制");
            handler.sendEmptyMessage(AudioRecorderContract.RECORD_TOO_SHORT);
            recordTime.setText("00:00:00");
            count = 0;
            handler.removeMessages(AudioRecorderContract.PLAY_TIME);
        }
        handler.removeMessages(AudioRecorderContract.PLAY_TIME);
        //录音完成释放资源
        releaseRecorder();
    }

    /*录制失败*/
    private void recordFail() {
        mAudioFile = null;
        handler.sendEmptyMessage(AudioRecorderContract.RECORD_FAIL);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //页面销毁，线程要关闭
        releaseRecorder();
        mExecutorService.shutdownNow();
        handler.removeMessages(AudioRecorderContract.PLAY_TIME);
    }

    @OnClick({R.id.delete, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.delete:
                files = new File(mFilePath);
                deleteFiles(files);
                recordContent.setText("请按住下面录音按钮进行录制");
                audioRecord.setVisibility(View.VISIBLE);
                recordComplete.setVisibility(View.GONE);
                recordTime.setText("00:00");
                count = 0;
                break;
            case R.id.submit:
                startActivity(new Intent(this, AudioActivity.class));
                finish();
                break;
        }
    }
}
