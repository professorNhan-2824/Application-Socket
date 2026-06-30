package com.example.applicationsocket.ViewModel.LoginScreen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.applicationsocket.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen( comback: () -> Unit, getPassEmail: (String, String, Context) -> Unit) {
    var email = remember { mutableStateOf("") }
    var pass = remember { mutableStateOf("") }
    var isTextFieldEmpty by remember { mutableStateOf(true) }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF111111))
            .padding(10.dp)
            .verticalScroll(rememberScrollState())
            .imePadding(),
    ) {
        // Content 1
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 15.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    comback()
                },
                modifier = Modifier.size(50.dp),
                containerColor = Color.Black,
                contentColor = Color.White
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = "Floating action button"
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Content 2
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Vui lòng nhập Email !",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = email.value,
                    onValueChange = {
                        email.value = it
                    },
                    label = { Text("Email", color = Color(0xFFb4b4b4)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFF616161),
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Content 3
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Vui lòng nhập Pass !",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = pass.value,
                    onValueChange = {
                        pass.value = it
                        isTextFieldEmpty = it.isEmpty()
                    },
                    label = { Text("Password", color = Color(0xFFb4b4b4)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFF616161),
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
                )
            }
        }

        // Content 4
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val announce = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color(0xFF616161))) {
                    append("Cảm ơn bạn đã đồng ý với ")
                }
                withStyle(style = SpanStyle(color = Color(0xFFb4b4b4))) {
                    append("Điều khoản của chúng tôi ")
                }
                withStyle(style = SpanStyle(color = Color(0xFF616161))) {
                    append("và ")
                }
                withStyle(style = SpanStyle(color = Color(0xFFb4b4b4))) {
                    append("Chính sách quyền riêng tư")
                }
            }
            Text(
                text = announce,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(20.dp)
            )
        }

        // Content 5
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    getPassEmail(email.value, pass.value, context)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isTextFieldEmpty) Color.Gray else Color.Yellow,
                    contentColor = if (isTextFieldEmpty) Color.White else Color.Black
                ),
                enabled = !isTextFieldEmpty
            ) {
                Text(text = "Tiếp Tục", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

