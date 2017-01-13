package com.martian.martiannews.mvp.view;

import com.martian.martiannews.greendao.NewsChannelTable;
import com.martian.martiannews.mvp.view.base.BaseView;

import java.util.List;

/**
 * Created by yangpei on 2016/12/12.
 */

public interface NewsChannelView extends BaseView {
    void initRecyclerViews(List<NewsChannelTable> newsChannelsMine, List<NewsChannelTable> newsChannelsMore);
}
