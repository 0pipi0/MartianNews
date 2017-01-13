package com.martian.martiannews.mvp.presenter;

import com.martian.martiannews.common.PhotoRequestType;
import com.martian.martiannews.mvp.presenter.base.BasePresenter;

/**
 * Created by yangpei on 2016/12/13.
 */

public interface PhotoDetailPresenter extends BasePresenter {
    void handlePicture(String imageUrl, @PhotoRequestType.PhotoRequestTypeChecker int type);
}
