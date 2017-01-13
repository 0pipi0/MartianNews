package com.martian.martiannews.mvp.interactor;

import com.martian.martiannews.listener.RequestCallBack;

import rx.Subscription;

/**
 * Created by yangpei on 2016/12/10.
 */

public interface MainInteractor<T> {
    Subscription lodeNewsChannels(RequestCallBack<T> callback);
}
