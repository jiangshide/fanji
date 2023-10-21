package com.fanji.android.publish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.fanji.android.R
import com.fanji.android.databinding.FragmentProductBinding
import com.fanji.android.resource.vm.feed.data.Feed
import com.fanji.android.ui.adapter.HORIZONTAL
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment

/**
 * @author: jiangshide
 * @date: 2023/10/3
 * @email: 18311271399@163.com
 * @description:
 */
class ProductFragment : BaseFragment<FragmentProductBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentProductBinding.inflate(layoutInflater))

    private var circleAdapter: KAdapter<Feed>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.productTitle.setListener { s, input ->

        }
        binding.productContent.setListener { s, input ->

        }
        circleAdapter = binding.productCircleRecyclerView.create(
            R.layout.fragment_publish_product_circle,
            {
                val productCircleName = findViewById<TextView>(R.id.productCircleName)
                productCircleName.text = it.name
            },
            {}, manager = binding.productCircleRecyclerView.HORIZONTAL()
        )

        val list = ArrayList<Feed>()
        for (i in 1..10) {
            val feed = Feed()
            feed.name = "梵记圈$i"
            list.add(feed)
        }
        circleAdapter?.add(list)
    }
}