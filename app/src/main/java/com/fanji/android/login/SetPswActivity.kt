package com.fanji.android.login

import android.os.Bundle
import com.fanji.android.databinding.ActivitySetPswBinding
import com.fanji.android.ui.base.BaseActivity

/**
 * @author: jiangshide
 * @date: 2023/10/9
 * @email: 18311271399@163.com
 * @description:
 */
class SetPswActivity : BaseActivity<ActivitySetPswBinding>() {
    override fun getViewBinding() =
        initView(ActivitySetPswBinding.inflate(layoutInflater), title = "设置新密码")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.psw.setListener { s, input ->

        }
        binding.rePsw.setListener { s, input ->

        }
        binding.sure.setOnClickListener {

        }
    }
}