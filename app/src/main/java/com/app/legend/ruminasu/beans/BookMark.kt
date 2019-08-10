package com.app.legend.ruminasu.beans

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


data class BookMark(var id:Int,var comicId:Int,var chapterId:Int,var page:Int):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(comicId)
        parcel.writeInt(chapterId)
        parcel.writeInt(page)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookMark> {
        override fun createFromParcel(parcel: Parcel): BookMark {
            return BookMark(parcel)
        }

        override fun newArray(size: Int): Array<BookMark?> {
            return arrayOfNulls(size)
        }
    }

}