package com.strk2333.purelrc.main

import com.strk2333.purelrc.di.component.ApplicationComponent
import com.strk2333.purelrc.di.scope.ActivityScope
import dagger.Component


@ActivityScope
@Component(modules = arrayOf(MainModule::class), dependencies = arrayOf(ApplicationComponent::class))
interface MainComponent {
    fun inject(activity: MainActivity)
}