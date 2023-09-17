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
            val list = FJFiles.getAllSdPaths(requireActivity())
            list.forEach {
                LogUtil.e("----jsd---", "----list~name:", it)
            }
            val duration = System.currentTimeMillis() - startTime
            LogUtil.e("----jsd----", "---list.size:", list.size, " | duration:", duration)
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        feedVM!!.recommendBlog()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        super.onLoadMore(refreshLayout)
        feedVM!!.recommendBlog(isRefresh = false)
    }
}