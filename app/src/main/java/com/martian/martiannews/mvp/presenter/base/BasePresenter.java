package com.martian.martiannews.mvp.presenter.base;

import android.support.annotation.NonNull;

import com.martian.martiannews.mvp.view.base.BaseView;

/**
 * Created by yangpei on 2016/12/10.
 */

public interface BasePresenter {

    void onCreate();

    void attachView(@NonNull BaseView view);

    void onDestroy();
}
