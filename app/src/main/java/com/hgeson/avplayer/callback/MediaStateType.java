package com.hgeson.avplayer.callback;

/**
 * @Describe：
 * @Date：2018/8/13
 * @Author：hgeson
 */

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//状态（初始化、正在准备、正在播放、暂停、释放）
@IntDef({MediaStateType.INIT,
        MediaStateType.PREPARING,
        MediaStateType.PLAYING,
        MediaStateType.PAUSE,
        MediaStateType.RELEASE})
@Retention(RetentionPolicy.SOURCE)
public @interface MediaStateType {
    int INIT = 0;
    int PREPARING = 1;
    int PLAYING = 2;
    int PAUSE = 3;
    int RELEASE = 4;
}
