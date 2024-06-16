package com.example.proplayer.data

import android.os.Parcel
import android.os.Parcelable


data class MediaFiles(
    val id:String,
    val titleName:String,
    val disPlaysName:String,
    val size:String,
    val path: String,
    val duration: Long,
    val dateAdded:String,
    val quality:String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(titleName)
        parcel.writeString(disPlaysName)
        parcel.writeString(size)
        parcel.writeString(path)
        parcel.writeLong(duration)
        parcel.writeString(dateAdded)
        parcel.writeString(quality)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MediaFiles> {
        override fun createFromParcel(parcel: Parcel): MediaFiles {
            return MediaFiles(parcel)
        }

        override fun newArray(size: Int): Array<MediaFiles?> {
            return arrayOfNulls(size)
        }
    }

}
