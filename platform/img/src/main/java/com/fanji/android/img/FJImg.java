package com.fanji.android.img;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.fanji.android.img.dispatcher.BitmapDispatcher;
import com.fanji.android.img.listener.IBitmapListener;
import com.fanji.android.img.listener.ILoadImgListener;
import com.fanji.android.img.request.BitmapRequest;
import com.fanji.android.img.transformation.BitmapTransformation;
import com.fanji.android.img.transformation.BlurTransformation;
import com.fanji.android.img.transformation.ZdCircleTransform;
import com.fanji.android.img.transformation.ZdRoundTransform;
import com.fanji.android.util.AppUtil;
import com.fanji.android.util.ImgUtil;

import java.io.File;
import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * created by jiangshide on 2015-09-12.
 * email:18311271399@163.com
 */
public final class FJImg {

    private static volatile FJImg instance;

    private LinkedBlockingQueue<BitmapRequest> requestQueue;

    private BitmapDispatcher[] bitmapDispatchers;

    private static final int DEFAULT_ROUND = 0;

    private static final String[] colors = {"#77B2CF", "#7EA3B0", "#A99DCA", "#7F9DC1", "#98CAAE", "#E2D0EB"};

    private final String CACHE_FILE = "image_manager_disk_cache";

    private FJImg() {
        this.requestQueue = new LinkedBlockingQueue<>();
        init();
    }

    public static FJImg getInstance() {
        if (instance == null) {
            synchronized (FJImg.class) {
                if (instance == null) {
                    instance = new FJImg();
                }
            }
        }
        return instance;
    }

    public static BitmapRequest with(Context context) {
        return new BitmapRequest(context);
    }

    private void init() {
        stop();
        initDispatch();
    }

    private void initDispatch() {
        int threadCount = Runtime.getRuntime().availableProcessors();
        bitmapDispatchers = new BitmapDispatcher[threadCount];
        for (int i = 0; i < threadCount; i++) {
            BitmapDispatcher bitmapDispatcher = new BitmapDispatcher(requestQueue);
            bitmapDispatcher.start();
            bitmapDispatchers[i] = bitmapDispatcher;
        }
    }

    public void stop() {
        if (bitmapDispatchers != null && bitmapDispatchers.length > 0) {
            for (BitmapDispatcher bitmapDispatcher : bitmapDispatchers) {
                if (!bitmapDispatcher.isInterrupted()) {
                    bitmapDispatcher.isInterrupted();
                }
            }
        }
    }

    public void addBitmapRequest(BitmapRequest bitmapRequest) {
        if (bitmapRequest == null) return;
        if (!requestQueue.contains(bitmapRequest)) {
            requestQueue.add(bitmapRequest);
        }
    }

    /**
     * @return
     */
    private static String getRandomColor() {
        return colors[(int) (Math.random() * colors.length)];
    }

    /**
     * 基于Activity为载体加载图片，必须保证Activity没有
     *
     * @param activity
     * @param imgUrl
     * @param imgView
     */
    public static void loadImage(AppCompatActivity activity, String imgUrl, ImageView imgView) {
        if (!contextAble(activity)) return;

        Glide.with(activity).load(imgUrl).into(imgView);
    }

    /**
     * 加载本地res资源文件
     *
     * @param resId   本地资源文件
     * @param imgView 图片view
     */
    public static void loadImage(int resId, ImageView imgView) {
        if (!contextAble(imgView.getContext())) return;
        Glide.with(imgView.getContext()).load(resId).into(imgView);
    }

    /**
     * @param imgUrl
     * @param imgView
     */
    public static void loadImage(String imgUrl, ImageView imgView) {
        if (!contextAble(imgView.getContext())) return;
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(new ColorDrawable(Color.parseColor(getRandomColor())));
        Glide.with(imgView.getContext()).load(imgUrl)
                .apply(requestOptions).dontAnimate().skipMemoryCache(false)
                .into(imgView);
    }

    /**
     * @param imgUrl
     * @param imgView
     */
    public static void loadImage(Uri imgUrl, ImageView imgView) {
        if (!contextAble(imgView.getContext())) return;
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(new ColorDrawable(Color.parseColor(getRandomColor())));
        Glide.with(imgView.getContext()).load(imgUrl)
                .apply(requestOptions).dontAnimate().skipMemoryCache(false)
                .into(imgView);
    }


