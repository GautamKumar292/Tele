package com.saiesh.tele.app

import android.app.Application

class TeleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        System.loadLibrary("tdjni")
    }
}
