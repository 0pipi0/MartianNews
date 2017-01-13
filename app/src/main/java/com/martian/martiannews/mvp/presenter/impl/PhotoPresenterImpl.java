package com.martian.martiannews.mvp.presenter.impl;

import com.martian.martiannews.common.LoadNewsType;
import com.martian.martiannews.mvp.entity.PhotoGirl;
import com.martian.martiannews.mvp.interactor.impl.PhotoInteractorImpl;
import com.martian.martiannews.mvp.presenter.PhotoPresenter;
import com.martian.martiannews.mvp.presenter.base.BasePresenterImpl;
import com.martian.martiannews.mvp.view.PhotoView;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by yangpei on 2016/12/12.
 */

public class PhotoPresenterImpl extends BasePresenterImpl<PhotoView,List<PhotoGirl>> implements PhotoPresenter{
    private PhotoInteractorImpl mPhotoInteractor;
    private static int SIZE = 20;
    private int mStartPage = 1;
    private boolean misFirstLoad;
    private boolean mIsRefresh = true;

    @Inject
    public PhotoPresenterImpl(PhotoInteractorImpl photoInteractor) {
        mPhotoInteractor = photoInteractor;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        loadPhotoData();
    }

    @Override
    public void beforeRequest() {
        if (!misFirstLoad) {
            mView.showProgress();
        }
    }

    @Override
    public void success(List<PhotoGirl> items) {
        super.success(items);
        misFirstLoad = true;
        if (items != null) {
            mStartPage += 1;
        }
        int loadType = mIsRefresh ? LoadNewsType.TYPE_REFRESH_SUCCESS : LoadNewsType.TYPE_LOAD_MORE_SUCCESS;
        if (mView != null) {
            mView.setPhotoList(items, loadType);
            mView.hideProgress();
        }
    }

    @Override
    public void onError(String errorMsg) {
        super.onError(errorMsg);
        if (mView != null) {
            int loadType = mIsRefresh ? LoadNewsType.TYPE_REFRESH_ERROR : LoadNewsType.TYPE_LOAD_MORE_ERROR;
            mView.setPhotoList(null, loadType);
        }
    }

    @Override
    public void refreshData() {
        mStartPage = 1;
        mIsRefresh = true;
        loadPhotoData();
    }

    @Override
    public void loadMore() {
        mIsRefresh = false;
        loadPhotoData();
    }

    private void loadPhotoData() {
        mPhotoInteractor.loadPhotos(this, SIZE, mStartPage);
    }
}
