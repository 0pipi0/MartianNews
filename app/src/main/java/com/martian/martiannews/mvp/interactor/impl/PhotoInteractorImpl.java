package com.martian.martiannews.mvp.interactor.impl;

import com.martian.martiannews.R;
import com.martian.martiannews.base.MyApplication;
import com.martian.martiannews.common.HostType;
import com.martian.martiannews.listener.RequestCallBack;
import com.martian.martiannews.mvp.entity.GirlData;
import com.martian.martiannews.mvp.entity.PhotoGirl;
import com.martian.martiannews.mvp.interactor.PhotoInteractor;
import com.martian.martiannews.repository.network.RetrofitManager;
import com.martian.martiannews.uitl.TransformUtils;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by yangpei on 2016/12/12.
 */

public class PhotoInteractorImpl implements PhotoInteractor<List<PhotoGirl>> {

    @Inject
    public PhotoInteractorImpl() {
    }

    @Override
    public Subscription loadPhotos(final RequestCallBack<List<PhotoGirl>> listener, int size, int page) {
        return RetrofitManager.getInstance(HostType.GANK_GIRL_PHOTO)
                .getPhotoListObservable(size,page)
                .map(new Func1<GirlData, List<PhotoGirl>>() {
                    @Override
                    public List<PhotoGirl> call(GirlData girlData) {
                        return girlData.getResults();
                    }
                })
                .compose(TransformUtils.<List<PhotoGirl>>defaultSchedulers())
                .subscribe(new Subscriber<List<PhotoGirl>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onError(MyApplication.getAppContext().getString(R.string.load_error));
                    }

                    @Override
                    public void onNext(List<PhotoGirl> photoGirls) {
                        listener.success(photoGirls);
                    }
                });
    }
}
