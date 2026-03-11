package uk.ac.tees.mad.meetmeds

import android.app.Application
import uk.ac.tees.mad.meetmeds.di.AppContainer

class MeetMedsApplication : Application() {

    // Global access point for all manually wired dependencies
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}