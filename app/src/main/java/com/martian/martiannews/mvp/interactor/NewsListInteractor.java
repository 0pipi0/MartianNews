package com.martian.martiannews.mvp.interactor;

import com.martian.martiannews.listener.RequestCallBack;

import rx.Subscription;

/**
 * Created by yangpei on 2016/12/11.
 */

public interface NewsListInteractor<T> {

    Subscription loadNews(RequestCallBack<T> listener, String type, String id, int startPage);
}
