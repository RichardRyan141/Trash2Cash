package com.example.fp_imk_mobile

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fp_imk_mobile.data.Transaction
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TransactionHistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val transactionList: List<Transaction> = intent.getParcelableArrayListExtra<Transaction>("transactionList") ?: emptyList()

        setContent {
            TransactionHistoryScreen(transactionList)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(transactionList: List<Transaction>) {
    val context = LocalContext.current
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var masukChecked by remember { mutableStateOf(true) }
    var keluarChecked by remember { mutableStateOf(true) }

    val calendar = Calendar.getInstance()

    val startDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            startDate = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val endDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            endDate = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
    val filteredTransactions = remember(transactionList, masukChecked, keluarChecked) {
        transactionList
            .filter { transaction ->
                (masukChecked && transaction.masuk) || (keluarChecked && !transaction.masuk)
            }
            .sortedByDescending { sdf.parse(it.waktu) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Riwayat Transaksi",
                        color = Color.Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        val intent = Intent(context, HomepageActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            )
        },
        containerColor = Color(0xFFF0F0F0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF0F0F0))
        ) {
//            Text(
//                text = "Filter Tanggal",
//                color = Color.Black,
//                fontSize = 24.sp,
//                fontWeight = FontWeight.SemiBold,
//                modifier = Modifier.padding(top = 12.dp, start = 12.dp)
//            )
//
//            Row(modifier = Modifier.padding(top = 12.dp)) {
//                Box(
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
//                        .background(Color.White, RoundedCornerShape(12.dp))
//                        .padding(8.dp)
//                ) {
//                    TextButton(onClick = { startDatePickerDialog.show() }) {
//                        Text(
//                            text = if (startDate.isEmpty()) "Pilih Tanggal Awal" else "Awal: $startDate",
//                            fontSize = 14.sp
//                        )
//                    }
//                }
//
//                Spacer(modifier = Modifier.width(8.dp))
//
//                Box(
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
//                        .background(Color.White, RoundedCornerShape(12.dp))
//                        .padding(8.dp)
//                ) {
//                    TextButton(onClick = { endDatePickerDialog.show() }) {
//                        Text(
//                            text = if (endDate.isEmpty()) "Pilih Tanggal Akhir" else "Akhir: $endDate",
//                            fontSize = 14.sp
//                        )
//                    }
//                }
//            }

            Text(
                text = "Jenis Transaksi",
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 12.dp, start = 12.dp)
            )

            Row(modifier = Modifier.padding(top = 12.dp)) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = masukChecked,
                            onCheckedChange = { masukChecked = it }
                        )
                        Text("Transaksi Masuk")
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = keluarChecked,
                            onCheckedChange = { keluarChecked = it }
                        )
                        Text("Transfer Keluar")
                    }
                }
            }

            Text(
                text = "Daftar Transaksi",
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 12.dp, start = 12.dp)
            )

            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                if (filteredTransactions.isEmpty()) {
                    Text("Tidak ada transaksi yang sesuai filter.", color = Color.Gray)
                } else {
                    Column {
                        filteredTransactions.forEachIndexed { index, transaction ->
                            TransactionItem(transaction)

                            if (index < filteredTransactions.size - 1) {
                                Divider(
                                    color = Color.LightGray,
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
