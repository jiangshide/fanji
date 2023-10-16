package com.fanji.android.cloud.setting

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fanji.android.R
import com.fanji.android.databinding.CommonRecyclerviewBinding
import com.fanji.android.mine.vip.VIPCenterFragment
import com.fanji.android.ui.FJButton
import com.fanji.android.ui.FJDialog
import com.fanji.android.ui.FJDialog.DialogViewListener
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment

/**
 * @author: jiangshide
 * @date: 2023/10/15
 * @email: 18311271399@163.com
 * @description:
 */
class CloudSettingFragment : BaseFragment<CommonRecyclerviewBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(CommonRecyclerviewBinding.inflate(layoutInflater), title = "云库设置")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.create(
            R.layout.common_recyclerview_item,
            {
                val name = findViewById<TextView>(R.id.name)
                name.text = it
                if (it == "云库空间") {
                    val action = findViewById<TextView>(R.id.action)
                    action.text = "2M/10G"
                }
            },
            {
                if (this == "云库空间") {
                    FJDialog.createView(
                        requireContext(),
                        R.layout.dialog_cloud_space,
                        dialogViewListener
                    ).setGravity(Gravity.BOTTOM).show()
                } else if (this == "回收站") {
                    push(RecycleFragment())
                }
            },
            arrayListOf("云库空间", "回收站")
        )
    }

    private val dialogViewListener = DialogViewListener {
        fun onView(view: View) {
            val cancel = view.findViewById<FJButton>(R.id.cancel)
            cancel.setOnClickListener {
                FJDialog.cancelDialog()
            }
            val openVip = view.findViewById<FJButton>(R.id.openVip)
            openVip.setOnClickListener {
                push(VIPCenterFragment())
            }
        }
    }
}