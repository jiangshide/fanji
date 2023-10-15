package com.fanji.android.find

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.fanji.android.R
import com.fanji.android.databinding.FragmentHomeBinding
import com.fanji.android.find.answers.AnswersFragment
import com.fanji.android.find.follow.FollowFragment
import com.fanji.android.find.recommend.RecommendFragment
import com.fanji.android.mine.personal.PersonalFragment
import com.fanji.android.resource.vm.channel.data.ChannelBlog
import com.fanji.android.search.OnChannelListener
import com.fanji.android.search.SearchFragment
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class FindFragment : BaseFragment<FragmentHomeBinding>(), ViewPager.OnPageChangeListener,
    OnChannelListener {

    companion object {
        var currentIndex = 10
    }

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentHomeBinding.inflate(layoutInflater), isTopPadding = true)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.homeViewPager.adapter = binding.homeViewPager.create(childFragmentManager)
            .setTitles(
                getResArr(R.array.home)
            )
            .setFragment(
                FollowFragment(),
                RecommendFragment(),
                AnswersFragment(),
            )
            .setPersistent(false)
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .setLinePagerIndicator(color(com.fanji.android.ui.R.color.alpha))
            .setTxtSelecteSize(16)
            .setTxtSelectedSize(20)
            .initTabs(activity, binding.homeTab, binding.homeViewPager)
            .setListener(this)
        binding.homeViewPager.currentItem = 1
        binding.personal.setOnClickListener {
            push(PersonalFragment())
        }

        binding.search.setOnClickListener {
            push(SearchFragment())
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        currentIndex = position
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onChannel(channelBlog: ChannelBlog) {

    }
}