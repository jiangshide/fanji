package com.fanji.android.login

import android.content.Intent
import android.os.Bundle
import com.fanji.android.databinding.ActivityPswLoginBinding
import com.fanji.android.resource.vm.user.UserVM
import com.fanji.android.ui.base.BaseActivity

/**
 * @author: jiangshide
 * @date: 2023/10/8
 * @email: 18311271399@163.com
 * @description:
 */
class PswLoginActivity : BaseActivity<ActivityPswLoginBinding>() {
    override fun getViewBinding() = initView(
        ActivityPswLoginBinding.inflate(layoutInflater), title = "欢迎登录",
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
        binding.psw.setListener { s, input ->

        }
        binding.code.setListener { s, input ->

        }
        binding.validateCode.setOnClickListener {

        }
        binding.forgotPsw.setOnClickListener {
            activity(ForgetPswActivity::class.java)
        }
        binding.login.setOnClickListener {

        }
        binding.checkedProtocol.setOnCheckedChangeListener { buttonView, isChecked ->

        }
        binding.userProtocol.setOnClickListener {

        }
        binding.privacyProtocol.setOnClickListener {

        }
        thirdLogin?.setListener(this, binding.wxLogin, binding.qqLogin)
        binding.codeLogin.setOnClickListener {
            activity(LoginActivity::class.java)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        thirdLogin?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}