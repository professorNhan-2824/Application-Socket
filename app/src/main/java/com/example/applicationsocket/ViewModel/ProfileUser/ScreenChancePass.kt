package com.example.applicationsocket.ViewModel.ProfileUser

import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.applicationsocket.data.UserIDModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyChangePass(userID:String,userIDModel: UserIDModel,){
    var pass by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var isTextFieldEmpty by remember { mutableStateOf(true) }
    val context = LocalContext.current


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
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Hãy Nhập Password !", fontWeight = FontWeight.Bold,fontSize = 20.sp, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = pass,
                onValueChange = {
                    pass = it
                    isTextFieldEmpty = it.isEmpty()
                },
                label = { Text("Password",color = Color(0xFFb4b4b4)) },
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
    bottomChangePass(userID,pass,isTextFieldEmpty,context)
}

fun uploadPass(userID: String, pass: String, context: Context, onSuccess: () -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("users")

    // Cập nhật mật khẩu trong Realtime Database
    myRef.child(userID).child("password").setValue(pass)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Cập nhật mật khẩu trong Firebase Authentication
                val user = FirebaseAuth.getInstance().currentUser
                user?.updatePassword(pass)
                    ?.addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            Toast.makeText(context, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
                            onSuccess()
                        } else {
                            Toast.makeText(context, "Lỗi cập nhật mật khẩu trong Authentication", Toast.LENGTH_SHORT).show()
//                            onFailure(authTask.exception ?: Exception("Unknown error"))
                        }
                    }
            } else {
                Toast.makeText(context, "Lỗi cập nhật mật khẩu trong Database", Toast.LENGTH_SHORT).show()
//                onFailure(task.exception ?: Exception("Unknown error"))
            }
        }
}

@Composable
fun bottomChangePass(userID: String, pass: String,isTextFieldEmpty: Boolean,context: Context){
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
                    if(pass.length >= 6){
                        uploadPass(userID,pass,context, onSuccess = {
                            Toast.makeText(context, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
                        })
                    }else{
                        Toast.makeText(context, "Mật khẩu chứa hơn 6 ký tự", Toast.LENGTH_SHORT).show()
                    }
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
            Text(text = "Xác Nhận", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }


}

@Composable
fun mainChangePass(userID: String,userIDModel: UserIDModel,comback:() -> Unit){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111111))
    ){
        headScreen { comback ()}
        BodyChangePass(userID,userIDModel)

    }
}
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun DefaultPreview() {
//    mainChangePass( "123",UserIDModel())
//}