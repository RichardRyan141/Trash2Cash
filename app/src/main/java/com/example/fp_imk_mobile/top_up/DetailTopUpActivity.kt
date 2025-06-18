package com.example.fp_imk_mobile.top_up

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fp_imk_mobile.CategorySessionManager
import com.example.fp_imk_mobile.HomepageActivity
import com.example.fp_imk_mobile.TransactionSessionManager
import com.example.fp_imk_mobile.data.Category
import com.example.fp_imk_mobile.data.Transaction
import com.example.fp_imk_mobile.data.TransactionDetail
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.round

class DetailTopUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var transfer = intent.getParcelableExtra<Transaction>("topUpDetail") ?: Transaction(nominal=-1)

        setContent {
            DetailTopUpScreen(transfer)
        }
    }
}

data class displayDetailTransaksi(val nama: String, val berat: Double, val hargaPerKG: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopUpScreen(transfer: Transaction) {
    var allCategories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var addedDetails by remember { mutableStateOf<List<TransactionDetail>>(emptyList()) }
    var displayDetails by remember { mutableStateOf<List<displayDetailTransaksi>>(emptyList()) }

    val sumber = transfer.sumber
    val nominal = transfer.nominal
    val waktu = transfer.waktu
    val noRef = transfer.noRef

    val context = LocalContext.current
    val displayTime = if (waktu.isNotBlank()) {
        waktu
    } else {
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        formatter.format(Date())
    }

    LaunchedEffect(transfer.noRef) {
        CategorySessionManager.getCategoryList (
            onSuccess = { allCategories = it },
            onError = { error -> Log.e("FirebaseError", "Error fetching categories", error.toException())}
        )
        TransactionSessionManager.getDetailsFor(transfer.noRef) {
            addedDetails = it
            displayDetails = listOf()

            for (item in addedDetails) {
                val matchedCategory = allCategories.find { it.id == item.category_id }

                if (matchedCategory != null) {
                    displayDetails = displayDetails + displayDetailTransaksi(
                        nama = matchedCategory.namaKategori,
                        berat = item.berat,
                        hargaPerKG = matchedCategory.hargaPerKg
                    )
                } else {
                    Log.w("DataWarning", "Category with ID ${item.category_id} not found")
                }
            }

        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Detail Top Up",
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    (context as? ComponentActivity)?.finish()
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

        Box(
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.CenterHorizontally)
                .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Rp%,d".format(nominal),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = displayTime,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.CenterHorizontally)
                .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sumber :",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = sumber,
                    fontSize = 20.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }

        KategoriTable(displayDetails)

        Column(
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.CenterHorizontally)
                .border(1.dp, Color.Gray, RoundedCornerShape(24.dp))
                .background(Color.White, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Text(
                text = "Nomor Referensi",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = noRef,
                fontSize = 20.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun KategoriTable(kategoriList: List<displayDetailTransaksi>) {
    val total = kategoriList.sumOf { it.berat * it.hargaPerKG }

    Column(
        modifier = Modifier
            .padding(12.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TableRow(
            listOf("No", "Nama Kategori", "Berat", "Harga/kg (Rp)", "Total (Rp)"),
            isHeader = true
        )

        kategoriList.forEachIndexed { index, kategori ->
            val totalPerItem = kategori.berat * kategori.hargaPerKG
            TableRow(
                listOf(
                    "${index + 1}",
                    kategori.nama,
                    "${kategori.berat} kg",
                    "%,d".format(kategori.hargaPerKG),
                    "%,d".format(totalPerItem.toInt())
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Total",
                modifier = Modifier
                    .weight(3f)
                    .padding(8.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Rp%,d".format(total.toInt()),
                modifier = Modifier
                    .weight(2f)
                    .padding(8.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TableRow(cells: List<String>, isHeader: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth()) {
        cells.forEachIndexed { index, cell ->
            Text(
                text = cell,
                modifier = Modifier
                    .weight(
                        when (index) {
                            0 -> 0.5f
                            1 -> 1.5f
                            else -> 1f
                        }
                    )
                    .padding(8.dp),
                fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
