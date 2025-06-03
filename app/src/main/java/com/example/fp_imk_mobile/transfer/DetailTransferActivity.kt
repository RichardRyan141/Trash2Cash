package com.example.fp_imk_mobile.transfer

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fp_imk_mobile.HomepageActivity
import com.example.fp_imk_mobile.R
import com.example.fp_imk_mobile.data.Transaction
import com.example.fp_imk_mobile.getStringLocally
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailTransferActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var transfer = intent.getParcelableExtra<Transaction>("transferDetail") ?: Transaction(
            false,
            "",
            "",
            "",
            0,
            "",
            ""
        )
        val username = getStringLocally(this, "username") ?: "Unknown"
        var from = intent.getStringExtra("from") ?: ""

        setContent {
            DetailTransferScreen(transfer, username, from)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTransferScreen(
    transfer: Transaction,
    username: String,
    from: String
) {
    val selectedWallet = transfer.tujuan.split(' ')[0]
    val noTelp = transfer.tujuan.split(' ')[1]
    val nama = transfer.tujuan.split(' ').drop(2).joinToString(" ")
    val nominal = transfer.nominal
    val pesan = transfer.pesan
    val waktu = transfer.waktu
    val noRef = transfer.noRef

    val context = LocalContext.current
    val displayTime = if (waktu.isNotBlank()) {
        waktu
    } else {
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        formatter.format(Date())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Detail Transfer",
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    if (from == "TransferHistory") {
                        (context as? ComponentActivity)?.finish()
                    } else {
                        val intent = Intent(context, HomepageActivity::class.java)
                        context.startActivity(intent)
                        (context as? ComponentActivity)?.finish()
                    }
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
                    text = "Dikirim oleh :",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = username,
                    fontSize = 20.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }

            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tujuan :",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.weight(1f))
                Column(modifier = Modifier.padding(16.dp))
                {
                    Row(modifier = Modifier.padding(vertical = 8.dp))
                    {
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
                                .padding(end = 12.dp)
                                .size(40.dp)
                        )
                        Column {
                            Text(
                                text = nama,
                                fontSize = 24.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = noTelp,
                                fontSize = 16.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            if (pesan.isNotBlank()) {
                Row(
                    modifier = Modifier.padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pesan :",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = pesan,
                        fontSize = 20.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

        }

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