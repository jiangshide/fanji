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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.fanji.android.screenshort.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 测试中文问题
 */
public class ScreenShotImageView extends androidx.appcompat.widget.AppCompatImageView implements LifecycleObserver{

    private final String TAG = "ScreenShotImageView--jsd--";

    private boolean mEnabledHwBitmapsInSwMode;

    public interface DoodleCallback {
        void onDrawStart();

        void onDrawing();

        void onDrawComplete();

        void onRevertStateChanged(boolean canRevert);

        void onEnableRecover(boolean canRecover);
    }

    public enum MODE {
        NONE, GRAPH_MODE, DOODLE_MODE, MOSAIC_MODE, DRAG, TEXT, ZOOM, DRAG_START, DRAG_END
    }

    public enum GRAPH_TYPE {
        RECT, OVAL, ARROW, LINE, DIRECT_LINE
    }

    private DoodleCallback mCallBack;

    private int mViewWidth, mViewHeight;

    private float mValidRadius = convertDpToPixel(2);

    /**
     * 判断手指移动距离，是否画了有效图形的区域
     */
    private float mGraphValidRange = convertDpToPixel(6);

    /**
     * 点击选择图形，扩大的相应的有效范围
     */
    private float mGraphValidClickRange = convertDpToPixel(8);
    /**
     * 可拖动的点的半径
     */
    private float mDotRadius = convertDpToPixel(8);

    /**
     * 暂时的涂鸦画笔
     */
    private Paint mTempPaint;
    /**
     * 暂时的涂鸦路径
     */
    private Path mTempPath;

    /**
     * 暂时的马赛克路径
     */
    private Path mTempMosaicPath;

    private Paint mTempMosaicPaint;

    /**
     * 暂时的图形实例，用来move时实时画路径
     */
    private DoodleBean mTempGraphBean;

    private Paint mMosaicPaint;
    /**
     * 画笔的颜色
     */
    private int mPaintColor = Color.RED;

    /**
     * 画笔的粗细
     */
    private int mPaintWidth = convertDpToPixel(3);

    private int mPaintSize = 40;

    private Paint mBitmapPaint;
    /**
     * 框住图形的path的画笔
     */
    private Paint mGraphRectPaint;
    private Paint mDotPaint;

    private Bitmap mMoasicBitmap;
    private Bitmap mOriginBitmap;

    private MODE mMode = MODE.NONE;

    private GRAPH_TYPE mCurrentGraphType = GRAPH_TYPE.LINE;

    /**
     * 是否可编辑
     */
    private boolean mIsEditable = false;

    private boolean mIsRecordAllPath = true;

    /**
     * 记录所有的路径
     */
    private ArrayList<DoodleBean> revertAllPath = new ArrayList<>();

    /**
     * recoverPath
     */
    private ArrayList<DoodleBean> recoverPath = new ArrayList<>();

    /**
     * 图形的路径
     */
    private ArrayList<DoodleBean> mGraphPath = new ArrayList<>();

    /**
     * 涂鸦的路径
     */
    private ArrayList<DoodleBean> mDoodlePath = new ArrayList<>();

    /**
     * 文本的路径
     */
    private ArrayList<DoodleBean> mTextPath = new ArrayList<>();

    /**
     * 马赛克路径
     */
    private ArrayList<DoodleBean> mMosaicPath = new ArrayList<>();


    /**
     * 图形的当前操作模式
     */
    private MODE mGraphMode = MODE.NONE;
    /**
     * 当前选中的图形
     */
    private DoodleBean mCurrentGraphBean;
    /**
     * 是否点击到图形了
     */
    private boolean mIsClickOnGraph = false;

    private float mStartX, mStartY;
    private float mMoveX, mMoveY;

    /**
     * 区分点击和滑动
     */
    private float mDelaX, mDelaY;

    public ScreenShotImageView(Context context) {
        super(context);
        Log.e(TAG,"ScreenShotImageView1");
        init();
        autoBindLifecycle(context);
    }

