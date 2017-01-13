package com.martian.martiannews.mvp.ui.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.martian.martiannews.base.MyApplication;
import com.martian.martiannews.dagger.component.DaggerFragmentComponent;
import com.martian.martiannews.dagger.component.FragmentComponent;
import com.martian.martiannews.dagger.module.FragmentModule;
import com.martian.martiannews.mvp.presenter.base.BasePresenter;
import com.martian.martiannews.uitl.MyUtils;
import com.squareup.leakcanary.RefWatcher;

import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by yangpei on 2016/12/10.
 */

public abstract class BaseFragment <T extends BasePresenter> extends Fragment {

    protected FragmentComponent mFragmentComponent;

    protected T mPresenter;

    private View mFragmentView;

    protected Subscription mSubscription;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentComponent = DaggerFragmentComponent.builder()
                .applicationComponent(((MyApplication) getActivity().getApplication()).getApplicationComponent())
                .fragmentModule(new FragmentModule(this))
                .build();
        initInjector();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(getLayoutId(), container, false);
            ButterKnife.bind(this, mFragmentView);
            initViews(mFragmentView);
        }
        return mFragmentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);

        if (mPresenter != null) {
            mPresenter.onDestroy();
        }

        MyUtils.cancelSubscription(mSubscription);
    }

    public FragmentComponent getFragmentComponent() {
        return mFragmentComponent;
    }
    public abstract void initInjector();

    public abstract void initViews(View view);

    public abstract int getLayoutId();
}
