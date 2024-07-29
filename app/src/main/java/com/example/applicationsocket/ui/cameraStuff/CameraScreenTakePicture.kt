package com.example.applicationsocket.ui.cameraStuff

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.applicationsocket.CameraPreview
//import com.example.applicationsocket.CameraPreview
import com.example.applicationsocket.R
import com.example.applicationsocket.ui.theme.ApplicationSocketTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun TopBar(modifier: Modifier = Modifier){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            modifier = Modifier
                .size(60.dp),
            onClick = { /* Xử lý sự kiện nhấn nút 1 */ },
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_1),
                contentDescription = "Icon 1",
                modifier = Modifier.size(100.dp),
            )
        }

        Button(
            onClick = { /* Xử lý sự kiện nhấn nút 2 */ }
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_1),
                contentDescription = "Icon 1",
            )
            Text("Nút 2")
        }

        Button(
            onClick = { /* Xử lý sự kiện nhấn nút 3 */ },
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_3),
                contentDescription = "Icon 3",
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { onFlashToggle() },
        ) {
            Image(
                imageVector = if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                contentDescription = "Turn on or off flash light",
            )
        }

        Button(
            onClick = onTakePicture
        ) {
            Image(
                imageVector = Icons.Default.Camera,
                contentDescription = "Take picture",
            )
        }

        Button(
            onClick = onSwitchCamera,
        ) {
            Image(
                imageVector = Icons.Default.Cameraswitch,
                contentDescription = "Switch camera"
            )
        }
    }
}

@Composable
fun testmain1(){
    val context = LocalContext.current
    val (currentCamera, setCurrentCamera) = remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    val (flashEnabled, setFlashEnabled) = remember { mutableStateOf(false) }
    val (showWhiteScreen, setShowWhiteScreen) = remember { mutableStateOf(false) }
    val imageCapture = remember {
        ImageCapture.Builder().build()
    }

    val coroutineScope = rememberCoroutineScope()

    fun capturePhoto() {

        if (flashEnabled && currentCamera == CameraSelector.DEFAULT_BACK_CAMERA) {
            imageCapture.flashMode = ImageCapture.FLASH_MODE_ON
        } else {
            imageCapture.flashMode = ImageCapture.FLASH_MODE_OFF
        }

        // Tạo một tệp tin để lưu trữ ảnh
        val photoFile = File(
            context.getExternalFilesDir(null),
            "${System.currentTimeMillis()}.jpg"
        )

        // Thiết lập các tùy chọn đầu ra cho ImageCapture
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Chụp ảnh và lưu trữ nó vào tệp tin đã tạo
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                // Xử lý khi chụp ảnh thất bại
                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraPreview", "Photo capture failed: ${exception.message}", exception)
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Lấy URI của ảnh đã lưu
                    val savedUri = Uri.fromFile(photoFile)
                    Log.d("CameraPreview", "Photo capture succeeded: $savedUri")
                    // Handle the saved image URI here
                    // Xử lý URI của ảnh đã lưu ở đây
                    Toast.makeText(context, "Photo capture succeeded: $savedUri", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    fun takePicture() {
        if (flashEnabled && currentCamera == CameraSelector.DEFAULT_FRONT_CAMERA) {
            setShowWhiteScreen(true)
            coroutineScope.launch {
                delay(1000)
                capturePhoto()
                setShowWhiteScreen(false)
            }
        } else {
            capturePhoto()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Phần trên cùng của màn hình
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.15625f)
                .background(Color.Red),
//            contentAlignment = Alignment.Center
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showWhiteScreen) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White))
            } else {
                TopBar()
            }
        }

        // Phần giữa của màn hình
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4375f)
                .background(Color.Green)
                .clip(RoundedCornerShape(55.dp)),
//            contentAlignment = Alignment.Center
        ) {
            CameraPreview(
                currentCamera,
                onImageCaptured = { uri ->
                // Handle captured image URI here
                },
                imageCapture,
                modifier = Modifier
//                        .align(Alignment.Center)
            )
        }

        // Phần dưới cùng của màn hình
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.375f)
                .background(Color.Blue),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showWhiteScreen) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White))
            } else {
                BottomBar(
                    onTakePicture = { takePicture() },
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
        }
    }
}

