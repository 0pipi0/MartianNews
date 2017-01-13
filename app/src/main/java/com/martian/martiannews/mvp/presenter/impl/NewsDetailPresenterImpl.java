package com.martian.martiannews.mvp.presenter.impl;

import android.support.annotation.NonNull;

import com.martian.martiannews.mvp.entity.NewsDetail;
import com.martian.martiannews.mvp.interactor.NewsDetailInteractor;
import com.martian.martiannews.mvp.interactor.impl.NewsDetailInteractorImpl;
import com.martian.martiannews.mvp.presenter.NewsDetailPresenter;
import com.martian.martiannews.mvp.presenter.base.BasePresenterImpl;
import com.martian.martiannews.mvp.view.NewsDetailView;
import com.martian.martiannews.mvp.view.base.BaseView;

import javax.inject.Inject;

/**
 * Created by yangpei on 2016/12/12.
 */

public class NewsDetailPresenterImpl extends BasePresenterImpl<NewsDetailView, NewsDetail> implements NewsDetailPresenter {

    private NewsDetailInteractor<NewsDetail> mNewsDetailInteractor;
    private String mPostId;

    @Inject
    public NewsDetailPresenterImpl(NewsDetailInteractorImpl newsDetailInteractor) {
        mNewsDetailInteractor = newsDetailInteractor;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSubscription = mNewsDetailInteractor.loadNewsDetail(this, mPostId);
    }

    @Override
    public void success(NewsDetail data) {
        super.success(data);
        mView.setNewsDetail(data);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void setPosId(String postId) {
        mPostId = postId;
    }
}
