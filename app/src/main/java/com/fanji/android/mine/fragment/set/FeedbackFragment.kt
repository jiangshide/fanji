package com.fanji.android.mine.fragment.set

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.fanji.android.R
import com.fanji.android.databinding.FragmentSetAboutFeedbackBinding
import com.fanji.android.files.FJFiles
import com.fanji.android.files.FileListener
import com.fanji.android.img.FJImg
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.resource.vm.report.ReportVM
import com.fanji.android.third.aliyun.oss.OssClient
import com.fanji.android.ui.FJToast
import com.fanji.android.util.data.FileData

/**
 * @author: jiangshide
 * @date: 2023/9/8
 * @email: 18311271399@163.com
 * @description:
 */
class FeedbackFragment : BaseFragment<FragmentSetAboutFeedbackBinding>(),
    OssClient.HttpFileListener, FileListener {

    private var content: String = ""
    private var mUrl: String = ""
    private var contact: String = ""
    var reportVM: ReportVM? = create(ReportVM::class.java)

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSetAboutFeedbackBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.feedbackEdit.setListener { s, input ->
            this.content = input
            validate()
        }
        binding.feedbackImg.setOnClickListener {
            openFile()
        }
        binding.feedbackMethod.setListener { s, input ->
            contact = input
            validate()
        }
        reportVM!!.feedback.observe(viewLifecycleOwner, Observer {
//            hiddle()
            if (it.msg != null) {
                FJToast.txt(it.msg)
                return@Observer
            }
            FJToast.txt(getString(R.string.success)) {
                pop()
            }
        })
    }

    private fun openFile() {
//        FJFiles.setType(IMG)
//            .setMax(1)
//            .setFloat(true)
//            .setSingleListener(this)
//            .open(this)
        FJFiles.openFile(requireContext(), this)
    }

    private fun validate() {
        val disable = TextUtils.isEmpty(content)
//        setRight(getString(R.string.send))?.setRightColor(R.color.redLight)
//            ?.setRightEnable(!disable)
//            ?.setRightListener {
//                reportVM?.feedback(
//                    contentId = contentId, content = content, url = mUrl, contact = contact,
//                    source = source
//                )
//                showLoading()
//            }
    }

    override fun onProgress(
        currentSize: Long,
        totalSize: Long,
        progress: Int
    ) {
    }

    override fun onSuccess(
        position: Int,
        url: String
    ) {
        mUrl = url
    }

    override fun onFailure(
        clientExcepion: Exception,
        serviceException: Exception
    ) {
        FJToast.txt(serviceException?.message)
    }

    override fun onFiles(files: List<FileData>) {
        mUrl = files[0].path!!
        FJImg.loadImage(
            mUrl,
            binding.feedbackImg,
            com.fanji.android.files.R.mipmap.image_placeholder
        )
        OssClient.instance.setListener(this)
            .setFileData(files[0])
            .start()
        validate()
    }
}