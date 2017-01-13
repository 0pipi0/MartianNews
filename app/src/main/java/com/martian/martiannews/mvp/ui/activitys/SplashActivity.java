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
        initAnimation();
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
        Observable.timer(2*1000,TimeUnit.MILLISECONDS)
                .compose(TransformUtils.<Long>defaultSchedulers())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
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
