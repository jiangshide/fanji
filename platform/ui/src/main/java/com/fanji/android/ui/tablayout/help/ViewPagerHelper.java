package com.fanji.android.ui.tablayout.help;

import androidx.viewpager.widget.ViewPager;

import com.fanji.android.ui.FJTabLayout;

/**
 * created by jiangshide on 2019-08-05.
 * email:18311271399@163.com
 */
public class ViewPagerHelper {
    public static void bind(final FJTabLayout zdTabLayout, ViewPager viewPager) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                zdTabLayout.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                zdTabLayout.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                zdTabLayout.onPageScrollStateChanged(state);
            }
        });
    }
}
