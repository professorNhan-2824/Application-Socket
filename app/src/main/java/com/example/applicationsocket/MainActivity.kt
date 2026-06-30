package com.example.applicationsocket


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import android.Manifest
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.applicationsocket.ViewModel.Home.Home
import com.example.applicationsocket.ViewModel.Home.mainListFriend
import com.example.applicationsocket.ViewModel.ProfileUser.mainChangePass
import com.example.applicationsocket.ViewModel.ProfileUser.mainScreenChanceName
import com.example.applicationsocket.ViewModel.ProfileUser.mainScreenFeedback
import com.example.applicationsocket.ViewModel.ProfileUser.mainScreenProfile
import com.example.applicationsocket.ViewModel.LoginScreen.CreatedEmail
import com.example.applicationsocket.ViewModel.LoginScreen.CreatedName
import com.example.applicationsocket.ViewModel.LoginScreen.CreatedPass
import com.example.applicationsocket.ViewModel.LoginScreen.LoginScreen
import com.example.applicationsocket.ViewModel.LoginScreen.ScreenLogin
import com.example.applicationsocket.ViewModel.LoginScreen.checkIfUserExists
import com.example.applicationsocket.ViewModel.LoginScreen.completeCreate
import com.example.applicationsocket.ViewModel.LoginScreen.createOTP
import com.example.applicationsocket.ViewModel.LoginScreen.saveBasicUserData
import com.example.applicationsocket.data.UserIDModel
import com.example.applicationsocket.data.UserSessionViewModel
import com.example.applicationsocket.data.getUserName
import com.example.applicationsocket.data.modelNameUser
//import com.example.applicationsocket.ViewModel.Screen.ScreenLoginEmail
//import com.example.applicationsocket.ViewModel.Screen.ScreenLoginModel
import com.example.applicationsocket.ui.theme.ApplicationSocketTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.FirebaseDatabase
import com.example.applicationsocket.ui.cameraStuff.CameraScreenTakePicture
import com.example.applicationsocket.ui.cameraStuff.CameraScreenEditPicture
import com.example.applicationsocket.ui.cameraStuff.ListImage
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Khởi tạo Firebase

        setContent {
            ApplicationSocketTheme {
                MainNaviga()
            }
        }

        // Request permissions
        requestPermissions()

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun requestPermissions() {
        requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // Handle permissions result
            val allPermissionsGranted = permissions.values.all { it }
            if (!allPermissionsGranted) {
                // Handle case where not all permissions are granted
                // For example, show a message to the user
            }
        }

        requestPermissionsLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}

