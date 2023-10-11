package com.fanji.android.login

import android.os.Bundle
import com.fanji.android.databinding.ActivityForgetPswBinding
import com.fanji.android.ui.base.BaseActivity

/**
 * @author: jiangshide
 * @date: 2023/10/9
 * @email: 18311271399@163.com
 * @description:
 */
class ForgetPswActivity : BaseActivity<ActivityForgetPswBinding>() {
    override fun getViewBinding() =
        initView(ActivityForgetPswBinding.inflate(layoutInflater), title = "验证手机号")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.phone.setListener { s, input ->

        }
        binding.code.setListener { s, input ->

        }
        binding.validateCode.setOnClickListener {

        }
        binding.next.setOnClickListener {
            activity(SetPswActivity::class.java)
        }
    }
}