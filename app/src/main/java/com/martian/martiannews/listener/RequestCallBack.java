package com.martian.martiannews.listener;

/**
 * Created by yangpei on 2016/12/10.
 */

public interface RequestCallBack<T> {

    void beforeRequest();

    void success(T data);

    void onError(String errorMsg);
}
