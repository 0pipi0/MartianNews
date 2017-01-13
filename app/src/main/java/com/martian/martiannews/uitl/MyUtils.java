package com.martian.martiannews.uitl;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.view.ViewGroup;

import com.martian.martiannews.R;
import com.martian.martiannews.base.MyApplication;
import com.martian.martiannews.common.Constants;
import com.orhanobut.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;

/**
 * Created by yangpei on 2016/12/10.
 */

public class MyUtils {


    /**
     * 取消Subscription
     * @param subscription
     */
    public static void cancelSubscription(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public static SharedPreferences getSharedPreferences() {
        return MyApplication.getAppContext()
                .getSharedPreferences(Constants.SHARES_COLOURFUL_NEWS, Context.MODE_PRIVATE);
    }

    /**
     * 动态设置Tab模式
     * @param tabLayout
     */
    public static void dynamicSetTabLayoutMode(TabLayout tabLayout) {
        int tabWidth = calculateTabWidth(tabLayout);
        int screenWidth = getScreenWith();

        if (tabWidth <= screenWidth) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
    }

    private static int calculateTabWidth(TabLayout tabLayout) {
        int tabWidth = 0;
        for (int i = 0; i < tabLayout.getChildCount(); i++) {
            final View view = tabLayout.getChildAt(i);
            view.measure(0, 0); // 通知父view测量，以便于能够保证获取到宽高
            tabWidth += view.getMeasuredWidth();
        }
        return tabWidth;
    }
    public static int getScreenWith() {
        return MyApplication.getAppContext().getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * from yyyy-MM-dd HH:mm:ss to MM-dd HH:mm
     */
    public static String formatDate(String before) {
        String after;
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .parse(before);
            after = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(date);
        } catch (ParseException e) {
            Logger.e("转换新闻日期格式异常：" + e.toString());
            return before;
        }
        return after;
    }

    /**
     * 分析请求服务器返回的错误
     * @param e
     * @return
     */
    public static String analyzeNetworkError(Throwable e) {
        String errMsg = MyApplication.getAppContext().getString(R.string.load_error);
        if (e instanceof HttpException) {
            int state = ((HttpException) e).code();
            if (state == 403) {
                errMsg = MyApplication.getAppContext().getString(R.string.retry_after);
            }
        }
        return errMsg;
    }

    public static int getColor(int nightColor, int dayColor) {
        int color;
        if (!MyUtils.isNightMode()) {
            color = nightColor;
        } else {
            color = dayColor;
        }
        return color;
    }

    public static boolean isNightMode() {
        SharedPreferences preferences = MyApplication.getAppContext().getSharedPreferences(
                Constants.SHARES_COLOURFUL_NEWS, Activity.MODE_PRIVATE);
        return preferences.getBoolean(Constants.NIGHT_THEME_MODE, false);
    }

    public static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }

    public static void saveTheme(boolean isNight) {
        SharedPreferences preferences = MyApplication.getAppContext().getSharedPreferences(
                Constants.SHARES_COLOURFUL_NEWS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.NIGHT_THEME_MODE, isNight);
        editor.apply();
    }

    public static void  getTaskInfo(Context context){
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList = am.getRunningTasks(10);
        for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskInfoList) {
            Logger.d("id: " + runningTaskInfo.id);
            Logger.d("description: " + runningTaskInfo.description);
            Logger.d("number of activities: " + runningTaskInfo.numActivities);
            Logger.d("topActivity: " + runningTaskInfo.topActivity);
            Logger.d("baseActivity: " + runningTaskInfo.baseActivity.toString());
        }
    }
}