@Composable
fun MainNaviga(){
    val userViewModel: UserSessionViewModel = viewModel()
    val userIDModel: UserIDModel = viewModel()
    val navController = rememberNavController()
    val context = LocalContext.current
    ApplicationSocketTheme {
        NavHost(navController = navController, startDestination = "homeLogin") {
            // Navigate to Login screen
            composable("homeLogin") {
                ScreenLogin(
                    openCreateEmail = {
                        navController.navigate("createEmail")
                    },
                    openLoginEmail = {
                        navController.navigate("loginEmail")
                    }
                )
            }
            // Navigate to CreateEmail screen
            composable("createEmail") {
                CreatedEmail(
                    openloginOTP = { email, otp ->
                        navController.navigate("createOTP/$email/$otp")
                    },
                    comback = {
                        navController.navigate("homeLogin")
                    }
                )
            }
            composable(
                "createOTP/{email}/{otp}",
                arguments = listOf(
                    navArgument("email") { type = NavType.StringType },
                    navArgument("otp") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email")
                requireNotNull(email)// Provide a default value if null
                val otp = backStackEntry.arguments?.getString("otp")
                requireNotNull(otp)// Provide a default value for otp as well
                createOTP(email = email, otp = otp,
                    comback = {
                        navController.navigate("createEmail")
                    },
                    checkMail = {email ->
                        navController.navigate("createPass/$email")
                    },
                )
            }

            composable(
                "createPass/{email}",
                arguments = listOf(
                    navArgument("email") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email")
                requireNotNull(email)// Provide a default value if null
                CreatedPass(
                    email = email,
                    comback = {
                        navController.navigate("createEmail")
                    },
                    getPassEmail = {email, pass, context ->
                        val auth = FirebaseAuth.getInstance()
                        auth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    user?.let {
//                                        val userId = it.uid
                                        saveBasicUserData(email, pass)
                                        navController.navigate("CreateName/$email")
                                    }
                                } else {
                                    checkIfUserExists(email, pass, context)
                                }
                            }
                    }
                )
            }

            composable("CreateName/{email}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType }
            )
            ){backStackEntry ->
                val email = backStackEntry.arguments?.getString("email")
                requireNotNull(email)// Provide a default value if null
                CreatedName(
                    userid = email,
                    comback = {
                        navController.navigate("HomeLogin")
                    },
                    tologin = {email , ho, ten , context ->
                        val database = FirebaseDatabase.getInstance()
                        val userRef = database.getReference("users").child(encodeEmail(email)).child("information")

                        val information = modelNameUser(ho, ten)

                        userRef.setValue(information)
                            .addOnSuccessListener {
                                navController.navigate("completeCreateUser")
                            }
                            .addOnFailureListener { exception ->
                                // Failed to save user data
                                Toast.makeText(context, "Lỗi: ${exception.message}", Toast.LENGTH_LONG).show()
                            }

                    })
            }

            composable("completeCreateUser"){
                completeCreate(
                    comback = {
                        navController.navigate("HomeLogin")
                    }
                )

            }   // sau khi đăng nhập truy vấn realrime firebase để lấy first và last name để hiển thị làm key cho user
            composable("loginEmail"){
                LoginScreen(
                    comback = {
                        navController.navigate("homeLogin")
                    },
                    getPassEmail = {email, pass, context ->
                        login(email, pass, context, userViewModel, userIDModel ) { email ->
                            navController.navigate("Home/$email")
                        }
                    },
                )
            }
//            composable(
//                "Home/{email}",
//                arguments = listOf(navArgument("email") { type = NavType.StringType })
//            ) { backStackEntry ->
//                val email = backStackEntry.arguments?.getString("email")
//                requireNotNull(email) // Ensure userId is not null
//                Home(userID = email,
//                    userViewModel = userViewModel,
//                    idmodelUser = userIDModel,
//                    toProfile = {
//                        navController.navigate("profile/$email")
//                    },
//                    toFriendList = {
//                        navController.navigate("friendList/$email")
//                    }
//                )
//            }
            composable("Home/{email}",
                arguments = listOf(navArgument("email") { type = NavType.StringType })
            ) {backStackEntry ->
                val email = backStackEntry.arguments?.getString("email")
                requireNotNull(email)
            CameraScreenTakePicture(
                toGetImage = { photoUri ->
                    val testPicture: String = Uri.encode(photoUri.toString())
//                    val Email = idModel.userID.value ?: ""
//                    val encodedEmail: String = Uri.encode(Email.toString())
                    navController.navigate("edit_picture_screen/$testPicture/$email")
                    Log.d("Navigation", "Navigating to edit_picture_screen with URI: $testPicture and Email: $email")
                },
                toProfile = {
                    navController.navigate("profile/$email")
                },
                toFriend = {
                    navController.navigate("friendList/$email")
                },
                toListImage = {
                    navController.navigate("listImage/$email")
                },
                userModel = userViewModel,
                idModel = userIDModel
            )
        }
            composable("listImage/{email}",
                arguments = listOf(navArgument("email") { type = NavType.StringType })
            ) { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email")
                requireNotNull(email) { "Email is null" }
                ListImage(email, userViewModel)

            }
            composable(
                "edit_picture_screen/{testPicture}/{email}",
                arguments = listOf(
                    navArgument("testPicture") { type = NavType.StringType },
                    navArgument("email") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val testPictureUriString = backStackEntry.arguments?.getString("testPicture")
                val emailEncoded = backStackEntry.arguments?.getString("email")

                Log.d("Navigation", "Received testPictureUriString: $testPictureUriString and emailEncoded: $emailEncoded")

                requireNotNull(testPictureUriString) { "Test picture URI is null" }
                requireNotNull(emailEncoded) { "Email is null" }

                val testPictureUri = Uri.parse(Uri.decode(testPictureUriString))
//                val email = Uri.decode(emailEncoded)

                Log.d("Navigation", "Decoded testPictureUri: $testPictureUri and email: $emailEncoded")

                CameraScreenEditPicture(testPictureUri, emailEncoded, userIDModel, comback = {
                    navController.navigate("Home/$emailEncoded")
                })
            }




            composable("profile/{email}",
                arguments = listOf(navArgument("email") { type = NavType.StringType })
            ){backStackEntry ->
                val email = backStackEntry.arguments?.getString("email")
                requireNotNull(email)// Provide a default value if null
                mainScreenProfile(email,userViewModel,userIDModel,
                    comback = {
                        navController.navigate("Home/$email")
                    },
                    tologout = {
                        navController.navigate("homeLogin") {
                            // tác dụng không cho quay lại màn hình trước sau khi đăng xuất
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    },
                    toChangeName = {
                        navController.navigate("ChangeName/$email")
                    },
                    toChangePass = {
                        navController.navigate("ChangePass/$email")
                    },
                    toSendFeedBack = {
                        navController.navigate("sendFeedBack/$email")
                    },
                    )
            }
            composable("ChangeName/{email}",
                arguments = listOf(
                    navArgument("email") { type = NavType.StringType } ,
                )
                ){
                val email = it.arguments?.getString("email")
                requireNotNull(email)// Provide a default value if null
                mainScreenChanceName(userModel = userViewModel, email,
                    comback = {
                        navController.navigate("profile/$email")
                    }
                )

            }
            composable("ChangePass/{email}",
                arguments = listOf(navArgument("email") { type = NavType.StringType },)
                ){
                val email = it.arguments?.getString("email")
                requireNotNull(email)// Provide a default value if null
                mainChangePass(email,userIDModel,
                    comback = {
                        navController.navigate("profile/$email")
                    })

            }
            composable("sendFeedBack/{email}",
                arguments = listOf(navArgument("email") { type = NavType.StringType })
                ){
                val email = it.arguments?.getString("email")
                requireNotNull(email)// Provide a default value if null
                mainScreenFeedback( email, userIDModel,
                    comback = {
                        navController.navigate("profile/$email")
                    })
            }
            composable("friendList/{email}",
                arguments = listOf(navArgument("email") { type = NavType.StringType })
                ){
                val email = it.arguments?.getString("email")
                requireNotNull(email)// Provide a default value if null
                mainListFriend(userIDModel,email,
                    comback = {
                        navController.navigate("Home/$email")
                    },)

            }

        }
    }
}
fun login(email: String, pass: String, context: Context, userViewModel: UserSessionViewModel,userIDModel: UserIDModel, toHome: (String) -> Unit) {
    val auth = FirebaseAuth.getInstance()
    auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_LONG).show()
//            val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
            val emailuser = encodeEmail(email)
            // nêm truyền ữ liệu sau khi đăng nhập để dễ goji và sữ đụng
            getUserName(emailuser, context, userViewModel, togoHome = {
                toHome(emailuser)
            })
        } else {
            val errorMessage = task.exception?.let {
                when (it) {
                    is FirebaseAuthInvalidCredentialsException -> "Mật khẩu không không chính xác"
                    is FirebaseAuthInvalidUserException -> "Tài khoản không tồn tại hoặc đã bị vô hiệu hóa"
                    else -> "Đăng nhập thất bại: ${it.message}"
                }
            } ?: "Lỗi không xác định"
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }
}
fun encodeEmail(email: String): String {
    return email.replace(".", ",")
        .replace("#", "%23")
        .replace("$", "%24")
        .replace("[", "%5B")
        .replace("]", "%5D")
}
