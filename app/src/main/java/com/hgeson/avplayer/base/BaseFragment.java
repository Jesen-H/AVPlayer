package com.hgeson.avplayer.base;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hgeson.avplayer.callback.NetEvent;
import com.hgeson.avplayer.utils.EventUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @Describe：
 * @Date：2018/9/27
 * @Author：hgeson
 */

public abstract class BaseFragment extends Fragment implements NetEvent{
    protected Unbinder unbinder;
    private NetReceiver netReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(setContentLayout(),container,false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViews();
        init();
        setListeners();
        EventUtil.register(this);
    }

    protected abstract int setContentLayout();

    protected abstract void findViews();

    protected abstract void init();

    protected abstract void setListeners();

    @Override
    public void onStart() {
        super.onStart();
        if (netReceiver == null){
            netReceiver = new NetReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            getActivity().registerReceiver(netReceiver,filter);
        }
        netReceiver.setNetEvent(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String str){

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventUtil.unregister(this);
        if (netReceiver != null) {
            //注销广播
            getActivity().unregisterReceiver(netReceiver);
        }
    }

    @Override
    public void onNetChange(int netMobile) {

    }
}
