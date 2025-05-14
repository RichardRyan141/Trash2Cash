package com.example.fp_imk_mobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class NotificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotificationScreen()
        }
    }
}

data class Notification(val text: String, val hasRead: Boolean, val date: String)

val dummyNotifications = listOf(
    Notification("Saldo berhasil ditambahkan ke akun Anda", hasRead = false, date = "13 Mei 2025"),
    Notification("Transaksi berhasil diproses dan tercatat di histori Anda", hasRead = true, date = "12 Mei 2025"),
    Notification("Penarikan sedang diproses dan akan masuk dalam 2 hari kerja", hasRead = false, date = "11 Mei 2025"),
    Notification("Profil Anda berhasil diperbarui dengan informasi terbaru", hasRead = true, date = "10 Mei 2025"),
    Notification("Sampahmu telah dijemput oleh kurir dan sedang diproses", hasRead = false, date = "09 Mei 2025")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Notifikasi",
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

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(dummyNotifications.size) { index ->
                val notification = dummyNotifications[index]
                NotificationItem(
                    text = notification.text,
                    hasRead = notification.hasRead,
                    date = notification.date
                )

                if (index < dummyNotifications.size - 1) {
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

@Composable
fun NotificationItem(
    text: String,
    hasRead: Boolean,
    date: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (!hasRead) FontWeight.Bold else FontWeight.Normal,
            color = Color.Black,
            maxLines = 3,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )

        Text(
            text = date,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.Top)
        )
    }
}
