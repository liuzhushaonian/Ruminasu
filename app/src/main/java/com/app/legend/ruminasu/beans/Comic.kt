package com.app.legend.ruminasu.beans


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Comic(var book:String,var title:String,var path:String,var hide:Int,var id:Int,var zip:Int,var isExist:Boolean) : Parcelable