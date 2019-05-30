package com.app.legend.ruminasu.beans

import android.os.Parcel
import android.os.Parcelable

data class Comic(var book:String?,var title:String?,var chapter:Int,var pager:Int,var path:String?) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(book)
        parcel.writeString(title)
        parcel.writeInt(chapter)
        parcel.writeInt(pager)
        parcel.writeString(path)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Comic> {
        override fun createFromParcel(parcel: Parcel): Comic {
            return Comic(parcel)
        }

        override fun newArray(size: Int): Array<Comic?> {
            return arrayOfNulls(size)
        }
    }


}