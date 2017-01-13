package com.martian.martiannews.dagger.component;

import android.app.Activity;
import android.content.Context;

import com.martian.martiannews.dagger.module.ActivityModule;
import com.martian.martiannews.dagger.module.ApplicationModule;
import com.martian.martiannews.dagger.scope.ContextLife;
import com.martian.martiannews.dagger.scope.PerActivity;
import com.martian.martiannews.mvp.ui.activitys.MainActivity;
import com.martian.martiannews.mvp.ui.activitys.NewsChannelActivity;
import com.martian.martiannews.mvp.ui.activitys.NewsDetailActivity;
import com.martian.martiannews.mvp.ui.activitys.NewsPhotoDetailActivity;
import com.martian.martiannews.mvp.ui.activitys.PhotoActivity;
import com.martian.martiannews.mvp.ui.activitys.PhotoDetailActivity;

import dagger.Component;

/**
 * Created by yangpei on 2016/12/9.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class,modules = ActivityModule.class)
public interface ActivityComponent {

    @ContextLife("Activity")
    Context getActivtyContext();

    @ContextLife("Application")
    Context getApplicationContext();

    Activity getActivity();

    void inject(MainActivity mainActivity);

    void inject(NewsDetailActivity newsDetailActivity);

    void inject(NewsChannelActivity newsChannelActivity);

    void inject(PhotoActivity photoActivity);

    void inject(PhotoDetailActivity photoDetailActivity);

    void inject(NewsPhotoDetailActivity newsPhotoDetailActivity);
}
