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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fp_imk_mobile.HomepageActivity
import com.example.fp_imk_mobile.R

class Transfer1Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Transfer1Screen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Transfer1Screen() {
    val context = LocalContext.current
    val options = listOf("OVO", "DANA", "GoPay", "ShopeePay")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Pilih E-Wallet",
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    context.startActivity(Intent(context, HomepageActivity::class.java))
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
                .padding(16.dp)
        ) {
            Column {
                options.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .clickable {
                                val intent = Intent(context, Transfer2Activity::class.java)
                                intent.putExtra("selectedWallet", option)
                                context.startActivity(intent)
//                                (context as? ComponentActivity)?.finish()
                            }
                    ) {
                        Image(
                            painter = painterResource(id = when (option) {
                                "OVO" -> R.drawable.logo_ovo
                                "DANA" -> R.drawable.logo_dana
                                "GoPay" -> R.drawable.logo_gopay
                                "ShopeePay" -> R.drawable.logo_shopeepay
                                else -> R.drawable.ic_launcher_foreground
                            }),
                            contentDescription = "Logo $option",
                            modifier = Modifier.size(32.dp)
                        )

                        Text(
                            text = option,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}