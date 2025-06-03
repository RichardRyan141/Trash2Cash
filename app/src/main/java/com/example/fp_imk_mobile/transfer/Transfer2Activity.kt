package com.example.fp_imk_mobile.transfer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fp_imk_mobile.R

class Transfer2Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val selectedWallet = intent.getStringExtra("selectedWallet") ?: "Unknown"

        setContent {
            Transfer2Screen(selectedWallet)
        }

    }
}

data class Account(val nama: String, val noTelp: String)

val dummyAccount = listOf(
    Account("Nama 1", "081123456789"),
    Account("Nama 2", "021987654321"),
    Account("Nama 3", "031838163172")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Transfer2Screen(selectedWallet: String) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
    ) {
        TopAppBar(
            title = {
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
                            .padding(end = 12.dp)
                            .size(32.dp)
                    )

                    Text(
                        text = selectedWallet,
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = {
                    context.startActivity(Intent(context, Transfer1Activity::class.java))
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
                .padding(16.dp)
                .fillMaxWidth()
                .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                .background(Color.White, RoundedCornerShape(12.dp))
                .clickable {
                    val intent = Intent(context, Transfer3Activity::class.java)
                    intent.putExtra("selectedWallet", selectedWallet)
                    context.startActivity(intent)
//                    (context as? ComponentActivity)?.finish()
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.new_acc_logo),
                    contentDescription = "Logo Transfer Akun Baru",
                    modifier = Modifier.size(48.dp)
                )

                Column(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = "Transfer ke tujuan baru",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Masukkan Nomor HP",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                .background(Color.White, RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = "Riwayat",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                dummyAccount.forEachIndexed { index, acc ->
                    AccountItem(
                        nama = acc.nama,
                        noTelp = acc.noTelp,
                        wallet = selectedWallet,
                        onClick = {
                            val intent = Intent(context, Transfer4Activity::class.java)
                            intent.putExtra("selectedWallet", selectedWallet)
                            intent.putExtra("Nama", acc.nama)
                            intent.putExtra("NoTelp", acc.noTelp)
                            context.startActivity(intent)
//                            (context as? ComponentActivity)?.finish()
                        }
                    )

                    if (index < dummyAccount.size - 1) {
                        Divider(
                            color = Color.LightGray,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AccountItem(
    nama: String,
    noTelp: String,
    wallet: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically

    ) {
        Image(
            painter = painterResource(
                id = when (wallet) {
                    "OVO" -> R.drawable.logo_ovo
                    "DANA" -> R.drawable.logo_dana
                    "GoPay" -> R.drawable.logo_gopay
                    "ShopeePay" -> R.drawable.logo_shopeepay
                    else -> R.drawable.ic_launcher_foreground
                }
            ),
            contentDescription = "Logo $wallet",
            modifier = Modifier
                .padding(end = 12.dp)
                .size(48.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = nama,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = noTelp,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}
