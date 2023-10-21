package com.fanji.android.find.answers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.fanji.android.R
import com.fanji.android.databinding.CommonRecyclerviewBinding
import com.fanji.android.feed.FeedDetailFragment
import com.fanji.android.resource.Resource
import com.fanji.android.resource.vm.feed.data.Feed
import com.fanji.android.ui.FJButton
import com.fanji.android.ui.FJCircleImg
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment

/**
 * @author: jiangshide
 * @date: 2023/10/2
 * @email: 18311271399@163.com
 * @description:
 */
class AnswersFragment : BaseFragment<CommonRecyclerviewBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(CommonRecyclerviewBinding.inflate(layoutInflater))

    private var answersAdapter: KAdapter<Feed>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        answersAdapter =
            binding.recyclerView.create(R.layout.fragment_answers_item, {
                val icon = findViewById<FJCircleImg>(R.id.icon)
                icon.load(Resource.getUrl())
                val name = findViewById<TextView>(R.id.name)
                val vip = findViewById<ImageView>(R.id.vip)
                val time = findViewById<TextView>(R.id.time)
                val circle = findViewById<FJButton>(R.id.circle)
                val status = findViewById<FJButton>(R.id.status)
                val title = findViewById<TextView>(R.id.title)
                val content = findViewById<TextView>(R.id.content)
                val bountyCoin = findViewById<TextView>(R.id.bountyCoin)
                val patchIn = findViewById<TextView>(R.id.patchIn)
                val depositStatus = findViewById<FJButton>(R.id.depositStatus)
                name.text = it.name
            }, {
                push(FeedDetailFragment(this))
            })

        val list = ArrayList<Feed>()
        for (i in 1..100) {
            val feed = Feed()
            feed.name = "梵记科技$i"
            list.add(feed)
        }
        answersAdapter?.add(list)
    }
}