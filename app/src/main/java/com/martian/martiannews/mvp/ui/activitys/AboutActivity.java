package com.martian.martiannews.mvp.ui.activitys;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.martian.martiannews.R;
import com.martian.martiannews.arouter.ARouterUtil;

/**
 * Created by yangpei on 2016/12/14.
 */
@Route(path = ARouterUtil.ABOUT_ABOUT)
public class AboutActivity extends BaseActivity{


    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void initInjector() {

    }

    @Override
    public void initViews() {

    }
}
