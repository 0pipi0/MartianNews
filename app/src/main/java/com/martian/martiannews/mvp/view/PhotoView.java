package com.martian.martiannews.mvp.view;

import com.martian.martiannews.common.LoadNewsType;
import com.martian.martiannews.mvp.entity.PhotoGirl;
import com.martian.martiannews.mvp.view.base.BaseView;

import java.util.List;

/**
 * Created by yangpei on 2016/12/12.
 */

public interface PhotoView extends BaseView{
    void setPhotoList(List<PhotoGirl> photoGirls, @LoadNewsType.checker int loadType);
}
