package com.fanji.android.mine.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fanji.android.R
import com.fanji.android.databinding.FragmentAccountSecureBinding
import com.fanji.android.ui.FJDialog
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.util.LogUtil

/**
 * @author: jiangshide
 * @date: 2023/10/15
 * @email: 18311271399@163.com
 * @description:
 */
class AccountSecureFragment : BaseFragment<FragmentAccountSecureBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentAccountSecureBinding.inflate(layoutInflater), title = "账号与安全")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.accountRecyclerView.create(R.layout.common_recyclerview_item, {
            val name = findViewById<TextView>(R.id.name)
            val action = findViewById<TextView>(R.id.action)
            if (it == "更换手机") {
                action.text = "131****0001"
            } else if (it.contains("实名认证")) {
                action.text = "去认证"
            }
            name.text = it
        }, {
            if (this == "更换手机") {
                push(ReplacePhoneFragment())
            } else if (this == "修改密码") {
                push(UpdatePswFragment())
            } else if (this == "第三方绑定") {
                push(BindFragment())
            } else if (this == "注销账号") {
                FJDialog.create(requireContext())
                    .setContent("确定注销账号?\n7天内重新登录可撤销注销")
                    .setLeftTxt("取消").setRightTxt("注销").setListener { isCancel, editMessage ->
                        LogUtil.e(
                            "--jsd---",
                            "------isCancel:",
                            isCancel,
                            " | editMessage:",
                            editMessage
                        )
                    }.show()
            }
        }, arrayListOf("更换手机", "修改密码", "第三方绑定", "实名认证(取消)", "注销账户"))
    }
}