//@Composable
//fun TestMain1() {
//    val context = LocalContext.current
//    val (currentCamera, setCurrentCamera) = remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
//    val (flashEnabled, setFlashEnabled) = remember { mutableStateOf(false) }
//    val (showWhiteScreen, setShowWhiteScreen) = remember { mutableStateOf(false) }
//    val imageCapture = remember {
//        ImageCapture.Builder().build()
//    }
//
//    val coroutineScope = rememberCoroutineScope()
//
//
//
//    fun capturePhoto() {
//        if (flashEnabled && currentCamera == CameraSelector.DEFAULT_BACK_CAMERA) {
//            imageCapture.flashMode = ImageCapture.FLASH_MODE_ON
//        } else {
//            imageCapture.flashMode = ImageCapture.FLASH_MODE_OFF
//        }
//
//        val photoFile = File(
//            context.getExternalFilesDir(null),
//            "${System.currentTimeMillis()}.jpg"
//        )
//
//        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
//
//        imageCapture.takePicture(
//            outputOptions,
//            ContextCompat.getMainExecutor(context),
//            object : ImageCapture.OnImageSavedCallback {
//                override fun onError(exception: ImageCaptureException) {
//                    Log.e("CameraPreview", "Photo capture failed: ${exception.message}", exception)
//                }
//
//                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                    val savedUri = Uri.fromFile(photoFile)
//                    Log.d("CameraPreview", "Photo capture succeeded: $savedUri")
//                    Toast.makeText(context, "Photo capture succeeded: $savedUri", Toast.LENGTH_SHORT).show()
//                    // Handle the saved image URI here
//                }
//            }
//        )
//    }
//
//    fun takePicture() {
//        capturePhoto()
//    }
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        // Phần trên cùng của màn hình
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(0.15625f)
//                .background(Color.Red),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            TopBar()
//        }
//
//        // Phần giữa của màn hình
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(0.4375f)
//                .background(Color.Green)
//                .clip(RoundedCornerShape(55.dp))
//        ) {
//            if (showWhiteScreen) {
//                Box(modifier = Modifier.fillMaxSize().background(Color.White))
//            } else {
//                CameraPreview(currentCamera, onImageCaptured = { uri ->
//                    // Handle captured image URI here
//                }, imageCapture)
//            }
//        }
//
//        // Phần dưới cùng của màn hình
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(0.375f)
//                .background(Color.Blue),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            BottomBar(
//                onTakePicture = { takePicture() },
//                onSwitchCamera = {
//                    setCurrentCamera(
//                        if (currentCamera == CameraSelector.DEFAULT_BACK_CAMERA)
//                            CameraSelector.DEFAULT_FRONT_CAMERA
//                        else
//                            CameraSelector.DEFAULT_BACK_CAMERA
//                    )
//                },
//                flashEnabled = flashEnabled,
//                onFlashToggle = { setFlashEnabled(!flashEnabled) }
//            )
//        }
//    }
//}



//@Composable
//fun testmain(){
//    Column(modifier = Modifier.fillMaxSize()) {
//        // Phần trên cùng của màn hình
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(2f)
//                .background(Color.Red),
//            contentAlignment = Alignment.Center
//        ) {
//            TopBar()
//        }
//
//        // Phần giữa của màn hình
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(4f)
//                .background(Color.Green),
//            contentAlignment = Alignment.Center
//        ) {
//            // có chỉnh sửa lại phần CameraPreview
//            CameraPreview()
//        }
//
//        // Phần dưới cùng của màn hình
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(3.25f)
//                .background(Color.Blue),
//            contentAlignment = Alignment.Center
//        ) {
//            BottomBar()
//        }
//    }
//}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TopBarPreview() {
    ApplicationSocketTheme {
//        testmain1()
    }
}
