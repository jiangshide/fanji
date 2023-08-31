package com.fanji.android.screenshort.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.fanji.android.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Time:2021/10/30 下午8:00
 * Author:Jack
 * Email:jiangshide@jingos.com
 * Description: Jingos
 */
public class DoodleView extends androidx.appcompat.widget.AppCompatImageView implements LifecycleObserver {

    private static final String TAG = "DoodleView";

    public interface DoodleCallback {
        void onDrawStart();

        void onDrawing();

        void onDrawComplete();

        void onRevertStateChanged(boolean canRevert);

        void onEnableRecover(boolean canRecover);
    }

    private DoodleCallback mCallBack;

    private int mViewWidth, mViewHeight;

    private float mValidRadius = convertDpToPixel(2);

    private float mGraphValidRange = convertDpToPixel(6);

    private float mGraphValidClickRange = convertDpToPixel(8);
    private float mDotRadius = convertDpToPixel(8);

    private Paint mTempPaint;
    private Path mTempPath;

    private Path mTempMosaicPath;

    private Paint mTempMosaicPaint;

    private DoodleBean mTempGraphBean;

    private Paint mMosaicPaint;
    private int mPaintColor = Color.RED;

    private int mPaintWidth = convertDpToPixel(3);

    private int mPaintSize = 40;

    private Paint mBitmapPaint;
    private Paint mGraphRectPaint;
    private Paint mDotPaint;

    private Bitmap mMoasicBitmap;
    private Bitmap mOriginBitmap;

    private MODE mMode = MODE.NONE;

    private GRAPH_TYPE mCurrentGraphType = GRAPH_TYPE.LINE;

    private ArrayList<DoodleBean> mGraphPath = new ArrayList<>();

    private boolean mIsEditable = false;

    private ArrayList<DoodleBean> mDoodlePath = new ArrayList<>();

    private ArrayList<DoodleBean> mDoodleTempPath = new ArrayList<>();

    private ArrayList<DoodleBean> mTextPath = new ArrayList<>();

    private ArrayList<DoodleBean> mMosaicPath = new ArrayList<>();


    public enum MODE {
        NONE, GRAPH_MODE, DOODLE_MODE, MOSAIC_MODE, DRAG, RECTANGLE, ROUND, ARROW, PEN, TEXT, MOSAIC, REVERT, RECOVER, SHARE, ZOOM, DRAG_START, DRAG_END
    }

    private MODE mGraphMode = MODE.NONE;
    private DoodleBean mCurrentGraphBean;
    private boolean mIsClickOnGraph = false;

    private float mStartX, mStartY;
    private float mMoveX, mMoveY;

    private float mDelaX, mDelaY;

    public DoodleView(Context context) {
        super(context);
        init();
        autoBindLifecycle(context);
    }

