package com.example.applicationsocket.ViewModel.ProfileUser


import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.applicationsocket.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.applicationsocket.data.UserIDModel
import com.example.applicationsocket.data.UserSessionViewModel
import com.example.applicationsocket.data.modelNameUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

//hàm ảnh profile user theo dạng tròn
@Composable
fun CircularImage(imageURI: String?, contentImage: String){
    val painter = if (!imageURI.isNullOrEmpty()) {
        rememberAsyncImagePainter(model = imageURI)
    } else {
        painterResource(id = R.drawable.user)
    }

    Image(
        painter = painter,
        contentDescription = contentImage,
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .border(2.dp, Color.Yellow, CircleShape)

    )
}
//hàm này xoá ảnh sau khi kiem tra imageprofileuser có ảnh cũ không
fun deleteImageFromFirebaseStorage(imageUrl: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
    if (imageUrl.isEmpty()) {
        Log.e("FirebaseStorage", "Cannot delete image: URL is empty")
        onFailure()
        return
    }

    try {
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
        storageReference.delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    } catch (e: IllegalArgumentException) {
        Log.e("FirebaseStorage", "Invalid URI: $imageUrl", e)
        onFailure()
    }
}




fun uploadImageToFirebaseStorage(email: String, imageUri: Uri, context: Context, onUploadSuccess: (String) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val userRef = database.getReference("users").child(email).child("information")

    // Kiểm tra và xóa ảnh cũ nếu có
    getImageProfileUser(email) { existingImageUrl ->
        existingImageUrl?.imageProfileUser?.let { url ->
            deleteImageFromFirebaseStorage(url, {
                // Xóa ảnh cũ thành công, tiếp tục tải lên ảnh mới
                uploadNewImage(imageUri, userRef, onUploadSuccess, context)
            }, {
                Toast.makeText(context, "Failed to delete old image", Toast.LENGTH_SHORT).show()
            })
        } ?: run {
            // Không có ảnh cũ, tiếp tục tải lên ảnh mới
            uploadNewImage(imageUri, userRef, onUploadSuccess, context)
        }
    }
}


private fun uploadNewImage(imageUri: Uri, userRef: DatabaseReference, onUploadSuccess: (String) -> Unit, context: Context) {
    val storageReference = FirebaseStorage.getInstance().reference
    val imageRef = storageReference.child("imagesProfile/${UUID.randomUUID()}.jpg")

    imageRef.putFile(imageUri)
        .addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                // Cập nhật URL ảnh mới vào Realtime Database
                userRef.child("imageProfileUser").setValue(uri.toString())
                onUploadSuccess(uri.toString())
            }
        }
        .addOnFailureListener { exception ->
            Toast.makeText(context, "Upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
}

//hàm này chọn ảnh của máy user
@Composable
fun ImagePicker(onImageSelected: (Uri) -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }

    Button(
        onClick = { launcher.launch("image/*") },
        colors = ButtonDefaults.buttonColors(
            containerColor =  Color(0xFFB4B4B4),
            contentColor =  Color.White
        ),
    ) {
        Text("Chọn ảnh")
    }
}

fun getImageProfileUser(email: String, onSuccess: (modelNameUser?) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val imageUser = database.getReference("users").child(email).child("information")

    imageUser.get()
        .addOnSuccessListener { task ->
            val imageProfileUser = task.child("imageProfileUser").getValue(String::class.java)
            val firstName = task.child("firstName").getValue(String::class.java)
            val lastName = task.child("lastName").getValue(String::class.java)

            val userInformation = modelNameUser(
                imageProfileUser = imageProfileUser,
                firstName = firstName,
                lastName = lastName
            )
            onSuccess(userInformation)
        }
        .addOnFailureListener { exception ->
            Log.e("FirebaseDatabase", "Error getting data ImageUser", exception)
            onSuccess(null)
        }
}


