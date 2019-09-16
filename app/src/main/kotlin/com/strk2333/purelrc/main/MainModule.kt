package com.strk2333.purelrc.main

import com.strk2333.purelrc.di.scope.ActivityScope
import dagger.Module
import dagger.Provides


@Module
class MainModule(private val view: IMainView) {

    @Provides
    @ActivityScope
    internal fun provideView(): IMainView {
        return view
    }
}