package com.app.legend.ruminasu.beans

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


data class Comic(var book:String?,var title:String?,var path:String?,var hide:Int,var id:Int) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(book)
        parcel.writeString(title)
        parcel.writeString(path)
        parcel.writeInt(hide)
        parcel.writeInt(id)
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