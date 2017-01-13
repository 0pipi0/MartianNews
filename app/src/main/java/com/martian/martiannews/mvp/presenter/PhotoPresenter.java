package com.martian.martiannews.mvp.presenter;

import com.martian.martiannews.mvp.presenter.base.BasePresenter;

/**
 * Created by yangpei on 2016/12/12.
 */

public interface PhotoPresenter extends BasePresenter{
    void refreshData();

    void loadMore();
}