    public DoodleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        autoBindLifecycle(context);
    }

    public DoodleView setCallBack(DoodleCallback callBack) {
        this.mCallBack = callBack;
        return this;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mViewWidth <= 0 || mViewHeight <= 0) {
            return;
        }

        if (mOriginBitmap != null) {
            canvas.drawBitmap(mOriginBitmap, 0, 0, mBitmapPaint);
        }

        drawMosaicPath(canvas);
        drawDoodlePath(canvas);
        drawGraphs(canvas);
        drawTxtPath(canvas);
        if (mCallBack != null) {
            int size = mDoodleTempPath.size();
            mCallBack.onEnableRecover(size > 0);
        }
    }

    private void drawDoodlePath(Canvas canvas) {
        if (mDoodlePath.size() > 0) {
            for (DoodleBean doodleBean : mDoodlePath) {
                canvas.drawPath(doodleBean.path, doodleBean.paint);
            }
        }
        if (mTempPath != null && mTempPaint != null) {
            canvas.drawPath(mTempPath, mTempPaint);
        }
    }

    private void drawTxtPath(Canvas canvas) {
        for (DoodleBean doodleBean : mTextPath) {
            canvas.drawText(doodleBean.str, doodleBean.x, doodleBean.y, doodleBean.paint);
        }
    }

    private void drawMosaicPath(Canvas canvas) {
        if (mMoasicBitmap != null) {
            int layerCount = canvas.saveLayer(0, 0, mViewWidth, mViewHeight, null, Canvas.ALL_SAVE_FLAG);
            if (mMosaicPath.size() > 0) {
                for (DoodleBean doodleBean : mMosaicPath) {
                    canvas.drawPath(doodleBean.path, doodleBean.paint);
                }
            }
            if (mTempMosaicPath != null && mTempMosaicPaint != null) {
                canvas.drawPath(mTempMosaicPath, mTempMosaicPaint);
            }
            canvas.drawBitmap(mMoasicBitmap, 0, 0, mMosaicPaint);
            canvas.restoreToCount(layerCount);
        }
    }

    private void drawGraphs(Canvas canvas) {
        if (mGraphPath.size() > 0) {
            for (int i = 0; i < mGraphPath.size(); i++) {
                DoodleBean graphBean = mGraphPath.get(i);
                if (graphBean.isPass) {
                    drawGraph(canvas, graphBean);
                    if (mIsClickOnGraph && i == mGraphPath.size() - 1) {
                        if (graphBean.type == GRAPH_TYPE.LINE) {
                            canvas.drawPath(getLineRectPath(graphBean), mGraphRectPaint);
                            canvas.drawCircle(graphBean.startX, graphBean.startY, mDotRadius, mDotPaint);
                            canvas.drawCircle(graphBean.endX, graphBean.endY, mDotRadius, mDotPaint);
                        } else {
                            canvas.drawRect(graphBean.startX, graphBean.startY, graphBean.endX, graphBean.endY, mGraphRectPaint);
                            canvas.drawCircle(graphBean.startX, graphBean.startY, mDotRadius, mDotPaint);
                            canvas.drawCircle(graphBean.endX, graphBean.endY, mDotRadius, mDotPaint);
                        }
                    }
                }
            }
        }
        if (mTempGraphBean != null) {
            drawGraph(canvas, mTempGraphBean);
        }
    }

    /**
     * @param canvas    canvas
     * @param graphBean graphBean
     */
    private void drawGraph(Canvas canvas, DoodleBean graphBean) {
        if (graphBean.isPass) {
            if (graphBean.type == GRAPH_TYPE.RECT) {
                graphBean.paint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(graphBean.startX, graphBean.startY, graphBean.endX, graphBean.endY, graphBean.paint);
            } else if (graphBean.type == GRAPH_TYPE.OVAL) {
                graphBean.paint.setStyle(Paint.Style.STROKE);
                canvas.drawOval(new RectF(graphBean.startX, graphBean.startY, graphBean.endX, graphBean.endY), graphBean.paint);
            } else if (graphBean.type == GRAPH_TYPE.ARROW) {
                graphBean.paint.setStyle(Paint.Style.FILL);
                drawArrow(graphBean.startX, graphBean.startY, graphBean.endX, graphBean.endY, canvas, graphBean.paint);
            } else if (graphBean.type == GRAPH_TYPE.LINE) {
                graphBean.paint.setStyle(Paint.Style.FILL);
                canvas.drawLine(graphBean.startX, graphBean.startY, graphBean.endX, graphBean.endY, graphBean.paint);
            } else if (graphBean.type == GRAPH_TYPE.DIRECT_LINE) {
                graphBean.paint.setStyle(Paint.Style.FILL);
                canvas.drawLine(graphBean.startX, graphBean.startY, graphBean.endX, graphBean.endY, graphBean.paint);
            }
        }
    }

    /**
     * @param originBitmap drawable
     */
    public void setOriginBitmap(@NonNull Bitmap originBitmap) {
        mOriginBitmap = originBitmap;
        initOriginBitmap();
    }

    private void init() {
        setMode(mMode);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            mViewWidth = w;
            mViewHeight = h;

            initOriginBitmap();
        }
    }

    private void initOriginBitmap() {
        if (mOriginBitmap != null && mViewHeight > 0 && mViewWidth > 0) {
            mOriginBitmap = Bitmap.createScaledBitmap(mOriginBitmap, mViewWidth, mViewHeight, true);
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);

            mMosaicPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mMosaicPaint.setFilterBitmap(false);
            mMosaicPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            makeMosaicBitmap();

            mGraphRectPaint = new Paint();
            mGraphRectPaint.setAntiAlias(true);
            mGraphRectPaint.setColor(mPaintColor);
            mGraphRectPaint.setStyle(Paint.Style.STROKE);
            mGraphRectPaint.setStrokeWidth(mPaintWidth);
            mGraphRectPaint.setStrokeCap(Paint.Cap.ROUND);
            mGraphRectPaint.setStrokeJoin(Paint.Join.ROUND);
            mGraphRectPaint.setPathEffect(new DashPathEffect(new float[]{convertDpToPixel(3.5f), convertDpToPixel(2.5f)}, 0));

            mDotPaint = new Paint();
            mDotPaint.setAntiAlias(true);
            mDotPaint.setColor(mPaintColor);
            mDotPaint.setStyle(Paint.Style.FILL);
            mDotPaint.setStrokeCap(Paint.Cap.ROUND);
            mDotPaint.setStrokeJoin(Paint.Join.ROUND);

            postInvalidate();
        }
    }

    private void setModePaint(MODE mode) {
        if (mode == MODE.DOODLE_MODE) {
            mTempPaint = new Paint();
            mTempPaint.setAntiAlias(true);
            mTempPaint.setColor(mPaintColor);
            mTempPaint.setStyle(Paint.Style.STROKE);
            mTempPaint.setStrokeWidth(mPaintWidth);
            mTempPaint.setStrokeCap(Paint.Cap.ROUND);
            mTempPaint.setStrokeJoin(Paint.Join.ROUND);
        } else if (mode == MODE.MOSAIC_MODE) {
            mTempMosaicPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTempMosaicPaint.setAntiAlias(true);
            mTempMosaicPaint.setDither(true);
            mTempMosaicPaint.setStyle(Paint.Style.STROKE);
            mTempMosaicPaint.setTextAlign(Paint.Align.CENTER);
            mTempMosaicPaint.setStrokeCap(Paint.Cap.ROUND);
            mTempMosaicPaint.setStrokeJoin(Paint.Join.ROUND);
            mTempMosaicPaint.setStrokeWidth(mPaintWidth * 2);
        } else if (mode == MODE.GRAPH_MODE) {
            mTempPaint = new Paint();
            mTempPaint.setAntiAlias(true);
            mTempPaint.setColor(mPaintColor);
            mTempPaint.setStrokeWidth(mPaintWidth);
            mTempPaint.setStrokeCap(Paint.Cap.ROUND);
            mTempPaint.setStrokeJoin(Paint.Join.ROUND);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsEditable) {
            mMoveX = event.getX();
            mMoveY = event.getY();
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                mStartX = mMoveX;
                mStartY = mMoveY;
                mDelaX = 0;
                mDelaY = 0;
                if (!mIsClickOnGraph) {
                    touchDownNormalPath();
                } else if (mCurrentGraphBean != null && mGraphPath.size() > 0) {
                    touchDownInitGraphOperate();
                }
                if (mCallBack != null) {
                    mCallBack.onDrawStart();
                }
                return true;
            } else if (action == MotionEvent.ACTION_MOVE) {
                mDelaX += Math.abs(mMoveX - mStartX);
                mDelaY += Math.abs(mMoveY - mStartY);
                if (!mIsClickOnGraph) {
                    touchMoveNormalDraw();
                } else if (mCurrentGraphBean != null && mGraphPath.size() > 0) {
                    touchMoveGraphOperate();
                }

                if (mCallBack != null) {
                    mCallBack.onDrawing();
                }
                postInvalidate();
                return true;
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                if (mDelaX < mValidRadius && mDelaY < mValidRadius) {
                    judgeGraphClick();
                    mTempPath = null;
                    mTempPaint = null;
                    mTempMosaicPath = null;
                    mTempMosaicPaint = null;
                    mTempGraphBean = null;
                    postInvalidate();
                    if (mCallBack != null) {
                        mCallBack.onDrawComplete();
                        mCallBack.onRevertStateChanged(getCurrentPathSize(mMode));
                    }
                    return false;
                }
                if (!mIsClickOnGraph) {
                    if (mMode == MODE.DOODLE_MODE) {
                        DoodleBean pathBean = new DoodleBean();
                        pathBean.setDraw(mTempPath, mTempPaint, MODE.DOODLE_MODE);
                        mDoodlePath.add(pathBean);
                    } else if (mMode == MODE.MOSAIC_MODE) {
                        DoodleBean pathBean = new DoodleBean();
                        pathBean.setDraw(mTempMosaicPath, mTempMosaicPaint, MODE.MOSAIC_MODE);
                        mMosaicPath.add(pathBean);
                    } else if (mMode == MODE.GRAPH_MODE) {
                        mGraphPath.add(mTempGraphBean);
                        if (mGraphPath.size() > 0) {
                            mCurrentGraphBean = mGraphPath.get(mGraphPath.size() - 1);
                            mIsClickOnGraph = true;
                            mGraphMode = MODE.DRAG;
                        }
                    }
                    mTempPath = null;
                    mTempPaint = null;
                    mTempMosaicPath = null;
                    mTempMosaicPaint = null;
                    mTempGraphBean = null;
                }

                if (mIsClickOnGraph && mCurrentGraphBean != null) {
                    mCurrentGraphBean.startPoint.x = mCurrentGraphBean.startX;
                    mCurrentGraphBean.startPoint.y = mCurrentGraphBean.startY;
                    mCurrentGraphBean.endPoint.x = mCurrentGraphBean.endX;
                    mCurrentGraphBean.endPoint.y = mCurrentGraphBean.endY;
                    mGraphMode = MODE.DRAG;
                } else {
                    mGraphMode = MODE.NONE;
                    mCurrentGraphBean = null;
                }

                if (mCallBack != null) {
                    mCallBack.onDrawComplete();
                    mCallBack.onRevertStateChanged(getCurrentPathSize(mMode));
                }
                postInvalidate();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    private void touchDownNormalPath() {
        if (mMode == MODE.GRAPH_MODE) {
            setModePaint(mMode);
            mTempGraphBean = new DoodleBean();
            mTempGraphBean.setGraph(mStartX, mStartY, mStartX, mStartY, mCurrentGraphType, mTempPaint, MODE.GRAPH_MODE);
        } else if (mMode == MODE.DOODLE_MODE) {
            setModePaint(mMode);
            mTempPath = new Path();
            mTempPath.moveTo(mStartX, mStartY);
        } else if (mMode == MODE.MOSAIC_MODE) {
            setModePaint(mMode);
            mTempMosaicPath = new Path();
            mTempMosaicPath.moveTo(mStartX, mStartY);
        }
    }

    private void touchDownInitGraphOperate() {
        mCurrentGraphBean.rectFList.add(new RectF(mCurrentGraphBean.startPoint.x, mCurrentGraphBean.startPoint.y,
                mCurrentGraphBean.endPoint.x, mCurrentGraphBean.endPoint.y));
        mCurrentGraphBean.clickPoint.set(mMoveX, mMoveY);
        RectF startDotRect = new RectF(mCurrentGraphBean.startX - mDotRadius, mCurrentGraphBean.startY - mDotRadius,
                mCurrentGraphBean.startX + mDotRadius, mCurrentGraphBean.startY + mDotRadius);
        RectF endDotRect = new RectF(mCurrentGraphBean.endX - mDotRadius, mCurrentGraphBean.endY - mDotRadius,
                mCurrentGraphBean.endX + mDotRadius, mCurrentGraphBean.endY + mDotRadius);
        if (startDotRect.contains(mMoveX, mMoveY)) {
            mGraphMode = MODE.DRAG_START;
        } else if (endDotRect.contains(mMoveX, mMoveY)) {
            mGraphMode = MODE.DRAG_END;
        } else {
            mGraphMode = MODE.DRAG;
        }
    }

    private void touchMoveNormalDraw() {
        if (mMode == MODE.DOODLE_MODE) {
            mTempPath.lineTo(mMoveX, mMoveY);
        } else if (mMode == MODE.MOSAIC_MODE) {
            mTempMosaicPath.lineTo(mMoveX, mMoveY);
        } else if (mMode == MODE.GRAPH_MODE) {
            if (mTempGraphBean != null) {
                if (mDelaX > mGraphValidRange || mDelaY > mGraphValidRange) {
                    mTempGraphBean.isPass = true;
                    if (mTempGraphBean.type == GRAPH_TYPE.DIRECT_LINE) {
                        float[] point = getDirectLineEndPoint(mTempGraphBean.startX, mTempGraphBean.startY, mMoveX, mMoveY);
                        mTempGraphBean.endX = point[0];
                        mTempGraphBean.endY = point[1];
                        mTempGraphBean.endPoint.x = point[0];
                        mTempGraphBean.endPoint.y = point[1];
                        if (mTempGraphBean.rectFList.size() == 1) {
                            RectF rectF = mTempGraphBean.rectFList.get(0);
                            if (mTempGraphBean.startY == mTempGraphBean.endY) {
                                rectF.left = mTempGraphBean.startX;
                                rectF.top = mTempGraphBean.startY - mDotRadius;
                                rectF.right = mTempGraphBean.endX;
                                rectF.bottom = mTempGraphBean.startY + mDotRadius;
                            } else {
                                rectF.left = mTempGraphBean.startX - mDotRadius;
                                rectF.top = mTempGraphBean.startY;
                                rectF.right = mTempGraphBean.startX + mDotRadius;
                                rectF.bottom = mTempGraphBean.endY;
                            }
                        }
                    } else {
                        mTempGraphBean.endX = mMoveX;
                        mTempGraphBean.endY = mMoveY;
                        mTempGraphBean.endPoint.x = mMoveX;
                        mTempGraphBean.endPoint.y = mMoveY;
                        if (mTempGraphBean.rectFList.size() == 1) {
                            mTempGraphBean.rectFList.get(0).right = mMoveX;
                            mTempGraphBean.rectFList.get(0).bottom = mMoveY;
                        }
                    }
                }
            }
        }
    }

    private void touchMoveGraphOperate() {
        if (mCurrentGraphBean != null && mGraphPath.size() > 0) {
            float dx = mMoveX - mCurrentGraphBean.clickPoint.x;
            float dy = mMoveY - mCurrentGraphBean.clickPoint.y;
            changeGraphRect(dx, dy);
        }
    }

    /**
     * @param offsetX
     * @param offsetY
     */
    private void changeGraphRect(float offsetX, float offsetY) {
        if (mCurrentGraphBean != null && mGraphPath.size() > 0) {
            int rectSize = mCurrentGraphBean.rectFList.size();
            if (rectSize > 0) {
                RectF tempRectF = mCurrentGraphBean.rectFList.get((rectSize - 1));
                if (mGraphMode == MODE.DRAG) {
                    mCurrentGraphBean.startX = mCurrentGraphBean.startPoint.x + offsetX;
                    mCurrentGraphBean.startY = mCurrentGraphBean.startPoint.y + offsetY;
                    mCurrentGraphBean.endX = mCurrentGraphBean.endPoint.x + offsetX;
                    mCurrentGraphBean.endY = mCurrentGraphBean.endPoint.y + offsetY;
                } else if (mGraphMode == MODE.DRAG_START) {
                    if (mCurrentGraphBean.type == GRAPH_TYPE.DIRECT_LINE) {
                        if (mCurrentGraphBean.startX == mCurrentGraphBean.endX) {
                            mCurrentGraphBean.startY = mCurrentGraphBean.startPoint.y + offsetY;
                        } else {
                            mCurrentGraphBean.startX = mCurrentGraphBean.startPoint.x + offsetX;
                        }
                    } else {
                        mCurrentGraphBean.startX = mCurrentGraphBean.startPoint.x + offsetX;
                        mCurrentGraphBean.startY = mCurrentGraphBean.startPoint.y + offsetY;
                    }
                    Log.d(TAG, "拖动起始点");
                } else if (mGraphMode == MODE.DRAG_END) {
                    if (mCurrentGraphBean.type == GRAPH_TYPE.DIRECT_LINE) {
                        if (mCurrentGraphBean.startX == mCurrentGraphBean.endX) {
                            mCurrentGraphBean.endY = mCurrentGraphBean.endPoint.y + offsetY;
                        } else {
                            mCurrentGraphBean.endX = mCurrentGraphBean.endPoint.x + offsetX;
                        }
                    } else {
                        mCurrentGraphBean.endX = mCurrentGraphBean.endPoint.x + offsetX;
                        mCurrentGraphBean.endY = mCurrentGraphBean.endPoint.y + offsetY;
                    }
                }
                if (mCurrentGraphBean.type == GRAPH_TYPE.DIRECT_LINE) {
                    if (mCurrentGraphBean.startX == mCurrentGraphBean.endX) {
                        tempRectF.left = mCurrentGraphBean.startX - mDotRadius;
                        tempRectF.top = mCurrentGraphBean.startY;
                        tempRectF.right = mCurrentGraphBean.startX + mDotRadius;
                        tempRectF.bottom = mCurrentGraphBean.endY;
                    } else {
                        tempRectF.left = mCurrentGraphBean.startX;
                        tempRectF.top = mCurrentGraphBean.startY - mDotRadius;
                        tempRectF.right = mCurrentGraphBean.endX;
                        tempRectF.bottom = mCurrentGraphBean.startY + mDotRadius;
                    }
                } else {
                    tempRectF.left = mCurrentGraphBean.startX;
                    tempRectF.top = mCurrentGraphBean.startY;
                    tempRectF.right = mCurrentGraphBean.endX;
                    tempRectF.bottom = mCurrentGraphBean.endY;
                }
            }
        }
    }

    private void judgeGraphClick() {
        int clickIndex = -1;
        mIsClickOnGraph = false;
        mGraphMode = MODE.NONE;
        if (mCurrentGraphBean != null) {
            if (mCurrentGraphBean.rectFList.size() > 1) {
                mCurrentGraphBean.rectFList.remove(mCurrentGraphBean.rectFList.size() - 1);
            }
        }
        for (int i = mGraphPath.size() - 1; i > -1; i--) {
            DoodleBean graphBean = mGraphPath.get(i);
            if (graphBean != null) {
                RectF rectF = null;
                if (graphBean.type == GRAPH_TYPE.DIRECT_LINE) {
                    if (graphBean.rectFList.size() > 0) {
                        RectF lastedRect = graphBean.rectFList.get(graphBean.rectFList.size() - 1);
                        rectF = new RectF(Math.min(lastedRect.left, lastedRect.right) - mGraphValidClickRange,
                                Math.min(lastedRect.top, lastedRect.bottom - mGraphValidClickRange),
                                Math.max(lastedRect.left, lastedRect.right + mGraphValidClickRange),
                                Math.max(lastedRect.top, lastedRect.bottom + mGraphValidClickRange));
                    }
                } else {
                    rectF = new RectF(Math.min(graphBean.startX, graphBean.endX) - mGraphValidClickRange,
                            Math.min(graphBean.startY, graphBean.endY) - mGraphValidClickRange,
                            Math.max(graphBean.startX, graphBean.endX) + mGraphValidClickRange,
                            Math.max(graphBean.startY, graphBean.endY) + mGraphValidClickRange);
                }
                if (rectF != null && rectF.contains(mMoveX, mMoveY)) {
                    mCurrentGraphBean = graphBean;
                    mIsClickOnGraph = true;
                    mGraphMode = MODE.DRAG;
                    clickIndex = i;
                    break;
                }
            } else {
                mCurrentGraphBean = null;
                mIsClickOnGraph = false;
                mGraphMode = MODE.NONE;
            }
        }
        if (mGraphPath.size() <= 0) {
            mCurrentGraphBean = null;
            mIsClickOnGraph = false;
            mGraphMode = MODE.NONE;
        }

        if (mIsClickOnGraph && mCurrentGraphBean != null && clickIndex > -1 && clickIndex < mGraphPath.size()) {
            mGraphPath.remove(clickIndex);
            mGraphPath.add(mCurrentGraphBean);
        } else {
            mCurrentGraphBean = null;
            mIsClickOnGraph = false;
            mGraphMode = MODE.NONE;
        }
    }

    private Path getLineRectPath(DoodleBean graphBean) {
        if (graphBean == null) {
            return null;
        }
        Path path = null;
        if (graphBean.type == GRAPH_TYPE.LINE) {
            path = new Path();
            float radius = getRotation(graphBean.startX, graphBean.startY, graphBean.endX, graphBean.endY);
            if (radius <= 0) {
                radius += 180;
            }
            radius -= 90;
            radius += 180;
            float[] point1 = getCoordinatePoint(graphBean.startX, graphBean.startY, mDotRadius, radius);
            float[] point4 = getCoordinatePoint(graphBean.startX, graphBean.startY, mDotRadius, radius + 180);
            float[] point2 = getCoordinatePoint(graphBean.endX, graphBean.endY, mDotRadius, radius);
            float[] point3 = getCoordinatePoint(graphBean.endX, graphBean.endY, mDotRadius, radius + 180);
            path.moveTo(point1[0], point1[1]);
            path.lineTo(point2[0], point2[1]);
            path.lineTo(point3[0], point3[1]);
            path.lineTo(point4[0], point4[1]);
            path.lineTo(point1[0], point1[1]);
            path.close();
        }
        return path;
    }

    /**
     * @param radius
     * @param cirAngle
     * @return x，y
     */
    public static float[] getCoordinatePoint(float centerX, float centerY, float radius, float cirAngle) {
        float[] point = new float[2];
        double arcAngle = Math.toRadians(cirAngle);
        if (cirAngle < 90) {
            point[0] = (float) (centerX + Math.cos(arcAngle) * radius);
            point[1] = (float) (centerY + Math.sin(arcAngle) * radius);
        } else if (cirAngle == 90) {
            point[0] = centerX;
            point[1] = centerY + radius;
        } else if (cirAngle > 90 && cirAngle < 180) {
            arcAngle = Math.PI * (180 - cirAngle) / 180.0;
            point[0] = (float) (centerX - Math.cos(arcAngle) * radius);
            point[1] = (float) (centerY + Math.sin(arcAngle) * radius);
        } else if (cirAngle == 180) {
            point[0] = centerX - radius;
            point[1] = centerY;
        } else if (cirAngle > 180 && cirAngle < 270) {
            arcAngle = Math.PI * (cirAngle - 180) / 180.0;
            point[0] = (float) (centerX - Math.cos(arcAngle) * radius);
            point[1] = (float) (centerY - Math.sin(arcAngle) * radius);
        } else if (cirAngle == 270) {
            point[0] = centerX;
            point[1] = centerY - radius;
        } else {
            arcAngle = Math.PI * (360 - cirAngle) / 180.0;
            point[0] = (float) (centerX + Math.cos(arcAngle) * radius);
            point[1] = (float) (centerY - Math.sin(arcAngle) * radius);
        }

        return point;
    }

    /**
     * @return
     */
    public int revertPath() {
        int size = 0;
        if (!mIsClickOnGraph && mMode == MODE.DOODLE_MODE) {
            size = mDoodlePath.size();
            if (size > 0) {
                DoodleBean drawPathBean = mDoodlePath.remove(size - 1);
                if (drawPathBean != null) {
                    mDoodleTempPath.add(drawPathBean);
                }
            }
            if (mCallBack != null) {
                mCallBack.onRevertStateChanged(mDoodlePath.size() > 0);
            }
        } else if (!mIsClickOnGraph && mMode == MODE.MOSAIC_MODE) {
            size = mMosaicPath.size();
            if (size > 0) {
                mMosaicPath.remove(size - 1);
            }
            if (mCallBack != null) {
                mCallBack.onRevertStateChanged(mMosaicPath.size() > 0);
            }
        } else if (mIsClickOnGraph && mCurrentGraphBean != null) {
            size = mCurrentGraphBean.rectFList.size();
            if (size > 1) {
                mCurrentGraphBean.rectFList.remove(size - 1);
                int currentSize = size - 1;
                if (currentSize > 0) {
                    RectF rectF = mCurrentGraphBean.rectFList.get(currentSize - 1);
                    if (mCurrentGraphBean.type == GRAPH_TYPE.DIRECT_LINE) {
                        if (mCurrentGraphBean.startX == mCurrentGraphBean.endX) {
                            mCurrentGraphBean.startY = rectF.top;
                            mCurrentGraphBean.endY = rectF.bottom;
                            mCurrentGraphBean.startPoint.y = rectF.top;
                            mCurrentGraphBean.endPoint.y = rectF.bottom;
                            mCurrentGraphBean.startX = (rectF.left + rectF.right) / 2;
                            mCurrentGraphBean.endX = (rectF.left + rectF.right) / 2;
                            mCurrentGraphBean.startPoint.x = (rectF.left + rectF.right) / 2;
                            mCurrentGraphBean.endPoint.x = (rectF.left + rectF.right) / 2;
                        } else {
                            mCurrentGraphBean.startX = rectF.left;
                            mCurrentGraphBean.endX = rectF.right;
                            mCurrentGraphBean.startPoint.x = rectF.left;
                            mCurrentGraphBean.endPoint.x = rectF.right;
                            mCurrentGraphBean.startY = (rectF.top + rectF.bottom) / 2;
                            mCurrentGraphBean.endY = (rectF.top + rectF.bottom) / 2;
                            mCurrentGraphBean.startPoint.y = (rectF.top + rectF.bottom) / 2;
                            mCurrentGraphBean.endPoint.y = (rectF.top + rectF.bottom) / 2;
                        }
                    } else {
                        mCurrentGraphBean.startX = rectF.left;
                        mCurrentGraphBean.startY = rectF.top;
                        mCurrentGraphBean.endX = rectF.right;
                        mCurrentGraphBean.endY = rectF.bottom;
                        mCurrentGraphBean.startPoint.x = rectF.left;
                        mCurrentGraphBean.startPoint.y = rectF.top;
                        mCurrentGraphBean.endPoint.x = rectF.right;
                        mCurrentGraphBean.endPoint.y = rectF.bottom;
                    }
                }
            } else {
                deleteCurrentGraph();
            }
            if (mCallBack != null) {
                if (!mIsClickOnGraph) {
                    mCallBack.onRevertStateChanged(getCurrentPathSize(mMode));
                } else {
                    mCallBack.onRevertStateChanged(mIsClickOnGraph);
                }
            }
            size -= 1;
        }
        postInvalidate();
        return size;
    }

    public void recover() {
        int size = mDoodleTempPath.size();
        if (size > 0) {
            DoodleBean drawPathBean = mDoodleTempPath.remove(size - 1);
            if (drawPathBean != null) {
                mDoodlePath.add(drawPathBean);
                postInvalidate();
            }
        }
    }

    /**
     * @param mode mode
     * @return boolean
     */
    public boolean getCurrentPathSize(MODE mode) {
        boolean result = false;
        if (!mIsClickOnGraph && mode == MODE.DOODLE_MODE) {
            if (mDoodlePath.size() > 0) {
                result = true;
            }
        } else if (!mIsClickOnGraph && mode == MODE.MOSAIC_MODE) {
            if (mMosaicPath.size() > 0) {
                result = true;
            }
        } else if (mIsClickOnGraph && mCurrentGraphBean != null) {
            result = true;
        }
        return result;
    }

    public void deleteCurrentGraph() {
        if (mIsClickOnGraph && mCurrentGraphBean != null && mGraphPath.size() > 0) {
            mGraphPath.remove(mCurrentGraphBean);
            mCurrentGraphBean = null;
            mIsClickOnGraph = false;
            mGraphMode = MODE.NONE;
        }
        postInvalidate();
    }

    public void clearGraphFocus() {
        mCurrentGraphBean = null;
        mIsClickOnGraph = false;
        mGraphMode = MODE.NONE;
        postInvalidate();
    }

    private Bitmap makeMosaicBitmap() {
        if (mMoasicBitmap != null) {
            return mMoasicBitmap;
        }

        int w = Math.round(mViewWidth / 16f);
        int h = Math.round(mViewHeight / 16f);

        if (mOriginBitmap != null) {
            mMoasicBitmap = Bitmap.createScaledBitmap(mOriginBitmap, w, h, false);
            mMoasicBitmap = Bitmap.createScaledBitmap(mMoasicBitmap, mViewWidth, mViewHeight, false);
        }
        return mMoasicBitmap;
    }

    /**
     * @return float
     */
    public float getRotation(float startX, float startY, float endX, float endY) {
        float deltaX = startX - endX;
        float deltaY = startY - endY;
        double radians = Math.atan2(deltaY, deltaX);
        return (float) Math.toDegrees(radians);
    }

    /**
     * @param graphType graphType
     */
    public void setGraphType(GRAPH_TYPE graphType) {
        clearGraphFocus();
        this.mCurrentGraphType = graphType;
        if (mCallBack != null) {
            mCallBack.onRevertStateChanged(getCurrentPathSize(mMode));
        }
    }

    public void setPaintSize(int size) {
        this.mPaintSize = size;
    }

    public void addTxt(String str) {
        Paint paint = new Paint();
        paint.setColor(mPaintColor);
        paint.setTextSize(mPaintSize);
        DoodleBean doodleBean = new DoodleBean();
        doodleBean.setText(str, 200, 300, paint, MODE.TEXT);
        mTextPath.add(doodleBean);
        invalidate();
    }

    /**
     * @param paintWidth
     */
    public void setPaintWidth(int paintWidth) {
        this.mPaintWidth = paintWidth;
    }

    private void drawArrow(float sx, float sy, float ex, float ey, Canvas canvas, Paint paint) {
        int size = 8;
        int count = 30;
        float x = ex - sx;
        float y = ey - sy;
        double d = x * x + y * y;
        double r = Math.sqrt(d);
        float zx = (float) (ex - (count * x / r));
        float zy = (float) (ey - (count * y / r));
        float xz = zx - sx;
        float yz = zy - sy;
        double zd = xz * xz + yz * yz;
        double zr = Math.sqrt(zd);
        Path triangle = new Path();
        triangle.moveTo(sx, sy);
        triangle.lineTo((float) (zx + size * yz / zr), (float) (zy - size * xz / zr));
        triangle.lineTo((float) (zx + size * 2 * yz / zr), (float) (zy - size * 2 * xz / zr));
        triangle.lineTo(ex, ey);
        triangle.lineTo((float) (zx - size * 2 * yz / zr), (float) (zy + size * 2 * xz / zr));
        triangle.lineTo((float) (zx - size * yz / zr), (float) (zy + size * xz / zr));
        triangle.close();
        canvas.drawPath(triangle, paint);
    }

    private float[] getDirectLineEndPoint(float sx, float sy, float ex, float ey) {
        float degrees = getRotation(sx, sy, ex, ey);
        float[] point = new float[2];
        if ((-45 <= degrees && degrees <= 45) || degrees >= 135 || degrees <= -135) {
            point[0] = ex;
            point[1] = sy;
        } else {
            point[0] = sx;
            point[1] = ey;
        }
        return point;
    }

    /**
     * @param color
     */
    public void setPaintColor(int color) {
        this.mPaintColor = color;
        LogUtil.e("setPaintColor~color:", color);
        if (mTempPaint != null) {
            mTempPaint.setColor(mPaintColor);
        }
    }

    public void setEditable(boolean editable) {
        this.mIsEditable = editable;
    }

    public void setMode(MODE mode) {
        clearGraphFocus();
        this.mMode = mode;
        if (mCallBack != null) {
            mCallBack.onRevertStateChanged(getCurrentPathSize(mMode));
        }
    }

    public enum GRAPH_TYPE {
        RECT, OVAL, ARROW, LINE, DIRECT_LINE
    }

    class DoodleBean {
        /**
         * draw
         */
        public Path path;

        /**
         * text
         */
        public String str;
        public float x;
        public float y;

        /**
         * graph
         */
        public float startX, startY, endX, endY;
        public GRAPH_TYPE type;
        public PointF clickPoint = new PointF();
        PointF startPoint = new PointF();
        PointF endPoint = new PointF();
        boolean isPass = false;
        List<RectF> rectFList = new ArrayList<>();

        /**
         * base
         */
        public Paint paint;
        public MODE mode;

        public void setBase(Paint paint, MODE mode) {
            this.paint = paint;
            this.mode = mode;
        }

        /**
         * draw
         *
         * @param path
         * @param paint
         * @param mode
         */
        public void setDraw(Path path, Paint paint, MODE mode) {
            setBase(paint, mode);
            this.path = path;
        }

        /**
         * text
         *
         * @param str
         * @param x
         * @param y
         * @param paint
         * @param mode
         */
        public void setText(String str, float x, float y, Paint paint, MODE mode) {
            setBase(paint, mode);
            this.str = str;
            this.x = x;
            this.y = y;
        }

        /**
         * graph:
         *
         * @param startX
         * @param startY
         * @param endx
         * @param endY
         * @param type
         * @param paint
         * @param mode
         */
        public void setGraph(float startX, float startY, float endx, float endY, GRAPH_TYPE type, Paint paint, MODE mode) {
            setBase(paint, mode);
            this.startX = startX;
            this.startY = startY;
            this.endX = endx;
            this.endY = endY;
            this.type = type;
            this.startPoint.x = startX;
            this.startPoint.y = startY;
            this.endPoint.x = endx;
            this.endPoint.y = endY;
            rectFList.add(new RectF(startX, startY, endx, endY));
        }
    }

    /**
     * auto recycling
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void clear() {
        if (mOriginBitmap != null && !mOriginBitmap.isRecycled()) {
            mOriginBitmap.recycle();
            mOriginBitmap = null;
        }
        if (mMoasicBitmap != null && !mMoasicBitmap.isRecycled()) {
            mMoasicBitmap.recycle();
            mMoasicBitmap = null;
        }
        mCallBack = null;
    }

    /**
     * auto bing lifecycle
     *
     * @param context
     */
    private void autoBindLifecycle(Context context) {
        if (context == null) {
            return;
        }
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            (activity).getLifecycle().addObserver(this);
            return;
        }
        if (context instanceof LifecycleOwner) {
            ((LifecycleOwner) context).getLifecycle().addObserver(this);
            return;
        }
    }

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        return (int) (metrics.density * dp + 0.5f);
    }

//    public Bitmap saveBitmap() {
//        mImage.stickAll();
//
//        float scale = 1f / mImage.getScale();
//
//        RectF frame = new RectF(mImage.getClipFrame());
//
//        Matrix m = new Matrix();
//        m.setRotate(mImage.getRotate(), frame.centerX(), frame.centerY());
//        m.mapRect(frame);
//
//        m.setScale(scale, scale, frame.left, frame.top);
//        m.mapRect(frame);
//
//        Bitmap bitmap = Bitmap.createBitmap(Math.round(frame.width()),
//                Math.round(frame.height()), Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(bitmap);
//
//        canvas.translate(-frame.left, -frame.top);
//        canvas.scale(scale, scale, frame.left, frame.top);
//
//        onDrawImages(canvas);
//
//        return bitmap;
//    }
}