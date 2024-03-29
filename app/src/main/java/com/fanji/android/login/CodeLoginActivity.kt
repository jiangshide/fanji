package com.fanji.android.login

import android.content.Intent
import android.os.Bundle
import com.fanji.android.R
import com.fanji.android.databinding.ActivityCodeLoginBinding
import com.fanji.android.resource.vm.user.UserVM
import com.fanji.android.ui.base.BaseActivity

/**
 * @author: jiangshide
 * @date: 2023/10/7
 * @email: 18311271399@163.com
 * @description:
 */
class CodeLoginActivity : BaseActivity<ActivityCodeLoginBinding>() {
    override fun getViewBinding() = initView(
        ActivityCodeLoginBinding.inflate(layoutInflater),
        title = "欢迎登录",
        rightBtn = "一键登录", rightCallClazz = LoginActivity::class.java
    )

    var user: UserVM? = create(UserVM::class.java)
    private var thirdLogin: ThirdLogin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        thirdLogin = ThirdLogin(this, user!!)

        binding.icon.load("", com.fanji.android.resource.R.mipmap.default_user)
        binding.phone.setListener { s, input ->

        }
        binding.code.setListener { s, input ->

        }
        binding.validateCode.setOnClickListener {

        }
        binding.login.setOnClickListener {

        }
        binding.checkedProtocol.setOnCheckedChangeListener { buttonView, isChecked ->

        }
        binding.userProtocol.setOnClickListener {
            openUrl(
                "http://192.168.1.11:8098/static/protocol/user_protocol.txt",
                getString(R.string.user_protocol)
            )
        }
        binding.privacyProtocol.setOnClickListener {
            openUrl(
                "http://192.168.1.11:8098/static/protocol/protect_protocol.txt",
                getString(R.string.protect_protocol)
            )
        }
        thirdLogin?.setListener(this, binding.wxLogin, binding.qqLogin)
        binding.pswLogin.setOnClickListener {
            activity(PswLoginActivity::class.java)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        thirdLogin?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}