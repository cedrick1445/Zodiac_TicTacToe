package dev.cdrck.mdgtictoe.main

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dev.cdrck.mdgtictoe.BuildConfig
import timber.log.Timber

@HiltAndroidApp
class TTTApp: Application() {
	
	override fun onCreate() {

		super.onCreate()
		if (BuildConfig.DEBUG)  Timber.plant(Timber.DebugTree())
	}
}