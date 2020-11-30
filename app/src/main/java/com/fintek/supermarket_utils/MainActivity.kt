package com.fintek.supermarket_utils

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fintek.upload_sdk.UploadService
import com.fintek.upload_sdk.UploadUtils
import com.fintek.upload_sdk.model.Config

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        UploadUtils.setConfig(
            Config(
            "", "Kota Emas", "http://106.14.161.98:8280/"
            )
        )
        startService(Intent(this, UploadService::class.java))
    }
}