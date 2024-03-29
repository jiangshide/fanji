package com.fanji.android.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.fanji.android.ui.navigation.MaterialMode;
import com.fanji.android.ui.navigation.NavigationController;
import com.fanji.android.ui.navigation.internal.CustomItemLayout;
import com.fanji.android.ui.navigation.internal.CustomItemVerticalLayout;
import com.fanji.android.ui.navigation.internal.MaterialItemLayout;
import com.fanji.android.ui.navigation.internal.MaterialItemVerticalLayout;
import com.fanji.android.ui.navigation.internal.Utils;
import com.fanji.android.ui.navigation.item.BaseTabItem;
import com.fanji.android.ui.navigation.item.MaterialItemView;
import com.fanji.android.ui.navigation.item.OnlyIconMaterialItemView;
import com.fanji.android.ui.navigation.item.SpecialTab;
import com.fanji.android.ui.navigation.item.SpecialTabRound;
import com.fanji.android.ui.navigation.listener.BottomLayoutController;
import com.fanji.android.ui.navigation.listener.ItemController;
import com.fanji.android.ui.navigation.listener.OnTabItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * created by jiangshide on 4/10/21.
 * email:18311271399@163.com
 */
public class FJNavigationView extends ViewGroup {
    private int mTabPaddingTop;
    private int mTabPaddingBottom;

    private NavigationController mNavigationController;

    private ViewPagerPageChangeListener mPageChangeListener;
    private ViewPager mViewPager;
    private OnTabItemSelectedListener mOnTabItemSelectedListener;

    private boolean mEnableVerticalLayout;

