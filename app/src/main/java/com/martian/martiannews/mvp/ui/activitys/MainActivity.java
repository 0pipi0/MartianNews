package com.martian.martiannews.mvp.ui.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.martian.martiannews.R;
import com.martian.martiannews.annotation.BindValues;
import com.martian.martiannews.common.Constants;
import com.martian.martiannews.event.ChannelChangeEvent;
import com.martian.martiannews.event.ScrollToTopEvent;
import com.martian.martiannews.greendao.NewsChannelTable;
import com.martian.martiannews.mvp.presenter.impl.MainPresenterImpl;
import com.martian.martiannews.mvp.ui.adapter.PagerAdapter.NewsFragmentPagerAdapter;
import com.martian.martiannews.mvp.ui.fragment.NewsListFragment;
import com.martian.martiannews.mvp.view.MainActivityView;
import com.martian.martiannews.uitl.MyUtils;
import com.martian.martiannews.uitl.RxBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

@BindValues(mIsHasNavigationView = true)
public class MainActivity extends BaseActivity implements MainActivityView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.add_channel_iv)
    ImageView addChannelIv;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;


    private String mCurrentViewPagerName;
    private List<String> mChannelNames;

    @Inject
    MainPresenterImpl mainPresenter;

    private List<Fragment> mNewsFragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubscription = RxBus.getInstance().toObservable(ChannelChangeEvent.class)
                .subscribe(new Action1<ChannelChangeEvent>() {
                    @Override
                    public void call(ChannelChangeEvent channelChangeEvent) {
                        mainPresenter.onChannelDbChanged();
                    }
                });

    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initInjector() {
        activityComponent.inject(this);
    }

    @Override
    public void initViews() {
        mBaseNavView = navigationView;
        mPresenter = mainPresenter;
        mPresenter.attachView(this);
    }

    @Override
    public void initViewPager(List<NewsChannelTable> newsChannels) {
        final List<String> channelNames = new ArrayList<>();
        if (newsChannels != null) {
            setNewsList(newsChannels, channelNames);
            setViewPager(channelNames);
        }
    }

    private void setNewsList(List<NewsChannelTable> newsChannels, List<String> channelNames) {
        mNewsFragmentList.clear();
        for (NewsChannelTable table:newsChannels){
            NewsListFragment newsListFragment = createListFragments(table);
            mNewsFragmentList.add(newsListFragment);
            channelNames.add(table.getNewsChannelName());
        }
    }

    /**
     * 创建Fragment
     * @param table
     */
    private NewsListFragment createListFragments(NewsChannelTable table) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.NEWS_ID, table.getNewsChannelId());
        bundle.putString(Constants.NEWS_TYPE, table.getNewsChannelType());
        bundle.putInt(Constants.CHANNEL_POSITION, table.getNewsChannelIndex());
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setViewPager(List<String> channelNames) {
        NewsFragmentPagerAdapter adapter = new NewsFragmentPagerAdapter(
                getSupportFragmentManager(),channelNames,mNewsFragmentList
        );
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);
        MyUtils.dynamicSetTabLayoutMode(tabs);
        mChannelNames = channelNames;
        setPageChangeListener();

        int currentViewPagerPosition = getCurrentViewPagerPosition();
        viewPager.setCurrentItem(currentViewPagerPosition, false);
    }
    private int getCurrentViewPagerPosition() {
        int position = 0;
        if (mCurrentViewPagerName != null) {
            for (int i = 0; i < mChannelNames.size(); i++) {
                if (mCurrentViewPagerName.equals(mChannelNames.get(i))) {
                    position = i;
                }
            }
        }
        return position;
    }
    /**
     * 设置ViewPager监听
     */
    private void setPageChangeListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentViewPagerName = mChannelNames.get(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick({R.id.fab, R.id.add_channel_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                RxBus.getInstance().post(new ScrollToTopEvent());
                break;
            case R.id.add_channel_iv:
                Intent intent = new Intent(this, NewsChannelActivity.class);
                startActivity(intent);
                break;
        }

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showMsg(String message) {
//        Snackbar.make(mFab, message, Snackbar.LENGTH_SHORT).show();
    }


}
