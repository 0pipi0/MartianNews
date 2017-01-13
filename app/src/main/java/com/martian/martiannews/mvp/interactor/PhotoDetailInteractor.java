package com.martian.martiannews.mvp.interactor;

import com.martian.martiannews.listener.RequestCallBack;

import rx.Subscription;

/**
 * Created by yangpei on 2016/12/13.
 */

public interface PhotoDetailInteractor<T> {
    Subscription saveImageAndGetImageUri(RequestCallBack<T> listener, String url);
}
