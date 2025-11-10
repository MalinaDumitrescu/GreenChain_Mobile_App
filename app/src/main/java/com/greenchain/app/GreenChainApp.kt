package com.greenchain.app

import android.app.Application
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.MapsInitializer.Renderer
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GreenChainApp : Application(), OnMapsSdkInitializedCallback {

    override fun onCreate() {
        super.onCreate()
        MapsInitializer.initialize(applicationContext, Renderer.LATEST, this)
    }

    override fun onMapsSdkInitialized(renderer: Renderer) {
        // In this sample, we don't need to do anything here, but this is where
        // you could handle different renderers if you needed to.
    }
}
