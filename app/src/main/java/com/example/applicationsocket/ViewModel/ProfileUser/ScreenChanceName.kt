package com.example.applicationsocket.ViewModel.ProfileUser

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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.applicationsocket.R
import com.example.applicationsocket.data.UserIDModel
import com.example.applicationsocket.data.UserSessionViewModel
import com.google.firebase.database.FirebaseDatabase

@Composable
fun headScreen(conback: () -> Unit) {
    Row(modifier = Modifier.padding(start = 10.dp, top = 15.dp)) {
        FloatingActionButton(
            onClick = {
                conback()
            },
            modifier = Modifier.width(30.dp),
            containerColor = Color(0xFF111111), // Set the FAB background color to black
            contentColor = Color.White //
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "Floating action button"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun bodyScreen(userModel: UserSessionViewModel,userid : String) {
    var ho by remember { mutableStateOf(userModel.userInformation.value?.firstName ?: "") }
    var ten by remember { mutableStateOf(userModel.userInformation.value?.lastName ?: "") }
    var isTextFieldEmpty by remember { mutableStateOf(true) }

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
            Text(text = "Hãy Nhập họ & tên  ! ", fontWeight = FontWeight.Bold,fontSize = 20.sp, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = ho,
                onValueChange = {
                    ho = it
                    isTextFieldEmpty = it.isEmpty()
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
                value = ten,
                onValueChange = {
                    ten = it
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
    bottomScreen(userID = userid,ho = ho, ten = ten, isTextFieldEmpty = isTextFieldEmpty)
}

@Composable
fun bottomScreen(userID: String ,ho: String, ten: String, isTextFieldEmpty: Boolean) {
    val context = LocalContext.current
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
                uploadData(userID,ho,ten, onSuccess = {
                    Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                })

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

fun uploadData(userID: String, ho: String, ten: String, onSuccess: () -> Unit){

    val database = FirebaseDatabase.getInstance()
    val nameUser = database.getReference("users").child(userID).child("information")
    val updates = mapOf<String, Any>(
        "firstName" to ho,
        "lastName" to ten
    )
    nameUser.updateChildren(updates)
        .addOnSuccessListener {
            // Thực hiện khi cập nhật thành công
            onSuccess()
        }
        .addOnFailureListener { exception ->
            // Xử lý khi có lỗi
//            onFailure(exception)
        }
}

@Composable
fun mainScreenChanceName(userModel: UserSessionViewModel,userID : String, comback: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111111))
    ) {
        headScreen(conback = comback)
        bodyScreen(userModel = userModel,userid = userID)
    }

}

