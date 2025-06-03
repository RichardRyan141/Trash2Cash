package com.example.fp_imk_mobile.data

import android.os.Parcelable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
    val formatter = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
    return formatter.format(Date())
}

fun getUserTransactionList(
    user_id: String,
    onSuccess: (List<Transaction>) -> Unit,
    onError: (DatabaseError) -> Unit
) {
    val dbRef = FirebaseDatabase.getInstance().getReference("transactions")

    dbRef.orderByChild("user_id").equalTo(user_id)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Transaction>()
                for (child in snapshot.children) {
                    val transaction = child.getValue(Transaction::class.java)
                    if (transaction != null) {
                        list.add(transaction)
                    }
                }
                onSuccess(list)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error)
            }
        })
}