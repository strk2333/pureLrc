package com.strk2333.purelrc.di.component

import android.content.Context
import android.content.SharedPreferences
import com.strk2333.purelrc.Application
import com.strk2333.purelrc.di.module.ApplicationModule

import com.strk2333.purelrc.di.module.DatabaseModule


import com.strk2333.purelrc.di.module.NetModule

import dagger.Component
import javax.inject.Singleton

@Singleton


@Component(modules = arrayOf(ApplicationModule::class,NetModule::class,DatabaseModule::class))




interface ApplicationComponent {
    fun app(): Application

    fun context(): Context

    fun preferences(): SharedPreferences
}
