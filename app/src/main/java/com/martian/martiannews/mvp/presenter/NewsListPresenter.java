package com.martian.martiannews.mvp.presenter;

import com.martian.martiannews.mvp.presenter.base.BasePresenter;

/**
 * Created by yangpei on 2016/12/11.
 */

public interface NewsListPresenter extends BasePresenter{

    void setNewsTypeAndId(String newsType, String newsId);

    void refreshData();

    void loadMore();
}
