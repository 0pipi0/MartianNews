
package com.martian.martiannews.mvp.presenter.impl;

import com.martian.martiannews.greendao.NewsChannelTable;
import com.martian.martiannews.mvp.interactor.MainInteractor;
import com.martian.martiannews.mvp.interactor.impl.MainInteractorImpl;
import com.martian.martiannews.mvp.presenter.MainPresenter;
import com.martian.martiannews.mvp.presenter.base.BasePresenterImpl;
import com.martian.martiannews.mvp.view.MainActivityView;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by yangpei on 2016/12/10.
 */

public class MainPresenterImpl extends BasePresenterImpl<MainActivityView, List<NewsChannelTable>> implements MainPresenter {

    private MainInteractor<List<NewsChannelTable>> mainInteractor;

    @Inject
    public MainPresenterImpl(MainInteractorImpl mainInteractorImpl) {
        mainInteractor = mainInteractorImpl;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        loadNewsChannels();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onChannelDbChanged() {
        loadNewsChannels();
    }

    @Override
    public void success(List<NewsChannelTable> data) {
        super.success(data);
        mView.initViewPager(data);
    }

    private void loadNewsChannels() {
        mSubscription = mainInteractor.lodeNewsChannels(this);
    }


}
