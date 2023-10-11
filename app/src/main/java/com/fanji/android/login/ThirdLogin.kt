package com.fanji.android.login

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.text.TextUtils
import android.widget.Button
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.android.sanskrit.wxapi.LOGIN_WECHAT
import com.android.sanskrit.wxapi.WXApiManager
import com.fanji.android.MainActivity
import com.fanji.android.R
import com.fanji.android.net.HTTP_OK
import com.fanji.android.resource.Resource
import com.fanji.android.resource.USER_NOT_EXIST
import com.fanji.android.resource.vm.user.UserVM
import com.fanji.android.ui.FJDialog
import com.fanji.android.ui.FJToast
import com.fanji.android.util.FJEvent
import com.fanji.android.util.FileUtil
import com.fanji.android.util.LogUtil
import com.tencent.connect.UserInfo
import com.tencent.connect.common.Constants
import com.tencent.tauth.DefaultUiListener
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import org.json.JSONException
import org.json.JSONObject

/**
 * @author: jiangshide
 * @date: 2023/10/8
 * @email: 18311271399@163.com
 * @description:
 */
class ThirdLogin(private val context: Activity, private val userVM: UserVM) : Handler() {

    private var tencent: Tencent? = null

    fun setListener(activity: LifecycleOwner, wxLogin: Button, qqLogin: Button) {
        wxLogin.setOnClickListener {
            wxLogin(activity)
        }
        qqLogin.setOnClickListener {
            qqLogin()
        }
    }

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        when (msg.what) {
            0 -> {
                FJDialog.loading(context)
                val json = msg.obj.toString()
                userVM?.weChat(json)
            }

            1 -> {
                val response = msg.obj as JSONObject
                LogUtil.e("----jsd---", "----handleMessage~response:", response)
                if (response.has("nickname")) {
                    try {
//                        mUserInfo.setVisibility(View.VISIBLE)
                        val name = response.getString("nickname")
                        LogUtil.e("----jsd---", "----handleMessage~name:", name)
//                        binding.userPhone.text = name
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                if (response.has("figureurl")) {
                    val url = response.getString("figureurl_qq_2")
                    LogUtil.e("----jsd---", "----handleMessage~url:", url)
//                    binding.userIcon.load(url)
                }
            }
        }
    }

    fun wxLogin(activity: LifecycleOwner) {
        if (!WXApiManager.isWxInstall()) {
            FJToast.txt(R.string.wechat_not_install)
            LogUtil.e("----jsd-0---", "----weixin-222----")
            return
        }
        FJEvent.get()
            .with(LOGIN_WECHAT, String::class.java)
            .observes(activity, Observer {
                LogUtil.e("---jsd---", "wechat------>>>>>it", it.toString())
                val msg = obtainMessage()
                msg.what = 0
                msg.obj = it
                sendMessage(msg)
            })
        registerLogin(activity)
        WXApiManager.sendLoginRequest()
    }

    fun qqLogin() {
        tencent = Tencent.createInstance("101898921", context, FileUtil.AUTHORITY)
        val isSessionValid = tencent?.isSessionValid
        LogUtil.e("-----jsd----", "----qqLogin:", isSessionValid)
        if (isSessionValid == false) {
            tencent?.login(context, "all", loginListener)
        }
    }

    var loginListener: IUiListener? = object : BaseUiListener() {

        protected override fun doComplete(values: JSONObject?) {
            LogUtil.e(
                "---jsd----SDKQQAgentPref",
                "AuthorSwitch_SDK:" + SystemClock.elapsedRealtime(),
                " | values:",
                values
            )
            initOpenidAndToken(values)
            updateUserInfo()
//            updateLoginButton()
        }
    }

    /**
     * {"ret":0,"msg":"","is_lost":0,"nickname":"杨光","gender":"男","gender_type":2,"province":"广东","city":"深圳","year":"1990","constellation":"","figureurl":"http:\/\/qzapp.qlogo.cn\/qzapp\/101898921\/AAE6CA578C8B1412C8AC3CE83EE5BAAE\/30","figureurl_1":"http:\/\/qzapp.qlogo.cn\/qzapp\/101898921\/AAE6CA578C8B1412C8AC3CE83EE5BAAE\/50","figureurl_2":"http:\/\/qzapp.qlogo.cn\/qzapp\/101898921\/AAE6CA578C8B1412C8AC3CE83EE5BAAE\/100","figureurl_qq_1":"http:\/\/thirdqq.qlogo.cn\/g?b=oidb&k=IhhdvXkv2V4UF4FGvt079Q&kti=ZSNAGAAAAAA&s=40&t=1693985619","figureurl_qq_2":"http:\/\/thirdqq.qlogo.cn\/g?b=oidb&k=IhhdvXkv2V4UF4FGvt079Q&kti=ZSNAGAAAAAA&s=100&t=1693985619","figureurl_qq":"http:\/\/thirdqq.qlogo.cn\/g?b=oidb&k=IhhdvXkv2V4UF4FGvt079Q&kti=ZSNAGAAAAAA&s=640&t=1693985619","figureurl_type":"1","is_yellow_vip":"0","vip":"0","yellow_vip_level":"0","level":"0","is_yellow_year_vip":"0"}
     */
    private fun updateUserInfo() {
        if (tencent != null && tencent!!.isSessionValid()) {
            val listener: IUiListener = object : DefaultUiListener() {
                override fun onError(e: UiError) {}
                override fun onComplete(response: Any) {
                    val msg = Message()
                    msg.obj = response
                    msg.what = 0
                    sendMessage(msg)
//                    object : Thread() {
//                        override fun run() {
//                            val json = response as JSONObject
//                            if (json.has("figureurl")) {
//                                var bitmap: Bitmap? = null
//                                try {
//                                    bitmap = Util.getbitmap(json.getString("figureurl_qq_2"))
//                                } catch (e: JSONException) {
//                                    SLog.e(TAG, "Util.getBitmap() jsonException : " + e.message)
//                                }
//                                val msg = Message()
//                                msg.obj = bitmap
//                                msg.what = 1
//                                mHandler.sendMessage(msg)
//                            }
//                        }
//                    }.start()
                }

                override fun onCancel() {}
            }
            val info = UserInfo(context, tencent!!.getQQToken())
            info.getUserInfo(listener)
        } else {
            LogUtil.e("----jsd----", "-----updateUserInfo??????")
//            mUserInfo.setText("")
//            mUserInfo.setVisibility(View.GONE)
//            mUserLogo.setVisibility(View.GONE)
        }
    }

    fun initOpenidAndToken(jsonObject: JSONObject?) {
        try {
            val token = jsonObject?.getString(Constants.PARAM_ACCESS_TOKEN)
            val expires = jsonObject?.getString(Constants.PARAM_EXPIRES_IN)
            val openId = jsonObject?.getString(Constants.PARAM_OPEN_ID)
            LogUtil.e(
                "---jsd---",
                "----initOpenidAndToken~token:",
                token,
                " | expires:",
                expires,
                " | openId:",
                openId
            )
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                && !TextUtils.isEmpty(openId)
            ) {
                tencent?.setAccessToken(token, expires)
                tencent?.openId = openId
            }
        } catch (e: Exception) {
        }
    }

