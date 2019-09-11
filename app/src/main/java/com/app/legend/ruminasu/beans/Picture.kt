package com.app.legend.ruminasu.beans

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

/**
 * content的形成
 * zip下的zip
 * header.name
 *
 *
 *
 */
@Parcelize
data class Picture(var id:Int,var content:String,var order:Int,var chapter_id:Int,var zip:Int,var comic_title:String,var comic_path:String,var chapter_name:String):Parcelable {
}