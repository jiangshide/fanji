package com.fanji.android.mine.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fanji.android.R
import com.fanji.android.databinding.FragmentSetBinding
import com.fanji.android.mine.MENU
import com.fanji.android.mine.fragment.set.AboutFragment
import com.fanji.android.mine.fragment.set.ShowSetFragment
import com.fanji.android.resource.Resource
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.util.FJEvent
import com.fanji.android.util.LogUtil
import com.fanji.android.util.SPUtil

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class SetFragment : BaseFragment<FragmentSetBinding>() {

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(
        FragmentSetBinding.inflate(layoutInflater),
        title = getString(R.string.setting)
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLeft(com.fanji.android.resource.R.mipmap.unmenu)
        setLeftListener {
            FJEvent.get().with(MENU).post(0)
        }
        val list = resources.getStringArray(R.array.settings).toMutableList()
        setLeft(this)
        binding.mineSetRecycleView.create(R.layout.mine_set_fragment_item, {
            val mineSetItemName = this.findViewById<TextView>(R.id.mineSetItemName)
            mineSetItemName.text = it
        }, {
            LogUtil.e(this)
            when (this) {
                getString(R.string.recycle) -> {
                    push(ShowSetFragment())
                }

                getString(R.string.favorites) -> {
                    push(ShowSetFragment())
                }

                getString(R.string.channel) -> {
                    push(ShowSetFragment())
                }

                getString(R.string.private_chat) -> {
                    push(ShowSetFragment())
                }

                getString(R.string.blacklist) -> {
                    push(ShowSetFragment())
                }

                getString(R.string.display) -> {
                    push(ShowSetFragment())
                }

                getString(R.string.comment) -> {
                    push(ShowSetFragment())
                }

                getString(R.string.cache) -> {
                    push(ShowSetFragment())
                }

                getString(R.string.notify) -> {
                    push(ShowSetFragment())
                }

                getString(R.string.about) -> {
                    push(AboutFragment())
                }

                getString(R.string.exit) -> {
                    SPUtil.clear(Resource.USER)

                }
            }
        },list)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        FJEvent.get().with(MENU).post(0)
    }
}