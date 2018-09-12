package me.chaozhouzhang.apkdownloadinstall.download;

/**
 * Created on 18/9/12 12:16
 *
 * @author zhangchaozhou
 */
public interface ReqCallBack<T> {

    /**
     * 响应成功
     *
     * @param result
     */
    void onReqSuccess(T result);

    /**
     * 响应失败
     *
     * @param ex
     */
    void onReqFailed(Exception ex);
}
