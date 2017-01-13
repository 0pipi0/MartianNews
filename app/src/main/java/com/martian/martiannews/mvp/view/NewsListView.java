package com.martian.martiannews.mvp.view;

import com.martian.martiannews.common.LoadNewsType;
import com.martian.martiannews.mvp.entity.NewsSummary;
import com.martian.martiannews.mvp.view.base.BaseView;

import java.util.List;

/**
 * Created by yangpei on 2016/12/11.
 */

public interface NewsListView extends BaseView {

    void setNewsList(List<NewsSummary> newsSummary, @LoadNewsType.checker int loadType);
}
