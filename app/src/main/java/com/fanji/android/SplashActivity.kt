package com.fanji.android

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fanji.android.login.LoginActivity
import com.fanji.android.resource.Resource
import com.fanji.android.ui.permission.FJPermission
import com.fanji.android.ui.permission.OnPermissionCallback

class SplashActivity : AppCompatActivity(), OnPermissionCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        FJPermission.with(this).permission(permissions.toMutableList()).request(this)
    }

    private val permissions = listOf(
//        Manifest.permission.CAMERA,
//        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
//        Manifest.permission.MANAGE_EXTERNAL_STORAGE,
//        Manifest.permission.REQUEST_INSTALL_PACKAGES,
//        Manifest.permission.SYSTEM_ALERT_WINDOW,
//        Manifest.permission.NOTIFICATION_SERVICE,
//        Manifest.permission.WRITE_SETTINGS,
    )

    override fun onGranted(permissions: List<String?>?, all: Boolean) {
        if (Resource.user == null) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}