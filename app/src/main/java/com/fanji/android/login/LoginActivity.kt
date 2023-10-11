package com.fanji.android.login

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.fanji.android.R
import com.fanji.android.databinding.ActivityLoginBinding
import com.fanji.android.img.FJImg
import com.fanji.android.location.data.PositionData
import com.fanji.android.net.HTTP_OK
import com.fanji.android.resource.COUNTDOWN_TIME
import com.fanji.android.resource.Resource
import com.fanji.android.resource.USER_NOT_EXIST
import com.fanji.android.resource.data.DeviceData
import com.fanji.android.resource.vm.user.UserVM
import com.fanji.android.ui.FJDialog
import com.fanji.android.ui.FJEditText
import com.fanji.android.ui.FJToast
import com.fanji.android.ui.base.BaseActivity
import com.fanji.android.util.LogUtil
import com.fanji.android.util.ValidateCodeUtil
import com.tencent.tauth.Tencent
import com.umeng.umverify.UMVerifyHelper
import com.umeng.umverify.listener.UMTokenResultListener


/**
 * @Author:jiangshide
 * @Date:8/28/23
 * @Email:18311271399@163.com
 * @Description:
 */
class LoginActivity : BaseActivity<ActivityLoginBinding>(), FJEditText.OnKeyboardListener,
    ValidateCodeUtil.ICountDown,UMTokenResultListener {
    override fun getViewBinding() = initView(
        ActivityLoginBinding.inflate(layoutInflater),
        title = "欢迎登录",
        rightBtn = "验证码登录",
        rightCallClazz = CodeLoginActivity::class.java
    )

    var user: UserVM? = create(UserVM::class.java)

    private var positionData: PositionData? = null
    private var deviceData: DeviceData? = null
    private var thirdLogin: ThirdLogin? = null
    private var umVerifyHelper:UMVerifyHelper?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Tencent.setIsPermissionGranted(true)
        thirdLogin = ThirdLogin(this, user!!)

        setListener()
        observers()
        if (Resource.user != null) {
            binding.userPhone.text = Resource.name
//            binding.userAuthor.text = ""
            FJImg.loadImageCircle(Resource.icon, binding.userIcon, R.mipmap.ic_launcher)
        }
        positionData = PositionData()
        deviceData = DeviceData(this)
    }

    private fun setListener() {

        binding.userLogin.setOnClickListener {
            umVerifyHelper = UMVerifyHelper.getInstance(this,this)
//        umVerifyHelper?.setAuthSDKInfo("")

            val envA = umVerifyHelper?.checkEnvAvailable(1)
            LogUtil.e("-----jsd----","-----envA:",envA)
            umVerifyHelper?.getLoginToken(this,5000)
        }

        binding.userProtocol.setOnClickListener {
//            openUrl(BuildConfig.PRIVACY_AGREEMENT, getString(R.string.user_protocol))
            openUrl(
                "http://192.168.1.11:8098/static/protocol/user_protocol.txt",
                getString(R.string.user_protocol)
            )

        }

        binding.privacyProtocol.setOnClickListener {
//            openUrl(BuildConfig.USE_AGREEMENT, getString(R.string.protect_protocol))
            openUrl(
                "http://192.168.1.11:8098/static/protocol/protect_protocol.txt",
                getString(R.string.protect_protocol)
            )
        }
        binding.thirdProtocol.setOnClickListener {

        }
        thirdLogin?.setListener(this, binding.wxLogin, binding.qqLogin)

        binding.pswLogin.setOnClickListener {
            activity(PswLoginActivity::class.java)
        }

        val animator = ObjectAnimator.ofFloat(binding.checkedProtocol, "scaleX", 1.0f, 1.5f, 1.0f)
        animator.duration = 500
        animator.start()
        val animator2 = ObjectAnimator.ofFloat(binding.checkedProtocol, "scaleY", 1.0f, 1.5f, 1.0f)
        animator2.duration = 500
        animator2.start()
        binding.checkedProtocol.toggle()
    }

    private fun observers() {
        user?.validate?.observe(this, Observer {
            LogUtil.e("validate------>>>>>it", it)
            FJDialog.cancelDialog()
            if (it.code != HTTP_OK) {
                FJToast.txt(it.msg)
                return@Observer
            }
            FJToast.txt("短信发送成功!")
            ValidateCodeUtil.getInstance().countDownSecond(this, COUNTDOWN_TIME)
        })

        user?.codeLogin?.observe(this, Observer {
            LogUtil.e("codeLogin------>>>>>it", it)
            FJDialog.cancelDialog()
            if (it.code != HTTP_OK) {
                val code = it.code
                if (code == USER_NOT_EXIST) {
//                    setShowPsw()
                } else {
                    FJToast.txt(it?.msg)
                }
            } else {
            }
        })

        user!!.reg.observe(this, Observer {
            LogUtil.e("reg------>>>>>it", it)
            FJDialog.cancelDialog()
            if (it.code != HTTP_OK) {
                FJToast.txt(it.msg)
            } else {
            }
        })
    }

    override fun show(height: Int) {
    }

    override fun hide(height: Int) {
    }

    override fun onCountDown(code: Int) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        thirdLogin?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onTokenSuccess(p0: String?) {
        LogUtil.e("----jsd----","-----onTokenSuccess~p0:",p0)
    }

    override fun onTokenFailed(p0: String?) {
        LogUtil.e("----jsd----","-----onTokenFailed~p0:",p0)
    }
}