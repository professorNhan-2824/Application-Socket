package com.example.applicationsocket

import com.example.applicationsocket.ui.theme.ApplicationSocketTheme
import android.Manifest
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.applicationsocket.ui.cameraStuff.CameraScreenTakePicture
import com.example.applicationsocket.ui.cameraStuff.CameraScreenEditPicture
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : ComponentActivity() {
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }

        requestPermissions()

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun requestPermissions() {
        val requiredPermissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        ActivityCompat.requestPermissions(
            this,
            requiredPermissions,
            REQUEST_CODE_PERMISSIONS
        )
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "take_picture_screen") {
        composable("take_picture_screen") {
            CameraScreenTakePicture(
//                onTakePicture = {},
                navController
            )
        }
        composable(
            "edit_picture_screen/{photoUri}",
            arguments = listOf(navArgument("photoUri") { type = NavType.StringType })
        ) { backStackEntry ->
            val photoUri = Uri.parse(backStackEntry.arguments?.getString("photoUri"))
            CameraScreenEditPicture(photoUri)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCamera() {
    ApplicationSocketTheme {

    }
}