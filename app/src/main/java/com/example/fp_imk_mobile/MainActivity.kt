package com.example.fp_imk_mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.fp_imk_mobile.login_register.LoginActivity
import com.example.fp_imk_mobile.login_register.RegisterActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val database = FirebaseDatabase.getInstance()
    val usersRef = database.getReference("users")

    usersRef.get()
        .addOnSuccessListener {
            Log.d("FirebaseTest", "Fetched users: ${it.childrenCount}")
        }
        .addOnFailureListener {
            Log.e("FirebaseTest", "Simple query failed", it)
        }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.intro_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text = "Trash2Cash",
                fontSize = 64.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(450.dp))

            Text(
                text = "Login",
                fontSize = 34.sp,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colorResource(id = R.color.login_button_bg))
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        context.startActivity(Intent(context, LoginActivity::class.java))
                    }
                    .padding(vertical = 16.dp)
            )

            Spacer(modifier = Modifier.height(64.dp))

            Text(
                text = "Register",
                fontSize = 34.sp,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colorResource(id = R.color.register_button_bg))
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        context.startActivity(Intent(context, RegisterActivity::class.java))
                    }
                    .padding(vertical = 16.dp)
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
