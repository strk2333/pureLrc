package com.strk2333.purelrc.splash

import com.strk2333.purelrc.main.MainActivity
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.os.Bundle
import com.strk2333.purelrc.base.BaseActivity


class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}