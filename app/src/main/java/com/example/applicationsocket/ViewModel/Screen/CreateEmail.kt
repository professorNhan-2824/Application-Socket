package com.example.applicationsocket.ViewModel.Screen

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import com.example.applicationsocket.R
import com.example.applicationsocket.SeverMyEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


@OptIn(ExperimentalMaterial3Api::class)
@Composable
// navController: NavController
fun CreatedEmail( openloginOTP: (String,String) -> Unit, comback: () -> Unit){
    var email = remember { mutableStateOf("") }
    var isTextFieldEmpty by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val isEmailValid = remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFF111111)),

    ) {
        //conten 1
        Row(modifier = Modifier.padding(start = 17.dp, top = 15.dp)) {
            FloatingActionButton(
                onClick = {
                    comback()
                },
                modifier = Modifier.width(30.dp),
                containerColor = Color.Black,
                contentColor = Color.White
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = "Floating action button"
                )
            }
        }
        //content 2
        Row{
            Column(
                modifier = Modifier
                    .width(450.dp)
                    .padding(start = 27.dp, end = 27.dp)
                    .height(570.dp)
                    ,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            )
            {
                Text(text = "Hãy Nhập Email của Bạn !", fontWeight = FontWeight.Bold,fontSize = 20.sp, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))


                TextField(
                    value = email.value,
                    onValueChange = {
                        email.value = it
                        isTextFieldEmpty = it.isEmpty()
                    },
                    label = { Text("Địa chỉ email", color = Color(0xFFb4b4b4)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    isError = !isEmailValid.value,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = if (isEmailValid.value) {
                            Color(0xFF616161) // Màu nền khi email hợp lệ
                        } else {
                            Color(0xFF616161) // Màu nền khi email không hợp lệ
                        },  // Thay đổi màu nền ở đây
                        unfocusedIndicatorColor = Color.Transparent, // Remove underline when not focused
                        disabledIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    )
                )

            }
        }
        Row(
            modifier = Modifier.padding(start = 27.dp, end = 27.dp, top = 10.dp)
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
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Button(
                onClick = {
                    val emailService = SeverMyEmail()
                    val handler = Handler(Looper.getMainLooper())
                    val database = FirebaseDatabase.getInstance()
                    val usersRef = database.getReference("users")

                    usersRef.orderByChild("email").equalTo(email.value).addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                isEmailValid.value = false
                                Toast.makeText(context, "Email đã tồn tại !", Toast.LENGTH_LONG).show()
                            } else {
                                val otp = generateOTP()
                                emailService.sendEmail(
                                    to = email.value,
                                    subject = "Mã OTP Socket Application",
                                    body = "Đây là mã OTP của bạn: $otp",
                                ) { success ->
                                    if (success) {
                                        val emailtest = email
                                        handler.post {
                                            Toast.makeText(context, "Gửi OTP Thành Công", Toast.LENGTH_LONG).show()
                                            openloginOTP(email.value, otp)
                                        }
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(context, "Lỗi !", Toast.LENGTH_LONG).show()
                        }
                    })
                },
                modifier = Modifier
                    .width(250.dp)
                    .height(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isTextFieldEmpty) Color.Gray else Color.Yellow,
                    contentColor = if (isTextFieldEmpty) Color.White else Color.Black
                ),
                enabled = !isTextFieldEmpty
            ) {
                Text(text = "Tiếp tục", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}
fun generateOTP(): String {
    return (100000..999999).random().toString()
}
