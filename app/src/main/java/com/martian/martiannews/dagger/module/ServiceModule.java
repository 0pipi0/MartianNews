package com.martian.martiannews.dagger.module;

import android.app.Service;
import android.content.Context;

import com.martian.martiannews.dagger.scope.ContextLife;
import com.martian.martiannews.dagger.scope.PerService;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yangpei on 2016/12/9.
 */
@Module
public class ServiceModule {
    private Service service;

    public ServiceModule(Service service) {
        this.service = service;
    }

    @Provides
    @PerService
    @ContextLife("Service")
    public Context provideServiceContext() {
        return service;
    }
}
