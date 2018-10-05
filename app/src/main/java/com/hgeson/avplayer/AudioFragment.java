package com.hgeson.avplayer;

import android.Manifest;
import android.content.Intent;
import android.media.AudioRecord;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hgeson.avplayer.base.BaseFragment;
import com.hgeson.avplayer.utils.PermissionUtils;
import com.hgeson.avplayer.view.AudioPlayView;

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

public class AudioFragment extends BaseFragment {
    @BindView(R.id.audio_view)
    AudioPlayView audioView;
    @BindView(R.id.recycier_view)
    RecyclerView recycierView;
    Unbinder unbinder;

    private List<String> list = new ArrayList<>();
    private BaseQuickAdapter<String, BaseViewHolder> adapter;

    @Override
    protected int setContentLayout() {
        return R.layout.activity_audio;
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void init() {
        audioView.setUrl("https://oss.meibbc.com/BeautifyBreast/file/audio/1529546659862.mp3");
        for (int i = 0; i < 20; i++) {
            list.add("第" + (i + 1) + "个item~");
        }
        recycierView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycierView.setAdapter(adapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_recycler, null) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv, item).addOnClickListener(R.id.tv_message_delete).addOnClickListener(R.id.content);
            }
        });
        adapter.addData(list);
    }

    @Override
    protected void setListeners() {
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.tv_message_delete:
                        adapter.remove(position);
                        break;
                    case R.id.content:
                        if (PermissionUtils.initPermissions(getActivity(),new String[]{
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                                new String[]{"录音","存储","读取"})){
                            startActivity(new Intent(getActivity(), AudioRecordActivity.class));
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onEvent(String str) {
        super.onEvent(str);
        if (str.equals(getString(R.string.back_top))){
            recycierView.smoothScrollToPosition(0);
        }
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
    public void onDestroy() {
        super.onDestroy();
        if (audioView != null) {
            audioView.release();
        }
    }
}
