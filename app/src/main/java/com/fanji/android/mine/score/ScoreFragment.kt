package com.fanji.android.mine.score

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fanji.android.R
import com.fanji.android.databinding.FragmentScoreBinding
import com.fanji.android.resource.vm.user.data.User
import com.fanji.android.ui.FJButton
import com.fanji.android.ui.FJDialog
import com.fanji.android.ui.FJDialog.DialogViewListener
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.util.LogUtil

/**
 * @author: jiangshide
 * @date: 2023/10/15
 * @email: 18311271399@163.com
 * @description:
 */
class ScoreFragment : BaseFragment<FragmentScoreBinding>(), DialogViewListener {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(
        FragmentScoreBinding.inflate(layoutInflater),
        title = "我的梵币",
        rightBtn = "签到"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRightListener {
            setRight("已签到")
        }
        binding.tradeDetail.setOnClickListener {
            push(TradeManagerFragment())
        }
        binding.investScore.setOnClickListener {
            FJDialog.createView(requireContext(), R.layout.dialog_invest_score, this)
                .setGravity(Gravity.BOTTOM).show()
        }
    }

    override fun onView(view: View?) {
        val investRecyclerView = view?.findViewById<RecyclerView>(R.id.investRecyclerView)
        val payType = view?.findViewById<RadioGroup>(R.id.payType)
        payType?.setOnCheckedChangeListener { group, checkedId ->
            LogUtil.e("---jsd---", "----checkedId:", checkedId)
        }
        investRecyclerView?.create(R.layout.common_recyclerview_item, {
            val title = findViewById<TextView>(R.id.name)
            val content = findViewById<TextView>(R.id.des)
            title.text = it.name
            content.text = it.reason
        }, {}, getInvest(6), GridLayoutManager(requireContext(), 3))

        view?.findViewById<FJButton>(R.id.surePay)?.setOnClickListener {

        }
    }

    fun getInvest(index: Int): ArrayList<User> {
        val list = ArrayList<User>()
        for (i in 1..index) {
            val user = User()
            user.name = "$i 元"
            user.reason = "$i 梵币"
            list.add(user)
        }
        return list
    }
}