package com.martian.martiannews.mvp.ui.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.martian.martiannews.R;
import com.martian.martiannews.uitl.TransformUtils;
import com.nineoldandroids.animation.Animator;
import com.zxinsight.MLink;
import com.zxinsight.MWConfiguration;
import com.zxinsight.MagicWindowSDK;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.functions.Action1;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.activity_splash_iv)
    ImageView activitySplashIv;
    @BindView(R.id.activity_splash_tx)
    TextView activitySplashTx;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.zoomin, 0);
        setContentView(R.layout.activity_splash);
        unbinder = ButterKnife.bind(this);
        initData();
        initAnimation();
    }

    private void initData() {
        MWConfiguration config = new MWConfiguration(this);
        //设置渠道，非必须（渠道推荐在AndroidManifest.xml内填写）
//        config.setChannel("你的渠道名称")
        //开启Debug模式，显示Log，release时注意关闭
               config.setDebugModel(true)
                //带有Fragment的页面。具体查看2.2.2
                .setPageTrackWithFragment(true);
                //设置分享方式，如果之前有集成sharesdk，可在此开启
//                .setSharePlatform(MWConfiguration.ORIGINAL);
        MagicWindowSDK.initSDK(config);

        MLink.getInstance(this).registerWithAnnotation(this);

        if (getIntent().getData()!=null) {
            MLink.getInstance(this).router(this, getIntent().getData());
            //跳转后结束当前activity
            finish();
        } else {
            //如果需要应用宝跳转，则调用。否则不需要
            //MLink.getInstance(this).checkYYB();
            //跳转到首页
            initAnimation();
        }
    }

    private void initAnimation() {
        activitySplashIv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                startAnimation();
                activitySplashIv.getViewTreeObserver()
                        .removeGlobalOnLayoutListener(this);

            }
        });
    }

    private void startAnimation() {
        YoYo.with(Techniques.FadeInUp).duration(1000).playOn(activitySplashIv);
        YoYo.with(Techniques.RubberBand).duration(1000).delay(1000).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                goToMain();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(activitySplashTx);
    }

    private void goToMain() {
//        Observable.timer(2*1000, TimeUnit.MILLISECONDS)
//                .compose((ObservableTransformer<Long, Object>) TransformUtils.<Long>defaultSchedulers())
//                .subscribe(new Action1<Long>(){
//                    @Override
//                    public void call(Long aLong) {
//                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
//                        overridePendingTransition(0, android.R.anim.fade_out);
//                        SplashActivity.this.finish();
//                    }
//                });

//        Observable.timer(2*1000,TimeUnit.MILLISECONDS)
//                .compose((ObservableTransformer<Long, Object>) TransformUtils.defaultSchedulers())
//                .subscribe(new Action(){
//
//                    @Override
//                    public void run() throws Exception {
//
//                    }
//                });
        Observable.timer(2 * 1000, TimeUnit.MILLISECONDS)
                .compose(TransformUtils.<Long>defaultSchedulers())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        overridePendingTransition(0, android.R.anim.fade_out);
                        SplashActivity.this.finish();
                    }
                });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
