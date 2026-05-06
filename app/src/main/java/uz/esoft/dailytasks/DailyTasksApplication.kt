package uz.esoft.dailytasks

import android.app.Application
import uz.esoft.dailytasks.di.AppContainer

class DailyTasksApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

