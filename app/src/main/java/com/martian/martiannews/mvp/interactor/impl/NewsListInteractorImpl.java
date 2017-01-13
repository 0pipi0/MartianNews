package com.martian.martiannews.mvp.interactor.impl;

import com.martian.martiannews.common.ApiConstants;
import com.martian.martiannews.common.HostType;
import com.martian.martiannews.listener.RequestCallBack;
import com.martian.martiannews.mvp.entity.NewsSummary;
import com.martian.martiannews.mvp.interactor.NewsListInteractor;
import com.martian.martiannews.repository.network.RetrofitManager;
import com.martian.martiannews.uitl.MyUtils;
import com.martian.martiannews.uitl.TransformUtils;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by yangpei on 2016/12/11.
 */

public class NewsListInteractorImpl implements NewsListInteractor<List<NewsSummary>> {
    @Inject
    public NewsListInteractorImpl() {
    }
    @Override
    public Subscription loadNews(final RequestCallBack listener, String type, final String id, int startPage) {
        return RetrofitManager.getInstance(HostType.NETEASE_NEWS_VIDEO).getNewsListObservable(type,id,startPage)
                .flatMap(new Func1<Map<String, List<NewsSummary>>, Observable<NewsSummary>>() {
                    @Override
                    public Observable<NewsSummary> call(Map<String, List<NewsSummary>> map) {
                        if (id.endsWith(ApiConstants.HOUSE_ID)) {
                            return Observable.from(map.get("北京"));
                        }
                        return Observable.from(map.get(id));
                    }
                }).map(new Func1<NewsSummary, NewsSummary>() {
                    @Override
                    public NewsSummary call(NewsSummary newsSummary) {
                        String ptime = MyUtils.formatDate(newsSummary.getPtime());
                        newsSummary.setPtime(ptime);
                        return newsSummary;
                    }
                })
                .distinct()
                .toSortedList(new Func2<NewsSummary, NewsSummary, Integer>() {
                    @Override
                    public Integer call(NewsSummary newsSummary, NewsSummary newsSummary2) {
                        return newsSummary2.getPtime().compareTo(newsSummary.getPtime());
                    }
                })
                .compose(TransformUtils.<List<NewsSummary>>defaultSchedulers())
                .subscribe(new Subscriber<List<NewsSummary>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onError(MyUtils.analyzeNetworkError(e));
                    }

                    @Override
                    public void onNext(List<NewsSummary> newsSummaries) {
                        listener.success(newsSummaries);
                    }
                });
    }
}
