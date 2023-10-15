package com.fanji.android.mine.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fanji.android.R
import com.fanji.android.databinding.CommonRecyclerviewBinding
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
class BindFragment : BaseFragment<CommonRecyclerviewBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(CommonRecyclerviewBinding.inflate(layoutInflater), title = "账号绑定")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.create(R.layout.common_recyclerview_item, {
            val name = findViewById<TextView>(R.id.name)
            val action = findViewById<TextView>(R.id.action)
            action.text = "立即绑定"
            name.text = it
        }, {
            FJDialog.create(requireContext()).setContent("使用$this 进行授权，\n是否继续")
                .setLeftTxt("取消").setRightTxt("继续").setListener { isCancel, editMessage ->
                    LogUtil.e(
                        "--jsd---",
                        "------isCancel:",
                        isCancel,
                        " | editMessage:",
                        editMessage
                    )
                }.show()
        }, arrayListOf("微信", "QQ"))
    }
}