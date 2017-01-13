package com.martian.martiannews.mvp.view;

import com.martian.martiannews.mvp.entity.NewsDetail;
import com.martian.martiannews.mvp.view.base.BaseView;

/**
 * Created by yangpei on 2016/12/12.
 */

public interface NewsDetailView extends BaseView {
    void setNewsDetail(NewsDetail newsDetail);
}
