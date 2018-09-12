package me.chaozhouzhang.apkdownloadinstall.download;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * @author zhangchaozhou
 */
public class OkhttpDownloader {
    private Handler mOkHttpHandler;
    private OkHttpClient mOkHttpClient;

    public OkhttpDownloader() {
        super();
        mOkHttpHandler = new Handler(Looper.getMainLooper());
        mOkHttpClient = new OkHttpClient();
    }

    /**
     * 下载文件
     *
     * @param fileUrl     文件url
     * @param destFileDir 存储目标目录
     */
    public <T> void downLoadFile(String fileUrl, final String destFileDir, final String suffix, final ReqProgressCallBack<T> callBack) {
        final String fileName = Md5Utils.encode(fileUrl) + "." + suffix;
        final File file = new File(destFileDir, fileName);
        if (file.exists()) {
            //TODO 已经存在下载好的安装包，则直接安装
            successCallBack((T) file, callBack);
            return;
        }
        final Request request = new Request.Builder().url(fileUrl).build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //TODO 下载失败
                failedCallBack(e, callBack);
            }

            @Override
            public void onResponse(Call call, Response response) {

                handleResponse(response, file, callBack);
            }
        });
    }

    /**
     * 将文件写入外部存储器
     *
     * @param response
     * @param file
     * @param callBack
     * @param <T>
     */
    private <T> void handleResponse(Response response, File file, ReqProgressCallBack<T> callBack) {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len;
        FileOutputStream fos = null;
        try {
            long total = response.body().contentLength();
            long current = 0;
            is = response.body().byteStream();
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                current += len;
                fos.write(buf, 0, len);
                //TODO 回调下载进度
                progressCallBack(total, current, callBack);
            }
            fos.flush();
            //TODO 下载成功
            successCallBack((T) file, callBack);
        } catch (IOException e) {
            //TODO 下载出错
            failedCallBack(e, callBack);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                //TODO 下载出错
                failedCallBack(e, callBack);
            }
        }
    }


    /**
     * 统一处理进度信息
     *
     * @param total    总计大小
     * @param current  当前进度
     * @param callBack
     * @param <T>
     */
    private <T> void progressCallBack(final long total, final long current, final ReqProgressCallBack<T> callBack) {
        mOkHttpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onProgress(total, current);
                }
            }
        });
    }

    /**
     * 统一同意处理成功信息
     *
     * @param result
     * @param callBack
     * @param <T>
     */
    private <T> void successCallBack(final T result, final ReqCallBack<T> callBack) {
        mOkHttpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqSuccess(result);
                }
            }
        });
    }

    /**
     * 统一处理失败信息
     *
     * @param ex
     * @param callBack
     * @param <T>
     */
    private <T> void failedCallBack(final Exception ex, final ReqCallBack<T> callBack) {
        mOkHttpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqFailed(ex);
                }
            }
        });
    }
}
