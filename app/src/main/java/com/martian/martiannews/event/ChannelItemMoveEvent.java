package com.martian.martiannews.event;

/**
 * Created by yangpei on 2016/12/12.
 */

public class ChannelItemMoveEvent {

    private int fromPosition;
    private int toPosition;

    public int getFromPosition() {
        return fromPosition;
    }

    public int getToPosition() {
        return toPosition;
    }

    public ChannelItemMoveEvent(int fromPosition, int toPosition) {
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;

    }
}
