package com.hgeson.avplayer.base;

import android.app.Activity;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.hgeson.avplayer.callback.NetEvent;
import com.hgeson.avplayer.utils.EventUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;

/**
 * @Describe：
 * @Date：2018/9/27
 * @Author：hgeson
 */

public abstract class BaseActivity extends FragmentActivity implements NetEvent {

    private NetReceiver netReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏
        super.onCreate(savedInstanceState);
        if (getActionBar() != null){
            getActionBar().hide();
        }
        setContentView(setContentLayout());
        ButterKnife.bind(this);
        initView();
        setListener();
        EventBus.getDefault().register(this);
    }

    protected abstract int setContentLayout();

    protected abstract void initView();

    protected abstract void setListener();

    @Override
    protected void onStart() {
        super.onStart();
        if (netReceiver == null){
            netReceiver = new NetReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(netReceiver,filter);
        }
        netReceiver.setNetEvent(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (netReceiver != null) {
            //注销广播
            unregisterReceiver(netReceiver);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String action){

    }

    @Override
    public void onNetChange(int netMobile) {

    }
}
