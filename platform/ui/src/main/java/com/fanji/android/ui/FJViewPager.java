package com.fanji.android.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.fanji.android.ui.adapter.FJPagerAdapter;
import com.fanji.android.ui.transforms.AccordionTransformer;
import com.fanji.android.ui.transforms.BackgroundToForegroundTransformer;
import com.fanji.android.ui.transforms.CubeInTransformer;
import com.fanji.android.ui.transforms.CubeOutTransformer;
import com.fanji.android.ui.transforms.DepthPageTransformer;
import com.fanji.android.ui.transforms.DrawerTransformer;
import com.fanji.android.ui.transforms.FlipHorizontalTransformer;
import com.fanji.android.ui.transforms.FlipVerticalTransformer;
import com.fanji.android.ui.transforms.ForegroundToBackgroundTransformer;
import com.fanji.android.ui.transforms.RotateDownTransformer;
import com.fanji.android.ui.transforms.RotateUpTransformer;
import com.fanji.android.ui.transforms.ScaleInOutTransformer;
import com.fanji.android.ui.transforms.StackTransformer;
import com.fanji.android.ui.transforms.TabletTransformer;
import com.fanji.android.ui.transforms.Transformer;
import com.fanji.android.ui.transforms.VerticalTransformer;
import com.fanji.android.ui.transforms.ZoomInTransformer;
import com.fanji.android.ui.transforms.ZoomOutSlideTransformer;
import com.fanji.android.ui.transforms.ZoomOutTransformer;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * created by jiangshide on 2019-08-05.
 * email:18311271399@163.com
 */
public class FJViewPager extends ViewPager implements ViewPager.PageTransformer {

    public FJPagerAdapter mCusPagerAdapter;
    private FJFragmentPagerAdapter mZdFragmentPagerAdapter;
    private boolean mIsCanScroll = true;
    private boolean mIsVertical = false;
    private boolean mIsAnim = false;
    @SuppressLint("WrongConstant")
    private Class[] mTransformClass = {AccordionTransformer.class, BackgroundToForegroundTransformer.class, CubeInTransformer.class, CubeOutTransformer.class, DepthPageTransformer.class,
            DrawerTransformer.class, FlipHorizontalTransformer.class, FlipVerticalTransformer.class, ForegroundToBackgroundTransformer.class, RotateDownTransformer.class, RotateUpTransformer.class,
            ScaleInOutTransformer.class, StackTransformer.class, TabletTransformer.class, VerticalTransformer.class, ZoomInTransformer.class, ZoomOutSlideTransformer.class, ZoomOutTransformer.class};

    public FJViewPager(@NonNull Context context) {
        super(context);
    }

    public FJViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScanScroll(boolean isCanScroll) {
        this.mIsCanScroll = isCanScroll;
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item, mIsAnim);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @SuppressLint("WrongConstant")
    public void setMode(boolean isVertical) {
        this.setMode(isVertical, -1);
    }

    public void setMode(boolean isVertical, @Transformer int mode) {
        this.setMode(isVertical, true, mode);
    }

    public void setMode(boolean isVertical, boolean reverseDrawingOrder, @Transformer int mode) {
        this.mIsVertical = isVertical;
        try {
            setPageTransformer(reverseDrawingOrder, mode < 0 ? this : (PageTransformer) mTransformClass[mode].newInstance());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        this.invalidate();
    }

    private MotionEvent swapTouchEvent(MotionEvent event) {
        float width = getWidth();
        float height = getHeight();
        event.setLocation((event.getY() / height) * width, (event.getX() / width) * height);
        return event;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mIsCanScroll && super.onInterceptTouchEvent(mIsVertical ? swapTouchEvent(MotionEvent.obtain(event)) : event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mIsCanScroll && super.onTouchEvent(mIsVertical ? swapTouchEvent(MotionEvent.obtain(event)) : event);
    }

    public void setResImg(int... resImgs) {
        List<View> views = new ArrayList<>();
        for (int resImg : resImgs) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(resImg);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
            views.add(imageView);
        }
        setViews(views);
    }

    public FJViewPager setAnim(boolean isAnim) {
        this.mIsAnim = isAnim;
        return this;
    }

    public FJViewPager setViews(List<View> views) {
        this.setAdapter(mCusPagerAdapter = new FJPagerAdapter(getContext()).setViews(views));
        return this;
    }

    public FJViewPager setViews(List<View> views, Boolean isLimitWidth) {
        this.setAdapter(mCusPagerAdapter = new FJPagerAdapter(getContext()).setViews(views, isLimitWidth));
        return this;
    }

    public FJViewPager setFragments(List<Fragment> fragments) {
        if (mZdFragmentPagerAdapter != null) {
            mZdFragmentPagerAdapter.setFragment(fragments);
        }
        return this;
    }

    public FJViewPager setTitles(List<String> titles) {
        if (mZdFragmentPagerAdapter != null) {
            mZdFragmentPagerAdapter.setTitles(titles);
        }
        return this;
    }

    public FJViewPager setPageListener(OnPageChangeListener listener) {
        if (mZdFragmentPagerAdapter != null) {
            mZdFragmentPagerAdapter.setListener(listener);
        }
        return this;
    }

    public FJViewPager initTabs(Context context, FJTabLayout cusTabLayout,
                                final FJViewPager viewPager) {
        if (mZdFragmentPagerAdapter != null) {
            mZdFragmentPagerAdapter.initTabs(context, cusTabLayout, viewPager);
        }
        return this;
    }

    public FJViewPager updateTabs() {
        if (mZdFragmentPagerAdapter != null) {
            mZdFragmentPagerAdapter.updateTabs();
        }
        return this;
    }


    public FJViewPager setListener(FJPagerAdapter.OnPagerItemClickListener listener) {
        if (mCusPagerAdapter != null) {
            mCusPagerAdapter.setListener(listener);
        }
        return this;
    }

    public FJFragmentPagerAdapter create(FragmentManager fragmentManager) {
        return mZdFragmentPagerAdapter = new FJFragmentPagerAdapter(fragmentManager,
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public void transformPage(@NonNull View view, float v) {
        float alpha = 0;
        if (0 <= v && v <= 1) {
            alpha = 1 - v;
        } else if (-1 < v && v < 0) {
            alpha = v + 1;
        }
        view.setAlpha(alpha);
        view.setTranslationX(view.getWidth() * -v);
        float yPosition = v * view.getHeight();
        view.setTranslationY(yPosition);
    }

    public void onRefresh() {
        if (null != mZdFragmentPagerAdapter) {
            mZdFragmentPagerAdapter.onRefresh();
        }
    }
}