    /**
     * @param imgUrl
     * @param imgView
     * @param fragment
     */
    public static void loadImage(String imgUrl, ImageView imgView, Fragment fragment) {
        if (!contextAble(imgView.getContext())) return;

        Glide.with(fragment).load(imgUrl).dontAnimate().skipMemoryCache(false).into(imgView);
    }

    /***
     *
     * @param imgUrl
     * @param imgView
     * @param imgDefaultId
     */
    public static void loadImage(String imgUrl, ImageView imgView, int imgDefaultId) {
        if (!contextAble(imgView.getContext())) return;
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(imgDefaultId);
        Glide.with(imgView.getContext()).load(imgUrl).apply(requestOptions).dontAnimate().skipMemoryCache(false).into(imgView);
    }

    /***
     *
     * @param imgUrl
     * @param imgView
     * @param path
     */
    public static void loadImage(String imgUrl, ImageView imgView, String path) {
        if (!contextAble(imgView.getContext())) return;
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(ImgUtil.path2Drawable(path));
        Glide.with(imgView.getContext()).load(imgUrl).apply(requestOptions).dontAnimate().skipMemoryCache(false).into(imgView);
    }

    /**
     * @param imgUrl
     * @param imgView
     * @param imgDefaultId
     */
    public static void loadImageCircle(String imgUrl, ImageView imgView, int imgDefaultId) {
        if (!contextAble(imgView.getContext())) return;
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(imgDefaultId);
        requestOptions.transform(new ZdCircleTransform());
        Glide.with(imgView.getContext()).load(imgUrl).apply(requestOptions).dontAnimate().skipMemoryCache(false).into(imgView);
    }

    /**
     * @param url
     * @return
     */
    public static String thumbAliOssUrl(String url,boolean isVideo) {
        return thumbAliOssUrl(url,0,0,-1,isVideo);
    }

    /**
     * @param url
     * @param width
     * @return
     */
    public static String thumbAliOssUrl(String url, int width,boolean isVideo) {
        return thumbAliOssUrl(url,width,0,-1,isVideo);
    }

    /**
     * @param url
     * @param width
     * @return
     */
    public static String thumbAliOssUrl(String url, int width) {
        return thumbAliOssUrl(url,width,0,-1,false);
    }

    /**
     * @param url
     * @param width
     * @return
     */
    public static String thumbAliOssUrl(String url, int width,int height) {
        return thumbAliOssUrl(url,width,height,-1,false);
    }

    /**
     * @param url
     * @param width
     * @return
     */
    public static String thumbAliOssUrl(String url, int width,int height,int rotate,boolean isVideo) {
        if (url != null && url.startsWith("http")) {
            if(isVideo)return String.format("%s?x-oss-process=video/snapshot,t_10000,m_fast", url);
            if(height > 0 && width > 0 && rotate !=-1) return String.format("%s?x-oss-process=image/resize,w_%d,h_%d/auto-orient,%d", url, width,height,rotate);
            if(height > 0 && width > 0)return String.format("%s?x-oss-process=image/resize,w_%d,h_%d", url, width,height);
            if(height > 0)return String.format("%s?x-oss-process=image/resize,w_%d", url, width);
        }
        return url;
    }

    /**
     * @param imgUrl
     * @param imgView
     */
    public static void loadImageCircle(String imgUrl, ImageView imgView) {
        if(imgView == null || imgView.getContext() == null)return;
        if (!contextAble(imgView.getContext())) return;
        RequestOptions requestOptions = new RequestOptions();
//        requestOptions.transform(new ColorDrawable(Color.parseColor(getRandomColor()));
        requestOptions.transform(new ZdCircleTransform());
        Glide.with(imgView.getContext()).load(imgUrl)
                .apply(requestOptions).dontAnimate().skipMemoryCache(false).into(imgView);
    }

    /**
     * @param imgUrl
     * @param imageView
     */
    public static void loadImageRound(String imgUrl, ImageView imageView) {
        loadImageRound(imgUrl, imageView, DEFAULT_ROUND);
    }

