package com.seniordialer

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.seniordialer.util.LocaleHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SeniorDialerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        LocaleHelper.syncFromPreferences(this)
        FirebaseApp.initializeApp(this)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    }
}
