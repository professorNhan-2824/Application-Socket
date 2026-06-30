package com.example.applicationsocket.ViewModel.LoginScreen

import android.os.Handler
import android.os.Looper
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.applicationsocket.R
import com.example.applicationsocket.SeverMyEmail
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun createOTP( email: String, otp: String, comback: () -> Unit, checkMail: (String) -> Unit, ){
    val time = remember { mutableStateOf(60) }
    var checkotp = remember {
        mutableStateOf("")
    }
    var isTextFieldEmpty by remember { mutableStateOf(true) }
    var canRetry = remember { mutableStateOf(false) }
    val emailService = SeverMyEmail()
    val handler = Handler(Looper.getMainLooper())
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF111111))
            .verticalScroll(rememberScrollState())
            .imePadding(),
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
        //content 2
        Row{
            Column(
                modifier = Modifier
                    .width(400.dp)
                    .padding(start = 20.dp, end = 20.dp)
                    .height(560.dp)
                , // Ensure the Column takes up the maximum height available
                verticalArrangement = Arrangement.Center, // Centers the content vertically
                horizontalAlignment = Alignment.Start
            )
            {
                Text(text = "Hãy Nhập OTP đã gửi về mail của bạn !", fontWeight = FontWeight.Bold,fontSize = 20.sp, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = checkotp.value,
                    onValueChange = {
                        checkotp.value = it
                        isTextFieldEmpty = it.isEmpty()
                    },
                    label = { Text("Nhập OTP", color = Color(0xFFb4b4b4)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White) ,
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFF616161), // Thay đổi màu nền ở đây
                    unfocusedIndicatorColor = Color.Transparent, // Remove underline when not focused
                    disabledIndicatorColor = Color.Transparent
                ),
                )
            }
        }

        LaunchedEffect(time) {
            if (time.value > 0) {
                while(time.value > 0){
                    delay(1000L)
                    time.value--
                }
            } else {
                canRetry.value = true
            }
        }
        Column(
            modifier = Modifier.padding(16.dp, top = 20.dp, bottom = 20.dp)
        ) {
            Text(
                text = "Hãy Thử lại sau "+ time.value + " giây",
                fontSize = 20.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = Color(0xFFb4b4b4),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (canRetry.value) {
                ClickableText(
                    text = buildAnnotatedString {
                        append("Thử Lại")
                        addStyle(SpanStyle(color = Color(0xFFb4b4b4)), 0, 8)
                    },
                    onClick = {
//                        val otp = generateOTP() // Implement function to generate OTP
//                        emailService.sendEmail(
//                            to = email,
//                            subject = "Mã OTP Socket Application",
//                            body = "Đây là mã OTP của bạn: $otp",
//                        ) { success ->
//                            if (success) {
//                                handler.post {
//                                    Toast.makeText(context, "Gửi OTP Thành Công", Toast.LENGTH_LONG)
//                                        .show()
//                                }
//                            }
//                        }
                        // Reset lại thời gian và trạng thái
                        time.value = 10
                        canRetry.value = false
                    }
                )
            }
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
                    if(checkotp.value == otp){
                        checkMail(email)
                    } else {
                        Toast.makeText(context, "Mã OTP không chính xác", Toast.LENGTH_LONG).show()
                    }
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
