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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.applicationsocket.data.UserIDModel
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun bodyScreenFeedback(userID: String,userIDModel: UserIDModel) {
    var email by remember { mutableStateOf(userIDModel.userID.value?.email ?: "") }
    var contentFeedBack by remember { mutableStateOf("") }
    var isTextFieldEmpty by remember { mutableStateOf(true) }
    var context = LocalContext.current
    Row{
        Column(
            modifier = Modifier
                .width(400.dp)
                .padding(start = 20.dp, end = 20.dp)
                .height(550.dp)
            , // Ensure the Column takes up the maximum height available
            verticalArrangement = Arrangement.Top, // Centers the content vertically
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Hãy góp ý cho chúng tôi !", fontWeight = FontWeight.Bold,fontSize = 20.sp, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = "",
                onValueChange = {

                },
                label = { Text("$email",color = Color(0xFFA8A8A8)) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White),
                shape = RoundedCornerShape(16.dp),
                enabled = false,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFF616161), // Thay đổi màu nền ở đây
                    unfocusedIndicatorColor = Color.Transparent, // Remove underline when not focused
                    disabledIndicatorColor = Color.Transparent
                ),
            )
            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                value = contentFeedBack,
                onValueChange = {
                    contentFeedBack = it
                    isTextFieldEmpty = it.isEmpty()
                },
                label = { Text("Những góp ý của bạn sẽ giúp chúng tôi phát triển hơn đem lại sự thuận lời như thân thiện cho người dùng",color = Color(0xFFb4b4b4)) },
                modifier = Modifier.fillMaxWidth().height(200.dp),
                textStyle = TextStyle(color = Color.White),
                shape = RoundedCornerShape(16.dp),
                maxLines = 10,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFF616161), // Thay đổi màu nền ở đây
                    unfocusedIndicatorColor = Color.Transparent, // Remove underline when not focused
                    disabledIndicatorColor = Color.Transparent
                ),
            )
        }
    }
    bottomScreenFeedback(userID, userIDModel, contentFeedBack, isTextFieldEmpty, context)
}

@Composable
fun bottomScreenFeedback(userID: String, userIDModel: UserIDModel, contentFeedBack: String, isTextFieldEmpty: Boolean, context: Context) {
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
                uploadFeedBack(userID,userIDModel, contentFeedBack, onSuccess = {
                    Toast.makeText(context, "Gửi thành công", Toast.LENGTH_SHORT).show()
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
            Text(text = "Gửi", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}


fun uploadFeedBack(userID: String,userIDModel: UserIDModel, contentFeedBack: String, onSuccess: () -> Unit){
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("FeedBack")
    val key = myRef.push().key
    val modelFeedBack = mapOf(
        "id" to userID,
        "email" to userIDModel.userID.value?.email,
        "contentFeedBack" to contentFeedBack
    )
    myRef.child(key!!).setValue(modelFeedBack).addOnSuccessListener {
        onSuccess()
    }
}
@Composable
fun mainScreenFeedback(userID: String, userIDModel: UserIDModel, comback:() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111111))
    ){
        headScreen { comback() }
        bodyScreenFeedback(userID,userIDModel)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewScreenFeedback() {
    mainScreenFeedback("123", UserIDModel(), {})
}