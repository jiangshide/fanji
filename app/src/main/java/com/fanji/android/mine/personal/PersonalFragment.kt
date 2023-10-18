package com.fanji.android.mine.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentPersonalBinding
import com.fanji.android.find.follow.FollowedFragment
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator
import com.google.android.material.appbar.AppBarLayout

/**
 * @author: jiangshide
 * @date: 2023/10/11
 * @email: 18311271399@163.com
 * @description:
 */
class PersonalFragment : BaseFragment<FragmentPersonalBinding>(),
    AppBarLayout.OnOffsetChangedListener {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentPersonalBinding.inflate(layoutInflater), title = "个人主页")

    private val url =
        "http:\\/\\/thirdqq.qlogo.cn\\/g?b=oidb&k=IhhdvXkv2V4UF4FGvt079Q&kti=ZSNAGAAAAAA&s=640&t=1693985619"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.personalHeader.icon.load(url)
        binding.personalHeader.icon.setOnClickListener {
            viewImg(url)
        }
        binding.personalHeader.follows.setOnClickListener {
            push(FollowedFragment(1))
        }
        binding.personalHeader.fans.setOnClickListener {
            push(FollowedFragment(2, "我粉丝"))
        }
        binding.personalHeader.fixInfo.setOnClickListener {
            push(FixPersonalInfoFragment())
        }
        binding.personalViewPager.adapter = binding.personalViewPager.create(childFragmentManager)
            .setTitles(
                "发布", "回复"
            )
            .setFragment(
                OpusFragment(),
                ReplyFragment(),
            )
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .initTabs(activity, binding.personalTab, binding.personalViewPager, true)
        binding.appBarLayout.addOnOffsetChangedListener(this)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        val size = verticalOffset * -1 / 308f
//        binding.mineTopTitleL.alpha = size
//        binding.mineTopFollow.alpha = size
////        binding.userL.alpha = 1 - size
//        if (size == 0.0f) {
//            binding.mineTopTitleL.visibility = View.GONE
//            binding.mineTopFollow.visibility = View.GONE
//        } else {
//            binding.mineTopTitleL.visibility = View.VISIBLE
//            if (id != Resource.uid) {
//                binding.mineTopFollow.visibility = View.VISIBLE
//            } else {
//                binding.mineTopFollow.visibility = View.GONE
//            }
//        }
    }

}