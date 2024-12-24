package com.sukui.authenticator

import android.app.Application
import com.sukui.authenticator.di.MauthDI
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class authenticator : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@authenticator)

            modules(MauthDI.all)
        }
    }
}