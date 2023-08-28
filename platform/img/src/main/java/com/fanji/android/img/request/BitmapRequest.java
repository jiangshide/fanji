package com.fanji.android.img.request;

import android.content.Context;
import android.widget.ImageView;

import com.fanji.android.img.FJImg;
import com.fanji.android.img.listener.IBitmapListener;
import com.fanji.android.util.EncryptUtil;

import java.lang.ref.SoftReference;

/**
 * created by jiangshide on 2015-09-13.
 * email:18311271399@163.com
 */
public class BitmapRequest {

    private String url;

    private Context context;

    private SoftReference<ImageView> imageViewSoftReference;

    private int resId;

    private IBitmapListener requestListener;

    private String urlMd5;

    public BitmapRequest(Context context) {
        this.context = context;
    }

    public BitmapRequest load(String url) {
        this.url = url;
        this.urlMd5 = EncryptUtil.encryptMd5(url);
        return this;
    }

    public BitmapRequest loading(int resId) {
        this.resId = resId;
        return this;
    }

    public BitmapRequest listener(IBitmapListener requestListener) {
        this.requestListener = requestListener;
        return this;
    }

    public void into(ImageView imageView) {
        imageView.setTag(this.urlMd5);
        this.imageViewSoftReference = new SoftReference<>(imageView);
        FJImg.getInstance().addBitmapRequest(this);
    }

    public String getUrl() {
        return url;
    }

    public Context getContext() {
        return context;
    }

    public ImageView getImageView() {
        return imageViewSoftReference.get();
    }

    public int getResId() {
        return resId;
    }

    public IBitmapListener getRequestListener() {
        return requestListener;
    }

    public String getUrlMd5() {
        return urlMd5;
    }
}
