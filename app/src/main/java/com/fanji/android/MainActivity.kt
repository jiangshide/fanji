package com.fanji.android

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.fanji.android.channel.ChannelFragment
import com.fanji.android.databinding.ActivityMainBinding
import com.fanji.android.home.HomeFragment
import com.fanji.android.message.MessageFragment
import com.fanji.android.mine.MineFragment
import com.fanji.android.net.HTTP_OK
import com.fanji.android.net.state.NetState
import com.fanji.android.publish.PublishFragment
import com.fanji.android.resource.R
import com.fanji.android.resource.Resource
import com.fanji.android.resource.TAB_BOTTOM_SCROLL
import com.fanji.android.resource.base.BaseActivity
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.resource.task.PUBLISH_UPLOAD
import com.fanji.android.resource.task.PublishTask
import com.fanji.android.resource.vm.publish.PublishVM
import com.fanji.android.resource.vm.publish.data.Publish
import com.fanji.android.resource.vm.user.data.User
import com.fanji.android.ui.FJToast
import com.fanji.android.ui.navigation.NavigationController
import com.fanji.android.ui.navigation.item.SpecialTabRound
import com.fanji.android.ui.navigation.listener.OnTabItemSelectedListener
import com.fanji.android.util.FJEvent
import com.fanji.android.util.LogUtil
import com.fanji.android.util.SPUtil
import com.fanji.android.util.data.FileData
import com.fanji.android.util.data.OnCompressListener

class MainActivity : BaseActivity<ActivityMainBinding>(), ViewPager.OnPageChangeListener,
    View.OnClickListener,
    OnTabItemSelectedListener, OnCompressListener, PublishTask.PublishTaskListener {
    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)

    private var publishVM: PublishVM? = null

    private lateinit var tabController: NavigationController

    private var user: User? = null

    private var specialTabRound: SpecialTabRound? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        publishVM = ViewModelProvider.NewInstanceFactory.instance.create(PublishVM::class.java)
        user = Resource.user
        specialTabRound =
            binding.mainTab.newRoundItem(
                R.mipmap.publish,
                R.mipmap.publish,
                "发布"
            ) as SpecialTabRound?
        specialTabRound?.setUrl(
            "http://zd112.oss-cn-beijing.aliyuncs.com/img/-1463646632.jpg",
            "http://zd112.oss-cn-beijing.aliyuncs.com/img/compress_mmexport1529243703493.jpg"
        )
        tabController =
            binding.mainTab.custom()
                .addItem(binding.mainTab.newItem(R.mipmap.unhome, R.mipmap.homed, "首页"))
                .addItem(binding.mainTab.newItem(R.mipmap.unchannel, R.mipmap.channeled, "频道"))
                .addItem(specialTabRound)
                .addItem(binding.mainTab.newItem(R.mipmap.unmsg, R.mipmap.msged, "消息"))
                .addItem(binding.mainTab.newItem(R.mipmap.unmine, R.mipmap.mined, "我的"))
                .build()
        tabController?.setMessageNumber(3, 1000)
        tabController?.setHasMessage(1, true)
        tabController?.setHasMessage(4, true)
        tabController?.setHasMessage(0, true)
        tabController?.addTabItemSelectedListener(this)
        binding.mainViewPager.adapter = binding.mainViewPager.create(supportFragmentManager)
            .setFragment(
                HomeFragment(),
                ChannelFragment(),
                PublishFragment(),
                MessageFragment(),
                MineFragment()
            )
            .initTabs(this, binding.mainViewPager, true).setListener(this)

        tabController.setupWithViewPager(binding.mainViewPager)
        binding.mainViewPager.setScanScroll(
            user != null && SPUtil.getBoolean(
                TAB_BOTTOM_SCROLL,
                true
            )
        )
        FJEvent.get().with(TAB_BOTTOM_SCROLL, Boolean::class.java).observes(this, {
            binding.mainViewPager.setScanScroll(it)
        })
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                binding.content.visibility = View.GONE
                binding.mainViewPager.visibility = View.VISIBLE
                binding.mainTab.visibility = View.VISIBLE
            } else {
                binding.content.visibility = View.VISIBLE
            }
        }
