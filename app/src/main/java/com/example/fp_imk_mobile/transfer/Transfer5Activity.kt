package com.example.fp_imk_mobile.transfer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fp_imk_mobile.R
import com.example.fp_imk_mobile.TransactionSessionManager
import com.example.fp_imk_mobile.data.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Transfer5Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val selectedWallet = intent.getStringExtra("selectedWallet") ?: "Unknown"
        var nama = intent.getStringExtra("Nama") ?: ""
        var noTelp = intent.getStringExtra("NoTelp") ?: ""
        var nominal = intent.getStringExtra("Nominal") ?: ""
        var pesan = intent.getStringExtra("Pesan") ?: ""

        setContent {
            Transfer5Screen(selectedWallet, nama, noTelp, nominal, pesan)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Transfer5Screen(
    selectedWallet: String,
    nama: String,
    noTelp: String,
    nominalStr: String,
    pesan: String
) {
    var context = LocalContext.current
    val nominal = nominalStr.toIntOrNull() ?: 0

    fun addTransaction() {
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: return
        val transfer = Transaction(
            masuk = false,
            sumber = "Saldo",
            tujuan = "$selectedWallet $noTelp $nama",
            nominal = nominal,
            pesan = pesan,
            user_id = uid,
            noRef = ""
        )
        TransactionSessionManager.addTransaction(transfer) { success, message, transferID ->
            if (success) {
                val transfer = transfer.copy(noRef = transferID!!)
                val intent = Intent(context, DetailTransferActivity::class.java)
                intent.putExtra("transferDetail", transfer)
                context.startActivity(intent)
            } else {
                Toast.makeText(context, message ?: "Terjadi kesalahan", Toast.LENGTH_LONG).show()
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
                    text = "Konfirmasi Transfer",
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    val intent = Intent(context, Transfer4Activity::class.java)
                    intent.putExtra("selectedWallet", selectedWallet)
                    intent.putExtra("Nama", nama)
                    intent.putExtra("NoTelp", noTelp)
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

        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(modifier = Modifier.padding(bottom = 16.dp)) {
                    Text(
                        text = "Target",
                        fontSize = 24.sp,
                        modifier = Modifier.width(100.dp)
                    )
                    Text(
                        text = ":",
                        fontSize = 24.sp,
                        modifier = Modifier.width(12.dp)
                    )
                    Row {
                        Image(
                            painter = painterResource(
                                id = when (selectedWallet) {
                                    "OVO" -> R.drawable.logo_ovo
                                    "DANA" -> R.drawable.logo_dana
                                    "GoPay" -> R.drawable.logo_gopay
                                    "ShopeePay" -> R.drawable.logo_shopeepay
                                    else -> R.drawable.ic_launcher_foreground
                                }
                            ),
                            contentDescription = "Logo $selectedWallet",
                            modifier = Modifier
                                .size(27.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = selectedWallet,
                            fontSize = 24.sp
                        )
                    }
                }

                Row(modifier = Modifier.padding(vertical = 12.dp)) {
                    Text(
                        text = "Nama",
                        fontSize = 24.sp,
                        modifier = Modifier.width(100.dp)
                    )
                    Text(
                        text = ":",
                        fontSize = 24.sp,
                        modifier = Modifier.width(12.dp)
                    )
                    Text(
                        text = nama,
                        fontSize = 24.sp
                    )
                }

                Row(modifier = Modifier.padding(vertical = 12.dp)) {
                    Text(
                        text = "No. Telepon",
                        fontSize = 24.sp,
                        modifier = Modifier.width(100.dp)
                    )
                    Text(
                        text = ":",
                        fontSize = 24.sp,
                        modifier = Modifier.width(12.dp)
                    )
                    Text(
                        text = noTelp,
                        fontSize = 24.sp
                    )
                }

                Row(modifier = Modifier.padding(vertical = 12.dp)) {
                    Text(
                        text = "Nominal",
                        fontSize = 24.sp,
                        modifier = Modifier.width(100.dp)
                    )
                    Text(
                        text = ":",
                        fontSize = 24.sp,
                        modifier = Modifier.width(12.dp)
                    )
                    Text(
                        text = "Rp%,d".format(nominal),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (pesan.isNotBlank()) {
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            text = "Pesan",
                            fontSize = 24.sp,
                            modifier = Modifier.width(100.dp)
                        )
                        Text(
                            text = ":",
                            fontSize = 24.sp,
                            modifier = Modifier.width(12.dp)
                        )
                        Text(
                            text = pesan,
                            fontSize = 24.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .border(1.dp, Color.Gray)
                .background(Color.White)
        ) {
            Button(
                onClick = {
                    addTransaction()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Kirim",
                    fontSize = 24.sp
                )
            }
        }
    }
}