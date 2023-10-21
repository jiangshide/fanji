package com.fanji.android.mine.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentPersonalBinding
import com.fanji.android.dialog.CurDialog
import com.fanji.android.find.follow.FollowedFragment
import com.fanji.android.resource.Resource
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
    ) = initView(FragmentPersonalBinding.inflate(layoutInflater), isTopPadding = false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.personalHeader.back.setOnClickListener {
            pop()
        }
        binding.personalHeader.share.setOnClickListener {
            CurDialog.share(requireContext())
        }
        binding.mineBgImg.load(Resource.getUrl())
        binding.personalHeader.icon.load(Resource.getUrl())
        binding.personalHeader.icon.setOnClickListener {
            viewImg(Resource.getUrl())
        }
        binding.personalHeader.follows.setOnClickListener {
            push(FollowedFragment(1))
        }
        binding.personalHeader.fans.setOnClickListener {
            push(FollowedFragment(2, "我粉丝"))
        }
        binding.personalHeader.fixInfo.setBgAlpha(125)
        binding.personalHeader.fixInfo.setOnClickListener {
            push(FixPersonalInfoFragment())
        }
        binding.personalViewPager.adapter = binding.personalViewPager.create(childFragmentManager)
            .setTitles(
                "创作", "回复"
            )
            .setFragment(
                OpusFragment(),
                ReplyFragment(),
            )
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .setTxtSelectedSize(16)
            .setTxtSelectSize(16)
            .setTxtSelectedColor(com.fanji.android.ui.R.color.neutralBlack)
            .setTxtSelectColor(com.fanji.android.ui.R.color.neutral)
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