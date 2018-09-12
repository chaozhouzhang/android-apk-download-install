package me.chaozhouzhang.apkdownloadinstall;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.math.BigDecimal;

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
        //TODO Android 8.0需要增加此标志 FLAG_GRANT_READ_URI_PERMISSION
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
        if (context == null || file == null) {
            throw new NullPointerException();
        }
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


    /**
     * 除法
     *
     * @param v1
     * @param v2
     * @param scale
     * @return
     */
    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(" the scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    /**
     * 计算百分比
     *
     * @param v1
     * @param v2
     * @return
     */
    public static int getPercent(double v1, double v2) {
        return (int) (div(v1, v2, 2) * 100);
    }

}
