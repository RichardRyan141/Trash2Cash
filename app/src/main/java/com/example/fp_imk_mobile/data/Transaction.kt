package com.example.fp_imk_mobile.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
data class Transaction(
    val masuk: Boolean = false,
    val waktu: String = getCurrentTimestamp(),
    val sumber: String = "",
    val tujuan: String = "",
    val nominal: Int = 0,
    val pesan: String = "",
    val user_id: String = "",
    val noRef: String = ""
) : Parcelable


fun getCurrentTimestamp(): String {
    val formatter = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date())
}
