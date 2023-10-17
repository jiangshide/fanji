package com.fanji.android

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fanji.android.permission.FJPermission
import com.fanji.android.permission.OnPermissionCallback
import com.fanji.android.resource.Resource

class SplashActivity : AppCompatActivity(), OnPermissionCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
//        startActivity(Intent(this, MainActivity::class.java))
        FJPermission.with(this).permission(permissions.toMutableList()).request(this)
    }

    private val permissions = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CONTACTS,
//        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
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

const val HUAEI_ADS_PROTOCOL = "huaweiAdsProtocol"