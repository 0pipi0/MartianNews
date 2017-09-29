package com.martian.martiannews.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.martian.martiannews.BuildConfig;
import com.martian.martiannews.common.Constants;
import com.martian.martiannews.dagger.component.ApplicationComponent;
import com.martian.martiannews.dagger.component.DaggerApplicationComponent;
import com.martian.martiannews.dagger.module.ApplicationModule;
import com.martian.martiannews.greendao.DaoMaster;
import com.martian.martiannews.greendao.DaoSession;
import com.martian.martiannews.greendao.NewsChannelTableDao;
import com.martian.martiannews.uitl.MyUtils;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.zxinsight.Session;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by yangpei on 2016/12/9.
 */

public class MyApplication extends Application {

    private static final String TAG = "---";
    private RefWatcher refWatcher;
    private static MyApplication sAppContext;
    private ApplicationComponent applicationComponent;
    private static DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = this;
        initLeakCanary();
        initActivityLifecycleLogs();
        initStrictMode();
        initDayNightMode();
        initApplicationComponent();
        initLogger();
        setupDatabase();
        initMC();
        initPush();
    }

    private void initPush() {
        XGPushConfig.enableDebug(this, true);
        //信鸽注册代码
        XGPushManager.registerPush(this, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                Log.d("TPush", "注册成功，设备token为：" + data);
            }
            @Override
            public void onFail(Object data, int errCode, String msg) {
                Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
            }
        });
    }

    /**
     * LeakCanary
     */
    private void initLeakCanary() {
        refWatcher = (BuildConfig.DEBUG) ? LeakCanary.install(this) : RefWatcher.DISABLED;
    }
    /**
     * ApplicationComponent
     */
    private void initApplicationComponent() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

    }
    /**
     * Logger
     */
    private void initLogger() {
        com.orhanobut.logger.Logger
                .init(TAG)                           // default PRETTYLOGGER or use just init()
                .setMethodCount(5)                   //default 2
                .hideThreadInfo()                    // default shown
                .setLogLevel(BuildConfig.DEBUG?LogLevel.FULL:LogLevel.NONE) // default LogLevel.FULL
//              .logAdapter(new AndroidLogAdapter()) //default AndroidLogAdapter
        ;
    }

    /**
     * Database
     */
    private void setupDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, Constants.DB_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
        // 在 QueryBuilder 类中内置两个 Flag 用于方便输出执行的 SQL 语句与传递参数的值
        QueryBuilder.LOG_SQL = BuildConfig.DEBUG;
        QueryBuilder.LOG_VALUES = BuildConfig.DEBUG;
    }

    public static RefWatcher getRefWatcher(Context context) {
        MyApplication myApplication = (MyApplication) context.getApplicationContext();
        return myApplication.refWatcher;
    }
    /**
     * 获取ApplicationContext
     * @return
     */
    public static Context getAppContext() {
        return sAppContext;
    }

    /**
     * 获取ApplicationComponent
     * @return
     */
    public ApplicationComponent getApplicationComponent(){
        return applicationComponent;
    }

    /**
     * 获取mDaoSession
     * @return
     */
    public static NewsChannelTableDao getNewsChannelTableDao() {
        return mDaoSession.getNewsChannelTableDao();
    }

    private void initActivityLifecycleLogs() {
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
//                Logger.d("---"+activity.getClass()+"---onActivityCreated-");
            }

            @Override
            public void onActivityStarted(Activity activity) {
//                Logger.d("---"+activity.getClass()+"---onActivityStarted-");
            }

            @Override
            public void onActivityResumed(Activity activity) {
//                Logger.d("---"+activity.getClass()+"---onActivityResumed-");
            }

            @Override
            public void onActivityPaused(Activity activity) {
//                Logger.d("---"+activity.getClass()+"---onActivityPaused-");
            }

            @Override
            public void onActivityStopped(Activity activity) {
//                Logger.d("---"+activity.getClass()+"---onActivityStopped-");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
//                Logger.d("---"+activity.getClass()+"---onActivitySaveInstanceState-");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
//                Logger.d("---"+activity.getClass()+"---onActivityDestroyed-");
            }
        });
    }

    public static boolean isHavePhoto() {
        return MyUtils.getSharedPreferences().getBoolean(Constants.SHOW_NEWS_PHOTO, true);
    }

    private void initStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
//                            .penaltyDialog() // 弹出违规提示对话框
                            .penaltyLog() // 在logcat中打印违规异常信息
                            .build());
            StrictMode.setVmPolicy(
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog()
                            .build());
        }
    }

    private void initDayNightMode() {
        if (MyUtils.isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
    private void initMC() {
        Session.setAutoSession(this);
    }
}
