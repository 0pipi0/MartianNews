package com.martian.martiannews.dagger.component;

import android.content.Context;

import com.martian.martiannews.dagger.module.ApplicationModule;
import com.martian.martiannews.dagger.scope.ContextLife;
import com.martian.martiannews.dagger.scope.PerApp;

import dagger.Component;

/**
 * Created by yangpei on 2016/12/9.
 */
@PerApp
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    @ContextLife("Application")
    Context getApplication();
}
