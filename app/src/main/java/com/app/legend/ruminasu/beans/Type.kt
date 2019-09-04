package com.app.legend.ruminasu.beans

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Type(var id:Int,var comicId:Int,var content:String): Parcelable