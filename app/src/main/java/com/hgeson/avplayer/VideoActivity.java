package com.hgeson.avplayer;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hgeson.avplayer.base.BaseActivity;
import com.hgeson.avplayer.utils.ScreenUtils;
import com.hgeson.avplayer.view.JsonVideoView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Describe：
 * @Date：2018/9/27
 * @Author：hgeson
 */
public class VideoActivity extends BaseActivity {
    @BindView(R.id.video)
    JsonVideoView videoPlayer;
    @BindView(R.id.recycier_view)
    RecyclerView recycierView;

    private List<String> list = new ArrayList<>();
    private BaseQuickAdapter<String, BaseViewHolder> adapter;
    private int screenWidth, screenHeight;
    private int netMobile;

    @Override
    protected int setContentLayout() {
        return R.layout.activity_video;
    }

    @Override
    protected void initView() {
        videoPlayer.setBackgroundColor(Color.BLACK);
        screenWidth = ScreenUtils.getScreenWidth(this);
        screenHeight = ScreenUtils.getScreenHeight(this);

        videoPlayer.setVidoUrl("http://oss.meibbc.com/BeautifyBreast/file/video/ueditor/1529919113810.mp4");
        videoPlayer.setVideoImage("https://oss.meibbc.com/BeautifyBreast/file/health/1531740544456.png");

        for (int i = 0; i < 20; i++) {
            list.add("第" + (i + 1) + "个item~");
        }
        recycierView.setLayoutManager(new LinearLayoutManager(this));
        recycierView.setAdapter(adapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_recycler, null) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv, item);
            }
        });
        adapter.addData(list);
    }

    @Override
    protected void setListener() {
        videoPlayer.setOnStatus(new JsonVideoView.onStatus() {
            @Override
            public void onFinish() {
                if (VideoActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    recycierView.setVisibility(View.VISIBLE);
                    setSize(false);
                    videoPlayer.setImageFull(R.mipmap.new_info_all_window);
                } else {
                    finish();
                }
            }

            @Override
            public void onFull() {
                if (VideoActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    recycierView.setVisibility(View.VISIBLE);
                    videoPlayer.setImageFull(R.mipmap.new_info_all_window);
                    setSize(false);
                } else if (VideoActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    recycierView.setVisibility(View.GONE);
                    videoPlayer.setImageFull(R.mipmap.small_full);
                    setSize(true);
                }
            }

            @Override
            public void onShare() {

            }

            @Override
            public void onPlaying() {

            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        videoPlayer.setPlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.pause();
        videoPlayer.setImageResoure(R.mipmap.new_info_audio_play_status);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoPlayer.release();
    }

    @Override
    public void onBackPressed() {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            recycierView.setVisibility(View.VISIBLE);
            setSize(false);
            videoPlayer.setImageFull(R.mipmap.new_info_all_window);
        }
        super.onBackPressed();
    }

    private void setSize(boolean mIsFullScreen) {
        ViewGroup.LayoutParams lp = videoPlayer.getLayoutParams();
        if (mIsFullScreen) {
            lp.width = screenHeight;
            lp.height = screenWidth;
        } else {
            lp.width = screenWidth;
            lp.height = ScreenUtils.dip2px(this, 230);
        }
        videoPlayer.setLayoutParams(lp);
    }

    @Override
    public void onNetChange(int netMobile) {
        super.onNetChange(netMobile);
        this.netMobile = netMobile;
        isNetConnect();
    }

    private void isNetConnect() {
        switch (netMobile) {
            case 1://wifi
                videoPlayer.setNetwrokState(true);
                if (!videoPlayer.isPlaying()) {
                    videoPlayer.setBigPlayer();
                }
                videoPlayer.setHasNet();
                break;
            case 0://移动数据
                videoPlayer.setNetwrokState(false);
                videoPlayer.setIsNet(true);
                if (!videoPlayer.isPlaying()) {
                    videoPlayer.setBigPlayer();
                }
                videoPlayer.setHasNet();
                break;
            case -1://没有网络
                videoPlayer.setNetwrokState(false);
                videoPlayer.setIsNet(false);
                break;
        }
    }
}
