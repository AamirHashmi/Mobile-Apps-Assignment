package com.example.mobile_apps_assignment

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ShoppingList (val id: String? = null, val name: String? = null, val ingredients: MutableList<String>? = null) : Parcelable