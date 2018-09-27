package com.hgeson.avplayer.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * @Describe：
 * @Date：2018/9/27
 * @Author：hgeson
 */

public class ToastUtils {

    public static void showToast(Context context, String str){
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

}
