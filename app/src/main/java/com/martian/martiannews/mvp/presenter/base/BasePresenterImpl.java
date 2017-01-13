package com.martian.martiannews.mvp.presenter.base;

import android.support.annotation.NonNull;

import com.martian.martiannews.listener.RequestCallBack;
import com.martian.martiannews.mvp.view.base.BaseView;
import com.martian.martiannews.uitl.MyUtils;

import rx.Subscription;

/**
 * Created by yangpei on 2016/12/10.
 */

public class BasePresenterImpl<T extends BaseView, E> implements BasePresenter ,RequestCallBack<E> {
    protected T mView;
    protected Subscription mSubscription;
    @Override
    public void onCreate() {

    }

    @Override
    public void attachView(@NonNull BaseView view) {
        mView = (T) view;
    }

    @Override
    public void onDestroy() {
        MyUtils.cancelSubscription(mSubscription);
    }

    @Override
    public void beforeRequest() {
        mView.showProgress();
    }

    @Override
    public void success(E data) {
        mView.hideProgress();
    }

    @Override
    public void onError(String errorMsg) {
        mView.hideProgress();
        mView.showMsg(errorMsg);
    }
}
