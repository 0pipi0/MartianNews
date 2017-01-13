package com.martian.martiannews.mvp.interactor;

import com.martian.martiannews.listener.RequestCallBack;

import rx.Subscription;

/**
 * Created by yangpei on 2016/12/12.
 */

public interface PhotoInteractor<T> {
    Subscription loadPhotos(RequestCallBack<T> listener, int size, int page);
}
