package com.fanji.android.login

import android.os.Bundle
import com.fanji.android.databinding.ActivityCodeLoginBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}