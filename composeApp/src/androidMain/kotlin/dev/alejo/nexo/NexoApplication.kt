package dev.alejo.nexo

import android.app.Application
import dev.alejo.nexo.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class NexoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@NexoApplication)
            androidLogger()
        }
    }
}