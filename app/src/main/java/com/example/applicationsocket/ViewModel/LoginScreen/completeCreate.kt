package com.example.applicationsocket.ViewModel.LoginScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.applicationsocket.R
import com.example.applicationsocket.ui.theme.ApplicationSocketTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun completeCreate(comback: () -> Unit) {
    var ho = remember { mutableStateOf("") }
    var ten = remember { mutableStateOf("") }
    var isTextFieldEmpty by remember { mutableStateOf(true) }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF111111)),

        ) {
        Row {
            Column(
                modifier = Modifier
                    .width(400.dp)
                    .padding(start = 20.dp, end = 20.dp)
                    .fillMaxSize(),// Ensure the Column takes up the maximum height available
                verticalArrangement = Arrangement.Center, // Centers the content vertically
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Image(
                    painter = painterResource(id = R.drawable.checked),
                    contentDescription = null,
                    modifier = Modifier.height(200.dp).width(300.dp),
                )

               Text(
                    text = "Đăng Ký Thành Công",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White, // Set text color to white
                    modifier = Modifier.padding(top = 20.dp)
                )
                Row(
                    modifier = Modifier.padding(start = 27.dp, end = 27.dp, top = 20.dp)
                ) {
                    val announce = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color(0xFF616161))) {
                            append("Cảm ơn bạn đã đồng ý với  ")
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
                        text = announce, fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    )

                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            comback()
                        },
                        modifier = Modifier
                            .width(250.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor =  Color.Yellow,
                            contentColor =  Color.Black
                        ),

                    ) {
                        Text(text = "Thoát", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }

            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun complete() {
    ApplicationSocketTheme {
        completeCreate( comback = {})
    }
}

