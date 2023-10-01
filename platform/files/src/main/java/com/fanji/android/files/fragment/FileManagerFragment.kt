package com.fanji.android.files.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.fanji.android.files.FileListener
import com.fanji.android.files.databinding.FragmentFileManagerBinding
import com.fanji.android.files.utils.PickerManager
import com.fanji.android.files.viewmodels.DocVM
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator
import com.fanji.android.util.LogUtil
import com.fanji.android.util.data.DOC
import com.fanji.android.util.data.FileData

/**
 * @author: jiangshide
 * @date: 2023/10/1
 * @email: 18311271399@163.com
 * @description:
 */
class FileManagerFragment(val type: Int, val fileListener: FileListener) :
    BaseFragment<FragmentFileManagerBinding>(), FileListener {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(
        FragmentFileManagerBinding.inflate(layoutInflater), isTopPadding = true, isTips = true,
        isTitle = true,
        title = "文档"
    )

    private val docVM = create(DocVM::class.java)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        docVM.lvDocData.observe(viewLifecycleOwner, Observer { files ->
            finishData(true, true, true)
            LogUtil.e("-----jsd----", "------files:", files.size)
            if (files.size == 0) {
                tips()
                return@Observer
            }
            val titles = ArrayList<String>()
            val fragments = ArrayList<FileFragment>()
            for ((key, value) in files) {
                LogUtil.e("-----jsd----", "------key:", key, " | values.size:", value.size)
                titles.add(key.title)
                fragments.add(FileFragment(DOC, key.title, key.drawable, this))
            }
            showView(titles, fragments)
        })

        docVM.getDocs(
            requireContext(),
            PickerManager.getFileTypes(),
            PickerManager.sortingType.comparator
        ).loading(tipsView)
    }

    private fun showView(titles: List<String>, fragments: List<FileFragment>) {
        binding.fileViewPager.adapter = binding.fileViewPager.create(childFragmentManager)
            .setTitles(titles)
            .setFragment(
                fragments
            )
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .setTxtSelectedColor(com.fanji.android.ui.R.color.white)
            .initTabs(requireContext(), binding.fileTab, binding.fileViewPager, true)
    }

    override fun onFiles(files: List<FileData>) {
        docVM.getDocs(
            requireContext(),
            PickerManager.getFileTypes(),
            PickerManager.sortingType.comparator
        ).loading(tipsView)
    }

}