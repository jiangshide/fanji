package com.fanji.android.mine

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fanji.android.R
import com.fanji.android.databinding.FragmentMineBinding
import com.fanji.android.mine.feedback.FeedbackFragment
import com.fanji.android.mine.message.MessageManagerFragment
import com.fanji.android.mine.personal.PersonalFragment
import com.fanji.android.mine.score.ScoreFragment
import com.fanji.android.mine.setting.SettingFragment
import com.fanji.android.mine.vip.VIPCenterFragment
import com.fanji.android.resource.Resource
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment

/**
 * @author: jiangshide
 * @date: 2023/10/14
 * @email: 18311271399@163.com
 * @description:
 */
class MineFragment : BaseFragment<FragmentMineBinding>() {
    override fun viewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        initView(FragmentMineBinding.inflate(layoutInflater))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.icon.load(Resource.testUrl[1])
        binding.icon.setOnClickListener {
            viewImg(Resource.testUrl[1])
        }
        binding.login.setOnClickListener {

        }
        binding.score.setOnClickListener {
            push(ScoreFragment())
        }
        binding.personalHome.setOnClickListener {
            push(PersonalFragment())
        }
        binding.collect.setOnClickListener {
            push(CollectFragment())
        }
        binding.trace.setOnClickListener {
            push(TraceFragment())
        }
        binding.message.setOnClickListener {
            push(MessageManagerFragment())
        }
        binding.vipRL.setOnClickListener {
            push(VIPCenterFragment())
        }
        binding.mineRecycleView.create(R.layout.common_recyclerview_item, {
            val itemName = findViewById<TextView>(R.id.name)
            itemName.gravity = Gravity.LEFT
            itemName.text = it
        }, {
            if (this == "我的积分") {
                push(ScoreFragment())
            } else if (this == "意见反馈") {
                push(FeedbackFragment())
            } else if (this == "系统设置") {
                push(SettingFragment())
            } else {

            }
        }, getResArr(R.array.mine).toMutableList())
    }
}