package com.fanji.android.mine.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fanji.android.R
import com.fanji.android.databinding.CommonRecyclerviewBinding
import com.fanji.android.ui.FJButton
import com.fanji.android.ui.FJToast
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment

/**
 * @author: jiangshide
 * @date: 2023/10/15
 * @email: 18311271399@163.com
 * @description:
 */
class SettingFragment : BaseFragment<CommonRecyclerviewBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(CommonRecyclerviewBinding.inflate(layoutInflater), title = "系统设置")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.create(
            R.layout.fragment_setting,
            {
                val account = findViewById<TextView>(R.id.account)
                account.text = it
                if (it == "清理缓存") {
                    val clearCache = findViewById<TextView>(R.id.clearCache)
                    clearCache.visibility = View.VISIBLE
                    clearCache.text = "12.0M"
                } else if (it == "版本信息") {
                    val update = findViewById<FJButton>(R.id.update)
                    update.visibility = View.VISIBLE
                    update.setOnClickListener {
                        FJToast.fixTxt(requireContext(), "暂无更新！")
                    }
                }
            },
            {
                if (this == "账号与安全") {
                    push(AccountSecureFragment())
                }
            },
            arrayListOf("账号与安全", "清理缓存", "版本信息")
        )
    }
}