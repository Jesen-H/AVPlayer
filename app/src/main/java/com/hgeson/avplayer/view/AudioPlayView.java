package com.hgeson.avplayer.view;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hgeson.avplayer.R;
import com.hgeson.avplayer.contract.AudioRecorderContract;
import com.hgeson.avplayer.utils.TimeUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hgeson on 2018/6/30.
 * 描述：
 * 作者：hgeson
 */

public class AudioPlayView extends RelativeLayout implements View.OnClickListener ,MediaPlayer.OnPreparedListener{
    Context context;
    private ImageView playstate,audioFrequency;
    private TextView beginTime,endTime;
    private SeekBar seekBar;
    private Timer timer;
    private TimeUtil timeUtil = new TimeUtil();
    private boolean isChanging = false;//互斥变量，防止定时器与SeekBar拖动时进度冲突
    private boolean isCellPlay = false;/*在挂断电话的时候，用于判断是否为是来电时中断*/
    private boolean isFitstInit = true;
    private boolean isPlaying = false;
    private int currentPosition;//当前音乐播放的进度
    private MyPhoneStateListener myPhoneStateListener;
    private MediaPlayer audioView;
    private onAudioPlay audioPlay;
    private int duration;

    public interface onAudioPlay{
        void onAudioplay();
    }

    public void setOnAudioPlayListener(onAudioPlay audioPlayListener){
        this.audioPlay = audioPlayListener;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AudioRecorderContract.PLAY_COMPLETION:
                    Toast.makeText(context, context.getString(R.string.play_over), Toast.LENGTH_SHORT).show();
                    break;
                case AudioRecorderContract.PLAY_ERROR:
                    Toast.makeText(context, context.getString(R.string.play_error), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public AudioPlayView(Context context) {
        super(context);
        init(context,null,0);
    }

    public AudioPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs,0);
    }

    public AudioPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    private void init(Context context,AttributeSet attrs,int defStyleAttr) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.item_new_info_audio_detail,this);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        playstate = (ImageView) view.findViewById(R.id.audio_play_state);
        beginTime = (TextView) view.findViewById(R.id.new_info_audio_time_begin);
        endTime = (TextView) view.findViewById(R.id.new_info_audio_time_end);
        audioFrequency = (ImageView) view.findViewById(R.id.audio_frequency);
        playstate.setOnClickListener(new PalyListener());
        seekBar.setOnSeekBarChangeListener(new SeekBarListener());
        myPhoneStateListener = new MyPhoneStateListener();
        TelephonyManager phoneyMana = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        phoneyMana.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onClick(View v) {

    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING://来电，应当停止音乐
                    if (audioView != null && audioView.isPlaying()) {
                        currentPosition = audioView.getCurrentPosition();//记录播放的位置
                        audioView.pause();//暂停
//                        audioLoadView.stop();
                        audioFrequency.setImageResource(R.drawable.icon_new_info_audio_static);
                        isCellPlay = true;//标记这是属于来电时暂停的标记
                        playstate.setImageResource(R.mipmap.new_info_audio_play_status);
                        timer.purge();//移除定时器任务;
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE://无电话状态
                    if (isCellPlay) {
                        isCellPlay = false;
                    }
                    break;
            }
        }
    }

    public void setUrl(String url){
        if (audioView == null){
            audioView = new MediaPlayer();
            audioView.reset();
            try {
                audioView.setAudioStreamType(AudioManager.STREAM_MUSIC);
                audioView.setDataSource(url);
                audioView.prepareAsync();
                audioView.setOnPreparedListener(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void release(){
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (audioView != null) {
            audioView.reset();
            audioView.release();
            audioView = null;
        }
        //关闭监听
        TelephonyManager tmgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        tmgr.listen(myPhoneStateListener, 0);
    }

    public class SeekBarListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            duration = audioView.getDuration();
            int position = audioView.getCurrentPosition();
            beginTime.setText(timeUtil.stringForTime(position));
            endTime.setText(timeUtil.stringForTime(duration));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isChanging = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            isChanging = false;
            audioView.seekTo(seekBar.getProgress());
            beginTime.setText(timeUtil.stringForTime(seekBar.getProgress()));
        }
    }

    /*播放或暂停事件处理*/
    private class PalyListener implements OnClickListener {
        public void onClick(View v) {
            if (audioView != null && isFitstInit) {
                isFitstInit = false;
                Glide.with(context)
                        .load(R.drawable.yuyin)
                        .asGif()
                        .dontAnimate()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(audioFrequency);
                playstate.setImageResource(R.mipmap.new_info_audio_stop);
                audioView.start();
                duration = audioView.getDuration();
//                audioPlay.onAudioplay();
                //将音乐总时间设置为SeekBar的最大值
                seekBar.setMax(duration);

                //监听播放时回调函数
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!isChanging) {
                            seekBar.setProgress(audioView.getCurrentPosition());
                        }
                    }
                }, 0, 50);
            } //音乐文件正在播放，则暂停并改变按钮样式
            else if (audioView != null && audioView.isPlaying()) {
                audioView.pause();
                audioFrequency.setImageResource(R.drawable.icon_new_info_audio_static);
                playstate.setImageResource(R.mipmap.new_info_audio_play_status);
            } else if (audioView != null && (!audioView.isPlaying())) {
                if (currentPosition > 0) {
                    audioView.seekTo(currentPosition);
                    currentPosition = 0;
                }
                //启动播放
                audioView.start();
                Glide.with(context)
                        .load(R.drawable.yuyin)
                        .asGif()
                        .dontAnimate()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(audioFrequency);
                playstate.setImageResource(R.mipmap.new_info_audio_stop);
//                audioPlay.onAudioplay();
            }

            //播放完监听
            audioView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    timer.cancel();
                    timer.purge();
                    //播放位置变为0
                    mediaPlayer.seekTo(0);
                    int position = mediaPlayer.getCurrentPosition();
                    audioFrequency.setImageResource(R.drawable.icon_new_info_audio_static);
                    beginTime.setText(timeUtil.stringForTime(position));
                    playstate.setImageResource(R.mipmap.new_info_audio_play_status);
                    handler.sendEmptyMessage(AudioRecorderContract.PLAY_COMPLETION);
                    isFitstInit = true;
                }
            });
            //播放发生错误监听事件
            audioView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    handler.sendEmptyMessage(AudioRecorderContract.PLAY_ERROR);
                    isFitstInit = true;
                    return true;
                }
            });
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        duration = mp.getDuration();
        int position = mp.getCurrentPosition();
        beginTime.setText(timeUtil.stringForTime(position));
        endTime.setText(timeUtil.stringForTime(duration));
    }

}
