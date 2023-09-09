package com.fanji.android.search

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.fanji.android.R
import com.fanji.android.databinding.FragmentSearchUserBinding
import com.fanji.android.img.FJImg
import com.fanji.android.net.HTTP_OK
import com.fanji.android.resource.Resource
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.resource.vm.user.UserVM
import com.fanji.android.resource.vm.user.data.User
import com.fanji.android.ui.FJCircleImg
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.refresh.api.RefreshLayout

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class SearchUserFragment(private val users: MutableList<User>? = null) :
    BaseFragment<FragmentSearchUserBinding>() {

    var user: UserVM? = create(UserVM::class.java)
    private var adapter: KAdapter<User>? = null
    private var selectedAdapter: KAdapter<User>? = null
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSearchUserBinding.inflate(layoutInflater)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.usesEdit.setListener { s, input ->
            if (TextUtils.isEmpty(input)) {
                user?.users()
            } else {
                user?.users(name = input)
            }
        }

        user!!.users.observe(viewLifecycleOwner, Observer {
            refreshFinish(it.isRefresh)
            if (it.code == HTTP_OK) {
                showView(it.data!!, it.isRefresh)
                hiddenTips()
            } else if (adapter == null || adapter?.count() == 0) {
                tips()
            }
        })

        showSelectedView()
        user?.users()
        loading()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        user?.users()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        super.onLoadMore(refreshLayout)
        user?.users(isRefresh = false)
    }

    private fun showView(data: MutableList<User>, isRefresh: Boolean) {
        data?.forEach { newUser ->
            selectedAdapter?.datas()
                ?.forEach {
                    if (newUser.nick == it.nick) {
                        newUser.selected = true
                    }
                }
        }
        if (adapter != null) {
            adapter?.add(data, isRefresh)
            return
        }
        adapter = binding.usesRecycleView.create(data, R.layout.search_user_fragment_item, {
            val usersIcon: FJCircleImg = this.findViewById(R.id.usersIcon)
            val usersNick: TextView = this.findViewById(R.id.usersNick)
            val usersDes: TextView = this.findViewById(R.id.usersDes)
            val userCheck: ImageView = this.findViewById(R.id.userCheck)
            val user = it
            FJImg.loadImageCircle(
                it.icon,
                usersIcon,
                com.fanji.android.resource.R.mipmap.default_user
            )
            usersNick.text = it.nick
            it.setSex(usersDes)
            userCheck.isSelected = it.selected
            if (it.id == Resource?.uid) {
                userCheck.visibility = View.GONE
            } else {
                userCheck.visibility = View.VISIBLE
            }
        }, {
            if (id == Resource?.uid) return@create
            selected = !selected
            if (selected) {
                if (selectedAdapter!!.count() >= 6) {
                    return@create
                }
                var isAdd = true
                selectedAdapter?.datas()
                    ?.forEach {
                        if (nick == it.nick) {
                            isAdd = false
                            return@forEach
                        }
                    }
                if (isAdd) {
                    selectedAdapter?.add(this)
                }
            } else {
                selectedAdapter?.remove(this)
            }
            adapter?.notifyDataSetChanged()
            setStatus()
        })
    }

    private fun setStatus() {
        val sure = getString(com.fanji.android.ui.R.string.sure)
        setRight("$sure(${selectedAdapter?.count()}/6)")?.setRightListener {
//            ZdEvent.get()
//                .with(USERS)
//                .post(selectedAdapter?.datas()!!)
            pop()
        }
    }

    private fun showSelectedView() {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        selectedAdapter =
            binding.usesSelectedRecycleView.create(
                arrayListOf(),
                R.layout.search_user_selected_fragment_item,
                {
                    val usersSelectedIcon: ImageView = this.findViewById(R.id.usersSelectedIcon)
                    FJImg.loadImageCircle(it.icon, usersSelectedIcon)
                },
                {
                    selectedAdapter?.remove(this)
                    setStatus()
                    adapter?.notifyDataSetChanged()
                },
                layoutManager
            )
        if (users != null) {
            selectedAdapter?.add(users)
        }
    }
}