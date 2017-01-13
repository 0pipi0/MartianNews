package com.martian.martiannews.mvp.ui.fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.martian.martiannews.R;
import com.martian.martiannews.common.Constants;
import com.martian.martiannews.common.LoadNewsType;
import com.martian.martiannews.event.ScrollToTopEvent;
import com.martian.martiannews.listener.OnItemClickListener;
import com.martian.martiannews.mvp.entity.NewsPhotoDetail;
import com.martian.martiannews.mvp.entity.NewsSummary;
import com.martian.martiannews.mvp.presenter.impl.NewsListPresenterImpl;
import com.martian.martiannews.mvp.ui.activitys.NewsDetailActivity;
import com.martian.martiannews.mvp.ui.activitys.NewsPhotoDetailActivity;
import com.martian.martiannews.mvp.ui.adapter.NewsListAdapter;
import com.martian.martiannews.mvp.ui.fragment.base.BaseFragment;
import com.martian.martiannews.mvp.view.NewsListView;
import com.martian.martiannews.uitl.NetUtil;
import com.martian.martiannews.uitl.RxBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by yangpei on 2016/12/10.
 */

public class NewsListFragment extends BaseFragment implements NewsListView, SwipeRefreshLayout.OnRefreshListener,NewsListAdapter.OnNewsListItemClickListener {
    @BindView(R.id.news_rv)
    RecyclerView newsRv;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.empty_view)
    TextView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private String mNewsId;
    private String mNewsType;

    private boolean mIsAllLoaded;

    @Inject
    NewsListAdapter mNewsListAdapter;

    @Inject
    NewsListPresenterImpl mNewsListPresenter;

    @Inject
    Activity mActivity;

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    public void initViews(View view) {
        initSwipeRefreshLayout();
        initRecyclerView();
        initPresenter();
        registerScrollToTopEvent();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_news;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValues();
        NetUtil.isNetworkErrThenShowMsg();
    }

    private void initValues() {
        if (getArguments() != null) {
            mNewsId = getArguments().getString(Constants.NEWS_ID);
            mNewsType = getArguments().getString(Constants.NEWS_TYPE);
        }
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getActivity().getResources().getIntArray(R.array.gplus_colors));
    }

    private void initPresenter() {
        mNewsListPresenter.setNewsTypeAndId(mNewsType, mNewsId);
        mPresenter = mNewsListPresenter;
        mPresenter.attachView(this);
        mPresenter.onCreate();
    }
    private void initRecyclerView() {
        newsRv.setHasFixedSize(true);
        newsRv.setLayoutManager(new LinearLayoutManager(mActivity,LinearLayoutManager.VERTICAL,false));
        newsRv.setItemAnimator(new DefaultItemAnimator());
        newsRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

                int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager)
                        .findLastVisibleItemPosition();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();

                if (!mIsAllLoaded && visibleItemCount > 0 && newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItemPosition >= totalItemCount - 1) {
                    mNewsListPresenter.loadMore();
                    mNewsListAdapter.showFooter();
                    newsRv.scrollToPosition(mNewsListAdapter.getItemCount() - 1);
                }
            }
        });

        mNewsListAdapter.setOnItemClickListener(this);
        newsRv.setAdapter(mNewsListAdapter);
    }

    private void registerScrollToTopEvent() {
        mSubscription = RxBus.getInstance().toObservable(ScrollToTopEvent.class)
                .subscribe(new Action1<ScrollToTopEvent>() {
                    @Override
                    public void call(ScrollToTopEvent scrollToTopEvent) {
                        newsRv.getLayoutManager().scrollToPosition(0);
                    }
                });
    }

    @Override
    public void setNewsList(List<NewsSummary> newsSummary, @LoadNewsType.checker int loadType) {
        switch (loadType){
            case LoadNewsType.TYPE_REFRESH_SUCCESS:
                swipeRefreshLayout.setRefreshing(false);
                mNewsListAdapter.setList(newsSummary);
                mNewsListAdapter.notifyDataSetChanged();
                checkIsEmpty(newsSummary);
                break;
            case LoadNewsType.TYPE_REFRESH_ERROR:
                swipeRefreshLayout.setRefreshing(false);
                checkIsEmpty(newsSummary);
                break;
            case LoadNewsType.TYPE_LOAD_MORE_SUCCESS:
                mNewsListAdapter.hideFooter();
                if (newsSummary == null || newsSummary.size() == 0) {
                    mIsAllLoaded = true;
                    Snackbar.make(newsRv, getString(R.string.no_more), Snackbar.LENGTH_SHORT).show();
                } else {
                    mNewsListAdapter.addMore(newsSummary);
                }
                break;
            case LoadNewsType.TYPE_LOAD_MORE_ERROR:
                mNewsListAdapter.hideFooter();
                break;
        }
    }
    private void checkIsEmpty(List<NewsSummary> newsSummary) {
        if (newsSummary == null && mNewsListAdapter.getList() == null) {
            newsRv.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);

        } else {
            newsRv.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        mNewsListPresenter.refreshData();
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
        // 网络不可用状态在此之前已经显示了提示信息
        if (NetUtil.isNetworkAvailable()) {
            Snackbar.make(newsRv, message, Snackbar.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.empty_view)
    public void onClick() {
        swipeRefreshLayout.setRefreshing(true);
        mNewsListPresenter.refreshData();
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onItemClick(View view, int position, boolean isPhoto) {
        if (isPhoto) {
            NewsPhotoDetail newsPhotoDetail = getPhotoDetail(position);
            goToPhotoDetailActivity(newsPhotoDetail);
        } else {
            goToNewsDetailActivity(view, position);
        }
    }

    private NewsPhotoDetail getPhotoDetail(int position) {
        NewsSummary newsSummary = mNewsListAdapter.getList().get(position);
        NewsPhotoDetail newsPhotoDetail = new NewsPhotoDetail();
        newsPhotoDetail.setTitle(newsSummary.getTitle());
        setPictures(newsSummary, newsPhotoDetail);
        return newsPhotoDetail;
    }

    private void setPictures(NewsSummary newsSummary, NewsPhotoDetail newsPhotoDetail) {
        List<NewsPhotoDetail.Picture> pictureList = new ArrayList<>();

        if (newsSummary.getAds() != null) {
            for (NewsSummary.AdsBean entity : newsSummary.getAds()) {
                setValuesAndAddToList(pictureList, entity.getTitle(), entity.getImgsrc());
            }
        } else if (newsSummary.getImgextra() != null) {
            for (NewsSummary.ImgextraBean entity : newsSummary.getImgextra()) {
                setValuesAndAddToList(pictureList, null, entity.getImgsrc());
            }
        } else {
            setValuesAndAddToList(pictureList, null, newsSummary.getImgsrc());
        }
        newsPhotoDetail.setPictures(pictureList);
    }

    private void setValuesAndAddToList(List<NewsPhotoDetail.Picture> pictureList, String title, String imgsrc) {
        NewsPhotoDetail.Picture picture = new NewsPhotoDetail.Picture();
        if (title != null) {
            picture.setTitle(title);
        }
        picture.setImgSrc(imgsrc);

        pictureList.add(picture);
    }

    private void goToPhotoDetailActivity(NewsPhotoDetail newsPhotoDetail) {
        Intent intent = new Intent(getActivity(), NewsPhotoDetailActivity.class);
        intent.putExtra(Constants.PHOTO_DETAIL, newsPhotoDetail);
        startActivity(intent);
    }
    private void goToNewsDetailActivity(View view, int position) {
        Intent intent = setIntent(position);
        startActivity(view, intent);
    }
    @NonNull
    private Intent setIntent(int position) {
        List<NewsSummary> newsSummaryList = mNewsListAdapter.getList();

        Intent intent = new Intent(mActivity, NewsDetailActivity.class);
        intent.putExtra(Constants.NEWS_POST_ID, newsSummaryList.get(position).getPostid());
        intent.putExtra(Constants.NEWS_IMG_RES, newsSummaryList.get(position).getImgsrc());
        return intent;
    }
    private void startActivity(View view, Intent intent) {
        ImageView newsSummaryPhotoIv = (ImageView) view.findViewById(R.id.news_summary_photo_iv);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(mActivity, newsSummaryPhotoIv, Constants.TRANSITION_ANIMATION_NEWS_PHOTOS);
            startActivity(intent, options.toBundle());
        } else {
/*            ActivityOptionsCompat.makeCustomAnimation(this,
                    R.anim.slide_bottom_in, R.anim.slide_bottom_out);
            这个我感觉没什么用处，类似于
            overridePendingTransition(R.anim.slide_bottom_in, android.R.anim.fade_out);*/

/*            ActivityOptionsCompat.makeThumbnailScaleUpAnimation(source, thumbnail, startX, startY)
            这个方法可以用于4.x上，是将一个小块的Bitmpat进行拉伸的动画。*/

            //让新的Activity从一个小的范围扩大到全屏
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeScaleUpAnimation(view, view.getWidth() / 2, view.getHeight() / 2, 0, 0);
            ActivityCompat.startActivity(mActivity, intent, options.toBundle());
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mNewsListPresenter.onDestroy();
    }

}
