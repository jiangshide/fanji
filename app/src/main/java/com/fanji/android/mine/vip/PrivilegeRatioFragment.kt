package com.fanji.android.mine.vip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.fanji.android.R
import com.fanji.android.databinding.FragmentPrivilegeRatioBinding
import com.fanji.android.resource.vm.feed.FeedVM
import com.fanji.android.resource.vm.user.data.User
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.refresh.api.RefreshLayout

/**
 * @author: jiangshide
 * @date: 2023/10/15
 * @email: 18311271399@163.com
 * @description:
 */
class PrivilegeRatioFragment() : BaseFragment<FragmentPrivilegeRatioBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(
        FragmentPrivilegeRatioBinding.inflate(layoutInflater),
        title = "权益比对",
        isRefresh = true
    )

    private var feedVM: FeedVM? = create(FeedVM::class.java)
    private var adapter: KAdapter<User>? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter =
            binding.privilegeRecyclerView.create(R.layout.fragment_privilege_ratio_item, {
                val privilege = findViewById<TextView>(R.id.privilege)
                val privilegeVip = findViewById<TextView>(R.id.privilegeVip)
                val privilegeNoVip = findViewById<TextView>(R.id.privilegeNoVip)
                privilege.text = it.name
                privilegeVip.text = it.date
                privilegeNoVip.text = it.reason
            }, {}, getList())
        feedVM!!.recommendBlog.observe(requireActivity(), Observer {
            finishData(true, true, true)

//            if (it.msg != null) {
//                tips()
//            }
            getList()
        })
        feedVM!!.recommendBlog().loading(tipsView)
    }

    private fun getList(): ArrayList<User> {
        val list = ArrayList<User>()
        for (i in 1..10) {
            val user = User()
            user.name = "梵山科技$i"
            user.date = "ss$i"
            user.reason = "ddadad$i"
            list.add(user)
        }
        return list
    }


    override fun onRetry(view: View?) {
        super.onRetry(view)
        feedVM!!.recommendBlog().loading(tipsView)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        feedVM!!.recommendBlog()
    }
}