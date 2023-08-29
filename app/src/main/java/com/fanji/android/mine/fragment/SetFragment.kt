package com.fanji.android.mine.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fanji.android.R
import com.fanji.android.databinding.FragmentSetBinding
import com.fanji.android.mine.MENU
import com.fanji.android.mine.fragment.set.ShowSetFragment
import com.fanji.android.resource.Resource
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.ui.adapter.create
import com.fanji.android.util.FJEvent
import com.fanji.android.util.LogUtil
import com.fanji.android.util.SPUtil

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class SetFragment : BaseFragment<FragmentSetBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSetBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.commonHead.leftImg.setImageResource(com.fanji.android.resource.R.mipmap.unmenu)
        binding.commonHead.leftImg.setOnClickListener {
            FJEvent.get().with(MENU).post(0)
        }
        binding.commonHead.topTitle.text = "设置"
        setLeft(this)
        val list = arrayListOf<String>(
            "回忆箱",
            "收藏夹",
            "频道管理",
            "私聊设置",
            "黑名单管理",
            "显示设置",
            "给点评分",
            "缓存管理",
            "消息通知",
            "关于我们",
            "退出登录"
        )

        binding.mineSetRecycleView.create(list, R.layout.mine_set_fragment_item, {
            val mineSetItemName = this.findViewById<TextView>(R.id.mineSetItemName)
            mineSetItemName.text = it
        }, {
            LogUtil.e(this)
            when (this) {
                "显示设置" -> {
                    push(ShowSetFragment())
                }

                "关于我们" -> {

                }

                "退出登录" -> {
                    SPUtil.clear(Resource.USER)

                }
            }
        })
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        FJEvent.get().with(MENU).post(0)
    }
}