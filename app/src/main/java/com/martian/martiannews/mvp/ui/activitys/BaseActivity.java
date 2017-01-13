package com.martian.martiannews.mvp.ui.activitys;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.martian.martiannews.R;
import com.martian.martiannews.annotation.BindValues;
import com.martian.martiannews.base.MyApplication;
import com.martian.martiannews.dagger.component.ActivityComponent;
import com.martian.martiannews.dagger.component.DaggerActivityComponent;
import com.martian.martiannews.dagger.module.ActivityModule;
import com.martian.martiannews.mvp.presenter.base.BasePresenter;
import com.martian.martiannews.uitl.MyUtils;
import com.martian.martiannews.uitl.NetUtil;
import com.orhanobut.logger.Logger;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.squareup.leakcanary.RefWatcher;

import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by yangpei on 2016/12/9.
 */

public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity {

    protected ActivityComponent activityComponent;

    protected boolean mIsHasNavigationView;

    private DrawerLayout drawerLayout;

    private Class mClass;

    protected Subscription mSubscription;
    protected NavigationView mBaseNavView;

    protected T mPresenter;

    private static final String isMainActivity = "MainActivity";

    private static final String isPhotoActivity = "PhotoActivity";

    private  String thisActivity = "MainActivity";

    private boolean isNeedFinish = false;

//    private View mNightView = null;
//    private boolean mIsAddedView;
//    private boolean mIsChangeTheme;
//
//    private WindowManager mWindowManager = null;


    public abstract int getLayoutId();

    public abstract void initInjector();

    public abstract void initViews();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAnnotation();
        NetUtil.isNetworkErrThenShowMsg();
        initActivityComponent();

        setStatusBarTranslucent();
        setNightOrDayMode();

        int layoutId = getLayoutId();
        setContentView(layoutId);
        initInjector();
        ButterKnife.bind(this);
        initToolBar();
        initViews();


        if (mIsHasNavigationView) {
            initDrawerLayout();
        }

        if (mPresenter != null) {
            mPresenter.onCreate();
        }

        initNightModeSwitch();
    }



    /**
     * Annotation
     */
    private  void initAnnotation(){
        if (getClass().isAnnotationPresent(BindValues.class)) {
            BindValues annotation = getClass().getAnnotation(BindValues.class);
            mIsHasNavigationView = annotation.mIsHasNavigationView();
        }
    }

    private void initActivityComponent() {
        activityComponent = DaggerActivityComponent.builder()
                .applicationComponent(((MyApplication) getApplication()).getApplicationComponent())
                .activityModule(new ActivityModule(this)).build();
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private  void initDrawerLayout(){

        //获取DrawerLayout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //获取ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //获取NavigationView
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if(navView!=null){
            navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_news:
                            mClass = MainActivity.class;
                            isNeedFinish = (TextUtils.equals(thisActivity,isMainActivity))?false:true;
                            break;
                        case R.id.nav_photo:
                            mClass = PhotoActivity.class;
                            isNeedFinish = (TextUtils.equals(thisActivity,isPhotoActivity))?false:true;
                            break;
                        case R.id.nav_video:
//                            Toast.makeText(BaseActivity.this, "施工准备中...", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return false;
                }
            });
        }

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener(){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (mClass != null) {
                    Intent intent = new Intent(BaseActivity.this, mClass);
                    // 此标志用于启动一个Activity的时候，若栈中存在此Activity实例，则把它调到栈顶。不创建多一个
//                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    mClass = null;
                    if (isNeedFinish){
                        isNeedFinish = false;
                        finish();
                    }

                }
            }
        });

    }

    private void initNightModeSwitch() {
        thisActivity = null;
        if (this instanceof MainActivity || this instanceof PhotoActivity) {
            thisActivity = (this instanceof MainActivity)?isMainActivity:isPhotoActivity;
            MenuItem menuNightMode = mBaseNavView.getMenu().findItem(R.id.nav_night_mode);
            SwitchCompat dayNightSwitch = (SwitchCompat) MenuItemCompat
                    .getActionView(menuNightMode);
            setCheckedState(dayNightSwitch);
            setCheckedEvent(dayNightSwitch);
        }
    }

    private void setCheckedState(SwitchCompat dayNightSwitch) {
        if (MyUtils.isNightMode()) {
            dayNightSwitch.setChecked(true);
        } else {
            dayNightSwitch.setChecked(false);
        }
    }

    private void setCheckedEvent(SwitchCompat dayNightSwitch) {
        dayNightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    changeToNight();
                    MyUtils.saveTheme(true);
                } else {
                    changeToDay();
                    MyUtils.saveTheme(false);
                }

//                mIsChangeTheme = true;
                drawerLayout.closeDrawer(GravityCompat.START);
                recreate();
            }
        });
    }

    public void changeToDay() {
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

//        mNightView.setBackgroundResource(android.R.color.transparent);
    }
//
    public void changeToNight() {
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        initNightView();
//        mNightView.setBackgroundResource(R.color.alpha_60_black);
    }

    public ActivityComponent getActivityComponent() {
        return activityComponent;
    }



    // TODO:适配4.4
    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected void setStatusBarTranslucent() {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                !(this instanceof NewsDetailActivity || this instanceof PhotoActivity
                        || this instanceof PhotoDetailActivity))
                || (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT
                && this instanceof NewsDetailActivity)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.colorPrimary);
        }
    }

    private void setNightOrDayMode() {
        if (MyUtils.isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

//            initNightView();
//            mNightView.setBackgroundResource(R.color.night_mask);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

//    private void initNightView() {
//        if (mIsAddedView) {
//            return;
//        }
//        // 增加夜间模式蒙板
//        WindowManager.LayoutParams nightViewParam = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.TYPE_APPLICATION,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                PixelFormat.TRANSPARENT);
//        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//        mNightView = new View(this);
//        mWindowManager.addView(mNightView, nightViewParam);
//        mIsAddedView = true;
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                }
                break;
            case R.id.action_about:
                if (mIsHasNavigationView) {
                    Intent intent = new Intent(this, AboutActivity.class);
                    startActivity(intent);
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mIsHasNavigationView) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (mIsHasNavigationView) {
            overridePendingTransition(0, 0);
        }
//        MyUtils.getTaskInfo(this);
//        getWindow().getDecorView().invalidate();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher(this);
        refWatcher.watch(this);
        MyUtils.cancelSubscription(mSubscription);

        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }
}
