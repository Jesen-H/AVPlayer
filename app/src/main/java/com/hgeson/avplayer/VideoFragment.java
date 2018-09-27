package com.hgeson.avplayer;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hgeson.avplayer.base.BaseFragment;
import com.hgeson.avplayer.callback.NetEvent;
import com.hgeson.avplayer.utils.EventUtil;
import com.hgeson.avplayer.utils.ScreenUtils;
import com.hgeson.avplayer.view.JsonVideoView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @Describe：
 * @Date：2018/9/27
 * @Author：hgeson
 */
public class VideoFragment extends BaseFragment implements NetEvent{
    @BindView(R.id.video)
    JsonVideoView videoPlayer;
    @BindView(R.id.recycier_view)
    RecyclerView recycierView;
    Unbinder unbinder;

    private List<String> list = new ArrayList<>();
    private BaseQuickAdapter<String, BaseViewHolder> adapter;
    private int screenWidth, screenHeight;
    private int netMobile;

    @Override
    protected int setContentLayout() {
        return R.layout.activity_video;
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void init() {
        videoPlayer.setBackgroundColor(Color.BLACK);
        screenWidth = ScreenUtils.getScreenWidth(getActivity());
        screenHeight = ScreenUtils.getScreenHeight(getActivity());

        videoPlayer.setVidoUrl("http://oss.meibbc.com/BeautifyBreast/file/video/ueditor/1529919113810.mp4");
        videoPlayer.setVideoImage("https://oss.meibbc.com/BeautifyBreast/file/health/1531740544456.png");

        for (int i = 0; i < 20; i++) {
            list.add("第" + (i + 1) + "个item~");
        }
        recycierView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycierView.setAdapter(adapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_recycler, null) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv, item);
            }
        });
        adapter.addData(list);
    }

    @Override
    protected void setListeners() {
        videoPlayer.setOnStatus(new JsonVideoView.onStatus() {
            @Override
            public void onFinish() {
                if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    recycierView.setVisibility(View.VISIBLE);
                    EventUtil.post("缩小显示");
                    setSize(false);
                    videoPlayer.setImageFull(R.mipmap.new_info_all_window);
                }
            }

            @Override
            public void onFull() {
                if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    recycierView.setVisibility(View.VISIBLE);
                    EventUtil.post("缩小显示");
                    videoPlayer.setImageFull(R.mipmap.new_info_all_window);
                    setSize(false);
                } else if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    recycierView.setVisibility(View.GONE);
                    EventUtil.post("全屏显示");
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        videoPlayer.setPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        videoPlayer.pause();
        videoPlayer.setImageResoure(R.mipmap.new_info_audio_play_status);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setSize(boolean mIsFullScreen) {
        ViewGroup.LayoutParams lp = videoPlayer.getLayoutParams();
        if (mIsFullScreen) {
            lp.width = screenHeight;
            lp.height = screenWidth;
        } else {
            lp.width = screenWidth;
            lp.height = ScreenUtils.dip2px(getActivity(), 230);
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
