package com.martian.martiannews.dagger.module;

import android.app.Activity;
import android.content.Context;

import com.martian.martiannews.dagger.scope.ContextLife;
import com.martian.martiannews.dagger.scope.PerActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yangpei on 2016/12/9.
 */
@Module
public class ActivityModule {
    private Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides
    @PerActivity
    @ContextLife("Activity")
    public Context provideActivityContext() {
        return activity;
    }

    @Provides
    @PerActivity
    public Activity provideActivity() {
        return activity;
    }

}
