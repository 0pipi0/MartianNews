package com.martian.martiannews.mvp.presenter.impl;

import com.martian.martiannews.common.LoadNewsType;
import com.martian.martiannews.listener.RequestCallBack;
import com.martian.martiannews.mvp.entity.NewsSummary;
import com.martian.martiannews.mvp.interactor.NewsListInteractor;
import com.martian.martiannews.mvp.interactor.impl.NewsListInteractorImpl;
import com.martian.martiannews.mvp.presenter.NewsListPresenter;
import com.martian.martiannews.mvp.presenter.base.BasePresenterImpl;
import com.martian.martiannews.mvp.view.NewsListView;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by yangpei on 2016/12/11.
 */

public class NewsListPresenterImpl extends BasePresenterImpl<NewsListView,List<NewsSummary>> implements NewsListPresenter ,RequestCallBack<List<NewsSummary>> {


    private String mNewsType;
    private String mNewsId;
    private int mStartPage;
    private boolean misFirstLoad;
    private boolean mIsRefresh = true;

    private NewsListInteractor<List<NewsSummary>> mNewsListInteractor;

    @Inject
    public NewsListPresenterImpl(NewsListInteractorImpl newsListInteractor) {
        mNewsListInteractor = newsListInteractor;
    }

    @Override
    public void onCreate() {
        if (mView != null) {
            loadNewsData();
        }
    }

    @Override
    public void beforeRequest() {
        if (!misFirstLoad) {
            mView.showProgress();
        }
    }

    @Override
    public void success(List<NewsSummary> data) {
        misFirstLoad = true;
        if (data != null) {
            mStartPage += 20;
        }

        int loadType = mIsRefresh ? LoadNewsType.TYPE_REFRESH_SUCCESS : LoadNewsType.TYPE_LOAD_MORE_SUCCESS;
        if (mView != null) {
            mView.setNewsList(data, loadType);
            mView.hideProgress();
        }
    }

    @Override
    public void onError(String errorMsg) {
        super.onError(errorMsg);
        if (mView != null) {
            int loadType = mIsRefresh ? LoadNewsType.TYPE_REFRESH_ERROR : LoadNewsType.TYPE_LOAD_MORE_ERROR;
            mView.setNewsList(null, loadType);
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void setNewsTypeAndId(String newsType, String newsId) {
        mNewsType = newsType;
        mNewsId = newsId;
    }

    @Override
    public void refreshData() {
        mStartPage = 0;
        mIsRefresh = true;
        loadNewsData();
    }

    @Override
    public void loadMore() {
        mIsRefresh = false;
        loadNewsData();
    }

    private void loadNewsData() {
        mSubscription = mNewsListInteractor.loadNews(this, mNewsType, mNewsId, mStartPage);
    }
}
