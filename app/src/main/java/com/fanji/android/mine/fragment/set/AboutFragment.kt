package com.fanji.android.mine.fragment.set

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fanji.android.BuildConfig
import com.fanji.android.R
import com.fanji.android.databinding.FragmentSetAboutBinding
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.util.AppUtil
import com.fanji.android.util.SystemUtil

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class AboutFragment : BaseFragment<FragmentSetAboutBinding>() {

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentSetAboutBinding.inflate(layoutInflater), title = getString(R.string.about))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val version = getString(R.string.version)
        binding.aboutVersion.text = "$version ${AppUtil.getAppVersionName()}"

        binding.aboutRecycleView.create(R.layout.mine_set_about_fragment_item, {
                val aboutItem = this.findViewById<TextView>(R.id.aboutItem)
                val aboutItemDes = this.findViewById<TextView>(R.id.aboutItemDes)
                aboutItem.text = it
                if (it == getString(R.string.business_cooperation)) {
                    aboutItemDes.text = "18311271399"
//                    aboutItemDes.setDrawableRight(R.drawable.alpha)
                } else {
                    aboutItemDes.text = ""
//                    aboutItemDes.setDrawableRight(R.mipmap.arrow)
                }
            }, {
                when (this) {
                    getString(R.string.function_introducation) -> {
//                        openUrl(BuildConfig.FUNCTION_INTRODUCE,getString(R.string.function_introducation))
                    }

                    getString(R.string.help_feedback) -> {
                        push(FeedbackFragment())
                    }

                    getString(R.string.check_version) -> {
//                        FJToast.txt(getString(R.string.no_update))
//          val dump = SystemUtil.createDumpFile()
//          LogUtil.e("-----------dump:",dump)
//         val size =  FileUtil.getFileSize(dump)
//          ZdToast.txt("----dump:$dump | $size")
                    }

                    getString(R.string.business_cooperation) -> {
                        SystemUtil.call(context, "18311271399")
                    }
                }
            },arrayListOf(
            getString(R.string.function_introducation),
            getString(R.string.help_feedback),
            getString(R.string.check_version),
            getString(R.string.business_cooperation)
        ))

        binding.aboutEmail.setOnClickListener {
            SystemUtil.sendEmail(context, "18311271399@163.com")
        }

        binding.aboutSoftProtocol.setOnClickListener {
            openUrl(BuildConfig.PRIVACY_AGREEMENT, getString(R.string.user_protocol))
        }
        binding.aboutPrivacyProtocol.setOnClickListener {
            openUrl(BuildConfig.USE_AGREEMENT, getString(R.string.protect_protocol))
        }
    }
}