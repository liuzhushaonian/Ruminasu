package com.app.legend.ruminasu.beans

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class History(var id:Int,var comicId:Int,var chapterId:Int,var page:Int) :Parcelable{
}