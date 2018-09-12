package me.chaozhouzhang.apkdownloadinstall;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import me.chaozhouzhang.apkdownloadinstall.download.OkhttpDownloader;
import me.chaozhouzhang.apkdownloadinstall.download.ReqProgressCallBack;
import me.chaozhouzhang.apkdownloadinstall.install.AppUtils;

import static me.chaozhouzhang.apkdownloadinstall.download.MathUtils.getPercent;

/**
 * @author zhangchaozhou
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private final int REQUEST_EXTERNAL_STORAGE = 2001;

    private EditText mEdtApkUrl;
    private Button mBtnDownloadInstall;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mEdtApkUrl = findViewById(R.id.edt_apk_url);
        mBtnDownloadInstall = findViewById(R.id.btn_download_install);
        mBtnDownloadInstall.setOnClickListener(this);
    }

    /**
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_download_install:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //TODO Android 6.0 动态访问写入外部存储器权限
                    /**
                     * TODO 当只用到读取外部存储器功能的时候只需要用到：READ_EXTERNAL_STORAGE。
                     * TODO 当需要用到写入外部存储器功能的时候必须要用到：WRITE_EXTERNAL_STORAGE；此时包含了READ_EXTERNAL_STORAGE，读取外部存储器时不需要再申请READ_EXTERNAL_STORAGE。
                     */
                    //TODO 此处如果为READ_EXTERNAL_STORAGE，则在8.0会有Permission Denied错误出现
                    boolean write = (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                    String[] permissions = new String[]{};
                    if (write) {
                        downloadInstall();
                    } else {
                        permissions = new String[permissions.length + 1];
                        permissions[permissions.length - 1] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                        ActivityCompat.requestPermissions(this, permissions, REQUEST_EXTERNAL_STORAGE);
                    }
                } else {
                    downloadInstall();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 下载安装
     */
    private void downloadInstall() {
        //TODO 下载安装apk
        String apkUrl = mEdtApkUrl.getText().toString().trim();
        if (TextUtils.isEmpty(apkUrl)) {
            Toast.makeText(mContext, "请输入apk网址", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        progressDialog.show();
        OkhttpDownloader okhttpDownloader = new OkhttpDownloader();
        okhttpDownloader.downLoadFile(apkUrl, Environment.getExternalStorageDirectory().getAbsolutePath(), "apk", new ReqProgressCallBack<File>() {
            @Override
            public void onProgress(long total, long current) {
                progressDialog.setProgress(getPercent(current, total));
            }

            @Override
            public void onReqSuccess(File result) {
                progressDialog.dismiss();
                AppUtils.installApk(MainActivity.this, result);
            }

            @Override
            public void onReqFailed(Exception ex) {
                Toast.makeText(MainActivity.this, "下载失败：" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }


    /**
     * 处理权限请求结果
     *
     * @param requestCode  请求权限时传入的请求码，用于区别是哪一次请求的
     * @param permissions  所请求的所有权限的数组
     * @param grantResults 权限授予结果，和 permissions 数组参数中的权限一一对应，元素值为两种情况，如下:
     *                     授予: PackageManager.PERMISSION_GRANTED
     *                     拒绝: PackageManager.PERMISSION_DENIED
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //TODO 已经获取到写入外部存储器的权限
                downloadInstall();
            }
        }
    }
}
