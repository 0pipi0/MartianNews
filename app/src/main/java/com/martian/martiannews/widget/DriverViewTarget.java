package com.martian.martiannews.widget;

import android.graphics.Bitmap;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

/**
 * Created by yangpei on 2016/12/13.
 */

public class DriverViewTarget extends BitmapImageViewTarget {
    private ImageView view;
    public DriverViewTarget(ImageView view) {
        super(view);
        this.view = view;
    }

    @Override
    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
        int viewWidth = view.getWidth();
        float scale = resource.getWidth() / (viewWidth * 1.0f);
        int viewHeight = (int) (resource.getHeight() * scale);
        android.widget.FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();
        lp.width = viewWidth;
        lp.height = viewHeight;
        view.setLayoutParams(lp);
//        setCardViewLayoutParams(viewWidth, viewHeight);
        super.onResourceReady(resource, glideAnimation);
    }
}
