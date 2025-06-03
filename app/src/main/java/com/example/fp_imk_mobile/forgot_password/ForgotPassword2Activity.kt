package com.example.fp_imk_mobile.forgot_password

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fp_imk_mobile.R
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ForgotPassword2Activity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val email = intent.getStringExtra("email") ?: "email@example.com"
        setContent {
            ForgotPassword2Screen(email)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OtpInputFields(codeFields: List<MutableState<String>>) {
    val focusRequesters = remember { List(6) { FocusRequester() } }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        codeFields.forEachIndexed { index, state ->
            OutlinedTextField(
                value = state.value,
                onValueChange = {
                    if (it.length <= 1 && it.all { char -> char.isDigit() }) {
                        state.value = it
                        if (it.isNotEmpty() && index < 5) {
                            focusRequesters[index + 1].requestFocus()
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .focusRequester(focusRequesters[index])
                    .onKeyEvent {
                        if (it.type == KeyEventType.KeyDown && it.key == Key.Backspace) {
                            if (state.value.isEmpty() && index > 0) {
                                codeFields[index - 1].value = ""
                                focusRequesters[index - 1].requestFocus()
                            }
                        }
                        false
                    },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = if (index == 5) ImeAction.Done else ImeAction.Next
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        focusRequesters.first().requestFocus()
    }
}


@Composable
fun ForgotPassword2Screen(email: String) {
    val context = LocalContext.current
    var timer by remember { mutableStateOf(60) }
    var isResendEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = timer) {
        if (timer > 0) {
            delay(1000)
            timer -= 1
        } else {
            isResendEnabled = true
        }
    }

    var codeFields = remember { List(6) { mutableStateOf("") } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Kode telah dikirimkan ke",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = email,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OtpInputFields(codeFields)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                timer = 60
                isResendEnabled = false
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            enabled = isResendEnabled,
            colors = buttonColors(
                containerColor = colorResource(id = R.color.resend_code_bg),
                contentColor = Color.Black
            )
        ) {
            Text(if (isResendEnabled) "Kirim Ulang Kode" else "Kirim Ulang Kode (${timer}s)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val code = codeFields.joinToString("") { it.value }
                val todayCode = SimpleDateFormat("ddMMyy", Locale.getDefault()).format(Date())

                if (code == todayCode) {
                    val intent = Intent(context, ForgotPassword3Activity::class.java).apply {
                        putExtra("email", email)
                    }
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Incorrect code $code supposed to be $todayCode", Toast.LENGTH_SHORT).show()
                }
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = buttonColors(
                containerColor = colorResource(id = R.color.green),
                contentColor = Color.White
            )
        ) {
            Text("Kirim Kode")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                (context as? Activity)?.finish()
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = buttonColors(
                containerColor = colorResource(id = R.color.back_button_bg),
                contentColor = Color.Black
            )
        ) {
            Text("Kembali")
        }

        Spacer(modifier = Modifier.height(280.dp))
    }
}