    public ScreenShotImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.e(TAG,"ScreenShotImageView2");
        init();
        autoBindLifecycle(context);
    }

    public void setHwBitmapsInSwModeEnabled(boolean enable) {
        mEnabledHwBitmapsInSwMode = enable;
        Log.e(TAG,"setHwBitmapsInSwModeEnabled~enable:"+enable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.setHwBitmapsInSwModeEnabled(mEnabledHwBitmapsInSwMode);
//        try {
//            super.onDraw(canvas);
//            Log.e(TAG,"ScreenShotImageView~onDraw");
//        } catch (Exception e) {
//            Log.e(TAG,"ScreenShotImageView~onDraw~e");
//            e.printStackTrace();
//        }

        if (mViewWidth <= 0 || mViewHeight <= 0) {
            return;
        }

        // 画原始图
        if (mOriginBitmap != null) {
            canvas.drawBitmap(mOriginBitmap, 0, 0, mBitmapPaint);
        }

        // 按照路径和画笔画图
        // 先画马赛克
        drawMosaicPath(canvas);
        // 再画涂鸦
        drawDoodlePath(canvas);
        // 再画图形
        drawGraphs(canvas);
        //文字标签
        drawTxtPath(canvas);
        if (mCallBack != null) {
            int size = recoverPath.size();
            mCallBack.onEnableRecover(size > 0);
        }
    }


    /**
     * 画涂鸦内容
     */
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

    /**
     * 画马赛克内容
     */
    private void drawMosaicPath(Canvas canvas) {
        if (mMoasicBitmap != null) {
            // 保存图层
            int layerCount = canvas.saveLayer(0, 0, mViewWidth, mViewHeight, null, Canvas.ALL_SAVE_FLAG);
            if (mMosaicPath.size() > 0) {
                for (DoodleBean doodleBean : mMosaicPath) {
                    canvas.drawPath(doodleBean.path, doodleBean.paint);
                }
            }
            if (mTempMosaicPath != null && mTempMosaicPaint != null) {
                canvas.drawPath(mTempMosaicPath, mTempMosaicPaint);
            }
            // 进行图层的合并

            canvas.drawBitmap(mMoasicBitmap, 0, 0, mMosaicPaint);
            canvas.restoreToCount(layerCount);
        }
    }

    /**
     * 画所有图形
     */
    private void drawGraphs(Canvas canvas) {
        if (mGraphPath.size() > 0) {
            for (int i = 0; i < mGraphPath.size(); i++) {
                DoodleBean graphBean = mGraphPath.get(i);
                if (graphBean.isPass) {
                    drawGraph(canvas, graphBean);

                    // 给最后一个画个框(直线除外）
                    if (mIsClickOnGraph && i == mGraphPath.size() - 1) {
                        if (graphBean.type == GRAPH_TYPE.LINE) {
                            canvas.drawPath(getLineRectPath(graphBean), mGraphRectPaint);
                            // 再给起点终点画圆
                            canvas.drawCircle(graphBean.startX, graphBean.startY, mDotRadius, mDotPaint);
                            canvas.drawCircle(graphBean.endX, graphBean.endY, mDotRadius, mDotPaint);
                        } else {
                            canvas.drawRect(graphBean.startX, graphBean.startY, graphBean.endX, graphBean.endY, mGraphRectPaint);
                            // 再给起点终点画个圆
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
     * 画某个图形
     *
     * @param canvas    canvas
     * @param graphBean graphBean
     */
    private void drawGraph(Canvas canvas, DoodleBean graphBean) {
        if (graphBean.isPass) {
            if (graphBean.type == GRAPH_TYPE.RECT) {
                // 矩形
                graphBean.paint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(graphBean.startX, graphBean.startY, graphBean.endX, graphBean.endY, graphBean.paint);
            } else if (graphBean.type == GRAPH_TYPE.OVAL) {
                // 椭圆
                graphBean.paint.setStyle(Paint.Style.STROKE);
                canvas.drawOval(new RectF(graphBean.startX, graphBean.startY, graphBean.endX, graphBean.endY), graphBean.paint);
            } else if (graphBean.type == GRAPH_TYPE.ARROW) {
                // 箭头
                graphBean.paint.setStyle(Paint.Style.FILL);
                drawArrow(graphBean.startX, graphBean.startY, graphBean.endX, graphBean.endY, canvas, graphBean.paint);
            } else if (graphBean.type == GRAPH_TYPE.LINE) {
                // 直线
                graphBean.paint.setStyle(Paint.Style.FILL);
                canvas.drawLine(graphBean.startX, graphBean.startY, graphBean.endX, graphBean.endY, graphBean.paint);
            } else if (graphBean.type == GRAPH_TYPE.DIRECT_LINE) {
                // 垂直或水平的直线
                graphBean.paint.setStyle(Paint.Style.FILL);
                canvas.drawLine(graphBean.startX, graphBean.startY, graphBean.endX, graphBean.endY, graphBean.paint);
            }
        }
    }

    /**
     * 设置原始的截图
     *
     * @param originBitmap drawable
     */
    public void setOriginBitmap(@NonNull Bitmap originBitmap) {
        mOriginBitmap = originBitmap;
        initOriginBitmap();
    }

    private void init() {
        setMode(mMode);

        modelsTypes.put(MODEL_TYPE.RECTANGLE, "rectangle");
        modelsTypes.put(MODEL_TYPE.ROUND, "round");
        modelsTypes.put(MODEL_TYPE.ARROW, "arrow");
        modelsTypes.put(MODEL_TYPE.PEN, "pen");
        modelsTypes.put(MODEL_TYPE.TXT, "txt");
        modelsTypes.put(MODEL_TYPE.MOSAIC, "mosaic");
        modelsTypes.put(MODEL_TYPE.REVERT, "revert");
        modelsTypes.put(MODEL_TYPE.RECOVER, "recover");
        modelsTypes.put(MODEL_TYPE.SHARE, "share");
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

            Log.d(TAG, "onSizeChanged:w:" + mViewWidth + "//h:" + mViewHeight);
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
            // 3.5实线，2.5空白
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
            // 绘画图形时，总是红色画笔
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
                Log.d(TAG, "ACTION_DOWN");
                mStartX = mMoveX;
                mStartY = mMoveY;
                mDelaX = 0;
                mDelaY = 0;

                // 正常的画图操作
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
                Log.d(TAG, "ACTION_UP");
                if (mDelaX < mValidRadius && mDelaY < mValidRadius) {
                    // 是点击事件
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
                // 非点击，正常Up
                if (!mIsClickOnGraph) {
                    if (mMode == MODE.DOODLE_MODE) {
                        // 把path加到队列中
                        DoodleBean pathBean = new DoodleBean();
                        pathBean.setDraw(mTempPath, mTempPaint, MODE.DOODLE_MODE);
                        mDoodlePath.add(pathBean);
                    } else if (mMode == MODE.MOSAIC_MODE) {
                        // 把path加到队列中
                        DoodleBean pathBean = new DoodleBean();
                        pathBean.setDraw(mTempMosaicPath, mTempMosaicPaint, MODE.MOSAIC_MODE);
                        mMosaicPath.add(pathBean);
                    } else if (mMode == MODE.GRAPH_MODE) {
                        // 如果是图形，画完之后，可以立即编辑当前图形
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

    /**
     * 按下时，初始化绘图参数
     */
    private void touchDownNormalPath() {
        if (mMode == MODE.GRAPH_MODE) {
            setModePaint(mMode);
            // 创建一个对象
            mTempGraphBean = new DoodleBean();
            mTempGraphBean.setGraph(mStartX, mStartY, mStartX, mStartY, mCurrentGraphType, mTempPaint, MODE.GRAPH_MODE);
        } else if (mMode == MODE.DOODLE_MODE) {
            // 设置对应mode的画笔
            setModePaint(mMode);
            mTempPath = new Path();
            mTempPath.moveTo(mStartX, mStartY);
        } else if (mMode == MODE.MOSAIC_MODE) {
            setModePaint(mMode);
            mTempMosaicPath = new Path();
            mTempMosaicPath.moveTo(mStartX, mStartY);
        }
    }

    /**
     * 按下时，初始化正在操作的图形
     */
    private void touchDownInitGraphOperate() {
        // 此时，在操作某个图形
        mCurrentGraphBean.rectFList.add(new RectF(mCurrentGraphBean.startPoint.x, mCurrentGraphBean.startPoint.y,
                mCurrentGraphBean.endPoint.x, mCurrentGraphBean.endPoint.y));
        mCurrentGraphBean.clickPoint.set(mMoveX, mMoveY);
        // 判断是否点击到了起点或者终点
        RectF startDotRect = new RectF(mCurrentGraphBean.startX - mDotRadius, mCurrentGraphBean.startY - mDotRadius,
                mCurrentGraphBean.startX + mDotRadius, mCurrentGraphBean.startY + mDotRadius);
        RectF endDotRect = new RectF(mCurrentGraphBean.endX - mDotRadius, mCurrentGraphBean.endY - mDotRadius,
                mCurrentGraphBean.endX + mDotRadius, mCurrentGraphBean.endY + mDotRadius);
        if (startDotRect.contains(mMoveX, mMoveY)) {
            Log.d(TAG, "点击到起始点了");
            mGraphMode = MODE.DRAG_START;
        } else if (endDotRect.contains(mMoveX, mMoveY)) {
            Log.d(TAG, "点击到终点了");
            mGraphMode = MODE.DRAG_END;
        } else {
            mGraphMode = MODE.DRAG;
        }
    }

    /**
     * 移动时，绘制路径或者图形
     */
    private void touchMoveNormalDraw() {
        // 使用队列中最后一条path进行操作
        if (mMode == MODE.DOODLE_MODE) {
            mTempPath.lineTo(mMoveX, mMoveY);
        } else if (mMode == MODE.MOSAIC_MODE) {
            mTempMosaicPath.lineTo(mMoveX, mMoveY);
        } else if (mMode == MODE.GRAPH_MODE) {
            // 只操作暂时的图形对象
            if (mTempGraphBean != null) {
                // 只有移动了足够距离，才算合格的图形
                if (mDelaX > mGraphValidRange || mDelaY > mGraphValidRange) {
                    mTempGraphBean.isPass = true;
                    if (mTempGraphBean.type == GRAPH_TYPE.DIRECT_LINE) {
                        // 由于笔直的线的特殊性，需要特殊处理
                        float[] point = getDirectLineEndPoint(mTempGraphBean.startX, mTempGraphBean.startY, mMoveX, mMoveY);
                        mTempGraphBean.endX = point[0];
                        mTempGraphBean.endY = point[1];
                        mTempGraphBean.endPoint.x = point[0];
                        mTempGraphBean.endPoint.y = point[1];
                        // 此时的rectList应该只有一条数据
                        if (mTempGraphBean.rectFList.size() == 1) {
                            // 它对应的rect要么是水平的，要么是垂直的
                            RectF rectF = mTempGraphBean.rectFList.get(0);
                            if (mTempGraphBean.startY == mTempGraphBean.endY) {
                                // 水平的直线
                                rectF.left = mTempGraphBean.startX;
                                rectF.top = mTempGraphBean.startY - mDotRadius;
                                rectF.right = mTempGraphBean.endX;
                                rectF.bottom = mTempGraphBean.startY + mDotRadius;
                            } else {
                                // 垂直的直线
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
                        // 此时的rectList应该只有一条数据
                        if (mTempGraphBean.rectFList.size() == 1) {
                            mTempGraphBean.rectFList.get(0).right = mMoveX;
                            mTempGraphBean.rectFList.get(0).bottom = mMoveY;
                        }
                    }
                }
            }
        }
    }

    /**
     * 移动图形，包括缩放等
     */
    private void touchMoveGraphOperate() {
        if (mCurrentGraphBean != null && mGraphPath.size() > 0) {
            float dx = mMoveX - mCurrentGraphBean.clickPoint.x;
            float dy = mMoveY - mCurrentGraphBean.clickPoint.y;
            // 如果是拖拽模式
            changeGraphRect(dx, dy);
        }
    }

    /**
     * 拖拽缩放图形的操作
     *
     * @param offsetX x偏移量
     * @param offsetY y偏移量
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
                    // 如果是拖动起始点
                    // 只需要变化起始点的坐标即可
                    if (mCurrentGraphBean.type == GRAPH_TYPE.DIRECT_LINE) {
                        // 如果是笔直线，只在该对应方向进行平移
                        if (mCurrentGraphBean.startX == mCurrentGraphBean.endX) {
                            // 垂直的直线,x不变，只变化y
                            mCurrentGraphBean.startY = mCurrentGraphBean.startPoint.y + offsetY;
                        } else {
                            // 水平的直线，y不变
                            mCurrentGraphBean.startX = mCurrentGraphBean.startPoint.x + offsetX;
                        }
                    } else {
                        mCurrentGraphBean.startX = mCurrentGraphBean.startPoint.x + offsetX;
                        mCurrentGraphBean.startY = mCurrentGraphBean.startPoint.y + offsetY;
                    }
                    Log.d(TAG, "拖动起始点");
                } else if (mGraphMode == MODE.DRAG_END) {
                    // 如果是拖动终点
                    // 只需要变化终点的坐标即可
                    if (mCurrentGraphBean.type == GRAPH_TYPE.DIRECT_LINE) {
                        // 如果是笔直线，只在该对应方向进行平移
                        if (mCurrentGraphBean.startX == mCurrentGraphBean.endX) {
                            // 垂直的直线,x不变，只变化y
                            mCurrentGraphBean.endY = mCurrentGraphBean.endPoint.y + offsetY;
                        } else {
                            // 水平的直线，y不变
                            mCurrentGraphBean.endX = mCurrentGraphBean.endPoint.x + offsetX;
                        }
                    } else {
                        mCurrentGraphBean.endX = mCurrentGraphBean.endPoint.x + offsetX;
                        mCurrentGraphBean.endY = mCurrentGraphBean.endPoint.y + offsetY;
                    }
                    Log.d(TAG, "拖动终点");
                }
                // 更新围绕的rect
                if (mCurrentGraphBean.type == GRAPH_TYPE.DIRECT_LINE) {
                    if (mCurrentGraphBean.startX == mCurrentGraphBean.endX) {
                        // 垂直的直线
                        tempRectF.left = mCurrentGraphBean.startX - mDotRadius;
                        tempRectF.top = mCurrentGraphBean.startY;
                        tempRectF.right = mCurrentGraphBean.startX + mDotRadius;
                        tempRectF.bottom = mCurrentGraphBean.endY;
                    } else {
                        // 水平的直线
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
                Log.d(TAG, "拖动图形rect");
            }
        }
    }

    /**
     * 判断是否点击到图形，点击到的话，将点击的图形显示在最上层
     * 改成拖动模式，并设置是否点击图形为true
     */
    private void judgeGraphClick() {
        // 倒过来循环图形列表
        int clickIndex = -1;
        // 点击时，默认没选中
        mIsClickOnGraph = false;
        mGraphMode = MODE.NONE;
        // 重要！：当mCurrentGraphBean不为空时，在Down时，会new 一个记录操作的rect，典型的场景就是
        // 两个图形，第一个选中，此时点击第二个，就会多new 一个rect，因此在mCurrentGraphBean不是null，则需要移除这个多余的操作记录
        if (mCurrentGraphBean != null) {
            if (mCurrentGraphBean.rectFList.size() > 1) {
                mCurrentGraphBean.rectFList.remove(mCurrentGraphBean.rectFList.size() - 1);
            }
        }
        for (int i = mGraphPath.size() - 1; i > -1; i--) {
            DoodleBean graphBean = mGraphPath.get(i);
            if (graphBean != null) {
                RectF rectF = null;
                // 通过rect的contains判断，rect需要左上右下从小到大才能正确判断
                if (graphBean.type == GRAPH_TYPE.DIRECT_LINE) {
                    // 笔直的线的x或者y会有之一相等
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
                // 通过rect来判断
                // 看看点击的点是否在这个框框里(这个框框的判定很奇怪，需要坐标从小到大）
                if (rectF != null && rectF.contains(mMoveX, mMoveY)) {
                    // 点击到图形了，说明接下来会进行图形操作，给mCurrentGraphBean赋新值
                    mCurrentGraphBean = graphBean;
                    Log.d(TAG, "点击到图形啦！" + rectF);
                    mIsClickOnGraph = true;
                    mGraphMode = MODE.DRAG;
                    clickIndex = i;
                    // 直接跳出当前循环
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

        // 把在操作的图形添加到栈底
        if (mIsClickOnGraph && mCurrentGraphBean != null && clickIndex > -1 && clickIndex < mGraphPath.size()) {
            mGraphPath.remove(clickIndex);
            mGraphPath.add(mCurrentGraphBean);
        } else {
            mCurrentGraphBean = null;
            mIsClickOnGraph = false;
            mGraphMode = MODE.NONE;
        }
    }

    /**
     * 获取围绕直线的path
     */
    private Path getLineRectPath(DoodleBean graphBean) {
        if (graphBean == null) {
            return null;
        }
        Path path = null;
        if (graphBean.type == GRAPH_TYPE.LINE) {
            path = new Path();
            // 由于坐标参照系标准不同，需要进行转化
            // 获取与x的夹角
            float radius = getRotation(graphBean.startX, graphBean.startY, graphBean.endX, graphBean.endY);
            if (radius <= 0) {
                radius += 180;
            }
            radius -= 90;
            // 转换成canvas的圆坐标系
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
     * 撤销操作
     *
     * @return 撤销后剩余可以撤销的步骤
     */
    public int revertPath() {
        int size = 0;
        if (mIsRecordAllPath) {
            size = revertAllPath.size();
            if (size > 0) {
                DoodleBean doodleBean = revertAllPath.remove(size - 1);
                if (doodleBean != null) {
                    recoverPath.add(doodleBean);
                    if (doodleBean.mode == MODE.DOODLE_MODE) {
                        size = revertDoodle(size);
                    } else if (doodleBean.mode == MODE.MOSAIC_MODE) {
                        size = revertMosaic(size);
                    } else if (doodleBean.mode == MODE.GRAPH_MODE) {
                        size = revertGraph(size);
                    }
                }
            }
            return size;
        }

        // 撤销只针对当前模式的撤销，不是所有步骤的撤销
        if (!mIsClickOnGraph && mMode == MODE.DOODLE_MODE) {
            size = revertDoodle(size);
        } else if (!mIsClickOnGraph && mMode == MODE.MOSAIC_MODE) {
            size = revertMosaic(size);
        } else if (mIsClickOnGraph && mCurrentGraphBean != null) {
            size = revertGraph(size);
        }
        postInvalidate();
        return size;
    }

    /**
     * 撤销Doodle
     *
     * @param size
     * @return
     */
    private int revertDoodle(int size) {
        size = mDoodlePath.size();
        if (size > 0) {
            DoodleBean drawPathBean = mDoodlePath.remove(size - 1);
            if (drawPathBean != null) {
                recoverPath.add(drawPathBean);
            }
        }
        if (mCallBack != null) {
            mCallBack.onRevertStateChanged(mDoodlePath.size() > 0);
        }
        return size;
    }

    /**
     * 撤销Mosaic
     *
     * @param size
     * @return
     */
    private int revertMosaic(int size) {
        size = mMosaicPath.size();
        if (size > 0) {
            mMosaicPath.remove(size - 1);
        }
        if (mCallBack != null) {
            mCallBack.onRevertStateChanged(mMosaicPath.size() > 0);
        }
        return size;
    }

    /**
     * 撤销Graph
     *
     * @param size
     * @return
     */
    private int revertGraph(int size) {
        // 如果当前选中了某个图形,但是此处的rectList不能删光，需要保留一条最初的数据
        size = mCurrentGraphBean.rectFList.size();
        // 图行相关需要特殊处理，当撤销到最初始状态时，再按撤销，直接删除该图形！
        if (size > 1) {
            mCurrentGraphBean.rectFList.remove(size - 1);
            int currentSize = size - 1;
            // 移除之后，需要给起点和终点重新赋值
            if (currentSize > 0) {
                RectF rectF = mCurrentGraphBean.rectFList.get(currentSize - 1);
                if (mCurrentGraphBean.type == GRAPH_TYPE.DIRECT_LINE) {
                    // 判断水平还是垂直
                    if (mCurrentGraphBean.startX == mCurrentGraphBean.endX) {
                        // 垂直的，
                        mCurrentGraphBean.startY = rectF.top;
                        mCurrentGraphBean.endY = rectF.bottom;
                        mCurrentGraphBean.startPoint.y = rectF.top;
                        mCurrentGraphBean.endPoint.y = rectF.bottom;
                        mCurrentGraphBean.startX = (rectF.left + rectF.right) / 2;
                        mCurrentGraphBean.endX = (rectF.left + rectF.right) / 2;
                        mCurrentGraphBean.startPoint.x = (rectF.left + rectF.right) / 2;
                        mCurrentGraphBean.endPoint.x = (rectF.left + rectF.right) / 2;
                    } else {
                        // 水平的，y相关的都不需要变化
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
            // 只有删除了图形，这个选中图形才真正撤销完成，所以失去焦点，就撤销完了
            if (!mIsClickOnGraph) {
                // 还有一点需要注意的是，可能是在其他涂鸦或者马赛克模式下操作的图形
                mCallBack.onRevertStateChanged(getCurrentPathSize(mMode));
            } else {
                mCallBack.onRevertStateChanged(mIsClickOnGraph);
            }
        }
        size -= 1;
        return size;
    }

    /**
     * 恢复
     */
    public void recover() {
        int size = recoverPath.size();
        if (size > 0) {
            DoodleBean drawPathBean = recoverPath.remove(size - 1);
            if (drawPathBean != null) {
                mDoodlePath.add(drawPathBean);
                postInvalidate();
            }
        }
    }

    /**
     * 获取指定模式下，是否可撤销
     *
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

    /**
     * 删除选中的某个图形
     */
    public void deleteCurrentGraph() {
        if (mIsClickOnGraph && mCurrentGraphBean != null && mGraphPath.size() > 0) {
            mGraphPath.remove(mCurrentGraphBean);
            mCurrentGraphBean = null;
            mIsClickOnGraph = false;
            mGraphMode = MODE.NONE;
        }
        postInvalidate();
    }

    /**
     * 清楚正在操作的图形的焦点
     */
    public void clearGraphFocus() {
        mCurrentGraphBean = null;
        mIsClickOnGraph = false;
        mGraphMode = MODE.NONE;
        postInvalidate();
    }

    /**
     * 获取马赛克的bitmap
     */
    private Bitmap makeMosaicBitmap() {
        if (mMoasicBitmap != null) {
            return mMoasicBitmap;
        }

        int w = Math.round(mViewWidth / 16f);
        int h = Math.round(mViewHeight / 16f);

        if (mOriginBitmap != null) {
            // 先创建小图
            mMoasicBitmap = Bitmap.createScaledBitmap(mOriginBitmap, w, h, false);
            // 再把小图放大
            mMoasicBitmap = Bitmap.createScaledBitmap(mMoasicBitmap, mViewWidth, mViewHeight, false);
        }
        return mMoasicBitmap;
    }

    /**
     * 计算两点对应的角度
     *
     * @return float
     */
    public float getRotation(float startX, float startY, float endX, float endY) {
        float deltaX = startX - endX;
        float deltaY = startY - endY;
        // 计算坐标点相对于圆点所对应的弧度
        double radians = Math.atan2(deltaY, deltaX);
        // 把弧度转换成角度
        return (float) Math.toDegrees(radians);
    }

    /**
     * 设置所要画图形的种类
     *
     * @param graphType graphType
     */
    public void setGraphType(GRAPH_TYPE graphType) {
        // 设置模式前，先把焦点给clear一下
        clearGraphFocus();
        this.mCurrentGraphType = graphType;
        if (mCallBack != null) {
            mCallBack.onRevertStateChanged(getCurrentPathSize(mMode));
        }
    }

    public void setPaintSize(int size) {
        this.mPaintSize = size;
    }

    /**
     * add 文字
     */
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
     * 画笔大小
     *
     * @param paintWidth
     */
    public void setPaintWidth(int paintWidth) {
        this.mPaintWidth = paintWidth;
    }

    /**
     * 画箭头
     */
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

    /**
     * 画笔直笔直的直线
     */
    private float[] getDirectLineEndPoint(float sx, float sy, float ex, float ey) {
        float degrees = getRotation(sx, sy, ex, ey);
        float[] point = new float[2];
        // 根据角度画直线
        if ((-45 <= degrees && degrees <= 45) || degrees >= 135 || degrees <= -135) {
            // 往x轴的方向绘制，即y值不变
            point[0] = ex;
            point[1] = sy;
        } else {
            // 往y轴的方向绘制，即x值不变
            point[0] = sx;
            point[1] = ey;
        }
        return point;
    }

    /**
     * 设置画笔的颜色
     *
     * @param color 颜色
     */
    public void setPaintColor(int color) {
        this.mPaintColor = color;
        if (mTempPaint != null) {
            mTempPaint.setColor(mPaintColor);
        }
    }

    /**
     * 设置是否可编辑
     *
     * @param editable 能否编辑
     */
    public void setEditable(boolean editable) {
        this.mIsEditable = editable;
    }

    public void setRecordAllPath(boolean recordAllPath) {
        this.mIsRecordAllPath = recordAllPath;
    }

    public void setMode(MODE mode) {
        // 设置模式前，先把焦点给clear一下
        clearGraphFocus();
        this.mMode = mode;
        if (mCallBack != null) {
            mCallBack.onRevertStateChanged(getCurrentPathSize(mMode));
        }
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
        // 这四个点是实时变化的，用来绘制图形的四个点
        public float startX, startY, endX, endY;
        public GRAPH_TYPE type;
        public PointF clickPoint = new PointF();
        // 两个点的变量，用于平移缩放的操作，只有在UP后，才会同步四个点的值
        PointF startPoint = new PointF();
        PointF endPoint = new PointF();
        // 是否是符合要求的图形
        boolean isPass = false;
        // 用于撤销移动缩放的操作
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
         * draw:记录画笔和画图的路径，主要用来撤销画图的操作
         *
         * @param path
         * @param paint
         * @param mode
         */
        public void setDraw(Path path, Paint paint, MODE mode) {
            setBase(paint, mode);
            this.path = path;
            if (mIsRecordAllPath) {
                revertAllPath.add(this);
            }
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
            if (mIsRecordAllPath) {
                revertAllPath.add(this);
            }
        }

        /**
         * graph:记录图形绘制的实例类
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
            if (mIsRecordAllPath) {
                revertAllPath.add(this);
            }
        }
    }

    /**
     * 组件结束时自动回收
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
     * 自动绑定组件生命周期
     *
     * @param context
     */
    private void autoBindLifecycle(Context context) {
        if (context == null) {
            return;
        }
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            ((AppCompatActivity) activity).getLifecycle().addObserver(this);
            return;
        }
        if (context instanceof LifecycleOwner) {
            ((LifecycleOwner) context).getLifecycle().addObserver(this);
            return;
        }
    }

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getContext().getApplicationContext().getResources().getDisplayMetrics();
        return (int) (metrics.density * dp + 0.5f);
    }

    /**
     * 依圆心坐标，半径，扇形角度，计算出扇形终射线与圆弧交叉点的xy坐标
     * 0为水平 270是正上方
     *
     * @param radius   半径
     * @param cirAngle 角度
     * @return x，y
     */
    private float[] getCoordinatePoint(float centerX, float centerY, float radius, float cirAngle) {
        float[] point = new float[2];
        //将角度转换为弧度
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


    private PopupWindow mPopupWindow;

    private HashMap<String, ImageView> images = new HashMap<>();

    private ImageView imgSmall, imgMiddle, imgBig;
    private ImageView redColor, blueColor, greenColor, yellowColor, pinkColor, whiteColor;

    private MODEL_TYPE modelType = MODEL_TYPE.RECTANGLE;
    private MODEL_SIZE modelSize = MODEL_SIZE.SMALL;

    private HashMap<String, String> defaultTxtSizeTag = new HashMap<>();
    private HashMap<String, Integer> defaultSelectedTxtSize = new HashMap<>();

    private HashMap<String, String> defaultRoundSizeTag = new HashMap<>();
    private HashMap<String, Integer> defaultRoundResourceColor = new HashMap<>();
    private HashMap<Integer, Integer> defaultSelectedRoundSize = new HashMap<>();

    private HashMap<String, String> defaultImageTag = new HashMap<>();
    private HashMap<String, Integer> defaultResourceColor = new HashMap<>();
    private HashMap<Integer, Integer> defaultSelectedColor = new HashMap<>();

    public enum MODEL_TYPE {
        RECTANGLE, ROUND, ARROW, PEN, TXT, MOSAIC, REVERT, RECOVER, SHARE
    }

    private HashMap<MODEL_TYPE, String> modelsTypes = new HashMap<>();

    enum MODEL_SIZE {
        SMALL, MIDDLE, BIG
    }

    public void popuWindow(View view, int layout, MODEL_TYPE type, int x) {
        View menus = LayoutInflater.from(getContext()).inflate(layout, null);
        mPopupWindow = new PopupWindow(menus, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setFocusable(true);
        mPopupWindow.showAsDropDown(view, x, 0);
        this.modelType = type;
        setEditable(true);
        images.clear();
        if (type == MODEL_TYPE.RECTANGLE || type == MODEL_TYPE.ROUND || type == MODEL_TYPE.ARROW) {
            initColor(menus);
            setMode(MODE.GRAPH_MODE);
        } else if (type == MODEL_TYPE.PEN || type == MODEL_TYPE.TXT) {
            initTxt(menus);
            initColor(menus);
            setMode(MODE.DOODLE_MODE);
        } else if (type == MODEL_TYPE.MOSAIC) {
            initTxt(menus);
            setMode(MODE.MOSAIC_MODE);
        }
        defaultColor();
        defaultTxtSize();
        defaultRoundSize();
        Log.e(TAG,"popuWindow show");
    }

    private void defaultTxtSize() {
        String tag = defaultTxtSizeTag.get(modelsTypes.get(modelType));
        if (!TextUtils.isEmpty(tag)) {
            int size = defaultSelectedTxtSize.get(tag);
            ImageView imageView = images.get(tag);
            setSelectedText(imageView, size, false);
        }
    }

    private void defaultRoundSize() {
        String tag = defaultRoundSizeTag.get(modelsTypes.get(modelType));
        if (!TextUtils.isEmpty(tag)) {
            int resource = defaultRoundResourceColor.get(tag);
            int size = defaultSelectedRoundSize.get(resource);
            ImageView imageView = images.get(tag);
            setSelectedRound(imageView, resource, size, false);
        }
    }

    private void defaultColor() {
        String tag = defaultImageTag.get(modelsTypes.get(modelType));
        if (!TextUtils.isEmpty(tag)) {
            int resource = defaultResourceColor.get(tag);
            int color = defaultSelectedColor.get(resource);
            ImageView imageView = images.get(tag);
            setSelectedColor(imageView, resource, color, false);
        }
    }

    private void initTxt(View view) {
        imgSmall = view.findViewById(R.id.imgSmall);
        imgMiddle = view.findViewById(R.id.imgMiddle);
        imgBig = view.findViewById(R.id.imgBig);
        setCacheTxtView();
        imgSmall.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (modelType == MODEL_TYPE.TXT) {
                    setSelectedText(imgSmall, 20, true);
                } else {
                    setSelectedRound(imgSmall, R.drawable.cycle_frame_small, 3, true);
                }
            }
        });
        imgMiddle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (modelType == MODEL_TYPE.TXT) {
                    setSelectedText(imgSmall, 60, true);
                } else {
                    setSelectedRound(imgMiddle, R.drawable.cycle_frame_middle, 8, true);
                }
            }
        });
        imgBig.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (modelType == MODEL_TYPE.TXT) {
                    setSelectedText(imgSmall, 100, true);
                } else {
                    setSelectedRound(imgBig, R.drawable.cycle_frame_big, 13, true);
                }
            }
        });
    }

    private void setCacheTxtView() {
        images.put(imgSmall.getTag().toString(), imgSmall);
        images.put(imgMiddle.getTag().toString(), imgMiddle);
        images.put(imgBig.getTag().toString(), imgBig);
    }

    private void initColor(View view) {
        redColor = view.findViewById(R.id.redColor);
        blueColor = view.findViewById(R.id.blueColor);
        greenColor = view.findViewById(R.id.greenColor);
        yellowColor = view.findViewById(R.id.yellowColor);
        pinkColor = view.findViewById(R.id.pinkColor);
        whiteColor = view.findViewById(R.id.whiteColor);
        setCacheColorView();
        redColor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedColor(redColor, R.drawable.rectangle_frame_red, R.color.red, true);
            }
        });
        blueColor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedColor(blueColor, R.drawable.rectangle_frame_blue, R.color.blue, true);
            }
        });
        greenColor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedColor(greenColor, R.drawable.rectangle_frame_green, R.color.green, true);
            }
        });
        yellowColor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedColor(yellowColor, R.drawable.rectangle_frame_yellow, R.color.yellow, true);
            }
        });
        pinkColor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedColor(pinkColor, R.drawable.rectangle_frame_pink, R.color.pink, true);
            }
        });
        whiteColor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedColor(whiteColor, R.drawable.rectangle_frame_white, R.color.white, true);
            }
        });
    }

    private void setCacheColorView() {
        images.put(redColor.getTag().toString(), redColor);
        images.put(blueColor.getTag().toString(), blueColor);
        images.put(greenColor.getTag().toString(), greenColor);
        images.put(yellowColor.getTag().toString(), yellowColor);
        images.put(pinkColor.getTag().toString(), pinkColor);
        images.put(whiteColor.getTag().toString(), whiteColor);
    }

    private void setSelectedText(ImageView imageView, int size, boolean isCancel) {
        imgSmall.setImageResource(R.mipmap.text_small);
        imgMiddle.setImageResource(R.mipmap.text_middle);
        imgBig.setImageResource(R.mipmap.text_big);
        imgSmall.setBackgroundResource(R.drawable.rectangle_frame_text);
        imgMiddle.setBackgroundResource(R.drawable.rectangle_frame_text);
        imgBig.setBackgroundResource(R.drawable.rectangle_frame_text);
        imageView.setBackgroundResource(R.drawable.white_round);
        setPaintSize(size);
        defaultTxtSizeTag.put(modelsTypes.get(modelType), imageView.getTag().toString());
        defaultSelectedTxtSize.put(imageView.getTag().toString(), size);
        if (isCancel) {
            cancel();
        }
    }

    private void setSelectedRound(ImageView imageView, int drawableColor, int size, boolean isCancel) {
        if (modelType == MODEL_TYPE.MOSAIC) {
            size *= 2;
        }
        imgSmall.setBackgroundResource(R.drawable.translation);
        imgMiddle.setBackgroundResource(R.drawable.translation);
        imgBig.setBackgroundResource(R.drawable.translation);
        imgSmall.setImageResource(R.drawable.cycle_small);
        imgMiddle.setImageResource(R.drawable.cycle_middle);
        imgBig.setImageResource(R.drawable.cycle_big);
        imageView.setImageResource(drawableColor);
        setPaintWidth(size);
        defaultRoundSizeTag.put(modelsTypes.get(modelType), imageView.getTag().toString());
        defaultRoundResourceColor.put(imageView.getTag().toString(), drawableColor);
        defaultSelectedRoundSize.put(drawableColor, size);
        if (isCancel) {
            cancel();
        }
    }

    private void setSelectedColor(ImageView imageView, int drawableColor, int color, boolean isCancel) {
        redColor.setImageResource(R.drawable.rectangle_red);
        blueColor.setImageResource(R.drawable.rectangle_blue);
        greenColor.setImageResource(R.drawable.rectangle_green);
        yellowColor.setImageResource(R.drawable.rectangle_yellow);
        pinkColor.setImageResource(R.drawable.rectangle_pink);
        whiteColor.setImageResource(R.drawable.rectangle_white);
        imageView.setImageResource(drawableColor);
        setPaintColor(getContext().getResources().getColor(color));
        defaultImageTag.put(modelsTypes.get(modelType), imageView.getTag().toString());
        defaultResourceColor.put(imageView.getTag().toString(), drawableColor);
        defaultSelectedColor.put(drawableColor, color);
        String defaultSelectedColorStr = defaultSelectedColor.toString();
        if (isCancel) {
            cancel();
        }
    }

    public void cancel() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }
}