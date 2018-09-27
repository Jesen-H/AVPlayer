package com.hgeson.avplayer.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hgeson.avplayer.R;
import com.hgeson.avplayer.callback.MediaStateType;
import com.hgeson.avplayer.utils.OnDoubleTouchUtils;
import com.hgeson.avplayer.utils.TimeUtil;

/**
 * @Describe：详情页视频播放器
 * @Date：2018/6/14
 * @Author：hgeson
 */

public class JsonVideoView extends RelativeLayout implements View.OnClickListener {
    private Context context;
    private View view;

    /*animation*/
    private Animation outAnima;
    private Animation inAnima;
    private Animation topOutAnima;
    private Animation topInAnima;

    private TextureVideoView textureView;
    private ImageView backOff;
    private ImageView coverImage;
    private ImageView playState;
    private ImageView fullScreen;
    private ImageView playCenter;
    private TextView noPlay;
    private TextView agressPlay;
    private TextView startTime;
    private TextView endTime;
    private SeekBar seekBar;
    private IOSLoadingView loading;
    private LinearLayout bottomView;
    private LinearLayout bigPlayer;
    private LinearLayout iswifi;
    private LinearLayout netError;
    private RelativeLayout mTopView;

    private ImageView share;

    private boolean isTool = false;
    private boolean modeState;
    private boolean doubliClick = false;
    private boolean isNetwork;
    //默认执行进度条等操作
    private boolean isClick = true;

    private onStatus status;

    private String videoUrl;
    private String pic;

    public interface onStatus {
        void onFinish();

        void onFull();

        void onShare();

        void onPlaying();
    }

