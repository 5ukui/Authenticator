package com.sukui.authr

import android.app.Application
import com.sukui.authr.di.MauthDI
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class authr : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@authr)

            modules(MauthDI.all)
        }
    }
}