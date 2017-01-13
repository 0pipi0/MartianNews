package com.martian.martiannews.dagger.component;

import android.content.Context;

import com.martian.martiannews.dagger.module.ServiceModule;
import com.martian.martiannews.dagger.scope.ContextLife;
import com.martian.martiannews.dagger.scope.PerService;

import dagger.Component;

/**
 * Created by yangpei on 2016/12/9.
 */
@PerService
@Component(dependencies = ApplicationComponent.class,modules = ServiceModule.class)
public interface ServiceComponent {

    @ContextLife("Application")
    Context getApplicationContext();

    @ContextLife("Service")
    Context getServiceContext();

}
