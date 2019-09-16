package com.strk2333.purelrc

import com.strk2333.purelrc.utils.timber.CrashReportTree
import timber.log.Timber
//import com.crashlytics.android.Crashlytics
//import io.fabric.sdk.android.Fabric
//import com.crashlytics.android.core.CrashlyticsCore
import com.strk2333.purelrc.di.module.ApplicationModule
import com.strk2333.purelrc.di.component.ApplicationComponent
import com.strk2333.purelrc.di.component.DaggerApplicationComponent


class Application : android.app.Application() {

    override fun onCreate() {
        super.onCreate()

        //di
        component = DaggerApplicationComponent.builder().applicationModule(ApplicationModule(this)).build()

        //Crashlytics
//        val core = CrashlyticsCore.Builder()
//                .disabled(BuildConfig.DEBUG)
//                .build()
//
//        Fabric.with(this, Crashlytics.Builder().core(core).build())

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportTree())
        }
    }

    companion object {

        var component: ApplicationComponent? = null
            private set
    }

}

