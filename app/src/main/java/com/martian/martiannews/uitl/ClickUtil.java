package com.martian.martiannews.uitl;

import android.os.SystemClock;

/**
 * Created by yangpei on 2016/12/12.
 */

public class ClickUtil {

    private static long mLastClickTime = 0;
    private static final int SPACE_TIME = 500;

    public static boolean isFastDoubleClick() {
        long time = SystemClock.elapsedRealtime();
        if (time - mLastClickTime <= SPACE_TIME) {
            return true;
        } else {
            mLastClickTime = time;
            return false;
        }
    }
}