@Composable
fun imageVSnameProfile(email: String, user: UserSessionViewModel, comback: () -> Unit) {
    var userInfor by remember { mutableStateOf<modelNameUser?>(null) }
    val context = LocalContext.current

    // Lấy dữ liệu imageUser từ firebase
    LaunchedEffect(email) {
        getImageProfileUser(email) { infor ->
            userInfor = infor
        }
    }

    FloatingActionButton(
        onClick = {
            comback()
        },
        modifier = Modifier
            .width(35.dp)
            .padding(start = 10.dp, top = 5.dp),
        containerColor = Color.Black,
        contentColor = Color.White
    ) {
        Icon(
            painter = painterResource(id = R.drawable.add),
            contentDescription = "Floating action button"
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, start = 20.dp, end = 20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Hiển thị ảnh người dùng
            CircularImage(imageURI = userInfor?.imageProfileUser, contentImage = "Profile Image")
            Spacer(modifier = Modifier.height(7.dp))
            Text(
                text = "${userInfor?.firstName} ${userInfor?.lastName}",
                color = Color.White,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(1.dp))
            ImagePicker { uri ->
                uploadImageToFirebaseStorage(email, uri, context) { uploadedImageUrl ->
                    // Cập nhật userInfor với ảnh mới
                    userInfor = userInfor?.copy(imageProfileUser = uploadedImageUrl)
                }
            }
        }
    }
}


@Composable
fun categoryChanceProfile(toChangeName: () -> Unit, toChangePass: () -> Unit, toSendFeedBack: () -> Unit){
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 15.dp, end = 15.dp, top = 20.dp)
    ) {
        Row{
            Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Tổng Quan", tint = Color(0xFFB4B4B4))
            Text(text =" Tổng Quan", color = Color(0xFFB4B4B4), fontSize = 19.sp,fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box (
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFA9A9A9))
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp)
                    .background(Color(0xFFA9A9A9)),

                ) {
                Row( modifier = Modifier.padding(top = 7.dp, bottom = 7.dp)){
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Thay đổi tên", tint = Color.White)
                    Text("Thay đổi tên",
                        color = Color.White, fontSize = 16.sp,fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 7.dp)
                            .clickable {
                                toChangeName()
                            },
                        style = MaterialTheme.typography.bodyMedium)
                }

                Divider(color = Color.Gray, thickness = 1.dp)
                Row( modifier = Modifier.padding(top = 7.dp, bottom = 7.dp)){
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Thay đổi mật khẩu", tint = Color.White)
                    Text("Thay đổi mật khẩu",
                        color = Color.White, fontSize = 16.sp,fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 7.dp)
                            .clickable {
                                toChangePass()
                            },
                        style = MaterialTheme.typography.bodyMedium)
                }
                Divider(color = Color.Gray, thickness = 1.dp)
                Row( modifier = Modifier.padding(top = 7.dp, bottom = 7.dp)){
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Báo cáo sự cố", tint = Color.White)
                    Text("Báo cáo sự cố",
                        color = Color.White, fontSize = 16.sp,fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 7.dp)
                            .clickable {
                                toSendFeedBack()
                            },
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun signOutProfile(usermodel: UserSessionViewModel, tologout: () -> Unit){
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 15.dp, end = 15.dp, top = 20.dp)
    ) {
        Row{
            Icon(imageVector = Icons.Default.Warning, contentDescription = "Vùng nguy hiểm", tint = Color(0xFFB4B4B4))
            Text(text =" Vùng nguy hiểm", color = Color(0xFFB4B4B4), fontSize = 19.sp,fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box (
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFA9A9A9))
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp)
                    .background(Color(0xFFA9A9A9)),

                ) {
                Row( modifier = Modifier.padding(top = 7.dp, bottom = 7.dp)){
                    Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Tổng Quan", tint = Color.White)
                    Text("Đăng xuất",
                        color = Color.White, fontSize = 16.sp,fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 7.dp)
                            .clickable {
                                //nơi xét sự kiện cho click
                                usermodel.logOut()
                                tologout()
                            },
                        style = MaterialTheme.typography.bodyMedium)
                }
                Divider(color = Color.Gray, thickness = 1.dp)
                Row( modifier = Modifier.padding(top = 7.dp, bottom = 7.dp)){
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Tổng Quan", tint = Color(0xFFFF4500))
                    Text("Xoá tài khoản",
                        color = Color(0xFFFF4500), fontSize = 16.sp,fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 7.dp)
                            .clickable {
                                //nơi xét sự kiện cho click
//                            usermodel.logOut()
                            },
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun mainScreenProfile(email:String,usermodel: UserSessionViewModel,userIDModel: UserIDModel,
                      comback: () -> Unit,
                      tologout: () -> Unit,
                      toChangeName: () -> Unit,
                      toChangePass: () -> Unit,
                      toSendFeedBack: () -> Unit,
                    ){
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .fillMaxSize()
        .background(Color(0xFF111111))) {
        item {
            imageVSnameProfile(email,usermodel, comback)
        }
        item {
            categoryChanceProfile(toChangeName, toChangePass, toSendFeedBack)
        }
        item {
            signOutProfile(usermodel, tologout)
        }
    }
}

