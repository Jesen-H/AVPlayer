package com.hgeson.avplayer.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;


import com.hgeson.avplayer.callback.MediaStateType;

import java.io.IOException;


/**
 * @Describe：
 * @Date：2018/5/19
 * @Author：hgeson
 */

public class TextureVideoView extends TextureView implements TextureView.SurfaceTextureListener {
    private MediaPlayer mediaPlayer;
    private Context context;

    @MediaStateType
    int mediaState;

    private boolean isRun = true;

    private int mVideoWidth;//视频宽度
    private int mVideoHeight;//视频高度

    private boolean isChangeScreen = false;

    public TextureVideoView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public TextureVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public TextureVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public boolean isChangeScreen() {
        return isChangeScreen;
    }

    public void setChangeScreen(boolean changeScreen) {
        isChangeScreen = changeScreen;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public interface OnStateChangeListener {

        void onSurfaceTextureDestroyed(SurfaceTexture surface);

        void onBuffering();

        void onPlaying();

        void onVideoSizeChanged(int width, int height);

        void onSeek();

        void onStop();

        void onRun();

        void onComplete();
    }

    OnStateChangeListener onStateChangeListener;

    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener;
    }

    //监听视频的缓冲状态
    private MediaPlayer.OnInfoListener onInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (onStateChangeListener != null) {
                onStateChangeListener.onPlaying();
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    onStateChangeListener.onBuffering();
                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    onStateChangeListener.onPlaying();
                }
            }
            return false;
        }
    };

    //视频缓冲进度更新
    private MediaPlayer.OnBufferingUpdateListener bufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (onStateChangeListener != null) {
                if (mediaState == MediaStateType.PLAYING) {
                    onStateChangeListener.onSeek();
                }
            }
        }
    };

    private MediaPlayer.OnVideoSizeChangedListener sizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {

        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            mVideoHeight = mediaPlayer.getVideoHeight();
            mVideoWidth = mediaPlayer.getVideoWidth();
            updateTextureViewSize();
            if (onStateChangeListener != null) {
                onStateChangeListener.onVideoSizeChanged(mVideoWidth, mVideoHeight);
            }
        }
    };

    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (onStateChangeListener != null) {
                if (mediaState == MediaStateType.PLAYING) {
                    onStateChangeListener.onComplete();
                }
            }
        }
    };

    boolean isError = true;
    private MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.e("TAG", "what = " + what + ", extra = " + extra);
            if (isError){
                isError = false;
                return true;
            }
            stop();
            isError = true;
            return true;
        }
    };

    public void init() {
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width,
                                          int height) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    mediaState = MediaStateType.PLAYING;
                }
            });
            mediaPlayer.setOnInfoListener(onInfoListener);
            mediaPlayer.setOnBufferingUpdateListener(bufferingUpdateListener);
//            mediaPlayer.setOnVideoSizeChangedListener(sizeChangedListener);
            mediaPlayer.setOnCompletionListener(completionListener);
//            mediaPlayer.setOnErrorListener(errorListener);
        }
        mediaPlayer.setSurface(new Surface(surfaceTexture));
        if (isChangeScreen) {
            isChangeScreen = false;
        } else {
            mediaState = MediaStateType.INIT;
        }
    }

    //停止播放
    public void stop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mediaState == MediaStateType.INIT) {
                        return;
                    }
                    if (mediaState == MediaStateType.PREPARING) {
                        mediaPlayer.reset();
                        mediaState = MediaStateType.INIT;
                        System.out.println("prepare->reset");
                        return;
                    }
                    if (mediaState == MediaStateType.PAUSE) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaState = MediaStateType.INIT;
                        System.out.println("pause->init");
                        return;
                    }
                    if (mediaState == MediaStateType.PLAYING) {
                        mediaPlayer.pause();
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaState = MediaStateType.INIT;
                        System.out.println("playing->init");
                        return;
                    }
                } catch (Exception e) {
                    mediaPlayer.reset();
                    mediaState = MediaStateType.INIT;
                } finally {
                    if (onStateChangeListener != null) {
                        onStateChangeListener.onStop();
                    }
                }
            }
        }).start();
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (onStateChangeListener != null) {
            onStateChangeListener.onSurfaceTextureDestroyed(surface);
        }
        return false;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
                                            int height) {
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    //开始播放视频
    public void play(String videoUrl) {
        if (mediaState == MediaStateType.PREPARING) {
            stop();
            return;
        }
        mediaPlayer.setLooping(false);
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(videoUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
        mediaState = MediaStateType.PREPARING;
    }

    //暂停播放
    public void pause() {
        mediaPlayer.pause();
        mediaState = MediaStateType.PAUSE;
    }

    public void seekTo(int position){
        if (mediaPlayer != null){
            mediaPlayer.seekTo(position);
        }
    }

    public void statusRun(boolean isStop) {
        this.isRun = isStop;
    }

    //播放视频
    public void start() {
        mediaPlayer.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRun) {
                    onStateChangeListener.onRun();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        mediaState = MediaStateType.PLAYING;
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            return (int) mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (mediaPlayer != null) {
            return (int) mediaPlayer.getDuration();
        }
        return 0;
    }

    //获取播放状态
    public int getState() {
        return mediaState;
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void updateTextureViewSize() {
        updateTextureViewSizeCenter();
    }


    //重新计算video的显示位置，让其全部显示并据中
    private void updateTextureViewSizeCenter() {

        float sx = (float) getWidth() / (float) mVideoWidth;
        float sy = (float) getHeight() / (float) mVideoHeight;

        Matrix matrix = new Matrix();

        //第1步:把视频区移动到View区,使两者中心点重合.
        matrix.preTranslate((getWidth() - mVideoWidth) / 2, (getHeight() - mVideoHeight) / 2);

        //第2步:因为默认视频是fitXY的形式显示的,所以首先要缩放还原回来.
        matrix.preScale(mVideoWidth / (float) getWidth(), mVideoHeight / (float) getHeight());

        //第3步,等比例放大或缩小,直到视频区的一边和View一边相等.如果另一边和view的一边不相等，则留下空隙
        if (sx >= sy) {
            matrix.postScale(sy, sy, getWidth() / 2, getHeight() / 2);
        } else {
            matrix.postScale(sx, sx, getWidth() / 2, getHeight() / 2);
        }

        setTransform(matrix);
        postInvalidate();
    }


    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateTextureViewSize();
    }
}
