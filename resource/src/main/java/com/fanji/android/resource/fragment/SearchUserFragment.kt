package com.fanji.android.resource.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fanji.android.img.FJImg
import com.fanji.android.net.HTTP_OK
import com.fanji.android.resource.R
import com.fanji.android.resource.Resource
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.resource.vm.user.UserVM
import com.fanji.android.resource.vm.user.data.User
import com.fanji.android.ui.FJCircleImg
import com.fanji.android.ui.FJEditText
import com.fanji.android.ui.FJRecycleView
import com.fanji.android.ui.refresh.api.RefreshLayout
import com.fanji.widget.adapter.KAdapter
import com.fanji.widget.adapter.create

/**
 * created by jiangshide on 5/5/21.
 * email:18311271399@163.com
 */
class SearchUserFragment(private val users: MutableList<User>? = null) : BaseFragment() {

    var user: UserVM? = null
    private var adapter: KAdapter<User>? = null
    private var selectedAdapter: KAdapter<User>? = null

    private lateinit var usesEdit: FJEditText
    private lateinit var usesRecycleView: FJRecycleView
    private lateinit var usesSelectedRecycleView: FJRecycleView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        user = ViewModelProvider.NewInstanceFactory.instance.create(UserVM::class.java)
        return view(R.layout.search_user_fragment, true, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        usesEdit = view.findViewById(R.id.usesEdit)
        usesRecycleView = view.findViewById(R.id.usesRecycleView)
        usesSelectedRecycleView = view.findViewById(R.id.usesSelectedRecycleView)

        usesEdit.setListener { s, input ->
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
        adapter = usesRecycleView.create(data, R.layout.search_user_fragment_item, {
            val usersIcon: FJCircleImg = this.findViewById(R.id.usersIcon)
            val usersNick: TextView = this.findViewById(R.id.usersNick)
            val usersDes: TextView = this.findViewById(R.id.usersDes)
            val userCheck: ImageView = this.findViewById(R.id.userCheck)
            val user = it
            FJImg.loadImageCircle(it.icon, usersIcon, R.mipmap.default_user)
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
            usesSelectedRecycleView.create(
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