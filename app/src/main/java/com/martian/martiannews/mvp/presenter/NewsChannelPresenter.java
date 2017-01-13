package com.martian.martiannews.mvp.presenter;

import com.martian.martiannews.greendao.NewsChannelTable;
import com.martian.martiannews.mvp.presenter.base.BasePresenter;

/**
 * Created by yangpei on 2016/12/12.
 */

public interface NewsChannelPresenter extends BasePresenter {
    void onItemSwap(int fromPosition, int toPosition);

    void onItemAddOrRemove(NewsChannelTable newsChannel, boolean isChannelMine);
}
