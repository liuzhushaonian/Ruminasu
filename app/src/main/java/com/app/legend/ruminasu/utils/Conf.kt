package com.app.legend.ruminasu.utils

import android.content.Context
import android.os.Environment

object Conf{

   public var PATH:String=Environment.getExternalStorageDirectory().absolutePath+"/release";

    val HIDE:Int=1

    val SHOW=-1

   private val BOOK="comic_book"

    val update="com.legend.ruminasu.update_ui"

    val progress="com.legend.ruminasu.progress"


}