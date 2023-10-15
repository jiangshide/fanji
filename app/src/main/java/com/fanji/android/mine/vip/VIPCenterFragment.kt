package com.fanji.android.mine.vip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.fanji.android.R
import com.fanji.android.databinding.FragmentVipCenterBinding
import com.fanji.android.resource.vm.user.data.User
import com.fanji.android.ui.FJButton
import com.fanji.android.ui.FJTextView
import com.fanji.android.ui.adapter.HORIZONTAL
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.util.LogUtil

/**
 * @author: jiangshide
 * @date: 2023/10/15
 * @email: 18311271399@163.com
 * @description:
 */
class VIPCenterFragment : BaseFragment<FragmentVipCenterBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentVipCenterBinding.inflate(layoutInflater), title = "会员中心")

    private var adapter: KAdapter<User>? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.privilegeRatio.setOnClickListener {
            push(PrivilegeRatioFragment())
        }
        binding.privilegeRecyclerView.create(R.layout.common_recyclerview_item, {
            val name = findViewById<TextView>(R.id.name)
            name.text = it
        }, {},
            arrayListOf(
                "免广告",
                "尊贵身份标识",
                "可创建5个圈子",
                "1T空间",
                "任务免费置顶1天",
                "在线解压文件"
            ),
            GridLayoutManager(requireContext(), 3)
        )
        adapter = binding.investRecyclerView.create(R.layout.fragment_invest_item, {
            val first = findViewById<FJButton>(R.id.first)
            val month = findViewById<TextView>(R.id.month)
            val symbol = findViewById<TextView>(R.id.symbol)
            val price = findViewById<TextView>(R.id.price)
            val costPrice = findViewById<FJTextView>(R.id.costPrice)
            if (it.fansNum == 1) {
                first.visibility = View.VISIBLE
            } else {
                first.visibility = View.GONE
            }
            month.text = it.name
            price.text = "${it.fansNum}"
            costPrice.underLine(it.reason)
        }, {}, getList(), binding.investRecyclerView.HORIZONTAL())

        binding.payType.setOnCheckedChangeListener { group, checkedId ->
            LogUtil.e("----jsd----", "------checkedId:", checkedId)
        }
    }

    fun getList(): ArrayList<User> {
        val list = ArrayList<User>()
        for (i in 1..4) {
            val user = User()
            if (i == 1) {
                user.name = "连续包月"
            } else {
                user.name = "{$i}个月"
            }
            user.fansNum = i
            user.reason = "¥$i"
            list.add(user)
        }
        return list
    }
}