package com.martian.martiannews.mvp.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.martian.martiannews.R;
import com.martian.martiannews.base.MyApplication;
import com.martian.martiannews.common.Constants;
import com.martian.martiannews.event.PhotoDetailOnClickEvent;
import com.martian.martiannews.mvp.ui.fragment.base.BaseFragment;
import com.martian.martiannews.uitl.RxBus;
import com.martian.martiannews.uitl.TransformUtils;
import com.orhanobut.logger.Logger;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by yangpei on 2016/12/22.
 */

public class PhotoDetailFragment extends BaseFragment {

    @BindView(R.id.photo_view)
    PhotoView photoView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private String mImgSrc;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImgSrc = getArguments().getString(Constants.PHOTO_DETAIL_IMGSRC);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        initViews(view);
        return view;
    }

    @Override
    public void initInjector() {
    }

    @Override
    public void initViews(View view) {
        progressBar.setVisibility(View.VISIBLE);
        initPhotoView();
        setPhotoViewClickEvent();
    }

    private void initPhotoView() {
        mSubscription = Observable.timer(100, TimeUnit.MILLISECONDS) // 直接使用glide加载的话，activity切换动画时背景短暂为默认背景色
                .compose(TransformUtils.<Long>defaultSchedulers())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Glide.with(MyApplication.getAppContext()).load(mImgSrc).asBitmap()
                                .format(DecodeFormat.PREFER_ARGB_8888)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .error(R.drawable.ic_load_fail)
                                .into(photoView);

                    }
                });
    }

    private void setPhotoViewClickEvent() {
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v1) {
                Logger.d("");
                handleOnTabEvent();
            }
        });
    }

    private void handleOnTabEvent() {
        RxBus.getInstance().post(new PhotoDetailOnClickEvent());
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_news_photo_detail;
    }

}
