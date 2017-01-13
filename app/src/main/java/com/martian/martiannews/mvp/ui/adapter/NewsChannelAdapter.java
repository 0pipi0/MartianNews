package com.martian.martiannews.mvp.ui.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.martian.martiannews.R;
import com.martian.martiannews.base.MyApplication;
import com.martian.martiannews.event.ChannelItemMoveEvent;
import com.martian.martiannews.greendao.NewsChannelTable;
import com.martian.martiannews.listener.OnItemClickListener;
import com.martian.martiannews.mvp.ui.adapter.base.BaseRecyclerViewAdapter;
import com.martian.martiannews.uitl.ClickUtil;
import com.martian.martiannews.uitl.MyUtils;
import com.martian.martiannews.uitl.RxBus;
import com.martian.martiannews.widget.ItemDragHelperCallback;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yangpei on 2016/12/12.
 */

public class NewsChannelAdapter extends BaseRecyclerViewAdapter<NewsChannelTable> implements
        ItemDragHelperCallback.OnItemMoveListener{

    private static final int TYPE_CHANNEL_FIXED = 2;
    private static final int TYPE_CHANNEL_NO_FIXED = 3;

    private ItemDragHelperCallback mItemDragHelperCallback;

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setItemDragHelperCallback(ItemDragHelperCallback itemDragHelperCallback) {
        mItemDragHelperCallback = itemDragHelperCallback;
    }

    public NewsChannelAdapter(List<NewsChannelTable> list) {
        super(list);
    }
    public List<NewsChannelTable> getData() {
        return mList;
    }

    @Override
    public NewsChannelViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_channel, parent, false);
        final NewsChannelViewHolder newsChannelViewHolder = new NewsChannelViewHolder(view);
        handleLongPress(newsChannelViewHolder);
        handleOnClick(newsChannelViewHolder);
        return newsChannelViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final NewsChannelTable newsChannel = mList.get(position);
        String newsChannelName = newsChannel.getNewsChannelName();
        NewsChannelViewHolder viewHolder = (NewsChannelViewHolder) holder;
        viewHolder.mNewsChannelTv.setText(newsChannelName);

        if (newsChannel.getNewsChannelIndex() == 0) {
            viewHolder.mNewsChannelTv.setTextColor(ContextCompat
                    .getColor(MyApplication.getAppContext(), getColorId()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        Boolean isFixed = mList.get(position).getNewsChannelFixed();
        if (isFixed) {
            return TYPE_CHANNEL_FIXED;
        } else {
            return TYPE_CHANNEL_NO_FIXED;
        }
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (isChannelFixed(fromPosition, toPosition)) {
            return false;
        }
        Collections.swap(mList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        RxBus.getInstance().post(new ChannelItemMoveEvent(fromPosition, toPosition));
        return true;
    }

    private void handleLongPress(final NewsChannelViewHolder newsChannelViewHolder) {
        if (mItemDragHelperCallback != null) {
            newsChannelViewHolder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    NewsChannelTable newsChannel = mList.get(newsChannelViewHolder.getLayoutPosition());
                    boolean isChannelFixed = newsChannel.getNewsChannelFixed();
                    if (isChannelFixed) {
                        mItemDragHelperCallback.setLongPressEnabled(false);
                    } else {
                        mItemDragHelperCallback.setLongPressEnabled(true);
                    }
                    return false;
                }
            });
        }
    }
    private void handleOnClick(final NewsChannelViewHolder newsChannelViewHolder) {
        if (mOnItemClickListener != null) {
            newsChannelViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!ClickUtil.isFastDoubleClick()) {
                        mOnItemClickListener.onItemClick(v, newsChannelViewHolder.getLayoutPosition());
                    }
                }
            });
        }
    }

    private int getColorId() {
        int colorId;
        if (MyUtils.isNightMode()) {
            colorId = R.color.alpha_40_white;
        } else {
            colorId = R.color.alpha_40_black;
        }
        return colorId;
    }

    private boolean isChannelFixed(int fromPosition, int toPosition) {
        return mList.get(fromPosition).getNewsChannelFixed() ||
                mList.get(toPosition).getNewsChannelFixed();
    }

    class NewsChannelViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.news_channel_tv)
        TextView mNewsChannelTv;

        public NewsChannelViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
