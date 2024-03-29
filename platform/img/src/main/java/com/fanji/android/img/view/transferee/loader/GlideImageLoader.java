package com.fanji.android.img.view.transferee.loader;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fanji.android.util.FileUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * created by jiangshide on 2020/4/20.
 * email:18311271399@163.com
 */
public class GlideImageLoader implements ImageLoader {
  private Context context;
  private Map<String, SourceCallback> callbackMap;

  private static final String CACHE_DIR = "TransGlide";

  private GlideImageLoader(Context context) {
    this.context = context;
    this.callbackMap = new HashMap<>();
  }

  public static GlideImageLoader with(Context context) {
    return new GlideImageLoader(context);
  }

  @Override
  public void showImage(final String imageUrl, final ImageView imageView,
      final Drawable placeholder, final SourceCallback sourceCallback) {
    callbackMap.put(imageUrl, sourceCallback);
    if (sourceCallback != null) sourceCallback.onStart();
    // download not support placeholder
    imageView.setImageDrawable(placeholder);
    Glide.with(imageView).download(imageUrl).listener(new RequestListener<File>() {
      @Override
      public boolean onLoadFailed(@Nullable
                                          GlideException e, Object model, Target<File> target, boolean isFirstResource) {
        SourceCallback callback = callbackMap.get(imageUrl);
        if (callback != null) callback.onDelivered(STATUS_DISPLAY_FAILED, null);
        return false;
      }

      @Override
      public boolean onResourceReady(File resource, Object model, Target<File> target,
                                     DataSource dataSource, boolean isFirstResource) {
        if (!imageUrl.endsWith(".gif")) // gif 图片需要 transferee 内部渲染，所以这里不作显示
        {
          imageView.setImageBitmap(BitmapFactory.decodeFile(resource.getAbsolutePath()));
        }
        checkSaveFile(resource, getFileName(imageUrl));
        SourceCallback callback = callbackMap.get(imageUrl);
        if (callback != null) {
          callback.onDelivered(STATUS_DISPLAY_SUCCESS, resource);
          callbackMap.remove(imageUrl);
        }
        return false;
      }
    }).preload();
  }

  @Override
  public void loadImageAsync(final String imageUrl, final ThumbnailCallback callback) {
    Glide.with(context).download(imageUrl).listener(new RequestListener<File>() {
      @Override
      public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target,
          boolean isFirstResource) {
        if (callback != null) {
          callback.onFinish(null);
        }
        return false;
      }

      @Override
      public boolean onResourceReady(File resource, Object model, Target<File> target,
          DataSource dataSource, boolean isFirstResource) {
        checkSaveFile(resource, getFileName(imageUrl));
        if (callback != null) {
          callback.onFinish(BitmapFactory.decodeFile(resource.getAbsolutePath()));
        }
        return false;
      }
    }).preload();
  }

  @Override
  public File getCache(String url) {
    File cacheFile = new File(getCacheDir(), getFileName(url));
    return cacheFile.exists() ? cacheFile : null;
  }

  @Override
  public void clearCache() {
    Glide.get(context).clearMemory();
    new Thread(new Runnable() {
      @Override
      public void run() {
        Glide.get(context).clearDiskCache();
        FileUtil.delete(getCacheDir());
      }
    }).start();
  }

  private File getCacheDir() {
    File cacheDir = new File(context.getCacheDir(), CACHE_DIR);
    if (!cacheDir.exists()) cacheDir.mkdirs();
    return cacheDir;
  }

  private String getFileName(String imageUrl) {
    String[] nameArray = imageUrl.split("/");
    return nameArray[nameArray.length - 1];
  }

  private void checkSaveFile(final File file, final String fileName) {
    final File cacheDir = getCacheDir();
    boolean exists = FileUtil.isFileExists(new File(cacheDir, fileName));
    if (!exists) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          File targetFile = new File(cacheDir, fileName);
          FileUtil.copyFile(file, targetFile);
        }
      }).start();
    }
  }
}
