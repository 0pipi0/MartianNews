package com.martian.martiannews.mvp.view;

import com.martian.martiannews.greendao.NewsChannelTable;
import com.martian.martiannews.mvp.view.base.BaseView;

import java.util.List;

/**
 * Created by yangpei on 2016/12/10.
 */

public interface MainActivityView extends BaseView{
    void initViewPager(List<NewsChannelTable> newsChannels);
}
