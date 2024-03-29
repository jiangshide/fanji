package com.fanji.android

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.fanji.android.circle.CircleManagerFragment
import com.fanji.android.cloud.CloudManagerFragment
import com.fanji.android.databinding.ActivityMainBinding
import com.fanji.android.find.FindFragment
import com.fanji.android.mine.MineFragment
import com.fanji.android.net.HTTP_OK
import com.fanji.android.net.state.NetState
import com.fanji.android.publish.PublishManagerFragment
import com.fanji.android.resource.Resource
import com.fanji.android.resource.task.PUBLISH_UPLOAD
import com.fanji.android.resource.task.PublishTask
import com.fanji.android.resource.vm.publish.PublishVM
import com.fanji.android.resource.vm.publish.data.Publish
import com.fanji.android.resource.vm.user.data.User
import com.fanji.android.ui.FJToast
import com.fanji.android.ui.base.BaseActivity
import com.fanji.android.ui.navigation.NavigationController
import com.fanji.android.ui.navigation.listener.OnTabItemSelectedListener
import com.fanji.android.util.FJEvent
import com.fanji.android.util.LogUtil
import com.fanji.android.util.data.FileData
import com.fanji.android.util.data.OnCompressListener

class MainActivity : BaseActivity<ActivityMainBinding>(), ViewPager.OnPageChangeListener,
    View.OnClickListener,
    OnTabItemSelectedListener, OnCompressListener, PublishTask.PublishTaskListener {

    private var publishVM: PublishVM? = create(PublishVM::class.java)

    private lateinit var tabController: NavigationController

    private var user: User? = null

    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.tabHost.setFragmentManager(supportFragmentManager)
        user = Resource.user
        tabController =
            binding.tabHost.setFragments(
                this,
                FindFragment(),
                CircleManagerFragment(),
                EmptyFragment(),
                CloudManagerFragment(),
                MineFragment()
            ).addGradientItem(R.mipmap.find, R.mipmap.finded, getString(R.string.tab_home))
                .addGradientItem(R.mipmap.circle, R.mipmap.circled, getString(R.string.tab_circle))
                .addRoundItem(R.mipmap.publish, R.mipmap.publish, "")
                .addGradientItem(R.mipmap.cloud, R.mipmap.clouded, getString(R.string.tab_message))
                .addGradientItem(R.mipmap.mine, R.mipmap.mined, getString(R.string.tab_mine))
                .build(this)!!
        tabController?.setMessageNumber(3, 1000)
        tabController?.setHasMessage(1, true)
        tabController?.setHasMessage(4, true)
        tabController?.setHasMessage(0, true)

//        tabController?.addTabItemSelectedListener(this)

        supportFragmentManager.addOnBackStackChangedListener {
        }
//        loginIm()
        FJEvent.get()
            .with(PUBLISH_UPLOAD)
            .observes(this, Observer {
                PublishTask().setCompressListener(this)
                    .setListener(this)
                    .publish()
            })
        publishVM!!.publishBlog.observe(this, Observer {
            if (it.code != HTTP_OK) {
                FJToast.txt(it.msg)
                return@Observer
            }
            Resource.clearPublish()
//            FJImg.loadImageCircle(
//                Resource.icon, mZdTab.floatIcon, R.mipmap.default_user
//            )
//            FJEvent.get()
//                .with(HOME_REFRESH)
//                .post(HOME_REFRESH)
        })
    }

    override fun onDestroy() {
        NetState.Companion.instance.unRegisterObserver(this) //注销网络监听
        super.onDestroy()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        LogUtil.e("position:", position)
//        if (position != 2) {
//            specialTabRound?.setUrl(
//                "http://zd112.oss-cn-beijing.aliyuncs.com/png/compress_mmexport1604111133648.png",
//                "http://zd112.oss-cn-beijing.aliyuncs.com/jpg/compress_%E6%A2%B5%E8%AE%B01604116379078.jpg"
//            )?.startAnim(true)
//        } else {
//            specialTabRound?.startAnim(false)
//        }
//        Net.updateAddress()
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onClick(v: View?) {
//        binding.mainViewPager?.currentItem = 0
    }

    override fun onSelected(index: Int, old: Int) {
    }

    override fun onRepeat(index: Int) {
    }

    override fun onEmpty(index: Int, old: Int) {
        push(
            PublishManagerFragment(),
            enter = com.fanji.android.ui.R.anim.bottom_enter,
            exit = com.fanji.android.ui.R.anim.bottom_exit
        )
    }

    override fun onResult(dest: String?) {

    }

    override fun onProgress(percent: Float) {

    }

    override fun start(fileData: FileData) {

    }

    override fun progress(totalProgress: Int, progress: Int) {
    }

    override fun onFail(throwable: Throwable?) {
    }

    override fun onSuccess(publish: Publish?) {
        publishVM?.publish(publish!!)
    }
}