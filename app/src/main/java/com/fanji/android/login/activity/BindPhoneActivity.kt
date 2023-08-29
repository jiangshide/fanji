package com.fanji.android.login.activity

import android.os.Bundle
import com.fanji.android.databinding.ActivityBindPhoneBinding
import com.fanji.android.resource.base.BaseActivity

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class BindPhoneActivity : BaseActivity<ActivityBindPhoneBinding>() {

    override fun getViewBinding() = ActivityBindPhoneBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}