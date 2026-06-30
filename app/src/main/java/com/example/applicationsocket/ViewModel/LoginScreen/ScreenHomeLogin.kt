package com.example.applicationsocket.ViewModel.LoginScreen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.applicationsocket.R

import kotlinx.coroutines.delay

// phần ảnh của screen login
@Composable
fun ScreenLogin(
    openLoginEmail: () -> Unit,
    openCreateEmail:() -> Unit
//    navController: NavController
) {
    val imageModifier = Modifier.size(440.dp)
    val imageIds = listOf(
        R.drawable.imagelogin1,
        R.drawable.imagelogin2,
        R.drawable.imagelogin3
    )

    var currentIndex by remember { mutableStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFF111111)),
    ) {
        Row {
            // Automatically switch images every 3 seconds
            //LaunchedEffect khối lệnh trong một coroutine và tiếp tục chạy lại khối lệnh này mỗi khi giá trị của khóa (key) thay đổi
            LaunchedEffect(key1 = currentIndex) {
                delay(3000)
                currentIndex = (currentIndex + 1) % imageIds.size
            }

            Crossfade(targetState = currentIndex) { index ->
                Image(
                    painter = painterResource(id = imageIds[index]),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = imageModifier
                )
            }
        }
        // content 2
        Row(
            modifier = Modifier
                .fillMaxWidth() // Đặt chiều rộng đầy đủ của màn hình
                .height(70.dp), // Đặt kích thước của Row để chiếm toàn bộ màn hình
            horizontalArrangement = Arrangement.Center, // Căn giữa các thành phần theo chiều ngang
            verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.application),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(40.dp)
            )
            // cách thành phần image và text
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "\uD835\uDE1A\uD835\uDE30\uD835\uDE24\uD835\uDE2C\uD835\uDE26\uD835\uDE35", color = Color.White, fontWeight = FontWeight.Bold,fontSize = 30.sp )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth() // Đặt chiều rộng đầy đủ của màn hình
                .height(110.dp), // Đặt chiều cao cụ thể là 80dp
            verticalArrangement = Arrangement.Center, // Căn giữa các thành phần theo chiều dọc
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Ả\uD835\uDE2F\uD835\uDE29 \uD835\uDE35\uD835\uDE33ự\uD835\uDE24 \uD835\uDE35\uD835\uDE2Aế\uD835\uDE31 \uD835\uDE35ừ \uD835\uDE23ạ\uD835\uDE2F \uD835\uDE23è,", color = Color(0xFF727272),fontWeight = FontWeight.Bold,fontSize = 25.sp)
            Text(text = "\uD835\uDE2F\uD835\uDE28\uD835\uDE22\uD835\uDE3A \uD835\uDE35\uD835\uDE33ê\uD835\uDE2F \uD835\uDE2Eà\uD835\uDE2F\uD835\uDE28 \uD835\uDE29ì\uD835\uDE2F\uD835\uDE29 \uD835\uDE24\uD835\uDE29í\uD835\uDE2F\uD835\uDE29", color = Color(0xFF727272),fontWeight = FontWeight.Bold,fontSize = 25.sp)
        }

        //content 3
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 6.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Button(onClick = {
//                navController.navigate(ScreenLogin.createEmail)
                openCreateEmail()
                             },
                modifier = Modifier.width(200.dp).height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Yellow,
                    contentColor = Color.Black
                )
            ) {
                Text(text = "Tạo một tài Khoản", fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                    openLoginEmail()
                             },
                modifier = Modifier.width(150.dp).height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF111111),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Đăng Nhập",fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,)
            }
        }

    }

}
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewScreenLogin() {
//    ApplicationSocketTheme {
//        ScreenLogin(
//            openCreateEmail = {}
//        )
//    }
//}