    /**
     * @param imgUrl
     * @param imgView
     * @param radiusDp
     */
    public static void loadImageRound(String imgUrl, ImageView imgView, int radiusDp) {
        if (!contextAble(imgView.getContext())) return;
        loadImageRound(imgUrl, imgView, radiusDp, ZdRoundTransform.CornerType.ALL);
    }
    /**
     * @param imgUrl
     * @param imgView
     * @param radiusDp
     */
    public static void loadImageRound(String imgUrl, ImageView imgView, int radiusDp,int imgDefaultId) {
        if (!contextAble(imgView.getContext())) return;
        loadImageRound(imgUrl, imgView, radiusDp, imgDefaultId,ZdRoundTransform.CornerType.ALL);
    }

    /**
     * @param imgUrl
     * @param imgView
     * @param cornerTypep
     */
    public static void loadImageRound(String imgUrl, ImageView imgView, ZdRoundTransform.CornerType cornerTypep) {
        if (!contextAble(imgView.getContext())) return;
        loadImageRound(imgUrl, imgView, DEFAULT_ROUND, cornerTypep);
    }

    /**
     * @param imgUrl
     * @param imgView
     * @param radiusDp
     * @param cornerType
     */
    public static void loadImageRound(String imgUrl, ImageView imgView, int radiusDp, ZdRoundTransform.CornerType cornerType) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(new ColorDrawable(Color.parseColor(getRandomColor())));
        requestOptions.transform(new ZdRoundTransform(radiusDp, cornerType));
        Glide.with(imgView.getContext()).load(imgUrl)
                .apply(requestOptions).dontAnimate().skipMemoryCache(false).into(imgView);
    }
    /**
     * @param imgUrl
     * @param imgView
     * @param radiusDp
     * @param cornerType
     */
    public static void loadImageRound(String imgUrl, ImageView imgView, int radiusDp,int imgDefaultId, ZdRoundTransform.CornerType cornerType) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(imgDefaultId);
        requestOptions.transform(new ZdRoundTransform(radiusDp, cornerType));
        Glide.with(imgView.getContext()).load(imgUrl)
                .apply(requestOptions).dontAnimate().skipMemoryCache(false).into(imgView);
    }

    /**
     * @param imgUrl
     * @param imgView
     * @param defDrawable
     */
    public static void loadImage(String imgUrl, ImageView imgView, Drawable defDrawable) {
        if (!contextAble(imgView.getContext())) return;
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(defDrawable);
        Glide.with(imgView.getContext()).load(imgUrl).apply(requestOptions).dontAnimate().skipMemoryCache(false).into(imgView);
    }

    /**
     * @param imgUrl
     * @param imageView
     */
    public static void loadImagBlur(String imgUrl, ImageView imageView) {
        loadImagBlur(imgUrl,imageView, BlurTransformation.MAX_RADIUS,BlurTransformation.DEFAULT_SAMPLING);
    }

    /**
     * @param imgUrl
     * @param imageView
     */
    public static void loadImagBlur(String imgUrl, ImageView imageView,int radius, int sampling) {
        if (!contextAble(imageView.getContext())) return;
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.transform(new BlurTransformation(imageView.getContext(), radius,sampling));
        Glide.with(imageView.getContext()).load(imgUrl)
                .apply(requestOptions).dontAnimate().skipMemoryCache(false)
                .into(imageView);
    }

    /**
     * 加载文件图片，不使用缓存
     *
     * @param picPath 图片在磁盘中的路径
     * @param imgView 图片view
     */
    public static void loadImageFromDisk(String picPath, ImageView imgView) {
        if (!contextAble(imgView.getContext())) return;
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.skipMemoryCache(true);
        Glide.with(imgView.getContext())
                .load(picPath)
                .apply(requestOptions).dontAnimate().skipMemoryCache(false)
                .into(imgView);
    }

    /**
     * @param url
     * @param listener
     */
    public static void loadImage(String url,final IBitmapListener listener) {
        loadImage(AppUtil.getApplicationContext(),url,DEFAULT_ROUND,ZdRoundTransform.CornerType.ALL,listener);
    }

    /**
     * @param url
     * @param listener
     */
    public static void loadImage(String url,int radiusDp, final IBitmapListener listener) {
        loadImage(AppUtil.getApplicationContext(),url,radiusDp,ZdRoundTransform.CornerType.ALL,listener);
    }

    /**
     * @param context
     * @param url
     * @param listener
     */
    public static void loadImage(Context context, String url,int radiusDp, ZdRoundTransform.CornerType cornerType, final IBitmapListener listener) {
        BitmapTransformation bitmapTransformation = new ZdRoundTransform(radiusDp,cornerType);
        if(radiusDp == -2){
            bitmapTransformation = new ZdCircleTransform();
        }
        Glide.with(context).asBitmap().load(url).dontAnimate().skipMemoryCache(false).transform(bitmapTransformation).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                if (listener != null) {
                    if (resource != null) {
                        listener.onSuccess(resource);
                    } else {
                        listener.onFailure();
                    }
                }
            }
        });
    }

    /**
     * @param context
     * @param imgUrl
     */
    public static void preloadImage(Context context, String imgUrl) {
        if (!contextAble(context)) return;
        Glide.with(context).load(imgUrl).preload();
    }

    /**
     * 同上，主要是为了回调后，将bitmap转换成drawable
     *
     * @param imgResId
     * @param listener
     */
    public static void preLoadImage(Context context, int imgResId, RequestListener listener) {
        if (!contextAble(context)) return;

        Glide.with(context).load(imgResId).listener(listener);
    }

    /**
     * 支持回调，同时支持图片是否缓存到磁盘
     */
    public static void preLoadImage(Context context, String imgUrl, RequestListener listener) {
        if (!contextAble(context)) return;

        Glide.with(context).load(imgUrl).listener(listener);
    }

    /**
     * 需要运行在子线程
     *
     * @param context
     * @param imgUrl
     * @return
     * @throws ExecutionException
     */
    public static File getBitmapFromCache(Context context, String imgUrl) throws ExecutionException, InterruptedException {
        return Glide.with(context).load(imgUrl)
                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .get();
    }

    /**
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static boolean contextAble(Context context) {
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            return !activity.isFinishing() && !activity.isDestroyed();
        }

        return true;
    }

    /**
     * @param activity
     * @param smallImgUrl
     * @param bigImgUrl
     * @param view
     * @param loadListener
     */
    public static void loadImage(AppCompatActivity activity, String smallImgUrl, String bigImgUrl, ImageView view, final ILoadImgListener loadListener) {
        RequestBuilder<Drawable> thumbnailRequest = Glide.with(activity).load(smallImgUrl);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        Glide.with(activity).load(bigImgUrl)
                .thumbnail(thumbnailRequest)
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return loadListener.onLoadFailed(e, model, isFirstResource);
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return loadListener.onResourceReady(resource, model, isFirstResource);
                    }
                }).into(view);
    }

    /**
     * 获取Glide磁盘缓存大小
     *
     * @return
     */
    public String getCacheSize() {
        try {
            return getFormatSize(getFolderSize(new File(AppUtil.getApplicationContext().getCacheDir() + "/" + CACHE_FILE)));
        } catch (Exception e) {
            e.printStackTrace();
            return "获取失败";
        }
    }

    /**
     * 清除Glide磁盘缓存，自己获取缓存文件夹并删除方法
     *
     * @return
     */
    public boolean cleanCatchDisk() {
        return deleteFolderFile(AppUtil.getApplicationContext().getCacheDir() + "/" + CACHE_FILE, true);
    }

    /**
     * 清除图片磁盘缓存，调用Glide自带方法
     *
     * @return
     */
    public boolean clearCacheDiskSelf() {
        try {
            Glide.get(AppUtil.getApplicationContext()).clearDiskCache();
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(AppUtil.getApplicationContext()).clearDiskCache();
                    }
                }).start();
            } else {
                Glide.get(AppUtil.getApplicationContext()).clearDiskCache();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 清除Glide内存缓存
     *
     * @return
     */
    public boolean clearCacheMemory() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                Glide.get(AppUtil.getApplicationContext()).clearMemory();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 获取指定文件夹内所有文件大小的和
     *
     * @param file
     * @return
     * @throws Exception
     */
    private long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    private static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    /**
     * 按目录删除文件夹文件方法
     *
     * @param filePath
     * @param deleteThisPath
     * @return
     */
    private boolean deleteFolderFile(String filePath, boolean deleteThisPath) {
        try {
            File file = new File(filePath);
            if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (File file1 : files) {
                    deleteFolderFile(file1.getAbsolutePath(), true);
                }
            }
            if (deleteThisPath) {
                if (!file.isDirectory()) {
                    file.delete();
                } else {
                    if (file.listFiles().length == 0) {
                        file.delete();
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
