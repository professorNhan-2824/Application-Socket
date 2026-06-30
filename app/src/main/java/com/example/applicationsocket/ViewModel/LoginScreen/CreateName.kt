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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.applicationsocket.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatedName(userid: String, comback: () -> Unit,  tologin: (String, String, String, Context) -> Unit ){
    var ho = remember { mutableStateOf("") }
    var ten = remember { mutableStateOf("") }
    var isTextFieldEmpty by remember { mutableStateOf(true) }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF111111)),

        ) {
        //conten 1
        Row(modifier = Modifier.padding(start = 10.dp, top = 15.dp)) {
            FloatingActionButton(
                onClick = {
                    comback()
                },
                modifier = Modifier.width(30.dp),
                containerColor = Color.Black, // Set the FAB background color to black
                contentColor = Color.White //
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = "Floating action button"
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        //content 2
        Row{
            Column(
                modifier = Modifier
                    .width(400.dp)
                    .padding(start = 20.dp, end = 20.dp)
                    .height(270.dp)
                , // Ensure the Column takes up the maximum height available
                verticalArrangement = Arrangement.Bottom, // Centers the content vertically
                horizontalAlignment = Alignment.Start
            )
            {
                Text(text = "Hãy Nhập họ & tên  !", fontWeight = FontWeight.Bold,fontSize = 20.sp, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = ho.value,
                    onValueChange = {
                        ho.value = it
                    },
                    label = { Text("Họ",color = Color(0xFFb4b4b4)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFF616161), // Thay đổi màu nền ở đây
                        unfocusedIndicatorColor = Color.Transparent, // Remove underline when not focused
                        disabledIndicatorColor = Color.Transparent
                    ),
                )
            }
        }
        Row{
            Column(
                modifier = Modifier
                    .width(400.dp)
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                    .height(250.dp)
                , // Ensure the Column takes up the maximum height available
                verticalArrangement = Arrangement.Top, // Centers the content vertically
                horizontalAlignment = Alignment.Start
            )
            {
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = ten.value,
                    onValueChange = {
                        ten.value = it
                        isTextFieldEmpty = it.isEmpty()

                    },
                    label = { Text("Tên",color = Color(0xFFb4b4b4)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFF616161), // Thay đổi màu nền ở đây
                        unfocusedIndicatorColor = Color.Transparent, // Remove underline when not focused
                        disabledIndicatorColor = Color.Transparent
                    ),
                )
            }
        }
        Row(
            modifier = Modifier.padding(start = 27.dp, end = 27.dp, top = 20.dp)
        ){
            val announce = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color(0xFF616161))) {
                    append("Bằng cách nhấn nào nut Tiếp tục, bạn đồng ý với chúng tôi ")
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
            Text(text = announce,fontWeight = FontWeight.Bold,
                fontSize = 15.sp,)

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Button(
                onClick = {
                    tologin(userid, ho.value, ten.value, context)
                },
                modifier = Modifier
                    .width(250.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isTextFieldEmpty) Color.Gray else Color.Yellow,
                    contentColor = if (isTextFieldEmpty) Color.White else Color.Black
                ),
                enabled = !isTextFieldEmpty
            ) {
                Text(text = "Đăng Ký", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }

    }
}