    private OnTabItemSelectedListener mTabItemListener = new OnTabItemSelectedListener() {
        @Override
        public void onSelected(int index, int old) {
            if (index == 2) {
                onEmpty(index, old);
                return;
            }
            if (mOnTabItemSelectedListener != null) {
                mOnTabItemSelectedListener.onSelected(index, old);
            }
            if (mViewPager != null) {
                mViewPager.setCurrentItem(index, false);
            }
        }

        @Override
        public void onRepeat(int index) {
            if (mOnTabItemSelectedListener != null) {
                mOnTabItemSelectedListener.onRepeat(index);
            }
        }

        @Override
        public void onEmpty(int index, int old) {
            if (mOnTabItemSelectedListener != null) {
                mOnTabItemSelectedListener.onEmpty(index, old);
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(old);
                }
            }
        }
    };

    public FJNavigationView(Context context) {
        this(context, null);
    }

    public FJNavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FJNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setPadding(0, 0, 0, 0);

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.PageNavigationView);
        if (a.hasValue(R.styleable.PageNavigationView_NavigationPaddingTop)) {
            mTabPaddingTop = a.getDimensionPixelSize(R.styleable.PageNavigationView_NavigationPaddingTop, 0);
        }
        if (a.hasValue(R.styleable.PageNavigationView_NavigationPaddingBottom)) {
            mTabPaddingBottom = a.getDimensionPixelSize(R.styleable.PageNavigationView_NavigationPaddingBottom, 0);
        }
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int count = getChildCount();

        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
            maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
        }

        setMeasuredDimension(maxWidth, maxHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        final int width = r - l;
        final int height = b - t;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            child.layout(0, 0, width, height);
        }
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return FJNavigationView.class.getName();
    }

    /**
     * 构建 Material Desgin 风格的导航栏
     */
    public MaterialBuilder material() {
        return new MaterialBuilder();
    }

    /**
     * 构建自定义导航栏
     */
    public CustomBuilder custom() {
        return new CustomBuilder();
    }

    public BaseTabItem newItem(int drawable, int checkedDrawable, String title) {
        return newItem(drawable, checkedDrawable, title, R.color.neutralLight, R.color.theme);
    }

    public BaseTabItem newGradientItem(int drawable, int checkedDrawable, String title) {
        return newItem(drawable, checkedDrawable, title, R.color.neutralLight, R.color.themeBlueStartColor, R.color.themeBlueEndColor);
    }

    public BaseTabItem newItem(int drawable, int checkedDrawable, String title, int selectColor, int selectedColor) {
        return newItem(drawable, checkedDrawable, title, selectColor, selectedColor, 0);
    }

    /**
     * 正常tab
     */
    public BaseTabItem newItem(int drawable, int checkedDrawable, String title, int selectColor, int selectedStartColor, int selectedEndColor) {
        SpecialTab mainTab = new SpecialTab(getContext());
        mainTab.initialize(drawable, checkedDrawable, title);
        mainTab.setTextDefaultColor(selectColor);
        mainTab.setTextCheckedColor(selectedStartColor);
        mainTab.setTextEndColor(selectedEndColor);
        return mainTab;
    }

    public BaseTabItem newRoundItem(int drawable, int checkedDrawable, String title) {
        return newRoundItem(drawable, checkedDrawable, title, R.color.neutralLight, R.color.theme);
    }

    /**
     * 圆形tab
     */
    public BaseTabItem newRoundItem(int drawable, int checkedDrawable, String title, int selectColor, int selectedColor) {
        SpecialTabRound mainTab = new SpecialTabRound(getContext());
        mainTab.initialize(drawable, checkedDrawable, title);
        mainTab.setTextDefaultColor(selectColor);
        mainTab.setTextCheckedColor(selectedColor);
        return mainTab;
    }

    /**
     * 构建 自定义 的导航栏
     */
    public class CustomBuilder {

        private List<BaseTabItem> items;

        private boolean enableVerticalLayout = false;
        private boolean animateLayoutChanges = false;

        CustomBuilder() {
            items = new ArrayList<>();
        }

        public NavigationController build() {
            return build(null, true);
        }

        /**
         * 完成构建
         *
         * @return {@link NavigationController},通过它进行后续操作
         * @throws RuntimeException 没有添加导航项时会抛出
         */
        public NavigationController build(OnTabItemSelectedListener listener, boolean isNoMiddle) {
            mOnTabItemSelectedListener = listener;
            mEnableVerticalLayout = enableVerticalLayout;

            //未添加任何按钮
            if (items.size() == 0) {
                throw new RuntimeException("must add a navigation item");
            }

            ItemController itemController;

            if (enableVerticalLayout) {//垂直布局
                CustomItemVerticalLayout verticalItemLayout = new CustomItemVerticalLayout(getContext());
                verticalItemLayout.initialize(items, animateLayoutChanges);
                verticalItemLayout.setPadding(0, mTabPaddingTop, 0, mTabPaddingBottom);

                FJNavigationView.this.removeAllViews();
                FJNavigationView.this.addView(verticalItemLayout);
                itemController = verticalItemLayout;
            } else {//水平布局
                CustomItemLayout customItemLayout = new CustomItemLayout(getContext());
                customItemLayout.initialize(items, animateLayoutChanges, isNoMiddle);
                customItemLayout.setPadding(0, mTabPaddingTop, 0, mTabPaddingBottom);

                FJNavigationView.this.removeAllViews();
                FJNavigationView.this.addView(customItemLayout);
                itemController = customItemLayout;
            }

            mNavigationController = new NavigationController(new Controller(), itemController);
            mNavigationController.addTabItemSelectedListener(mTabItemListener);

            return mNavigationController;
        }

        /**
         * 添加一个导航按钮
         *
         * @param baseTabItem {@link BaseTabItem},所有自定义Item都必须继承它
         * @return {@link CustomBuilder}
         */
        public CustomBuilder addItem(BaseTabItem baseTabItem) {
            items.add(baseTabItem);
            return CustomBuilder.this;
        }

        /**
         * 使用垂直布局
         *
         * @return {@link CustomBuilder}
         */
        public CustomBuilder enableVerticalLayout() {
            enableVerticalLayout = true;
            return CustomBuilder.this;
        }

        /**
         * 通过{@link NavigationController}动态移除/添加导航项时,显示默认的布局动画
         *
         * @return {@link CustomBuilder}
         */
        public CustomBuilder enableAnimateLayoutChanges() {
            animateLayoutChanges = true;
            return CustomBuilder.this;
        }
    }

    /**
     * 构建 Material Desgin 风格的导航栏
     */
    public class MaterialBuilder {

        private final int DEFAULT_COLOR = 0x56000000;

        private List<MaterialItemViewData> itemDatas;
        private int defaultColor;
        private int mode;
        private int messageBackgroundColor;
        private int messageNumberColor;
        private boolean enableVerticalLayout = false;
        private boolean doTintIcon = true;
        private boolean animateLayoutChanges = false;

        MaterialBuilder() {
            itemDatas = new ArrayList<>();
        }

        /**
         * 完成构建
         *
         * @return {@link NavigationController},通过它进行后续操作
         * @throws RuntimeException 没有添加导航项时会抛出
         */
        @NonNull
        public NavigationController build() {
            mEnableVerticalLayout = enableVerticalLayout;

            // 未添加任何按钮
            if (itemDatas.isEmpty()) {
                throw new RuntimeException("must add a navigation item");
            }

            // 设置默认颜色
            if (defaultColor == 0) {
                defaultColor = DEFAULT_COLOR;
            }

            ItemController itemController;

            if (enableVerticalLayout) {//垂直布局

                List<BaseTabItem> items = new ArrayList<>();

                for (MaterialItemViewData data : itemDatas) {

                    OnlyIconMaterialItemView materialItemView = new OnlyIconMaterialItemView(getContext());
                    materialItemView.initialization(data.title, data.drawable, data.checkedDrawable, doTintIcon, defaultColor, data.chekedColor);

                    //检查是否设置了消息圆点的颜色
                    if (messageBackgroundColor != 0) {
                        materialItemView.setMessageBackgroundColor(messageBackgroundColor);
                    }

                    //检查是否设置了消息数字的颜色
                    if (messageNumberColor != 0) {
                        materialItemView.setMessageNumberColor(messageNumberColor);
                    }

                    items.add(materialItemView);
                }

                MaterialItemVerticalLayout materialItemVerticalLayout = new MaterialItemVerticalLayout(getContext());
                materialItemVerticalLayout.initialize(items, animateLayoutChanges, doTintIcon, defaultColor);
                materialItemVerticalLayout.setPadding(0, mTabPaddingTop, 0, mTabPaddingBottom);

                FJNavigationView.this.removeAllViews();
                FJNavigationView.this.addView(materialItemVerticalLayout);

                itemController = materialItemVerticalLayout;

            } else {//水平布局

                boolean changeBackground = (mode & MaterialMode.CHANGE_BACKGROUND_COLOR) > 0;

                List<MaterialItemView> items = new ArrayList<>();
                List<Integer> checkedColors = new ArrayList<>();

                for (MaterialItemViewData data : itemDatas) {
                    // 记录设置的选中颜色
                    checkedColors.add(data.chekedColor);

                    MaterialItemView materialItemView = new MaterialItemView(getContext());
                    // 初始化Item,需要切换背景颜色就将选中颜色改成白色
                    materialItemView.initialization(data.title, data.drawable, data.checkedDrawable, doTintIcon, defaultColor, changeBackground ? Color.WHITE : data.chekedColor);

                    //检查是否设置了消息圆点的颜色
                    if (messageBackgroundColor != 0) {
                        materialItemView.setMessageBackgroundColor(messageBackgroundColor);
                    }

                    //检查是否设置了消息数字的颜色
                    if (messageNumberColor != 0) {
                        materialItemView.setMessageNumberColor(messageNumberColor);
                    }

                    items.add(materialItemView);
                }

                MaterialItemLayout materialItemLayout = new MaterialItemLayout(getContext());
                materialItemLayout.initialize(items, checkedColors, mode, animateLayoutChanges, doTintIcon, defaultColor);
                materialItemLayout.setPadding(0, mTabPaddingTop, 0, mTabPaddingBottom);

                FJNavigationView.this.removeAllViews();
                FJNavigationView.this.addView(materialItemLayout);

                itemController = materialItemLayout;
            }

            mNavigationController = new NavigationController(new Controller(), itemController);
            mNavigationController.addTabItemSelectedListener(mTabItemListener);

            return mNavigationController;
        }

        /**
         * 添加一个导航按钮
         *
         * @param drawableRes 图标资源
         * @param title       显示文字内容.尽量简短
         * @return {@link MaterialBuilder}
         */
        public MaterialBuilder addItem(@DrawableRes int drawableRes, @NonNull String title) {
            addItem(drawableRes, drawableRes, title, Utils.getColorPrimary(getContext()));
            return MaterialBuilder.this;
        }

        /**
         * 添加一个导航按钮
         *
         * @param drawableRes        图标资源
         * @param checkedDrawableRes 选中时的图标资源
         * @param title              显示文字内容.尽量简短
         * @return {@link MaterialBuilder}
         */
        public MaterialBuilder addItem(@DrawableRes int drawableRes, @DrawableRes int checkedDrawableRes, @NonNull String title) {
            addItem(drawableRes, checkedDrawableRes, title, Utils.getColorPrimary(getContext()));
            return MaterialBuilder.this;
        }

        /**
         * 添加一个导航按钮
         *
         * @param drawableRes 图标资源
         * @param title       显示文字内容.尽量简短
         * @param chekedColor 选中的颜色
         * @return {@link MaterialBuilder}
         */
        public MaterialBuilder addItem(@DrawableRes int drawableRes, @NonNull String title, @ColorInt int chekedColor) {
            addItem(drawableRes, drawableRes, title, chekedColor);
            return MaterialBuilder.this;
        }

        /**
         * 添加一个导航按钮
         *
         * @param drawableRes        图标资源
         * @param checkedDrawableRes 选中时的图标资源
         * @param title              显示文字内容.尽量简短
         * @param chekedColor        选中的颜色
         * @return {@link MaterialBuilder}
         * @throws Resources.NotFoundException drawable 资源获取异常
         */
        public MaterialBuilder addItem(@DrawableRes int drawableRes, @DrawableRes int checkedDrawableRes, @NonNull String title, @ColorInt int chekedColor) {
            Drawable defaultDrawable = ContextCompat.getDrawable(getContext(), drawableRes);
            Drawable checkDrawable = ContextCompat.getDrawable(getContext(), checkedDrawableRes);
            if (defaultDrawable == null) {
                throw new Resources.NotFoundException("Resource ID " + Integer.toHexString(drawableRes));
            }
            if (checkDrawable == null) {
                throw new Resources.NotFoundException("Resource ID " + Integer.toHexString(checkedDrawableRes));
            }
            addItem(defaultDrawable, checkDrawable, title, chekedColor);
            return MaterialBuilder.this;
        }

        /**
         * 添加一个导航按钮
         *
         * @param drawable 图标资源
         * @param title    显示文字内容.尽量简短
         * @return {@link MaterialBuilder}
         */
        public MaterialBuilder addItem(@NonNull Drawable drawable, @NonNull String title) {
            addItem(drawable, drawable, title, Utils.getColorPrimary(getContext()));
            return MaterialBuilder.this;
        }

        /**
         * 添加一个导航按钮
         *
         * @param drawable        图标资源
         * @param checkedDrawable 选中时的图标资源
         * @param title           显示文字内容.尽量简短
         * @return {@link MaterialBuilder}
         */
        public MaterialBuilder addItem(@NonNull Drawable drawable, @NonNull Drawable checkedDrawable, @NonNull String title) {
            addItem(drawable, checkedDrawable, title, Utils.getColorPrimary(getContext()));
            return MaterialBuilder.this;
        }

        /**
         * 添加一个导航按钮
         *
         * @param drawable    图标资源
         * @param title       显示文字内容.尽量简短
         * @param chekedColor 选中的颜色
         * @return {@link MaterialBuilder}
         */
        public MaterialBuilder addItem(@NonNull Drawable drawable, @NonNull String title, @ColorInt int chekedColor) {
            addItem(drawable, drawable, title, chekedColor);
            return MaterialBuilder.this;
        }

        /**
         * 添加一个导航按钮
         *
         * @param drawable        图标资源
         * @param checkedDrawable 选中时的图标资源
         * @param title           显示文字内容.尽量简短
         * @param chekedColor     选中的颜色
         * @return {@link MaterialBuilder}
         */
        public MaterialBuilder addItem(@NonNull Drawable drawable, @NonNull Drawable checkedDrawable, @NonNull String title, @ColorInt int chekedColor) {
            MaterialItemViewData data = new MaterialItemViewData();
            data.drawable = Utils.newDrawable(drawable);
            data.checkedDrawable = Utils.newDrawable(checkedDrawable);
            data.title = title;
            data.chekedColor = chekedColor;
            itemDatas.add(data);
            return MaterialBuilder.this;
        }

        /**
         * 设置导航按钮的默认（未选中状态）颜色
         *
         * @param color 16进制整形表示的颜色，例如红色：0xFFFF0000
         * @return {@link MaterialBuilder}
         */
        public MaterialBuilder setDefaultColor(@ColorInt int color) {
            defaultColor = color;
            return MaterialBuilder.this;
        }

        /**
         * 设置消息圆点的颜色
         *
         * @param color 16进制整形表示的颜色，例如红色：0xFFFF0000
         * @return {@link MaterialBuilder}
         */
        public MaterialBuilder setMessageBackgroundColor(@ColorInt int color) {
            messageBackgroundColor = color;
            return MaterialBuilder.this;
        }

        /**
         * 设置消息数字的颜色
         *
         * @param color 16进制整形表示的颜色，例如红色：0xFFFF0000
         * @return {@link MaterialBuilder}
         */
        public MaterialBuilder setMessageNumberColor(@ColorInt int color) {
            messageNumberColor = color;
            return MaterialBuilder.this;
        }

        /**
         * 设置模式(在垂直布局中无效)。默认文字一直显示，且背景色不变。
         * 可以通过{@link MaterialMode}选择模式。
         * <p>例如:</p>
         * {@code MaterialMode.HIDE_TEXT}
         * <p>或者多选:</p>
         * {@code MaterialMode.HIDE_TEXT | MaterialMode.CHANGE_BACKGROUND_COLOR}
         *
         * @param mode {@link MaterialMode}
         * @return {@link MaterialBuilder}
         */
        public MaterialBuilder setMode(int mode) {
            MaterialBuilder.this.mode = mode;
            return MaterialBuilder.this;
        }

        /**
         * 使用垂直布局
         *
         * @return {@link MaterialBuilder}
         */
        public MaterialBuilder enableVerticalLayout() {
            enableVerticalLayout = true;
            return MaterialBuilder.this;
        }

        /**
         * 不对图标进行染色
         *
         * @return {@link MaterialBuilder}
         */
        public MaterialBuilder dontTintIcon() {
            doTintIcon = false;
            return MaterialBuilder.this;
        }

        /**
         * 通过{@link NavigationController}动态移除/添加导航项时,显示默认的布局动画
         *
         * @return {@link MaterialBuilder}
         */
        public MaterialBuilder enableAnimateLayoutChanges() {
            animateLayoutChanges = true;
            return MaterialBuilder.this;
        }

    }

    /**
     * 材料设计的单项视图信息
     */
    private static class MaterialItemViewData {
        Drawable drawable;
        Drawable checkedDrawable;
        String title;
        @ColorInt
        int chekedColor;
    }

    /**
     * 实现控制接口
     */
    private class Controller implements BottomLayoutController {

        private ObjectAnimator animator;
        private boolean hide = false;

        @Override
        public void setupWithViewPager(ViewPager viewPager) {
            if (viewPager == null) {
                return;
            }

            mViewPager = viewPager;

            if (mPageChangeListener != null) {
                mViewPager.removeOnPageChangeListener(mPageChangeListener);
            } else {
                mPageChangeListener = new ViewPagerPageChangeListener();
            }

            if (mNavigationController != null) {
                int n = mViewPager.getCurrentItem();
                if (mNavigationController.getSelected() != n) {
                    mNavigationController.setSelect(n);
                }
                mViewPager.addOnPageChangeListener(mPageChangeListener);
            }
        }

        @Override
        public void hideBottomLayout() {
            if (!hide) {
                hide = true;
                getAnimator().start();
            }
        }

        @Override
        public void showBottomLayout() {
            if (hide) {
                hide = false;
                getAnimator().reverse();
            }
        }

        private ObjectAnimator getAnimator() {
            if (animator == null) {
                if (mEnableVerticalLayout) {// 垂直布局向左隐藏
                    animator = ObjectAnimator.ofFloat(
                            FJNavigationView.this, "translationX", 0, -FJNavigationView.this.getWidth());
                } else {// 水平布局向下隐藏
                    animator = ObjectAnimator.ofFloat(
                            FJNavigationView.this, "translationY", 0, FJNavigationView.this.getHeight());
                }
                animator.setDuration(300);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
            }
            return animator;
        }
    }

    private class ViewPagerPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if (mNavigationController != null && mNavigationController.getSelected() != position) {
                mNavigationController.setSelect(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    private static final String INSTANCE_STATUS = "INSTANCE_STATUS";
    private final String STATUS_SELECTED = "STATUS_SELECTED";

    @Override
    protected Parcelable onSaveInstanceState() {
        if (mNavigationController == null) {
            return super.onSaveInstanceState();
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState());
        bundle.putInt(STATUS_SELECTED, mNavigationController.getSelected());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            int selected = bundle.getInt(STATUS_SELECTED, 0);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATUS));

            if (selected != 0 && mNavigationController != null) {
                mNavigationController.setSelect(selected);
            }

            return;
        }
        super.onRestoreInstanceState(state);
    }
}
