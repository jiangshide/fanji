package com.fanji.android.find.answers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentAnswersDetailBinding
import com.fanji.android.mine.personal.PersonalFragment
import com.fanji.android.resource.Resource
import com.fanji.android.ui.base.BaseFragment

/**
 * @author: jiangshide
 * @date: 2023/10/21
 * @email: 18311271399@163.com
 * @description:
 */
class AnswersDetailFragment : BaseFragment<FragmentAnswersDetailBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentAnswersDetailBinding.inflate(layoutInflater), title = "问答详情")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.title.text
        binding.icon.load(Resource.getUrl())
        binding.icon.setOnClickListener {
            push(PersonalFragment())
        }
//        binding.name.text
//        binding.vip
//        binding.time.text
        binding.bounty.setOnClickListener {

        }
        binding.personal.setOnClickListener {

        }
//        binding.contents.text
        binding.joinRL.setOnClickListener {
            push(JoinDetailFragment())
        }
    }
}