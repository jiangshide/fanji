package com.fanji.android.screenshort.temp;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.View;

public interface ImgPortrait {

    boolean show();

    boolean remove();

    boolean dismiss();

    boolean isShowing();

    RectF getFrame();

    void onSticker(Canvas canvas);

    void registerCallback(IImg.Callback callback);

    void unregisterCallback(IImg.Callback callback);

    interface Callback {

        <V extends View & IImg> void onDismiss(V stickerView);

        <V extends View & IImg> void onShowing(V stickerView);

        <V extends View & IImg> boolean onRemove(V stickerView);
    }
}
