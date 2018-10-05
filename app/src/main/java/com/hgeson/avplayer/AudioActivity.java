package com.hgeson.avplayer;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hgeson.avplayer.base.BaseActivity;
import com.hgeson.avplayer.view.AudioPlayView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Describe：
 * @Date：2018/9/27
 * @Author：hgeson
 */
public class AudioActivity extends BaseActivity {
    private String TAG = this.getClass().getSimpleName();

    @BindView(R.id.audio_view)
    AudioPlayView audioView;
    @BindView(R.id.recycier_view)
    RecyclerView recycierView;

    private List<String> list = new ArrayList<>();
    private BaseQuickAdapter<String, BaseViewHolder> adapter;

    //录音所保存的文件
    private String mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio/";
    private File mAudioFile;
    private File[] fs;
    private File mFile;

    @Override
    protected int setContentLayout() {
        return R.layout.activity_audio;
    }

    @Override
    protected void initView() {
        mAudioFile = new File(mFilePath);
        fs = mAudioFile.listFiles();
        if (mAudioFile.isDirectory()) {
            for (File file : fs) {
                String path = file.getAbsolutePath();
                if (path.endsWith(".mp3")) {
                    mFile = fs[fs.length - 1];
                    Log.e(TAG, "mFile === " + mFile.getAbsolutePath());
                }
            }
        }

        if (mFile.exists()){
            audioView.setUrl(mFile.getAbsolutePath());
        }
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
        audioView.release();
    }
}
