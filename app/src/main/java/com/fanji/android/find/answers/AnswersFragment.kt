package com.fanji.android.find.answers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fanji.android.R
import com.fanji.android.databinding.FragmentAnswersBinding
import com.fanji.android.resource.vm.feed.data.Feed
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
class AnswersFragment : BaseFragment<FragmentAnswersBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentAnswersBinding.inflate(layoutInflater))

    private var answersAdapter: KAdapter<Feed>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        answersAdapter =
            binding.answersRecyclerView.create(R.layout.fragment_answers_item, {
                val answersIcons = findViewById<FJCircleImg>(R.id.answersIcons)
                val answersName = findViewById<TextView>(R.id.answersName)
                val answersCircle = findViewById<TextView>(R.id.answersCircle)
                val answersTime = findViewById<TextView>(R.id.answersTime)
                val answersStatus = findViewById<TextView>(R.id.answersStatus)
                val answersTitle = findViewById<TextView>(R.id.answersTitle)
                val answersContent = findViewById<TextView>(R.id.answersContent)
                val bountyCoin = findViewById<TextView>(R.id.bountyCoin)
                val patchIn = findViewById<TextView>(R.id.patchIn)
                val depositStatus = findViewById<TextView>(R.id.depositStatus)
                answersName.text = it.name
            }, {})

        val list = ArrayList<Feed>()
        for (i in 1..100) {
            val feed = Feed()
            feed.name = "梵记科技$i"
            list.add(feed)
        }
        answersAdapter?.add(list)
    }
}