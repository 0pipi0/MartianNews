package com.martian.martiannews.mvp.interactor.impl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.martian.martiannews.R;
import com.martian.martiannews.base.MyApplication;
import com.martian.martiannews.listener.RequestCallBack;
import com.martian.martiannews.mvp.interactor.PhotoDetailInteractor;
import com.martian.martiannews.uitl.TransformUtils;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by yangpei on 2016/12/13.
 */

public class PhotoDetailInteractorImpl implements PhotoDetailInteractor<Uri> {

    @Inject
    public PhotoDetailInteractorImpl() {
    }

    @Override
    public Subscription saveImageAndGetImageUri(final RequestCallBack<Uri> listener, final String url) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(final Subscriber<? super Bitmap> subscriber) {
                Logger.d(Thread.currentThread().getName());

                final Bitmap[] bitmap = {null};
                Glide.with(MyApplication.getAppContext())
                        .load(url)
                        .asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        bitmap[0] = resource;
                    }
                });

                if (bitmap[0] == null) {
                    subscriber.onError(new Exception("下载图片失败"));
                }
                subscriber.onNext(bitmap[0]);
                subscriber.onCompleted();


            }
        }).flatMap(new Func1<Bitmap, Observable<Uri>>() {
                @Override
                public Observable<Uri> call(Bitmap bitmap) {
                    Logger.d(Thread.currentThread().getName());
                    return getUriObservable(bitmap, url);
                }
            })
                .compose(TransformUtils.<Uri>defaultSchedulers())
                .subscribe(new Subscriber<Uri>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.toString());
                        listener.onError(MyApplication.getAppContext().getString(R.string.error_try_again));
                    }

                    @Override
                    public void onNext(Uri uri) {
                        listener.success(uri);
                    }
                });
    }
    @NonNull
    private Observable<Uri> getUriObservable(Bitmap bitmap, String url) {
        File file = getImageFile(bitmap, url);
        if (file == null) {
            return Observable.error(new NullPointerException("Save image file failed!"));
        }
        Uri uri = Uri.fromFile(file);
        // 通知图库更新 //Update the System --> MediaStore.Images.Media --> 更新ContentUri
        Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        MyApplication.getAppContext().sendBroadcast(scannerIntent);
        return Observable.just(uri);
    }

    private File getImageFile(Bitmap bitmap, String url) {
        String fileName = "/martian/photo/" + url.hashCode() + ".jpg";
        File file = new File(Environment.getExternalStorageDirectory(), fileName); // getFilesDir()等只能在程序内部访问不能用作分享路径
        if (!file.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
