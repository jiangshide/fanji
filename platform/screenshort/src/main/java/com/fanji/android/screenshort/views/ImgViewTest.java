package com.fanji.android.screenshort.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.fanji.android.screenshort.temp.IImg;
import com.fanji.android.screenshort.temp.Img;
import com.fanji.android.screenshort.temp.ImgPortrait;
import com.fanji.android.screenshort.temp.ImgTextView;
import com.fanji.android.util.LogUtil;

public class ImgViewTest extends FrameLayout implements
        ImgPortrait.Callback {

    private Img mImage = new Img();

    private boolean isText = true;

    public ImgViewTest(Context context) {
        this(context, null, 0);
    }

    public ImgViewTest(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImgViewTest(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageBitmap(Bitmap image) {
        mImage.setBitmap(image);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        onDrawImages(canvas);
    }

    private void onDrawImages(Canvas canvas) {
        canvas.save();
        mImage.onDrawImage(canvas);
        mImage.onDrawStickers(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mImage.onWindowChanged(right - left, bottom - top);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isText) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    setText("down", Color.RED, x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    setText("up", Color.BLUE, x, y);
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    public void enableText(boolean isText) {
        this.isText = isText;
    }

    public void setText(String str) {
        setText(str, Color.RED);
    }

    public void setText(String str, int color) {
        setText(str, color, 0, 0);
    }

    public void setText(String str, int color, float x, float y) {
        ImgTextView textView = new ImgTextView(getContext());

        textView.setText(str).setTextColor(color);

        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );

        layoutParams.gravity = Gravity.CENTER;

        textView.setX(x);
        textView.setY(y);
        LogUtil.e("----touch~x:", x, " | y:", y);
        addView(textView, layoutParams);
        textView.registerCallback(this);
        mImage.addSticker(textView);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public <V extends View & IImg> void onDismiss(V stickerView) {
        mImage.onDismiss(stickerView);
        invalidate();
    }

    @Override
    public <V extends View & IImg> void onShowing(V stickerView) {
        mImage.onShowing(stickerView);
        invalidate();
    }

    @Override
    public <V extends View & IImg> boolean onRemove(V stickerView) {
        if (mImage != null) {
            mImage.onRemoveSticker(stickerView);
        }
        stickerView.unregisterCallback(this);
        ViewParent parent = stickerView.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(stickerView);
        }
        return true;
    }

    public Bitmap saveBitmap() {
        mImage.stickAll();

        float scale = 1f / mImage.getScale();

        RectF frame = new RectF(mImage.getClipFrame());

        // 旋转基画布
        Matrix m = new Matrix();
        m.setRotate(mImage.getRotate(), frame.centerX(), frame.centerY());
        m.mapRect(frame);

        // 缩放基画布
        m.setScale(scale, scale, frame.left, frame.top);
        m.mapRect(frame);

        Bitmap bitmap = Bitmap.createBitmap(Math.round(frame.width()),
                Math.round(frame.height()), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        // 平移到基画布原点&缩放到原尺寸
        canvas.translate(-frame.left, -frame.top);
        canvas.scale(scale, scale, frame.left, frame.top);

        onDrawImages(canvas);

        return bitmap;
    }
}
