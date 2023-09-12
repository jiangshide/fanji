package com.fanji.android.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.fanji.android.databinding.FragmentFollowBinding
import com.fanji.android.files.FJFiles
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.resource.vm.feed.FeedVM
import com.fanji.android.ui.refresh.api.RefreshLayout
import com.fanji.android.util.LogUtil
import kotlinx.coroutines.launch
import java.io.File

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class FollowFragment : BaseFragment<FragmentFollowBinding>() {

    private var feedVM: FeedVM? = create(FeedVM::class.java)

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentFollowBinding.inflate(layoutInflater), isRefresh = true, isMore = true)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feedVM!!.recommendBlog.observe(requireActivity(), Observer {
            finishData(true, true, true)
            LogUtil.e(
                "----jsd----",
                "-----it.msg:",
                it.msg,
                " | code:",
                it.code,
                " | data:",
                it.data
            )
            if (it.msg != null) {
                tips()
            }
        })
        feedVM!!.recommendBlog().loading(tipsView)
    }

    override fun onRetry(view: View?) {
        super.onRetry(view)
        feedVM!!.recommendBlog().loading(tipsView)
        getFilesJsd()
    }

    fun getFilesJsd() {
        val filesDir = requireActivity().filesDir
        val cacheDir = requireActivity().cacheDir
        val obbDir = requireActivity().obbDir
        val codeCacheDir = requireActivity().codeCacheDir
        val externalFilesDir = requireActivity().getExternalFilesDir("*")
        val externalCacheDir = requireActivity().externalCacheDir
        val databasePath = requireActivity().getDatabasePath("*")
//        val dir = requireActivity().getDir("",FileMode)
        val packageCodePath = requireActivity().packageCodePath
//        val rootDirectory = requireActivity().getRoot
//        val externalStorageDirectory = requireActivity().externalS
//        val externalStoragePublicDirctory = requireActivity().externalStor
//        val downloadCacheDirectory = requireActivity().getD
        val fileStreamPath = requireActivity().getFileStreamPath("*")
        LogUtil.e(
            "--------jsd---",
            "----filesDir:",
            filesDir,
            "---cacheDir:",
            cacheDir,
            "---obbDir:",
            obbDir,
            "---codeCacheDir:",
            codeCacheDir,
            "---externalFilesDir:",
            externalFilesDir,
            " | externalCacheDir:",
            externalCacheDir,
            "---databasePath:",
            databasePath,
            "---packageCodePath:",
            packageCodePath,
            "---fileStreamPath:",
            fileStreamPath
        )
        val startTime = System.currentTimeMillis()
        uiScope.launch {
            val file = File("/")
            LogUtil.e("----jsd---", "----listRoots:", file)
//            files(file)
            val list = FJFiles.getDocs(requireActivity())
            val duration = System.currentTimeMillis() - startTime
            LogUtil.e("----jsd----", "---list.size:", list.size, " | duration:", duration)
        }
    }

//    val list = ArrayList<File>()
//    fun files(file: File) {
//        if (file.exists() && file.isDirectory) {
//            val datas = file.listFiles()
//            LogUtil.e("--jsd---", "----datas:", datas)
//            if (datas != null) {
//                LogUtil.e("--jsd---", "----datas.size:", datas.size)
//                for (file in datas) {
//                    files(file)
//                }
//            }
//        } else if (file.exists() && file.isFile) {
//            LogUtil.e("----jsd---", "----file:", file)
//            list.add(file)
//        } else {
//            LogUtil.e("----jsd---", "----file exception:", file)
//        }
//    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        feedVM!!.recommendBlog()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        super.onLoadMore(refreshLayout)
        feedVM!!.recommendBlog(isRefresh = false)
    }
}