package com.app.legend.ruminasu.utils

import android.app.Application
import android.content.Context

class RuminasuApp : Application() {



    override fun onCreate() {
        super.onCreate()

        context=applicationContext
    }


    companion object{

        lateinit var context:Context

    }


}