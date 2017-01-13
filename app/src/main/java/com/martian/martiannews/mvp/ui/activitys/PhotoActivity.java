package com.martian.martiannews.mvp.ui.activitys;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.martian.martiannews.R;
import com.martian.martiannews.annotation.BindValues;
import com.martian.martiannews.common.Constants;
import com.martian.martiannews.common.LoadNewsType;
import com.martian.martiannews.listener.OnItemClickListener;
import com.martian.martiannews.mvp.entity.PhotoGirl;
import com.martian.martiannews.mvp.presenter.impl.PhotoPresenterImpl;
import com.martian.martiannews.mvp.ui.adapter.PhotoListAdapter;
import com.martian.martiannews.mvp.view.PhotoView;
import com.martian.martiannews.uitl.NetUtil;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yangpei on 2016/12/12.
 */
@BindValues(mIsHasNavigationView = true)
public class PhotoActivity extends BaseActivity implements PhotoView, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.photo_rv)
    RecyclerView photoRv;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.empty_view)
    TextView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Inject
    PhotoPresenterImpl mPhotoPresenter;
    @Inject
    PhotoListAdapter mPhotoListAdapter;
    @Inject
    Activity mActivity;

    private boolean mIsAllLoaded;

    private StaggeredGridLayoutManager sglm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_photo;
    }

    @Override
    public void initInjector() {
        activityComponent.inject(this);
    }

    @Override
    public void initViews() {
        mBaseNavView = navView;

        initSwipeRefreshLayout();
        initRecyclerView();
        setAdapterItemClickEvent();
        initPresenter();
    }



    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.gplus_colors));
    }

    private void initRecyclerView() {
        photoRv.setHasFixedSize(true);
//        sglm = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
//        sglm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE    );
        GridLayoutManager glm = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        photoRv.setLayoutManager(glm);
        photoRv.setItemAnimator(new DefaultItemAnimator());
        photoRv.setAdapter(mPhotoListAdapter);
        setRvScrollEvent();
    }

    private void setRvScrollEvent() {
        photoRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                sglm.invalidateSpanAssignments();
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
//                int[] lastVisibleItemPosition = ((StaggeredGridLayoutManager) layoutManager)
//                        .findLastVisibleItemPositions(null);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                if (!mIsAllLoaded && visibleItemCount > 0 &&
                        (newState == RecyclerView.SCROLL_STATE_IDLE) &&
                        (lastVisibleItemPosition>=totalItemCount-1)
//                        ((lastVisibleItemPosition[0] >= totalItemCount - 1) ||
//                                (lastVisibleItemPosition[1] >= totalItemCount - 1))
                        ) {
                    mPhotoPresenter.loadMore();
                    mPhotoListAdapter.showFooter();
                    photoRv.scrollToPosition(mPhotoListAdapter.getItemCount() - 1);
                }
            }
        });
    }

    private void setAdapterItemClickEvent() {
        mPhotoListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(PhotoActivity.this, PhotoDetailActivity.class);
                intent.putExtra(Constants.PHOTO_DETAIL, mPhotoListAdapter.getList().get(position).getUrl());
                startActivity(view, intent);
            }
        });
    }

    private void startActivity(View view, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(mActivity, view, Constants.TRANSITION_ANIMATION_NEWS_PHOTOS);
            startActivity(intent, options.toBundle());
        } else {
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeScaleUpAnimation(view, view.getWidth() / 2, view.getHeight() / 2, 0, 0);
            ActivityCompat.startActivity(mActivity, intent, options.toBundle());
        }
    }

    private void initPresenter() {
        mPresenter = mPhotoPresenter;
        mPresenter.attachView(this);
        mPresenter.onCreate();
    }

    @Override
    public void setPhotoList(List<PhotoGirl> photoGirls, @LoadNewsType.checker int loadType) {
        switch (loadType) {
            case LoadNewsType.TYPE_REFRESH_SUCCESS:
                swipeRefreshLayout.setRefreshing(false);
                mPhotoListAdapter.setList(photoGirls);
                mPhotoListAdapter.notifyDataSetChanged();
                checkIsEmpty(photoGirls);
                mIsAllLoaded = false;
                break;
            case LoadNewsType.TYPE_REFRESH_ERROR:
                swipeRefreshLayout.setRefreshing(false);
                checkIsEmpty(photoGirls);
                break;
            case LoadNewsType.TYPE_LOAD_MORE_SUCCESS:
                mPhotoListAdapter.hideFooter();
                if (photoGirls == null || photoGirls.size() == 0) {
                    mIsAllLoaded = true;
                    Snackbar.make(photoRv, getString(R.string.no_more), Snackbar.LENGTH_SHORT).show();
                } else {
                    mPhotoListAdapter.addMore(photoGirls);
                }
                break;
            case LoadNewsType.TYPE_LOAD_MORE_ERROR:
                mPhotoListAdapter.hideFooter();
                break;
        }
    }

    private void checkIsEmpty(List<PhotoGirl> photoGirls) {
        if (photoGirls == null && mPhotoListAdapter.getList() == null) {
            photoRv.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            photoRv.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showMsg(String message) {
        progressBar.setVisibility(View.GONE);
        if (NetUtil.isNetworkAvailable()) {
            Snackbar.make(photoRv, message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRefresh() {
        mPhotoPresenter.refreshData();
    }

    @OnClick({R.id.empty_view, R.id.fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.empty_view:
                swipeRefreshLayout.setRefreshing(true);
                mPhotoPresenter.refreshData();
                break;
            case R.id.fab:
                photoRv.smoothScrollToPosition(0);
                break;
        }
    }
}
