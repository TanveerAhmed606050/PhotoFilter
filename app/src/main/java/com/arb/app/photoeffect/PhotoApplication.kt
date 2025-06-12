package com.arb.app.photoeffect

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PhotoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}