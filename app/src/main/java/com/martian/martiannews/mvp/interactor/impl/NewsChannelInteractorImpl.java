package com.martian.martiannews.mvp.interactor.impl;

import com.martian.martiannews.R;
import com.martian.martiannews.base.MyApplication;
import com.martian.martiannews.common.Constants;
import com.martian.martiannews.greendao.NewsChannelTable;
import com.martian.martiannews.listener.RequestCallBack;
import com.martian.martiannews.mvp.interactor.NewsChannelInteractor;
import com.martian.martiannews.repository.db.NewsChannelTableManager;
import com.martian.martiannews.uitl.TransformUtils;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by yangpei on 2016/12/12.
 */

public class NewsChannelInteractorImpl implements NewsChannelInteractor<Map<Integer, List<NewsChannelTable>>> {

    private ExecutorService mSingleThreadPool;

    @Inject
    public NewsChannelInteractorImpl() {
    }

    @Override
    public Subscription lodeNewsChannels(final RequestCallBack<Map<Integer, List<NewsChannelTable>>> callback) {
        return Observable.create(new rx.Observable.OnSubscribe<Map<Integer, List<NewsChannelTable>>>() {
            @Override
            public void call(Subscriber<? super Map<Integer, List<NewsChannelTable>>> subscriber) {
                Map<Integer, List<NewsChannelTable>> newsChannelListMap = getNewsChannelData();
                subscriber.onNext(newsChannelListMap);
                subscriber.onCompleted();
            }

        })
                .compose(TransformUtils.<Map<Integer, List<NewsChannelTable>>>defaultSchedulers())
                .subscribe(new Subscriber<Map<Integer, List<NewsChannelTable>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(MyApplication.getAppContext().getString(R.string.db_error));
                    }

                    @Override
                    public void onNext(Map<Integer, List<NewsChannelTable>> newsChannelListMap) {
                        callback.success(newsChannelListMap);
                    }
                });
    }

    @Override
    public void swapDb(final int fromPosition, final int toPosition) {
        createThreadPool();
        mSingleThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Logger.d(Thread.currentThread().getName());
                Logger.d("fromPosition: " + fromPosition + "； toPosition: " + toPosition);

                NewsChannelTable fromNewsChannel = NewsChannelTableManager.loadNewsChannel(fromPosition);
                NewsChannelTable toNewsChannel = NewsChannelTableManager.loadNewsChannel(toPosition);

                if (isAdjacent(fromPosition, toPosition)) {
                    swapAdjacentIndexAndUpdate(fromNewsChannel, toNewsChannel);
                } else if (fromPosition - toPosition > 0) {
                    List<NewsChannelTable> newsChannels = NewsChannelTableManager
                            .loadNewsChannelsWithin(toPosition, fromPosition - 1);

                    increaseOrReduceIndexAndUpdate(newsChannels, true);
                    changeFromChannelIndexAndUpdate(fromNewsChannel, toPosition);
                } else if (fromPosition - toPosition < 0) {
                    List<NewsChannelTable> newsChannels = NewsChannelTableManager
                            .loadNewsChannelsWithin(fromPosition + 1, toPosition);

                    increaseOrReduceIndexAndUpdate(newsChannels, false);
                    changeFromChannelIndexAndUpdate(fromNewsChannel, toPosition);
                }
            }

            private boolean isAdjacent(int fromChannelIndex, int toChannelIndex) {
                return Math.abs(fromChannelIndex - toChannelIndex) == 1;
            }

            private void swapAdjacentIndexAndUpdate(NewsChannelTable fromNewsChannel,
                                                    NewsChannelTable toNewsChannel) {
                fromNewsChannel.setNewsChannelIndex(toPosition);
                toNewsChannel.setNewsChannelIndex(fromPosition);

                NewsChannelTableManager.update(fromNewsChannel);
                NewsChannelTableManager.update(toNewsChannel);
            }
        });
    }

    @Override
    public void updateDb(final NewsChannelTable newsChannel, final boolean isChannelMine) {
        createThreadPool();
        mSingleThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Logger.d(Thread.currentThread().getName());

                int channelIndex = newsChannel.getNewsChannelIndex();
                if (isChannelMine) {
                    List<NewsChannelTable> newsChannels = NewsChannelTableManager.loadNewsChannelsIndexGt(channelIndex);
                    increaseOrReduceIndexAndUpdate(newsChannels, false);

                    int targetIndex = NewsChannelTableManager.getAllSize();
                    ChangeIsSelectAndIndex(targetIndex, false);
                } else {
                    List<NewsChannelTable> newsChannels = NewsChannelTableManager.loadNewsChannelsIndexLtAndIsUnselect(channelIndex);
                    increaseOrReduceIndexAndUpdate(newsChannels, true);

                    int targetIndex = NewsChannelTableManager.getNewsChannelSelectSize();
                    ChangeIsSelectAndIndex(targetIndex, true);
                }
            }

            private void ChangeIsSelectAndIndex(int targetIndex, boolean isSelect) {
                newsChannel.setNewsChannelSelect(isSelect);
                changeFromChannelIndexAndUpdate(newsChannel, targetIndex);
            }
        });
    }

    private Map<Integer, List<NewsChannelTable>> getNewsChannelData() {
        Map<Integer, List<NewsChannelTable>> map = new HashMap<>();
        List<NewsChannelTable> channelListMine = NewsChannelTableManager.loadNewsChannelsMine();
        List<NewsChannelTable> channelListMore = NewsChannelTableManager.loadNewsChannelsMore();
        map.put(Constants.NEWS_CHANNEL_MINE, channelListMine);
        map.put(Constants.NEWS_CHANNEL_MORE, channelListMore);
        Logger.d("-getNewsChannelData--:"+(map.size()));
        return map;
    }

    private void createThreadPool() {
        if (mSingleThreadPool == null) {
            mSingleThreadPool = Executors.newSingleThreadExecutor();
        }
    }

    private void increaseOrReduceIndexAndUpdate(List<NewsChannelTable> newsChannels, boolean isIncrease) {
        for (NewsChannelTable newsChannel : newsChannels) {
            increaseOrReduceIndex(isIncrease, newsChannel);
            NewsChannelTableManager.update(newsChannel);
        }
    }

    private void increaseOrReduceIndex(boolean isIncrease, NewsChannelTable newsChannel) {
        int targetIndex;
        if (isIncrease) {
            targetIndex = newsChannel.getNewsChannelIndex() + 1;
        } else {
            targetIndex = newsChannel.getNewsChannelIndex() - 1;
        }
        newsChannel.setNewsChannelIndex(targetIndex);
    }

    private void changeFromChannelIndexAndUpdate(NewsChannelTable fromNewsChannel, int toPosition) {
        fromNewsChannel.setNewsChannelIndex(toPosition);
        NewsChannelTableManager.update(fromNewsChannel);
    }
}
