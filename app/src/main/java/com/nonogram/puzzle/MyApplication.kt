package com.nonogram.puzzle

import android.app.Application
import android.os.Handler
import android.os.Looper
import com.nonogram.puzzle.data.db.AppDatabase
import com.nonogram.puzzle.utils.AdManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MyApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        // Defer Mobile Ads so Application.onCreate returns quickly (reduces startup ANRs).
        Handler(Looper.getMainLooper()).post {
            AdManager.initialize(this@MyApplication)
        }
        // Open Room off the main thread so HomeViewModel does not pay first-open SQLite cost on UI.
        applicationScope.launch(Dispatchers.IO) {
            AppDatabase.getDatabase(this@MyApplication)
        }
    }
}