    var commonChannelApiListener: IUiListener = BaseUiListener()

    private open class BaseUiListener : DefaultUiListener() {
        override fun onComplete(response: Any) {//{"ret":0,"openid":"AAE6CA578C8B1412C8AC3CE83EE5BAAE","access_token":"96988A3A6F21D11808153EE11571703D","pay_token":"5804246E23F68B06DBF20FDE2C44BC6C","expires_in":7776000,"pf":"desktop_m_qq-10000144-android-2002-","pfkey":"5d0592ebd91ad6797c6158c6e8303dbc","msg":"","login_cost":112,"query_authority_cost":0,"authority_cost":0,"expires_time":1704556895612}
            LogUtil.e("---jsd---", "----response:", response)
            if (null == response) {
//                Util.showResultDialog(this@MainActivity, "返回为空", "登录失败")
                return
            }
            val jsonResponse = response as JSONObject
            if (jsonResponse.length() == 0) {
//                Util.showResultDialog(this@MainActivity, "返回为空", "登录失败")
                LogUtil.e("---jsd---", "----登录失败:")
                return
            }
//            Util.showResultDialog(this@MainActivity, response.toString(), "登录成功")
            LogUtil.e("---jsd---", "----response.toString():", response.toString())
            // 有奖分享处理
//            handlePrizeShare()
            doComplete(response)
        }

        protected open fun doComplete(values: JSONObject?) {}
        override fun onError(e: UiError) {
//            Util.toastMessage(this@MainActivity, "onError: " + e.errorDetail)
//            Util.dismissDialog()
            LogUtil.e("---jsd---", "----onError~e:", e)
        }

        override fun onCancel() {
            LogUtil.e("---jsd---", "----onCancel:")
//            Util.toastMessage(this@MainActivity, "onCancel: ")
//            Util.dismissDialog()
//            if (isServerSideLogin) {
//                isServerSideLogin = false
//            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Constants.REQUEST_LOGIN, Constants.REQUEST_APPBAR -> {
                Tencent.onActivityResultData(requestCode, resultCode, data, loginListener)
            }

            Constants.REQUEST_COMMON_CHANNEL -> {
                Tencent.onActivityResultData(
                    requestCode,
                    resultCode,
                    data,
                    commonChannelApiListener
                )
            }

            else -> {}
        }
    }

    fun registerLogin(activity: LifecycleOwner) {
        userVM?.login?.observe(activity, Observer {
            LogUtil.e("login------>>>>>it", it.data, " | code:" + it.code + " | msg:" + it.msg)
            FJDialog.cancelDialog()
            if (it?.code != HTTP_OK) {
                FJToast.txt(it.msg)
                return@Observer
            }
            Resource.user = it.data
            if (it.code == USER_NOT_EXIST) {
//                startActivity(Intent(this, BindPhoneActivity::class.java))
            } else {
//                FJEvent.get().with("main").post("main")
                context.startActivity(Intent(context, MainActivity::class.java))
                LogUtil.e("-----go main")
            }
        })
    }
}