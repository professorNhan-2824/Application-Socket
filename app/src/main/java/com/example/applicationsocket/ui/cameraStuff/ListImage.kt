package com.example.applicationsocket.ui.cameraStuff

import android.net.http.UrlRequest
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.applicationsocket.data.UserIDModel
import com.example.applicationsocket.data.UserSessionViewModel
import com.example.applicationsocket.data.modelContentUser
import com.example.applicationsocket.ui.theme.ApplicationSocketTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.reflect.Modifier

// hàm này lấy dữ liệu nhánh status của user
fun getStatusUser(userID: String, onSuccess: (List<modelContentUser>) -> Unit, onError: (DatabaseError) -> Unit) {
    Log.e("TAG_useID", "getStatusUser: $userID")
    val database = FirebaseDatabase.getInstance()
    val userStatus = database.getReference("users").child(userID).child("content").child("content1")


    userStatus.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val imageLinks = mutableListOf<String>()
            for (snapshot in dataSnapshot.children) {
                val imageLink = snapshot.child("image").getValue(String::class.java)
                if (imageLink != null) {
                    imageLinks.add(imageLink)
                    Log.d("TAG_iamgeFirebase", "onDataChange: $imageLinks")
                }
            }
            // Xử lý danh sách imageLinks, ví dụ như hiển thị lên UI
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Xử lý lỗi
        }
    })
}
@Composable
fun DisplayUserImagesInGrid(userID: String) {
    val (statusList, setStatusList) = remember { mutableStateOf<List<modelContentUser>>(emptyList()) }
    val (error, setError) = remember { mutableStateOf<DatabaseError?>(null) }

    // Load data from Firebase
    LaunchedEffect(userID) {
        getStatusUser(userID, onSuccess = { statuses ->
            setStatusList(statuses)
        }, onError = { databaseError ->
            setError(databaseError)
            // Optionally show an error message
        })
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // 3 columns for the grid
        modifier = androidx.compose.ui.Modifier.fillMaxSize()
    ) {
        items(statusList.size) { index ->
            val status = statusList[index]
            ImageItem(imageUrl = status.image ?: "")
        }
    }
}

@Composable
fun ImageItem(imageUrl: String) {
    Box(
        modifier = androidx.compose.ui.Modifier
            .padding(4.dp)
            .aspectRatio(1f) // Ensures each item is a square
            .background(Color.LightGray) // Background color in case image doesn't load
    ) {
        // Use Coil for image loading
        val painter = rememberAsyncImagePainter(
            model = imageUrl,
            contentScale = ContentScale.Crop
        )

        Image(
            painter = painter,
            contentDescription = null,
            modifier = androidx.compose.ui.Modifier.fillMaxSize() // Ensures image fills the available space
        )
    }
}
@Composable
fun ListImage(Email: String,userModel: UserSessionViewModel) {
    val listState = rememberLazyListState()
    val backgroundColorLocket = Color(0xFF111111)
    Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .background(backgroundColorLocket)
            .padding(8.dp)
    ) {
        TopBar(toProfile = { /*TODO*/ }, toFriend = { /*TODO*/ }, userIDModel = userModel)

        DisplayUserImagesInGrid(userID = Email)

    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun testLisst() {
    ApplicationSocketTheme {
        ListImage(Email = "test",userModel = UserSessionViewModel())
    }
}