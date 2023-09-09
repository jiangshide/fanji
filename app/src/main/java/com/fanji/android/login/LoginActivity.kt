package com.fanji.android.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import com.android.sanskrit.wxapi.LOGIN_WECHAT
import com.android.sanskrit.wxapi.WXApiManager
import com.fanji.android.MainActivity
import com.fanji.android.R
import com.fanji.android.databinding.ActivityLoginBinding
import com.fanji.android.img.FJImg
import com.fanji.android.net.HTTP_OK
import com.fanji.android.resource.COUNTDOWN_TIME
import com.fanji.android.resource.Resource
import com.fanji.android.resource.USER_NOT_EXIST
import com.fanji.android.resource.base.BaseActivity
import com.fanji.android.resource.data.DeviceData
import com.fanji.android.resource.data.PositionData
import com.fanji.android.resource.vm.user.UserVM
import com.fanji.android.ui.FJDialog
import com.fanji.android.ui.FJEditText
import com.fanji.android.ui.FJToast
import com.fanji.android.util.FJEvent
import com.fanji.android.util.LogUtil
import com.fanji.android.util.ValidateCodeUtil

/**
 * @Author:jiangshide
 * @Date:8/28/23
 * @Email:18311271399@163.com
 * @Description:
 */
class LoginActivity : BaseActivity<ActivityLoginBinding>(), FJEditText.OnKeyboardListener,
    ValidateCodeUtil.ICountDown {
    override fun getViewBinding() = ActivityLoginBinding.inflate(layoutInflater)

    private var name = ""
    private var psw = ""
    private var rePsw = ""
    private var code = ""
    private var adCode = "+86"

    var user: UserVM? = create(UserVM::class.java)

    private var positionData: PositionData? = null
    private var deviceData: DeviceData? = null

    private val handler = Handler {
        val w = it.what
        when (w) {
            1 -> {
                FJDialog.loading(this)
                val json = it.obj.toString()
                user?.weChat(
                    json, positionData!!.gson,
                    deviceData!!.gson
                )
            }
        }
        return@Handler false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setListener()
        observers()
        if (Resource.user != null) {
            binding.phoneEdit?.setText(Resource.name)
            binding.appName?.text = Resource.user!!.nick
            FJImg.loadImageCircle(Resource.icon, binding.userIcon, R.mipmap.ic_launcher)
        }
        positionData = PositionData()
        deviceData = DeviceData(this)
    }

    private fun setListener() {
        binding.weiXinLogin.setOnClickListener {
            if (!WXApiManager.isWxInstall()) {
                FJToast.txt(R.string.wechat_not_install)
                LogUtil.e("----jsd-0---", "----weixin-222----")
                return@setOnClickListener
            }
            WXApiManager.sendLoginRequest()
        }
        binding.phoneLogin.setOnClickListener {
            binding.weiXinLoginL?.visibility = View.GONE
            binding.phoneLoginL?.visibility = View.VISIBLE
            binding.appName?.text = "梵记"
            binding.userIcon?.setImageResource(R.mipmap.ic_launcher)
            Resource.clearUser()
        }

        binding.goWeiXinLogin.setOnClickListener {
            binding.phoneEdit?.hide()
            binding.weiXinLoginL?.visibility = View.VISIBLE
            binding.phoneLoginL?.visibility = View.GONE
        }

        binding.goPhonePswLogin.setOnClickListener {
            if (binding.goPhonePswLogin?.text == "验证码登录") {
                binding.goPhonePswLogin?.text = "密码登录"
                binding.phoneCodeEdit?.setText("")
                binding.phoneCodeEdit?.hint = "请输入验证码"
                binding.phoneCodeEdit.setMaxLength(50)
                binding.phoneCodeTxt.visibility = View.VISIBLE

            } else {
                binding.goPhonePswLogin?.text = "验证码登录"
                binding.phoneCodeEdit?.setText("")
                binding.phoneCodeEdit?.hint = "请输入密码"
                binding.phoneCodeEdit.setMaxLength(6)
                binding.phoneCodeTxt.visibility = View.GONE
            }
        }

        binding.phoneEdit.setListener { s, input ->
            this.name = input
        }

        binding.phoneCodeEdit.setListener { s, input ->
            this.psw = input
        }

        binding.phoneCodeTxt.setOnClickListener {
            user?.validate("${this.adCode}${this.name}")
            binding.phoneCodeTxt?.isEnabled = false
            binding.phoneCodeTxt?.setTextColor(color(com.fanji.android.resource.R.color.disable))
            binding.phoneCodeEdit?.setText("")
            binding.phoneCodeEdit?.hint = "请输入验证码"
            FJDialog.loading(this)
        }

        binding.loginBtn.setOnClickListener {
            binding.phoneEdit?.hide()
            if (binding.goPhonePswLogin?.text == "密码登录") {
                this.code = this.psw
                user?.codeLogin(
                    this.name, this.psw, this.adCode
                )
            } else {
                user?.login(
                    this.name,
                    this.psw, positionData!!.gson,
                    deviceData!!.gson
                )
            }
        }

        binding.protocolUser.setOnClickListener {
//            openUrl(BuildConfig.PRIVACY_AGREEMENT, getString(R.string.user_protocol))
            openUrl(
                "http://192.168.1.11:8098/static/protocol/user_protocol.txt",
                getString(R.string.user_protocol)
            )

        }

        binding.protocolPrivacy.setOnClickListener {
//            openUrl(BuildConfig.USE_AGREEMENT, getString(R.string.protect_protocol))
            openUrl(
                "http://192.168.1.11:8098/static/protocol/protect_protocol.txt",
                getString(R.string.protect_protocol)
            )

        }
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
            binding.phoneCodeTxt?.isEnabled = true
            binding.phoneCodeTxt?.setTextColor(color(com.fanji.android.ui.R.color.blue))
        })
        user?.login?.observe(this, Observer {
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
                startActivity(Intent(this, MainActivity::class.java))
                LogUtil.e("-----go main")
            }
        })

        user?.codeLogin?.observe(this, Observer {
            LogUtil.e("codeLogin------>>>>>it", it)
            FJDialog.cancelDialog()
            if (it.code != HTTP_OK) {
                val code = it.code
                if (code == USER_NOT_EXIST) {
                    setShowPsw()
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

        FJEvent.get()
            .with(LOGIN_WECHAT, String::class.java)
            .observes(this, Observer {
                LogUtil.e("---jsd---", "wechat------>>>>>it", it.toString())
                val msg = handler.obtainMessage()
                msg.what = 1
                msg.obj = it
                handler.sendMessage(msg)
            })
    }

    private fun setShowPsw() {
        binding.phoneLoginL?.visibility = View.GONE
        binding.setPswL?.visibility = View.VISIBLE
        this.code = this.psw
        this.psw = ""
        setPswLogin()
        binding.setPswPhone?.text = this.name
    }

    private fun setPswLogin() {
        binding.phonePsw?.setListener { s, input ->
            this.psw = input
            validateSetPsw()
        }
        binding.phoneRePsw?.setListener { s, input ->
            this.rePsw = input
            validateSetPsw()
        }
        binding.sureLogin?.setOnClickListener {
            user?.reg(
                this.name, this.psw, this.code, positionData!!.gson,
                deviceData!!.gson, adCode = this.adCode
            )
            FJDialog.loading(this)
        }
        validateSetPsw()
    }

    override fun onResume() {
        super.onResume()
        ValidateCodeUtil.getInstance().countDownSecond(this)
    }

    override fun onStop() {
        super.onStop()
        ValidateCodeUtil.getInstance().cancel()
    }

    override fun onCountDown(code: Int) {
        if (code == 0) {
            binding.phoneCodeTxt?.text = getString(R.string.resend)
            binding.phoneCodeTxt?.isEnabled = true
            binding.phoneCodeTxt?.setTextColor(color(com.fanji.android.resource.R.color.disable))
            return
        }
        binding.phoneCodeTxt?.text =
            "${code}s后${getString(R.string.resend)}"
    }

    private fun validate() {
        if (binding.loginBtn == null) return
        binding.loginBtn?.isEnabled = binding.phoneEdit?.text?.trim().toString().isNotEmpty()
        if (!TextUtils.isEmpty(this.name)) {
            if (binding.goPhonePswLogin?.text == "密码登录") {
                binding.phoneCodeTxt?.text = "获取验证码"
//                phoneCodeTxt?.setDrawableRight(R.drawable.alpha)
                binding.phoneCodeEdit?.setMaxLength(6)
                if (this.name.length > 7) {
                    binding.phoneCodeTxt?.setTextColor(color(com.fanji.android.resource.R.color.disable))
                    binding.phoneCodeTxt?.isEnabled = true
                } else {
                    binding.phoneCodeTxt?.setTextColor(color(com.fanji.android.ui.R.color.gray))
                    binding.phoneCodeTxt?.isEnabled = false
                }
//                phoneCodeEdit?.setPswHide(true)
            } else {
                binding.phoneCodeTxt.text = ""
//                phoneCodeTxtTips?.visibility = View.GONE
//                phoneCodeTxt?.setDrawableRight(R.drawable.eye)
                binding.phoneCodeEdit?.setMaxLength(50)
//                phoneCodeEdit?.setPswHide(false)
            }
            binding.phoneCodeTxt?.visibility = View.VISIBLE

            if (!TextUtils.isEmpty(this.psw)) {
                binding.loginBtn.normalColor = com.fanji.android.ui.R.color.blackLightMiddle
                binding.loginBtn?.setTextColor(color(com.fanji.android.ui.R.color.font))
            } else {
                binding.loginBtn.normalColor = com.fanji.android.ui.R.color.blue
                binding.loginBtn?.setTextColor(color(com.fanji.android.ui.R.color.fontLight))
            }
        } else {
            binding.phoneCodeTxt?.visibility = View.GONE
            binding.phoneCodeTxt?.isEnabled = false
//            phoneCodeTxtTips?.visibility = View.GONE
            binding.loginBtn.normalColor = com.fanji.android.ui.R.color.blue
            binding.loginBtn?.setTextColor(color(com.fanji.android.ui.R.color.fontLight))
        }
    }

    private fun validateSetPsw() {
        if (!TextUtils.isEmpty(this.psw) && !TextUtils.isEmpty(this.rePsw)) {
            if (this.psw == this.rePsw) {
                binding.phoneRePswTips.visibility = View.GONE
                binding.sureLogin?.isEnabled = true
                binding.sureLogin?.normalColor = com.fanji.android.ui.R.color.blackLightMiddle
                binding.sureLogin?.setColor(color(com.fanji.android.ui.R.color.font))
            } else {
                binding.phoneRePswTips.visibility = View.VISIBLE
            }
        } else {
            binding.phoneRePswTips.visibility = View.GONE
            binding.sureLogin?.isEnabled = false
            binding.sureLogin?.normalColor = com.fanji.android.ui.R.color.gray
            binding.sureLogin?.setColor(color(com.fanji.android.ui.R.color.fontLight))
        }
    }

    override fun show(height: Int) {
        binding.userR?.visibility = View.GONE
    }

    override fun hide(height: Int) {
        binding.userR?.visibility = View.VISIBLE
    }
}