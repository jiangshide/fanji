package com.fanji.android.mine.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentFixPersonalInfoBinding
import com.fanji.android.files.FJFiles
import com.fanji.android.files.FileListener
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.util.data.FileData
import com.fanji.android.util.data.IMG

/**
 * @author: jiangshide
 * @date: 2023/10/11
 * @email: 18311271399@163.com
 * @description:
 */
class FixPersonalInfoFragment : BaseFragment<FragmentFixPersonalInfoBinding>(), FileListener {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentFixPersonalInfoBinding.inflate(layoutInflater), title = "修改资料")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.headIconRL.setOnClickListener {
            FJFiles.openFile(requireContext(), IMG, this)
        }
        binding.nickRL.setOnClickListener {
            push(PersonalNickFragment())
        }
        binding.signRL.setOnClickListener {
            push(PersonalSignFragment())
        }
    }

    override fun onFiles(files: List<FileData>) {
        binding.headIcon.load(files[0].path)
    }
}