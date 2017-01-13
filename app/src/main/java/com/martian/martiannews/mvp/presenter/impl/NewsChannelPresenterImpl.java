package com.martian.martiannews.mvp.presenter.impl;

import android.support.annotation.NonNull;

import com.martian.martiannews.common.Constants;
import com.martian.martiannews.event.ChannelChangeEvent;
import com.martian.martiannews.greendao.NewsChannelTable;
import com.martian.martiannews.mvp.interactor.impl.NewsChannelInteractorImpl;
import com.martian.martiannews.mvp.presenter.NewsChannelPresenter;
import com.martian.martiannews.mvp.presenter.base.BasePresenterImpl;
import com.martian.martiannews.mvp.view.NewsChannelView;
import com.martian.martiannews.mvp.view.base.BaseView;
import com.martian.martiannews.uitl.RxBus;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by yangpei on 2016/12/12.
 */

public class NewsChannelPresenterImpl extends BasePresenterImpl<NewsChannelView,
        Map<Integer, List<NewsChannelTable>>> implements NewsChannelPresenter {

    private NewsChannelInteractorImpl mNewsChannelInteractor;
    private boolean mIsChannelChanged;


    @Inject
    public NewsChannelPresenterImpl(NewsChannelInteractorImpl newsChannelInteractor) {
        mNewsChannelInteractor = newsChannelInteractor;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mNewsChannelInteractor.lodeNewsChannels(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIsChannelChanged) {
            RxBus.getInstance().post(new ChannelChangeEvent());
        }
    }

    @Override
    public void success(Map<Integer, List<NewsChannelTable>> data) {
        super.success(data);
        mView.initRecyclerViews(data.get(Constants.NEWS_CHANNEL_MINE), data.get(Constants.NEWS_CHANNEL_MORE));
    }

    @Override
    public void onItemSwap(int fromPosition, int toPosition) {
        mNewsChannelInteractor.swapDb(fromPosition, toPosition);
        mIsChannelChanged = true;
    }

    @Override
    public void onItemAddOrRemove(NewsChannelTable newsChannel, boolean isChannelMine) {
        mNewsChannelInteractor.updateDb(newsChannel, isChannelMine);
        mIsChannelChanged = true;
    }
}
