package com.martian.martiannews.mvp.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.martian.martiannews.R;
import com.martian.martiannews.base.MyApplication;
import com.martian.martiannews.mvp.entity.PhotoGirl;
import com.martian.martiannews.mvp.ui.adapter.base.BaseRecyclerViewAdapter;
import com.martian.martiannews.uitl.DimenUtil;
import com.martian.martiannews.widget.RatioImageView;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yangpei on 2016/12/12.
 */

public class PhotoListAdapter extends BaseRecyclerViewAdapter<PhotoGirl> {

    private int width = (int) ((DimenUtil.getScreenSize()-DimenUtil.dp2px(6)) / 2);

    private Map<Integer, Integer> mHeights = new HashMap<>();

    @Inject
    public PhotoListAdapter() {
        super(null);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case TYPE_FOOTER:
                view = getView(parent, R.layout.item_news_footer);
                return new FooterViewHolder(view);
            case TYPE_ITEM:
                view = getView(parent, R.layout.item_photo);
                final ItemViewHolder itemViewHolder = new ItemViewHolder(view);
//                itemViewHolder.setIsRecyclable(false);
                setItemOnClickEvent(itemViewHolder);
                return itemViewHolder;
            default:
                throw new RuntimeException("there is no type that matches the type " +
                        viewType + " + make sure your using types correctly");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof ItemViewHolder) {
           final ImageView imageView = ((ItemViewHolder) holder).mPhotoIv;
            android.widget.FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) imageView.getLayoutParams();
                            lp.width = width;
                            lp.height = width;
                            imageView.setLayoutParams(lp);
//            Logger.d("--width-"+imageView.getWidth()+"---"+imageView.getMeasuredWidth());
            Glide.with(MyApplication.getAppContext())
                    .load(mList.get(position).getUrl())
//                    .asBitmap()*//*.format(DecodeFormat.PREFER_ARGB_8888)*//* // 没有动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
//                    .placeholder(R.color.image_place_holder)
                    .error(R.drawable.ic_load_fail)
                    .thumbnail(0.4f)
                    .override(width, width)
//                    .listener(new RequestListener<String, GlideDrawable>() {
//                        @Override
//                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//
//                            int rwidth = resource.getIntrinsicWidth();
//                            int rheigth = resource.getIntrinsicWidth();
//                            Logger.d("--width:"+rwidth+"--heigth:"+rwidth);
//                            float ratio = ((float) rwidth/(float)rheigth);
//                            int height = (int)((float)width/ratio);
//                            android.widget.FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) imageView.getLayoutParams();
//                            lp.width = width;
//                            lp.height = height;
//                            Logger.d("--lp.width:"+lp.width+"--lp.height:"+lp.height+"-ratio--:"+ratio);
//                            imageView.setLayoutParams(lp);
//                            return true;
//                        }
//                    })
//                    .into(simpleTarget);
                    .into(imageView);

//            Picasso.with(MyApplication.getAppContext()).load(mList.get(position).getUrl())
//                    .placeholder(R.color.image_place_holder)
//                    .error(R.drawable.ic_load_fail)
//                    .into(((ItemViewHolder) holder).mPhotoIv);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mIsShowFooter && isFooterPosition(position)) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    private void setItemOnClickEvent(final RecyclerView.ViewHolder holder) {
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, holder.getLayoutPosition());
                }
            });
        }
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.photo_iv)
//        RatioImageView mPhotoIv;

        @BindView(R.id.photo_iv)
        ImageView mPhotoIv;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
