package com.martian.martiannews.mvp.ui.activitys;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.martian.martiannews.R;
import com.martian.martiannews.common.Constants;
import com.martian.martiannews.event.PhotoDetailOnClickEvent;
import com.martian.martiannews.mvp.entity.NewsPhotoDetail;
import com.martian.martiannews.mvp.ui.adapter.PhotoPagerAdapter;
import com.martian.martiannews.mvp.ui.fragment.PhotoDetailFragment;
import com.martian.martiannews.uitl.RxBus;
import com.martian.martiannews.widget.PhotoViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * Created by yangpei on 2016/12/11.
 */

public class NewsPhotoDetailActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewpager)
    PhotoViewPager viewpager;
    @BindView(R.id.photo_detail_title_tv)
    TextView photoDetailTitleTv;

    private List<PhotoDetailFragment> mPhotoDetailFragmentList = new ArrayList<>();
    private NewsPhotoDetail mNewsPhotoDetail;
    @Override
    public int getLayoutId() {
        return R.layout.activity_news_photo_detail;
    }

    @Override
    public void initInjector() {
        activityComponent.inject(this);
    }

    @Override
    public void initViews() {
        mNewsPhotoDetail = getIntent().getParcelableExtra(Constants.PHOTO_DETAIL);
        createFragment(mNewsPhotoDetail);
        initViewPager();
        setPhotoDetailTitle(0);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubscription = RxBus.getInstance().toObservable(PhotoDetailOnClickEvent.class)
                .subscribe(new Action1<PhotoDetailOnClickEvent>() {
                    @Override
                    public void call(PhotoDetailOnClickEvent photoDetailOnClickEvent) {
                        if (photoDetailTitleTv.getVisibility() == View.VISIBLE) {
                            startAnimation(View.GONE, 0.9f, 0.5f);
                        } else {
                            photoDetailTitleTv.setVisibility(View.VISIBLE);
                            startAnimation(View.VISIBLE, 0.5f, 0.9f);
                        }
                    }
                });
    }

    private void startAnimation(final int endState, float startValue, float endValue) {
        ObjectAnimator animator = ObjectAnimator
                .ofFloat(photoDetailTitleTv, "alpha", startValue, endValue)
                .setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                photoDetailTitleTv.setVisibility(endState);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private void createFragment(NewsPhotoDetail newsPhotoDetail) {
        mPhotoDetailFragmentList.clear();
        for (NewsPhotoDetail.Picture picture : newsPhotoDetail.getPictures()) {
            PhotoDetailFragment fragment = new PhotoDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.PHOTO_DETAIL_IMGSRC, picture.getImgSrc());
            fragment.setArguments(bundle);
            mPhotoDetailFragmentList.add(fragment);
        }
    }

    private void initViewPager() {
        PhotoPagerAdapter photoPagerAdapter = new PhotoPagerAdapter(getSupportFragmentManager(), mPhotoDetailFragmentList);
        viewpager.setAdapter(photoPagerAdapter);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setPhotoDetailTitle(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setPhotoDetailTitle(int position) {
        String title = getTitle(position);
        photoDetailTitleTv.setText(getString(R.string.photo_detail_title, position + 1,
                mPhotoDetailFragmentList.size(), title));
    }

    private String getTitle(int position) {
        String title = mNewsPhotoDetail.getPictures().get(position).getTitle();
        if (title == null) {
            title = mNewsPhotoDetail.getTitle();
        }
        return title;
    }
}
