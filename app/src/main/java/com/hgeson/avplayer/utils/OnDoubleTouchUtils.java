package com.hgeson.avplayer.utils;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by hgeson on 2018/6/5.
 * @描述：双击事件
 * @作者：hgeson
 */

public class OnDoubleTouchUtils implements View.OnTouchListener {
    private int count = 0;
    private long firClick = 0;
    private long secClick = 0;
    private Context context;

    /*间隔秒数*/
    private final int interval = 500;
    private DoubleClickCallback mCallback;

    public interface DoubleClickCallback {
        void onDoubleClick();

        void onFirstClick();
    }

    public OnDoubleTouchUtils(DoubleClickCallback mCallback) {
        super();
        this.mCallback = mCallback;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            count++;
            if (1 == count) {
                firClick = System.currentTimeMillis();
                mCallback.onFirstClick();
                Log.e("TAG","firstClick()------");
            } else if (2 == count) {
                secClick = System.currentTimeMillis();
                if (secClick - firClick < interval) {
                    if (mCallback != null) {
                        mCallback.onDoubleClick();
                        Log.e("TAG","secondClick()------");
                    } else {
                        Log.e("TAG", "请在构造方法中传入一个双击回调");
                    }
                    count = 0;
                    firClick = 0;
                } else {
                    firClick = secClick;
                    count = 1;
                    mCallback.onFirstClick();
                    Log.e("TAG","firstsssClick()------");
                }
                secClick = 0;
            }
        }
        return true;
    }
}