    public void setOnStatus(onStatus status) {
        this.status = status;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    startTime.setText(TimeUtil.stringTime(textureView.getCurrentPosition()));
                    seekBar.setProgress(textureView.getCurrentPosition());
                    break;
            }
        }
    };

    public JsonVideoView(Context context) {
        this(context,null);
    }

    public JsonVideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public JsonVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.item_new_info_new_player, this);
        textureView = (TextureVideoView) view.findViewById(R.id.video_player);
        backOff = (ImageView) view.findViewById(R.id.back_off);
        coverImage = (ImageView) view.findViewById(R.id.video_image);
        playState = (ImageView) view.findViewById(R.id.playstate);
        fullScreen = (ImageView) view.findViewById(R.id.new_info_all_video);
        playCenter = (ImageView) view.findViewById(R.id.play_center);
        startTime = (TextView) view.findViewById(R.id.start_time);
        endTime = (TextView) view.findViewById(R.id.end_time);
        agressPlay = (TextView) view.findViewById(R.id.agress_play);
        noPlay = (TextView) view.findViewById(R.id.no_play);
        loading = (IOSLoadingView) view.findViewById(R.id.progress_wite);
        bottomView = (LinearLayout) view.findViewById(R.id.bottom_status);
        bigPlayer = (LinearLayout) view.findViewById(R.id.bigplay);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        iswifi = (LinearLayout) view.findViewById(R.id.iswifi);
        netError = (LinearLayout) view.findViewById(R.id.neterror);
        share = (ImageView) view.findViewById(R.id.share);
        mTopView = (RelativeLayout) view.findViewById(R.id.top_status);

        setBackgroundColor(Color.BLACK);

        outAnima = AnimationUtils.loadAnimation(context, R.anim.exit_from_bottom);
        inAnima = AnimationUtils.loadAnimation(context, R.anim.enter_from_bottom);
        topOutAnima = AnimationUtils.loadAnimation(context, R.anim.topexit_from_bottom);
        topInAnima = AnimationUtils.loadAnimation(context, R.anim.topenter_from_bottom);

        bigPlayer.setOnClickListener(this);
        backOff.setOnClickListener(this);
        playState.setOnClickListener(this);
        fullScreen.setOnClickListener(this);
        noPlay.setOnClickListener(this);
        agressPlay.setOnClickListener(this);
        share.setOnClickListener(this);
        textureView.setEnabled(false);
        bottomView.setVisibility(View.GONE);
        bottomView.getBackground().setAlpha(isTool ? 0 : 150);
        setListener();
    }

    public void setVideoImage(String pic) {
        if (pic.contains("http")) {
            Glide.with(context).load(pic).into(coverImage);
        } else {
            Glide.with(context).load("http://oss.meibbc.com/" + pic).into(coverImage);
        }
    }

    public void setVidoUrl(String url) {
        if (url.contains("http")) {
            if (url.contains("https")) {
                this.videoUrl = url.replace("https", "http");
            } else {
                this.videoUrl = url;
            }
        } else {
            this.videoUrl = "http://oss.meibbc.com/" + url;
        }
    }

    public void setBigPlayerVisible(boolean isVisible){
        bigPlayer.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void setShareVisible(boolean isVisible){
        share.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void setVideoImageVisible(boolean isVisible){
        coverImage.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void setIosLoadingVisible(boolean isVisible){
        loading.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void setImageResoure(int playstate) {
        playState.setImageResource(playstate);
    }

    public void setImageFull(int full) {
        fullScreen.setImageResource(full);
    }

    public void pause() {
        textureView.pause();
    }

    public void release() {
        textureView.release();
    }

    public void stop(){
        textureView.stop();
    }

    public void setToolIcon(boolean isTool){
        this.isTool = isTool;
        bottomView.getBackground().setAlpha(isTool ? 0 : 150);
    }

    private void setListener() {
        /*double click*/
        textureView.setOnTouchListener(new OnDoubleTouchUtils(new OnDoubleTouchUtils.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                if (doubliClick) {
                    play();
                }
            }

            @Override
            public void onFirstClick() {
                onControlPanel();
            }
        }));

        /*TextureView Listener*/
        textureView.setOnStateChangeListener(new TextureVideoView.OnStateChangeListener() {
            @Override
            public void onSurfaceTextureDestroyed(SurfaceTexture surface) {

            }

            @Override
            public void onBuffering() {
                loading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPlaying() {
                playState.setImageResource(isTool ? R.mipmap.tool_stop : R.mipmap.new_info_audio_stop);
                status.onPlaying();
                loading.setVisibility(View.GONE);
                coverImage.setVisibility(View.GONE);
                seekBar.setMax(textureView.getDuration());
                endTime.setText("/" + TimeUtil.stringTime(textureView.getDuration()));
            }

            @Override
            public void onSeek() {

            }

            @Override
            public void onStop() {
            }

            @Override
            public void onRun() {
                handler.sendEmptyMessage(2);
            }

            @Override
            public void onComplete() {
                textureView.pause();
                textureView.seekTo(0);
                playState.setImageResource(isTool ? R.mipmap.tool_play : R.mipmap.new_info_audio_play_status);
//                bigPlayer.setVisibility(View.VISIBLE);
//                playCenter.setImageResource(R.mipmap.replay);
            }

            @Override
            public void onVideoSizeChanged(int mVideoWidth, int mVideoHeight) {

            }
        });

        /*Seekbar Listener*/
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    startTime.setText(TimeUtil.stringTime(progress));//set time
                    textureView.getMediaPlayer().seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeMessages(2);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessageDelayed(2, 3000);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back_off) {
            status.onFinish();
        }else if (id == R.id.share){
            status.onShare();
        }else if (id == R.id.playstate) {
            play();
        } else if (id == R.id.new_info_all_video) {
            status.onFull();
        } else if (id == R.id.bigplay) {
            if (modeState) {
                play();
                doubliClick = true;
                textureView.setEnabled(true);
                bigPlayer.setVisibility(GONE);
            } else {
                if (isNetwork) {
                    bigPlayer.setVisibility(GONE);
                    iswifi.setVisibility(VISIBLE);
                }else{
                    bigPlayer.setVisibility(GONE);
                    netError.setVisibility(VISIBLE);
                }
            }
        } else if (id == R.id.no_play) {
            iswifi.setVisibility(GONE);
            bigPlayer.setVisibility(VISIBLE);
        } else if (id == R.id.agress_play) {
            iswifi.setVisibility(GONE);
            play();
            doubliClick = true;
            textureView.setEnabled(true);
            setNetwrokState(true);
        }
    }

    public boolean isPlaying(){
        return textureView.isPlaying();
    }

    public void setHasNet() {
        netError.setVisibility(View.GONE);
    }

    public void setBigPlayer(){
        bigPlayer.setVisibility(View.VISIBLE);
    }

    public void setNetwrokState(boolean mode) {
        modeState = mode;
    }

    public void setIsNet(boolean net){
        isNetwork = net;
    }

    public void setGoneBackOff(boolean isGone){
        backOff.setVisibility(isGone ? GONE : VISIBLE);
    }

    public void setPlay(){
        if (textureView.getState() == MediaStateType.PAUSE){
            play();
        }
    }

    public void play() {
        if (textureView.getState() == MediaStateType.INIT || textureView.getState() == MediaStateType.RELEASE) {
            if (TextUtils.isEmpty(videoUrl)) {
                return;
            }
            textureView.play(videoUrl);
            textureView.start();
            textureView.statusRun(true);
            bigPlayer.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
        } else if (textureView.getState() == MediaStateType.PAUSE) {
            textureView.start();
            playState.setImageResource(isTool ? R.mipmap.tool_stop : R.mipmap.new_info_audio_stop);
        } else if (textureView.getState() == MediaStateType.PLAYING) {
            textureView.pause();
            playState.setImageResource(isTool ? R.mipmap.tool_play : R.mipmap.new_info_audio_play_status);
        } else if (textureView.getState() == MediaStateType.PREPARING) {
            textureView.stop();
            playState.setImageResource(isTool ? R.mipmap.tool_play : R.mipmap.new_info_audio_play_status);
        }
    }

    /*control bottom*/
    public void onControlPanel() {
        if (isClick) {
            bottomView.startAnimation(inAnima);
            mTopView.startAnimation(topInAnima);
            bottomView.setVisibility(View.VISIBLE);
            mTopView.setVisibility(View.VISIBLE);
            isClick = false;
        }else{
            bottomView.startAnimation(outAnima);
            bottomView.setVisibility(View.GONE);
            mTopView.startAnimation(topOutAnima);
            mTopView.setVisibility(View.GONE);
            isClick = true;
        }
    }
}
