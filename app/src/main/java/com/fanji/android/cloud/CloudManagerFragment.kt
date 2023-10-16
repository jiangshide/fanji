package com.fanji.android.cloud

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.fanji.android.R
import com.fanji.android.cloud.setting.CloudSettingFragment
import com.fanji.android.cloud.transfer.TransferManagerFragment
import com.fanji.android.databinding.FragmentCloudManagerBinding
import com.fanji.android.search.SearchFragment
import com.fanji.android.ui.FJButton
import com.fanji.android.ui.FJDialog
import com.fanji.android.ui.FJDialog.DialogViewListener
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator
import com.fanji.android.util.LogUtil

/**
 * @author: jiangshide
 * @date: 2023/10/3
 * @email: 18311271399@163.com
 * @description:
 */
class CloudManagerFragment : BaseFragment<FragmentCloudManagerBinding>(),
    ViewPager.OnPageChangeListener, DialogViewListener {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentCloudManagerBinding.inflate(layoutInflater))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cloudManagerTransfer.setOnClickListener {
            push(TransferManagerFragment())
        }
        binding.cloudManagerSet.setOnClickListener {
            push(CloudSettingFragment())
        }
        binding.cloudSearch.setOnClickListener {
            push(SearchFragment())
        }
        binding.cloudManagerViewPager.adapter =
            binding.cloudManagerViewPager.create(childFragmentManager)
                .setTitles(
                    "智能", "文件"
                )
                .setFragment(
                    CloudMindFragment(),
                    CloudFileFragment()
                )
                .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
                .setTxtSelectedColor(com.fanji.android.ui.R.color.white)
                .initTabs(
                    activity,
                    binding.cloudManagerTab,
                    binding.cloudManagerViewPager,
                    false,
                    this
                )
    }

    override fun onView(view: View?) {
        val showRecyclerView = view?.findViewById<RecyclerView>(R.id.showRecyclerView)
        val sortRecyclerView = view?.findViewById<RecyclerView>(R.id.sortRecyclerView)
        val cancel = view?.findViewById<FJButton>(R.id.cancel)

        showRecyclerView?.create(R.layout.common_recyclerview_item_icon, {
            val name = findViewById<TextView>(R.id.name)
            name.text = it
        }, {}, arrayListOf("列表模式", "大图模式"))

        sortRecyclerView?.create(
            R.layout.common_recyclerview_item_icon,
            {
                val name = findViewById<TextView>(R.id.name)
                name.text = it
            },
            {},
            arrayListOf("按更新时间", "按文件名称", "按文件类型")
        )

        cancel?.setOnClickListener {
            FJDialog.cancelDialog()
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        LogUtil.e("--jsd---", "-----view:", view, " | position:", position)
        if (position == 1) {
            binding.showSet.visibility = View.VISIBLE
            binding.showSet.setOnClickListener {
                FJDialog.createView(requireContext(), R.layout.dialog_show_set, this)
                    .setGravity(Gravity.BOTTOM).show()
            }
        } else {
            binding.showSet.visibility = View.GONE
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
    }
}