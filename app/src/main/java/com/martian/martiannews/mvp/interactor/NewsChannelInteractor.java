package com.martian.martiannews.mvp.interactor;

import com.martian.martiannews.greendao.NewsChannelTable;
import com.martian.martiannews.listener.RequestCallBack;

import rx.Subscription;

/**
 * Created by yangpei on 2016/12/12.
 */

public interface NewsChannelInteractor <T> {
    Subscription lodeNewsChannels(RequestCallBack<T> callback);

    void swapDb(int fromPosition,int toPosition);

    void updateDb(NewsChannelTable newsChannel, boolean isChannelMine);
}
