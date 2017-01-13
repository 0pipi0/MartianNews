package com.martian.martiannews.mvp.ui.activitys;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.martian.martiannews.R;
import com.martian.martiannews.common.Constants;
import com.martian.martiannews.common.PhotoRequestType;
import com.martian.martiannews.dagger.scope.ContextLife;
import com.martian.martiannews.mvp.presenter.impl.PhotoDetailPresenterImpl;
import com.martian.martiannews.mvp.view.PhotoDetailView;
import com.martian.martiannews.uitl.MyUtils;
import com.martian.martiannews.uitl.SystemUiVisibilityUtil;
import com.martian.martiannews.widget.PullBackLayout;
import com.orhanobut.logger.Logger;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.Lazy;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by yangpei on 2016/12/13.
 */

public class PhotoDetailActivity extends BaseActivity implements PhotoDetailView, PullBackLayout.Callback {
    @BindView(R.id.photo_iv)
    ImageView photoIv;
//    @BindView(R.id.photo_touch_iv)
//    PhotoView photoTouchIv;
    @BindView(R.id.pull_back_layout)
    com.martian.martiannews.widget.PullBackLayout pullBackLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.background)
    FrameLayout background;

    @Inject
    Lazy<PhotoDetailPresenterImpl> mPhotoDetailPresenter;
    @Inject
    @ContextLife("Activity")
    Context mContext;

    private ColorDrawable mBackground;
    private boolean mIsToolBarHidden;
    private boolean mIsStatusBarHidden;
    private PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pullBackLayout.setCallback(this);
        initLazyLoadView();
    }



    @Override
    public int getLayoutId() {
        return R.layout.activity_photo_detail;
    }

    @Override
    public void initInjector() {
        activityComponent.inject(this);
    }

    @Override
    public void initViews() {
        initToolbar();
        initImageView();
        initBackground();
    }

    private void initToolbar() {
        toolbar.setTitle(getString(R.string.girl));
    }

    private void initImageView() {
        loadPhotoIv();
    }

    private void initBackground() {
        mBackground = new ColorDrawable(Color.BLACK);
        MyUtils.getRootView(this).setBackgroundDrawable(mBackground);
//        mBackground.setColor(Color.argb((int) (0xff/*255*/ * (1f - progress)),0,0,0));
    }


    private void setPhotoViewClickEvent() {
        mAttacher = new PhotoViewAttacher(photoIv);
        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                Logger.d("");
                hideOrShowToolbar();
                hideOrShowStatusBar();
            }
        });
    }


    private void loadPhotoIv() {
        Glide.with(this)
                .load(getIntent().getStringExtra(Constants.PHOTO_DETAIL))
                .into(photoIv);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_photo, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_share:
//                handlePicture(PhotoRequestType.TYPE_SHARE);
//                return true;
//            case R.id.action_save:
//                handlePicture(PhotoRequestType.TYPE_SAVE);
//                return true;
//            case R.id.action_set_wallpaper:
//                handlePicture(PhotoRequestType.TYPE_SET_WALLPAPER);
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void handlePicture(int type) {
        initPresenter();
        mPhotoDetailPresenter.get().handlePicture(getIntent().getStringExtra(Constants.PHOTO_DETAIL)
                , type);
    }

    private void initPresenter() {
        mPresenter = mPhotoDetailPresenter.get(); // 在这时才创建mPhotoDetailPresenter,以后每次调用get会得到同一个mPhotoDetailPresenter对象
        mPresenter.attachView(this);
    }

    private void toolBarFadeOut() {
        mIsToolBarHidden = false;
        hideOrShowToolbar();
    }

    @Override
    public void onPullStart() {
        toolBarFadeOut();

        mIsStatusBarHidden = true;
        hideOrShowStatusBar();
    }

    @Override
    public void onPull(float progress) {
        Logger.d("progress: " + progress);
        progress = Math.min(1f, progress * 1.5f);
        mBackground.setAlpha((int) (0xff/*255*/ * (1f - progress)));
    }

    @Override
    public void onPullCancel() {
        toolBarFadeIn();
    }

    @Override
    public void onPullComplete() {
        mBackground.setAlpha(0);
        supportFinishAfterTransition();
    }

    private void initLazyLoadView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getEnterTransition().addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }
                @Override
                public void onTransitionEnd(Transition transition) {
                    showToolBarAndPhotoTouchView();
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });
        } else {
            showToolBarAndPhotoTouchView();
        }
    }

    private void showToolBarAndPhotoTouchView() {
        toolBarFadeIn();
//        loadPhotoTouchIv();
        setPhotoViewClickEvent();
    }

    private void toolBarFadeIn() {
        mIsToolBarHidden = true;
        hideOrShowToolbar();
    }

//    private void loadPhotoTouchIv() {
//        Glide.with(this)
//                .load(getIntent().getStringExtra(Constants.PHOTO_DETAIL))
//                .error(R.drawable.ic_load_fail)
//                .listener(new RequestListener<String, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        photoIv.setVisibility(View.GONE);
//                        return false;
//                    }
//                })
//                .into(photoTouchIv);
//
//    }

    protected void hideOrShowToolbar() {
        toolbar.animate()
                .alpha(mIsToolBarHidden ? 1.0f : 0.0f)
                .setInterpolator(new DecelerateInterpolator(2))
                .start();
        mIsToolBarHidden = !mIsToolBarHidden;
    }

    private void hideOrShowStatusBar() {
        if (mIsStatusBarHidden) {
            SystemUiVisibilityUtil.enter(PhotoDetailActivity.this);
        } else {
            SystemUiVisibilityUtil.exit(PhotoDetailActivity.this);
        }
        mIsStatusBarHidden = !mIsStatusBarHidden;
    }

    @Override
    public void supportFinishAfterTransition() {
        mAttacher.cleanup();
        mAttacher = null;
        super.supportFinishAfterTransition();
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showMsg(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }
}
