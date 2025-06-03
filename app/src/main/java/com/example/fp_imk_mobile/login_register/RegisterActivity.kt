package com.example.fp_imk_mobile.login_register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.example.fp_imk_mobile.HomepageActivity
import com.example.fp_imk_mobile.R
import com.example.fp_imk_mobile.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegisterScreen()
        }
    }
}

@Composable
fun RegisterScreen() {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var noTelp by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }
    var confirmPasswordVisible by remember{ mutableStateOf(false) }
    var tosChecked by remember { mutableStateOf(false) }
    var privacyChecked by remember { mutableStateOf(false) }

    fun isFormValid(): Boolean {
        val passwordValid = password.length >= 8 && password.any { it.isDigit() }
        val fieldsFilled = username.isNotBlank() && email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()
        val passwordsMatch = password == confirmPassword
        val phoneValid = noTelp.all { it.isDigit() } && noTelp.length >= 10
        return fieldsFilled && phoneValid && passwordsMatch && passwordValid && tosChecked && privacyChecked
    }

    fun registerUser() {
        val database = FirebaseDatabase.getInstance()
        val auth = FirebaseAuth.getInstance()
        val usersRef = database.getReference("users")

        if (!isFormValid()) {
            Toast.makeText(context, "Lengkapi semua data dan ceklis persetujuan", Toast.LENGTH_SHORT).show()
            return
        }

        usersRef.orderByChild("noTelp").equalTo(noTelp).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    Toast.makeText(context, "Nomor telepon sudah digunakan", Toast.LENGTH_SHORT).show()
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseUser = auth.currentUser
                                val uid = firebaseUser?.uid ?: return@addOnCompleteListener
                                val user = User(
                                    username = username,
                                    email = email,
                                    noTelp = noTelp,
                                    balance = 0
                                )

                                usersRef.child(uid).setValue(user)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                                        context.startActivity(Intent(context, HomepageActivity::class.java))
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Gagal menyimpan data user", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Log.d("Registrasi gagal", "${task.exception?.message}")
                                Toast.makeText(context, "Registrasi gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseQuery", "Query failed", e)
                Toast.makeText(context, "Gagal memeriksa nomor telepon: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Masukkan Username") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
        if (!username.isNotBlank()) {
            Text(
                text = "Masukkan username",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Masukkan Email") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
        if (!email.isNotBlank()) {
            Text(
                text = "Masukkan Email",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = noTelp,
            onValueChange = { noTelp = it },
            label = { Text("Masukkan Nomor Telepon") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
        if (!noTelp.isNotBlank()) {
            Text(
                text = "Masukkan Nomor Telepon",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Masukkan Password") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .clickable { passwordVisible = !passwordVisible }
                        .padding(end = 8.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
        if (password.length < 8 || !password.any { it.isDigit() }) {
            Text(
                text = "Password harus minimal 8 karakter dan mengandung angka",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Konfirmasi Password") },
            singleLine = true,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .clickable { passwordVisible = !passwordVisible }
                        .padding(end = 8.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
        if (password != confirmPassword) {
            Text(
                text = "Password harus sama",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = tosChecked,
                onCheckedChange = { tosChecked = it }
            )
            Text("Saya menyetujui Ketentuan Layanan")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = privacyChecked,
                onCheckedChange = { privacyChecked = it }
            )
            Text("Saya menyetujui Kebijakan Privasi")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                registerUser()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid()
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "atau Masuk ke akun yang dimiliki?",
            modifier = Modifier
                .clickable {
                    context.startActivity(Intent(context, LoginActivity::class.java))
                }
        )

        Spacer(modifier = Modifier.height(48.dp))
    }
}