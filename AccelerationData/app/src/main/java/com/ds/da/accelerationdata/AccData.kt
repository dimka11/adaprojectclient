package com.ds.da.accelerationdata

import android.os.Parcelable

import kotlinx.android.parcel.Parcelize

@Parcelize
data class AccData(val x: Float, val y: Float, val z: Float) : Parcelable {
}