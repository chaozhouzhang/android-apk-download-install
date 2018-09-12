package me.chaozhouzhang.apkdownloadinstall.download;

/**
 * Created on 18/9/12 12:17
 *
 * @author zhangchaozhou
 */
public interface ReqProgressCallBack<T> extends ReqCallBack<T> {
    /**
     * 响应进度更新
     *
     * @param total
     * @param current
     */
    void onProgress(long total, long current);
}
