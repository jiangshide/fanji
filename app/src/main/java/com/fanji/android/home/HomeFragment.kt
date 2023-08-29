package com.fanji.android.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.fanji.android.R
import com.fanji.android.databinding.FragmentHomeBinding
import com.fanji.android.doc.PdfFragment
import com.fanji.android.home.fragment.FollowFragment
import com.fanji.android.home.fragment.RecommendFragment
import com.fanji.android.play.VideoFragment
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class HomeFragment : BaseFragment<FragmentHomeBinding>(), ViewPager.OnPageChangeListener {

    companion object {
        var currentIndex = 10
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.homeViewPager.adapter = binding.homeViewPager.create(childFragmentManager)
            .setTitles(
                "关注", "推荐", "发现", "北京"
            )
            .setFragment(
                FollowFragment(),
                RecommendFragment(),
                VideoFragment(),
                PdfFragment()
            )
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .setLinePagerIndicator(color(com.fanji.android.ui.R.color.alpha))
            .setTxtSelecteSize(16)
            .setTxtSelectedColor(R.color.white)
            .setPersistent(false)
            .setTxtSelectedSize(20)
            .initTabs(activity, binding.homeTab, binding.homeViewPager, true).setListener(this)

//        Anim.anim(earth, R.anim.heat)

        binding.earth.setOnClickListener {

        }
        binding.search.setOnClickListener {

        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        currentIndex = position
    }

    override fun onPageScrollStateChanged(state: Int) {
    }
}