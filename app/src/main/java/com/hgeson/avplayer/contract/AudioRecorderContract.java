package com.hgeson.avplayer.contract;

/**
 * Created by hgeson on 2018/5/3.
 * 描述：
 * 作者：hgeson
 */

public class AudioRecorderContract {

    public static final String PLAY_POSITION = "position";
    //录音成功
    public static final int RECORD_SUCCESS = 100;
    //录音失败
    public static final int RECORD_FAIL = 101;
    //录音时间太短
    public static final int RECORD_TOO_SHORT = 102;
    //安卓6.0以上手机权限处理
    public static final int PERMISSIONS_REQUEST_FOR_AUDIO = 1;
    //播放完成
    public static final int PLAY_COMPLETION = 103;
    //播放错误
    public static final int PLAY_ERROR = 104;

    public static final int PLAY_TIME = 105;

    public static final int FINISH = 106;
}
