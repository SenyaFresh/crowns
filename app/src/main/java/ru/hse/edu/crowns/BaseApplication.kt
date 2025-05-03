package ru.hse.edu.crowns

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.hse.edu.common.Core
import ru.hse.edu.common.CoreProvider
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication: Application() {

    @Inject
    lateinit var coreProvider: CoreProvider

    override fun onCreate() {
        super.onCreate()
        Core.init(coreProvider)
    }

}