package com.app.legend.ruminasu.activityes

import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.app.legend.ruminasu.utils.Database
import android.provider.MediaStore
import android.content.ContentResolver



abstract class BaseActivity : AppCompatActivity(){

    protected lateinit var database: Database


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        database= Database.getDefault(this)

    }

}