package com.hgeson.avplayer.utils;

import android.app.Activity;
import android.os.Build;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;
/**
 * @Describe：
 * @Date：2018/10/5
 * @Author：hgeson
 */
public class PermissionUtils {
    private static Boolean result = true;

    /**
     * Description：用于多权限申请,注意：不要将不相关的权限一起申请,权限申请和说明按对应顺序填写。
     */
    public static boolean initPermissions(final Activity context, final String[] permissions, final String[] description) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            for (String permission : permissions) {
//                if (ContextCompat.checkSelfPermission(context, permission) !=
//                        PackageManager.PERMISSION_GRANTED) {
//                    return requestPermission(context, permissions, description);
//                }
//                return true;
//            }
            result = false;
            RxPermissions rxPermissions = new RxPermissions(context);
            rxPermissions.requestEach(permissions).subscribe(new Consumer<Permission>() {
                @Override
                public void accept(Permission permission) {
                    if (permission.granted) {
                        result = true;
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        result = false;
                        for (int i = 0; i < permissions.length; i++) {
                            if (permissions[i].equals(permission.name)) {
                                Toast.makeText(context, "您拒绝了" + description[i] + "权限", Toast.LENGTH_SHORT);
                                break;
                            }
                        }
                    } else {
                        result = false;
                        for (int i = 0; i < permissions.length; i++) {
                            if (permissions[i].equals(permission.name)) {
                                Toast.makeText(context, "您拒绝了" + description[i] + "权限,需要您到设置手动开启", Toast.LENGTH_SHORT);
                                break;
                            }
                        }
                    }
                }
            });
        }
        return result;
    }
}
