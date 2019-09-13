package com.app.legend.ruminasu.utils

import android.content.Context
import android.os.Environment

object Conf{

   public var PATH:String=Environment.getExternalStorageState()+"/release";

    val HIDE:Int=1

    val SHOW=-1

   private val BOOK="comic_book"

    val update="com.legend.ruminasu.update_ui"

    val progress="com.legend.ruminasu.progress"

    const val SHARED="ruminasu_shared"

    const val VERTICAL=0x00100

    const val HORIZONTAL=0x00200

    const val READ_MODE="read_mode"

    const val TOUCH_MODE="touch_mode"

    const val TOUCH_LEFT=0x10020

    const val TOUCH_RIGHT=0x10021

    const val UP=0x210

    const val DOWN=0x326


}