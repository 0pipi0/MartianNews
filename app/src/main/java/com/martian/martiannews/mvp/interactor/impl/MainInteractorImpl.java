package com.martian.martiannews.mvp.interactor.impl;

import com.martian.martiannews.R;
import com.martian.martiannews.base.MyApplication;
import com.martian.martiannews.greendao.NewsChannelTable;
import com.martian.martiannews.listener.RequestCallBack;
import com.martian.martiannews.mvp.interactor.MainInteractor;
import com.martian.martiannews.repository.db.NewsChannelTableManager;
import com.martian.martiannews.uitl.TransformUtils;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by yangpei on 2016/12/10.
 */

public class MainInteractorImpl implements MainInteractor<List<NewsChannelTable>> {

    @Inject
    public MainInteractorImpl() {
    }
    @Override
    public Subscription lodeNewsChannels(final RequestCallBack<List<NewsChannelTable>> callback) {
        return Observable.create(new Observable.OnSubscribe<List<NewsChannelTable>>() {

            @Override
            public void call(Subscriber<? super List<NewsChannelTable>> subscriber) {
                NewsChannelTableManager.initDB();
                subscriber.onNext(NewsChannelTableManager.loadNewsChannelsMine());
                subscriber.onCompleted();

            }
        }).compose(TransformUtils.<List<NewsChannelTable>>defaultSchedulers())
                .subscribe(new Subscriber<List<NewsChannelTable>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(MyApplication.getAppContext().getString(R.string.db_error));
                    }

                    @Override
                    public void onNext(List<NewsChannelTable> newsChannelTables) {
                        callback.success(newsChannelTables);
                    }
                });
    }
}
