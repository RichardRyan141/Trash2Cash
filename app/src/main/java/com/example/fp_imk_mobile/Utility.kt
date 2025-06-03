package com.example.fp_imk_mobile

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fp_imk_mobile.data.Transaction
import com.example.fp_imk_mobile.top_up.DetailTopUpActivity
import com.example.fp_imk_mobile.transfer.DetailTransferActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.parcel.Parcelize

fun saveStringLocally(context: Context, tag: String, str: String) {
    val prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    prefs.edit().putString(tag, str).apply()
}

fun getStringLocally(context: Context, tag: String): String? {
    val prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    return prefs.getString(tag, null)
}

@Composable
fun TransactionItem(
    transaction: Transaction,
) {
    val context = LocalContext.current
    val isIncome = transaction.masuk
    val tanggal = transaction.waktu
    val party = if(isIncome) transaction.sumber else transaction.tujuan
    val nominal = transaction.nominal

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                if(isIncome) {
                    val intent = Intent(context, DetailTopUpActivity::class.java)
                    intent.putExtra("topUpDetail", transaction)
                    context.startActivity(intent)
                } else {
                    val intent = Intent(context, DetailTransferActivity::class.java)
                    intent.putExtra("transferDetail", transaction)
                    intent.putExtra("from", "TransferHistory")
                    context.startActivity(intent)
                }
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        Icon(
            imageVector = if (isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
            contentDescription = if (isIncome) "Income" else "Expense",
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = if (isIncome) Color(0xFFE0F7E9) else Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(4.dp),
            tint = if (isIncome) Color(0xFF2E7D32) else Color(0xFFC62828)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = tanggal,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = party,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }

        Text(
            text = if (isIncome) "Rp%,d".format(nominal) else "- Rp%,d".format(nominal),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isIncome) Color(0xFF2E7D32) else Color(0xFFC62828)
        )
        Spacer(modifier = Modifier.height(6.dp))
    }
}
