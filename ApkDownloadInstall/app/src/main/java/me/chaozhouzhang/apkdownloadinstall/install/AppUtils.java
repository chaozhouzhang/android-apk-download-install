package me.chaozhouzhang.apkdownloadinstall.install;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * @author zhangchaozhou
 */
public final class AppUtils {
    private AppUtils() {
    }


    /**
     * 安装外置存储器的apk
     *
     * @param context
     * @param file
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //TODO Android 8.0需要增加此标志 FLAG_GRANT_READ_URI_PERMISSION，对目标应用临时授权该Uri所代表的文件
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(getUriForFile(context, file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


    /**
     * 获取文件的Uri地址
     *
     * @param context
     * @param file
     * @return
     */
    public static Uri getUriForFile(Context context, File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            /**
             * TODO 此处需要注意Android 7.0：authority要与AndroidManifest.xml中的android.support.v4.content.FileProvider定义的authorities一致！
             */
            uri = FileProvider.getUriForFile(context.getApplicationContext(), context.getPackageName() + ".fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }


}
