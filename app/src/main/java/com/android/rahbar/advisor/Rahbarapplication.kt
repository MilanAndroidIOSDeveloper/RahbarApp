package com.android.rahbar.advisor

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Rahbarapplication : Application() {


    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        FirebaseApp.initializeApp(appContext);

    }

    companion object {
        lateinit var appContext: Context
    }


}