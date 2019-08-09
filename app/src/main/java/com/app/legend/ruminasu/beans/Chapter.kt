package com.app.legend.ruminasu.beans

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 章节 名称 顺序 归属类别 是否已阅读 首页图
 */
@Parcelize
data class Chapter(var name:String?,var order:Int,var type:String?,var read:Int,var book:String?,var id:Int) :
    Parcelable {

}