package com.android.rahbar.advisor.course

import android.os.Parcel
import android.os.Parcelable

data class CourseResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Course>
)


data class Course(
    val id: Int = 0,
    val title: String = "",
    val url: String = "",
    val is_paid: Boolean = false,
    val price: String = "",
    val visible_instructors: List<Instructor> = listOf(),
    val image_125_H: String = "",
    val headline: String = "",
    val image_480x270: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "",
        mutableListOf<Instructor>().apply {
            parcel.readList(this, Instructor::class.java.classLoader)
        },
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(url)
        parcel.writeByte(if (is_paid) 1 else 0)
        parcel.writeString(price)
        parcel.writeList(visible_instructors)
        parcel.writeString(image_125_H)
        parcel.writeString(headline)
        parcel.writeString(image_480x270)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Course> {
        override fun createFromParcel(parcel: Parcel): Course {
            return Course(parcel)
        }

        override fun newArray(size: Int): Array<Course?> {
            return arrayOfNulls(size)
        }
    }
}


data class Instructor(
    val title: String = "",
    val name: String = "",
    val display_name: String = "",
    val job_title: String = "",
    val image_50x50: String = "",
    val image_100x100: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(name)
        parcel.writeString(display_name)
        parcel.writeString(job_title)
        parcel.writeString(image_50x50)
        parcel.writeString(image_100x100)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Instructor> {
        override fun createFromParcel(parcel: Parcel): Instructor {
            return Instructor(parcel)
        }

        override fun newArray(size: Int): Array<Instructor?> {
            return arrayOfNulls(size)
        }
    }
}
