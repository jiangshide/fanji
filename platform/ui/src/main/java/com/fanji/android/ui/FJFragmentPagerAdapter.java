package com.fanji.android.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.fanji.android.ui.tablayout.adapter.CommonNavigatorAdapter;
import com.fanji.android.ui.tablayout.help.ViewPagerHelper;
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator;
import com.fanji.android.ui.tablayout.interfaces.IPagerIndicator;
import com.fanji.android.ui.tablayout.interfaces.IPagerTitleView;
import com.fanji.android.ui.tablayout.titles.ColorTransitionPagerTitleView;
import com.fanji.android.ui.tablayout.titles.SimplePagerTitleView;
import com.fanji.android.ui.tablayout.titles.badge.BadgeAnchor;
import com.fanji.android.ui.tablayout.titles.badge.BadgePagerTitleView;
import com.fanji.android.ui.tablayout.titles.badge.BadgeRule;
import com.fanji.android.util.DimenUtil;
import com.fanji.android.util.LogUtil;

import java.util.HashMap;
import java.util.List;

/**
 * created by jiangshide on 2019/2/13.
 * email:18311271399@163.com
 */
//public class CusFragmentPagerAdapter extends FragmentStatePagerAdapter {
public class FJFragmentPagerAdapter extends FragmentPagerAdapter
        implements ViewPager.OnPageChangeListener {

    private Fragment[] mFragments;
    private String[] mTitles;
    private Bundle mBundle;
    private CommonNavigatorAdapter commonNavigatorAdapter;
    //    private int mLinePagerIndicator = 0xFF1296db;
    private int[] mLineColors;
    private FJViewPager mZdViewPager;
    private int mSelected = -1;
    private int mTxtSelectColor;
    private int mTxtSelectedColor;
    private int mTxtSelectSize = 16;
    private int mTxtSelectedSize = 20;
    private int mBgColor;
    private boolean mDivider = false;
    private int mMode = LinePagerIndicator.MODE_MATCH_EDGE;
    private boolean mPersistent = true;
    private FJNavigator zdNavigator = null;
    private HashMap<Integer, Integer> dot = new HashMap();
    private ViewPager.OnPageChangeListener mListener;

    //public ZdFragmentPagerAdapter(FragmentManager fm) {
    //  super(fm);
    //}

    public static String APP_COLOR_FONT = "appColorFont";
    public static String APP_COLOR_FONT_LIGHT = "appColorFontLight";

    public FJFragmentPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    public FJFragmentPagerAdapter setFragment(Fragment... fragments) {
        if (fragments == null || fragments.length == 0) return this;
        this.mFragments = fragments;
        notifyDataSetChanged();
        return this;
    }

    public FJFragmentPagerAdapter setFragment(List<? extends Fragment> fragments) {
        if (fragments == null || fragments.size() == 0) return this;
        int size = fragments.size();
        Fragment[] list = new Fragment[size];
        for (int i = 0; i < size; i++) {
            list[i] = fragments.get(i);
        }
        return setFragment(list);
    }

    public FJFragmentPagerAdapter setTitles(String... titles) {
        if (titles == null || titles.length == 0) return this;
        this.mTitles = titles;
        notifyDataSetChanged();
        updateTabs();
        return this;
    }

    public FJFragmentPagerAdapter setTitles(List<String> titles) {
        if (titles == null || titles.size() == 0) return this;
        int size = titles.size();
        mTitles = new String[size];
        for (int i = 0; i < size; i++) {
            mTitles[i] = titles.get(i);
        }
        notifyDataSetChanged();
        updateTabs();
        return this;
    }

    public FJFragmentPagerAdapter setBundle(Bundle bundle) {
        this.mBundle = bundle;
        return this;
    }

    public FJFragmentPagerAdapter setDot(int index, int num) {
        dot.put(index, num);
        if (commonNavigatorAdapter != null) {
            commonNavigatorAdapter.notifyDataSetChanged();
        }
        return this;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return (null != mTitles && mTitles.length >= 0 && position <= mTitles.length)
                ? mTitles[position] : null;
    }

    @Override
    public float getPageWidth(int position) {
        return super.getPageWidth(position);
    }

    @Override
    public Fragment getItem(int position) {
        LogUtil.e("---jsd---","----getItem~position:",position);
        Fragment fragment = mFragments[position];
        fragment.setArguments(mBundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return mFragments == null ? 0 : mFragments.length;
    }

    public FJFragmentPagerAdapter initTabs(Context context,
                                           final FJViewPager viewPager, boolean mode) {
        return initTabs(context, null, viewPager, mode, mListener);
    }

    public FJFragmentPagerAdapter initTabs(Context context, FJTabLayout cusTabLayout,
                                           final FJViewPager viewPager, boolean mode) {
        return initTabs(context, cusTabLayout, viewPager, mode, mListener);
    }

    public FJFragmentPagerAdapter initTabs(Context context, FJTabLayout cusTabLayout,
                                           final FJViewPager viewPager) {
        return initTabs(context, cusTabLayout, viewPager, false, mListener);
    }

    public FJFragmentPagerAdapter initTabs(Context context, FJTabLayout cusTabLayout,
                                           final FJViewPager viewPager, ViewPager.OnPageChangeListener listener) {
        return initTabs(context, cusTabLayout, viewPager, false, listener);
    }

    public FJFragmentPagerAdapter updateTabs() {
        if (commonNavigatorAdapter != null) {
            commonNavigatorAdapter.notifyDataSetChanged();
        }
        return this;
    }

    public FJFragmentPagerAdapter setTxtSelectColor(int color) {
        this.mTxtSelectColor = color;
        return this;
    }

    public FJFragmentPagerAdapter setTxtSelectedColor(int color) {
        this.mTxtSelectedColor = color;
        return this;
    }

    public FJFragmentPagerAdapter setTxtSelectSize(int size) {
        this.mTxtSelectSize = size;
        return this;
    }

    public FJFragmentPagerAdapter setTxtSelectedSize(int size) {
        this.mTxtSelectedSize = size;
        return this;
    }

    public FJFragmentPagerAdapter setMode(int mode) {
        this.mMode = mode;
        return this;
    }

    public FJFragmentPagerAdapter setBgColor(int bgColor) {
        this.mBgColor = bgColor;
        return this;
    }

    public FJFragmentPagerAdapter setPersistent(boolean persistent) {
        this.mPersistent = persistent;
        return this;
    }

    public FJFragmentPagerAdapter setDivider(boolean divider) {
        this.mDivider = divider;
        return this;
    }

    public FJFragmentPagerAdapter setListener(ViewPager.OnPageChangeListener listener) {
        this.mListener = listener;
        //if (mZdViewPager != null) {
        //  mZdViewPager.addOnPageChangeListener(listener);
        //}
        return this;
    }

    public FJFragmentPagerAdapter initTabs(Context context, FJTabLayout zdTabLayout,
                                           final FJViewPager viewPager, boolean mode, ViewPager.OnPageChangeListener listener) {
        this.mListener = listener;
        this.mZdViewPager = viewPager;
        if (mPersistent) {
            viewPager.setOffscreenPageLimit(mFragments.length);
        }
        viewPager.addOnPageChangeListener(this);
        if (mTitles == null) return this;

        zdNavigator = new FJNavigator(context);
        zdNavigator.invalidate();
        zdNavigator.setAdjustMode(mode);
        zdNavigator.setAdapter(commonNavigatorAdapter = new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return (null != mTitles) ? mTitles.length : 0;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                BadgePagerTitleView badgePagerTitleView = new BadgePagerTitleView(context);
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setNormalColor(
                                mTxtSelectColor != 0 ? ContextCompat.getColor(context, mTxtSelectColor)
                                        : ContextCompat.getColor(context, R.color.font))
                        .setSelectedColor(
                                mTxtSelectedColor != 0 ? ContextCompat.getColor(context, mTxtSelectedColor)
                                        : ContextCompat.getColor(context,
                                        R.color.fontLight))
                        .setNormalSize(mTxtSelectSize).setSelectedSize(mTxtSelectedSize);
                if (null != mTitles && mTitles.length >= index) {
                    simplePagerTitleView.setText(mTitles[index]);
                    simplePagerTitleView.getPaint().setFakeBoldText(true);
                }
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                        badgePagerTitleView.setBadgeView(null);
                    }
                });
                badgePagerTitleView.setInnerPagerTitleView(simplePagerTitleView);

                if (dot.containsKey(index)) {
                    int num = dot.get(index);
                    if (num > 0) {
                        TextView
                                textView = (TextView) LayoutInflater.from(context)
                                .inflate(R.layout.simple_count_badge_layout, null);
                        textView.setText("" + num);
                        badgePagerTitleView.setBadgeView(textView);
                    }

                    badgePagerTitleView.setXBadgeRule(
                            new BadgeRule(BadgeAnchor.CONTENT_LEFT, -DimenUtil.dip2px(6.0f)));
                    badgePagerTitleView.setYBadgeRule(new BadgeRule(BadgeAnchor.CONTENT_TOP, 0));

                    badgePagerTitleView.setAutoCancelBadge(false);
                }
                return badgePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(mMode);
                linePagerIndicator.setColors(mLineColors);
                linePagerIndicator.setRoundRadius(5);
                return linePagerIndicator;
            }
        });

        if (zdTabLayout != null && zdNavigator != null) {
            zdTabLayout.setNavigator(zdNavigator, mBgColor);
            if (mDivider) {
                LinearLayout titleContainer = zdNavigator.getTitleContainer();
                titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                titleContainer.setDividerPadding(DimenUtil.dip2px(15.0f));
                titleContainer.setDividerDrawable(
                        context.getResources().getDrawable(R.drawable.simple_splitter));
            }
            ViewPagerHelper.bind(zdTabLayout, viewPager);
        }
        return this;
    }

    public FJFragmentPagerAdapter setLinePagerIndicator(int... colors) {
        this.mLineColors = colors;
        if (null != commonNavigatorAdapter) commonNavigatorAdapter.notifyDataSetChanged();
        return this;
    }

    public void onRefresh() {
        mFragments[mZdViewPager.getCurrentItem()].onResume();
    }

    public void onResume() {
        if (mFragments != null && mFragments.length >= mSelected) {
            mFragments[mSelected].onResume();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mListener != null) {
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
        if (mSelected == -1 && mFragments != null) {
            mFragments[position].onResume();
        }
    }

    @Override
    public void onPageSelected(int position) {
        mSelected = position;
        if (mListener != null) {
            mListener.onPageSelected(position);
        }
        if (mFragments != null) {
            mFragments[position].onResume();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mListener != null) {
            mListener.onPageScrollStateChanged(state);
        }
    }
}
