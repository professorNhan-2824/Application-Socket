package com.example.applicationsocket.ui.cameraStuff

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.applicationsocket.CameraPreview
import com.example.applicationsocket.R
import com.example.applicationsocket.data.UserIDModel
import com.example.applicationsocket.data.UserSessionViewModel
import com.example.applicationsocket.ui.theme.ApplicationSocketTheme
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun TopBar(toProfile: () -> Unit, toFriend: () -> Unit, userIDModel: UserSessionViewModel){
    val userInformation = userIDModel.userInformation.observeAsState().value
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FloatingActionButton(
            onClick = {
                toProfile()
            },
            shape = CircleShape,
            modifier = Modifier
                .size(45.dp),
            containerColor = Color.DarkGray,
        ) {
           Image(
               painter = painterResource(id = R.drawable.profile_user),
               contentDescription = "User Profile",
               modifier = Modifier
                   .size(30.dp)
                   .border(width = 2.dp, color = Color.White, shape = CircleShape)
                   .background(Color.White, shape = CircleShape)
           )
        }

        Button(
            onClick = {
                toFriend()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.DarkGray,
                contentColor = Color.White
            ),
            modifier = Modifier
                .width(200.dp)
//                .clip(RoundedCornerShape(55.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.friends),
                contentDescription = "All friends",
                modifier = Modifier.size(27.dp)
            )
                Spacer(modifier = Modifier.width(8.dp)) // Thêm khoảng cách giữa hình ảnh và văn bản
            Text(
                text = "Chào ${userInformation?.firstName} ${userInformation?.lastName}",
                fontWeight = FontWeight.Bold
            )
        }

        FloatingActionButton(
            onClick = { /*TODO*/ },
            shape = CircleShape,
            modifier = Modifier
                .size(45.dp),
            containerColor = Color.DarkGray,
        ) {
            Image(
                painter = painterResource(id = R.drawable.speech_bubble),
                contentDescription = "Messaging",
                modifier = Modifier
                    .size(30.dp)
                    .background(Color.DarkGray, shape = CircleShape)
            )
        }
    }
}

@Composable
fun BottomBar(
    onTakePicture: () -> Unit,
    onSwitchCamera: () -> Unit,
    flashEnabled: Boolean,
    onFlashToggle: () -> Unit,
    modifier: Modifier = Modifier
){
    val backgroundColorLocket = Color(0xFF111111)
    Row(
        modifier = Modifier
            .fillMaxWidth(),
//            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { onFlashToggle() },
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColorLocket,
            )
        ) {
            // Sử dụng painterResource để tải hình ảnh
            val flashDisablePainter = painterResource(id = R.drawable.flash)
            val flashEnablePainter = painterResource(id = R.drawable.thunder)

            Image(
                painter = if (flashEnabled) flashEnablePainter else flashDisablePainter,
                contentDescription = "Turn on or turn off flash",
                modifier = Modifier
                    .size(36.dp)
            )
        }


        val borderColorTakePictureIcon = Color(0xFFefaf0c)
        FloatingActionButton(
            onClick = onTakePicture,
            containerColor = Color.Black,
            shape = CircleShape,
            modifier = Modifier
                .size(95.dp) // Kích thước của nút
                .border(5.dp, borderColorTakePictureIcon, CircleShape) // Đường viền màu vàng
        ) {
            Image(
                painter = painterResource(id = R.drawable.button_take_picture_icon),
                contentDescription = "Turn on or turn off flash",
//                imageVector = flashEnableImageVector,
                modifier = Modifier
                    .size(75.dp)
                    .border(width = 2.dp, color = Color.White, shape = CircleShape)
                    .background(Color.White, shape = CircleShape)
            )
        }

        Button(
            onClick = onSwitchCamera,
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColorLocket,
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.camera_swap_icon),
                contentDescription = "Switch camera",
                modifier = Modifier
                    .size(55.dp)
                    .background(backgroundColorLocket)
            )
        }
    }
}



@Composable
fun HistoryPart(toListImage: () -> Unit){
    //su dung cho toan app
    val backgroundColorLocket = Color(0xFF111111)
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable {
                toListImage()
            }
            .background(backgroundColorLocket)
//            .fillMaxWidth()
    ) {
        Text(
            text = "Lịch sử",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Image(
            painter = painterResource(id = R.drawable.down_arrow),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
        )
    }
}

@Composable
fun CameraScreenTakePicture(
    toGetImage: (Uri) -> Unit,
    toProfile: () -> Unit,
    toFriend: () -> Unit,
    toListImage: () -> Unit,
    userModel: UserSessionViewModel,
    idModel: UserIDModel
) {
    val context = LocalContext.current
    val (currentCamera, setCurrentCamera) = remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    val (flashEnabled, setFlashEnabled) = remember { mutableStateOf(false) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val userid = idModel.userID.observeAsState().value
    Column(modifier = Modifier.fillMaxSize()) {
        val backgroundColorLocket = Color(0xFF111111)

        // Phần trên cùng của màn hình
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.15625f)
                .background(backgroundColorLocket),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TopBar(toProfile, toFriend, userModel)
        }

        // Phần giữa của màn hình
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4375f)
                .background(backgroundColorLocket)
                .clip(RoundedCornerShape(55.dp))
        ) {
            CameraPreview(currentCamera, onImageCaptured = { uri ->
                // Handle captured image URI here
            }, imageCapture)
        }

        // Phần dưới cùng của màn hình
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.21875f)
                .background(backgroundColorLocket)
                .padding(top = 20.dp),
        ) {
            BottomBar(
                onTakePicture = {
                    if (flashEnabled && currentCamera == CameraSelector.DEFAULT_BACK_CAMERA) {
                        imageCapture.flashMode = ImageCapture.FLASH_MODE_ON
                    } else {
                        imageCapture.flashMode = ImageCapture.FLASH_MODE_OFF
                    }

                    val photoFile = File(
                        context.getExternalFilesDir(null),
                        "${System.currentTimeMillis()}.jpg"
                    )

                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                    imageCapture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onError(exception: ImageCaptureException) {
                                Log.e("CameraPreview", "Photo capture failed: ${exception.message}", exception)
                            }

                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                val savedUri = Uri.fromFile(photoFile)
//                                val photoData = PhotoData(savedUri, userid?.email.toString())
                                Log.d("CameraPreview", "Photo capture succeeded: $savedUri")
                                Toast.makeText(
                                    context,
                                    "Photo capture succeeded: $savedUri",
                                    Toast.LENGTH_SHORT
                                ).show()
//                                toGetImage(savedUri, userid?.email.toString())
                                toGetImage(savedUri)
                            }
                        }
                    )
                },
                onSwitchCamera = {
                    setCurrentCamera(
                        if (currentCamera == CameraSelector.DEFAULT_BACK_CAMERA)
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        else
                            CameraSelector.DEFAULT_BACK_CAMERA
                    )
                },
                flashEnabled = flashEnabled,
                onFlashToggle = { setFlashEnabled(!flashEnabled) }
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColorLocket)
                .weight(0.1875f)
        ) {
            HistoryPart(toListImage = toListImage)
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TopBarPreview() {
    ApplicationSocketTheme {
        CameraPreview(currentCamera = CameraSelector.DEFAULT_BACK_CAMERA, onImageCaptured = {}, ImageCapture.Builder().build())
    }
}