//        WXApiManager.regToWX(this)
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

//        Net.registerWebSocket("ws://192.168.1.11:8080/v1/ws", object :
//            WebSocketHandler.WebSocketCallBack {
//            override fun onOpen() {
//                LogUtil.e("------------webSocket:onOpen")
//            }
//
//            override fun onMessage(str: String?) {
//                LogUtil.e("------------webSocket:onMessage~str:", str)
//            }
//
//            override fun onClose() {
//                LogUtil.e("------------webSocket:onClose")
//            }
//
//            override fun onConnectError(t: Throwable?) {
//                LogUtil.e("------------webSocket:onConnectError~t:", t)
//            }
//        })
//
//        thread {
//            val okHttpClient = OkHttpClient.Builder().build()
//            val request = Request.Builder().url("http://192.168.1.11:8080/v1/user").build()
//            try {
//                val res = okHttpClient.newCall(request).execute()
//                LogUtil.e("------------webSocket~res:$res")
//            } catch (e: Exception) {
//                LogUtil.e("------------webSocket~e:$e")
//            }
//        }
    }

    override fun push(fragment: BaseFragment<*>, bundle: Bundle?, enter: Int, exit: Int) {
        super.push(fragment, bundle, enter, exit)
        fragment.arguments = bundle
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.setCustomAnimations(enter, exit)
        beginTransaction.add(com.fanji.android.R.id.content, fragment)
        beginTransaction.addToBackStack(fragment.javaClass.canonicalName)
        beginTransaction.commitAllowingStateLoss()
    }

    override fun pop(flags: Int) {
        super.pop(flags)
        supportFragmentManager.popBackStackImmediate(null, flags)
    }

    override fun onDestroy() {
        NetState.Companion.instance.unRegisterObserver(this) //注销网络监听
        super.onDestroy()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        LogUtil.e("position:", position)
//        setStatusColor(statusBarColors[position])
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
        binding.mainViewPager?.currentItem = 0
    }

    override fun onSelected(index: Int, old: Int) {
    }

    private fun unUser(position: Int) {
        if (position != 0 && user == null) {
            binding.content.visibility = View.VISIBLE
            binding.mainViewPager.visibility = View.GONE
            binding.mainTab.visibility = View.GONE
//            push(LoginFragment(this))
        } else {
            binding.mainViewPager.visibility = View.VISIBLE
            binding.mainTab.visibility = View.VISIBLE
        }
    }

    override fun onRepeat(index: Int) {
    }

    override fun unLogin(index: Int) {
        unUser(index)
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

//    private fun loginIm() {
//        TUIKit.login("${Resource?.uid}", Resource?.user?.imSign, object : IUIKitCallBack {
//            override fun onSuccess(data: Any?) {
//                LogUtil.e("------im~success~data:", data)
//            }
//
//            override fun onError(module: String?, errCode: Int, errMsg: String?) {
//                LogUtil.e(
//                    "-------im~error~module:",
//                    module,
//                    " | errCode:",
//                    errCode,
//                    " | errMsg:",
//                    errMsg
//                )
//            }
//        })
//
//        val settings = TIMOfflinePushSettings()
////开启离线推送
//        settings.setEnabled(true)
////设置收到 C2C 离线消息时的提示声音，以把声音文件放在 res/raw 文件夹下为例
////        settings.setC2cMsgRemindSound(Uri.parse("android.resource://" + packageName + "/" + R.raw.dudulu))
////设置收到群离线消息时的提示声音，以把声音文件放在 res/raw 文件夹下为例
////        settings.setGroupMsgRemindSound(Uri.parse("android.resource://" + packageName + "/" + R.raw.dudulu))
//
//        TIMManager.getInstance().setOfflinePushSettings(settings)
//
//        // 未读消息监视器
//        ConversationManagerKit.getInstance().addUnreadWatcher(this)
//        GroupChatManagerKit.getInstance()
//    }
//
//    override fun updateUnread(count: Int) {
//        tabController?.setMessageNumber(3, count)
//    }
}