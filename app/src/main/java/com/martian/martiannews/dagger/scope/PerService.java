package com.martian.martiannews.dagger.scope;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by yangpei on 2016/12/9.
 */
@Scope
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface PerService {
}
