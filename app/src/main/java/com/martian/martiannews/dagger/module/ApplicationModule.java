package com.martian.martiannews.dagger.module;

import android.content.Context;

import com.martian.martiannews.base.MyApplication;
import com.martian.martiannews.dagger.scope.ContextLife;
import com.martian.martiannews.dagger.scope.PerApp;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yangpei on 2016/12/9.
 */
@Module
public class ApplicationModule {
    private MyApplication myApplication;

    public ApplicationModule(MyApplication myApplication){
         this.myApplication = myApplication;
    }

    @Provides
    @PerApp
    @ContextLife("Application")
    public Context provideApplicationContext(){
        return myApplication.getApplicationContext();
    }
}
