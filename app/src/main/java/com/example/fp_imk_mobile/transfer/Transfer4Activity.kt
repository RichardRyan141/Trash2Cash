package com.example.fp_imk_mobile.transfer

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fp_imk_mobile.R
import com.example.fp_imk_mobile.data.Transaction
import com.example.fp_imk_mobile.data.User
import com.example.fp_imk_mobile.data.getUserData
import com.example.fp_imk_mobile.data.getUserTransactionList
import com.example.fp_imk_mobile.dummyTransaction
import com.example.fp_imk_mobile.saveStringLocally
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Transfer4Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val selectedWallet = intent.getStringExtra("selectedWallet") ?: "Unknown"
        var nama = intent.getStringExtra("Nama") ?: ""
        var noTelp = intent.getStringExtra("NoTelp") ?: ""

        setContent {
            Transfer4Screen(selectedWallet, nama, noTelp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Transfer4Screen(selectedWallet: String, nama: String, noTelp: String) {
    val context = LocalContext.current
    var nominal by remember { mutableStateOf("") }
    var pesan by remember { mutableStateOf("") }

    fun fetchBalanceAndConfirm(onResult: (Boolean) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: ""
        if (uid.isEmpty()) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            onResult(false)
            return
        }

        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("balance")
        dbRef.get().addOnSuccessListener { snapshot ->
            val balance = snapshot.getValue(Long::class.java) ?: 0L
            val transferAmount = nominal.toLongOrNull() ?: 0L

            if (transferAmount <= balance) {
                onResult(true)
            } else {
                Toast.makeText(context, "Saldo tidak mencukupi", Toast.LENGTH_SHORT).show()
                onResult(false)
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Gagal mengambil saldo", Toast.LENGTH_SHORT).show()
            onResult(false)
        }
    }


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
                            .size(48.dp)
                    )

                    Column(
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = nama,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Text(
                            text = noTelp,
                            color = Color.Black,
                            fontSize = 16.sp
                        )
                    }
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
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                .background(Color.White, RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth() // only fill width, not height
            ) {
                OutlinedTextField(
                    value = nominal,
                    onValueChange = { input ->
                        nominal = input.filter { it.isDigit() }
                    },
                    label = { Text("Masukkan nominal transfer") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = pesan,
                    onValueChange = { pesan = it },
                    label = { Text("Masukkan pesan (opsional)") },
                    modifier = Modifier.fillMaxWidth()
                )
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
                    fetchBalanceAndConfirm { isSufficient ->
                        if (isSufficient) {
                            val intent = Intent(context, Transfer5Activity::class.java)
                            intent.putExtra("selectedWallet", selectedWallet)
                            intent.putExtra("Nama", nama)
                            intent.putExtra("NoTelp", noTelp)
                            intent.putExtra("Nominal", nominal)
                            intent.putExtra("Pesan", pesan)
                            context.startActivity(intent)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Konfirmasi",
                    fontSize = 24.sp
                )
            }
        }
    }
}