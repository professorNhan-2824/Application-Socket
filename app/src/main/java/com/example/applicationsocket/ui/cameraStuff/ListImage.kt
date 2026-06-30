package com.example.applicationsocket.ui.cameraStuff

import android.net.http.UrlRequest
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
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
import com.example.applicationsocket.encodeEmail

// hàm này lấy dữ liệu nhánh status của user
fun getStatusUser(userID: String, onSuccess: (List<modelContentUser>) -> Unit, onError: (DatabaseError) -> Unit) {
    val encodedUserID = if (userID.contains("@")) encodeEmail(userID) else userID
    Log.e("TAG_useID", "getStatusUser: $encodedUserID")
    val database = FirebaseDatabase.getInstance()
    val userStatus = database.getReference("users").child(encodedUserID).child("content")

    userStatus.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val contentList = mutableListOf<modelContentUser>()
            for (snapshot in dataSnapshot.children) {
                if (snapshot.key == "content1") {
                    // Hỗ trợ cấu trúc dữ liệu cũ: users/$userID/content/content1/$postId
                    for (oldSnapshot in snapshot.children) {
                        val contentItem = oldSnapshot.getValue(modelContentUser::class.java)
                        if (contentItem != null) {
                            contentList.add(contentItem)
                        }
                    }
                } else {
                    // Hỗ trợ cấu trúc dữ liệu mới: users/$userID/content/$postId
                    val contentItem = snapshot.getValue(modelContentUser::class.java)
                    if (contentItem != null && (contentItem.image != null || contentItem.content != null)) {
                        contentList.add(contentItem)
                    }
                }
            }
            onSuccess(contentList)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            onError(databaseError)
        }
    })
}
@Composable
fun DisplayUserImagesInGrid(userID: String) {
    var statusList by remember { mutableStateOf<List<modelContentUser>>(emptyList()) }

    Log.d("LISTIMAGE_DEBUG", "=== DisplayUserImagesInGrid composed with userID='$userID' ===")

    DisposableEffect(userID) {
        val encodedUserID = if (userID.contains("@")) encodeEmail(userID) else userID
        val fullPath = "users/$encodedUserID/content"
        Log.d("LISTIMAGE_DEBUG", "[1] Firebase path: $fullPath")
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference(fullPath)

        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("LISTIMAGE_DEBUG", "[2] onDataChange fired. exists=${dataSnapshot.exists()}, childCount=${dataSnapshot.childrenCount}")
                val contentList = mutableListOf<modelContentUser>()
                for (snapshot in dataSnapshot.children) {
                    Log.d("LISTIMAGE_DEBUG", "[3] child key='${snapshot.key}' value=${snapshot.value}")
                    if (snapshot.key == "content1") {
                        Log.d("LISTIMAGE_DEBUG", "    -> content1 node, iterating children")
                        for (oldSnapshot in snapshot.children) {
                            val item = oldSnapshot.getValue(modelContentUser::class.java)
                            Log.d("LISTIMAGE_DEBUG", "    -> old item: image=${item?.image} content=${item?.content}")
                            if (item != null) contentList.add(item)
                        }
                    } else {
                        val item = snapshot.getValue(modelContentUser::class.java)
                        Log.d("LISTIMAGE_DEBUG", "    -> new item: image=${item?.image} content=${item?.content}")
                        if (item != null && (item.image != null || item.content != null)) {
                            contentList.add(item)
                        }
                    }
                }
                Log.d("LISTIMAGE_DEBUG", "[4] Total items parsed: ${contentList.size}")
                contentList.forEachIndexed { i, it -> Log.d("LISTIMAGE_DEBUG", "  item[$i] image=${it.image}") }
                statusList = contentList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("LISTIMAGE_DEBUG", "[ERROR] Firebase cancelled: ${error.message} code=${error.code}")
            }
        }

        ref.addValueEventListener(listener)
        onDispose {
            Log.d("LISTIMAGE_DEBUG", "[DISPOSE] Removing listener for $encodedUserID")
            ref.removeEventListener(listener)
        }
    }

    Log.d("LISTIMAGE_DEBUG", "[5] Composing grid with ${statusList.size} items")

    if (statusList.isEmpty()) {
        Log.d("LISTIMAGE_DEBUG", "[5a] statusList is EMPTY - grid will show nothing")
        Box(
            modifier = androidx.compose.ui.Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(text = "Đang tải ảnh...", color = Color.White)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = androidx.compose.ui.Modifier.fillMaxSize()
        ) {
            items(statusList.size) { index ->
                val status = statusList[index]
                Log.d("LISTIMAGE_DEBUG", "[6] Rendering item[$index] url=${status.image}")
                ImageItem(imageUrl = status.image ?: "")
            }
        }
    }
}

@Composable
fun ImageItem(imageUrl: String) {
    Log.d("LISTIMAGE_DEBUG", "[ImageItem] Rendering url='$imageUrl'")

    val painter = rememberAsyncImagePainter(
        model = imageUrl,
        contentScale = ContentScale.Crop
    )

    // Log Coil painter state changes
    val state = painter.state
    when (state) {
        is AsyncImagePainter.State.Loading -> Log.d("LISTIMAGE_DEBUG", "  [Coil] LOADING: $imageUrl")
        is AsyncImagePainter.State.Success -> Log.d("LISTIMAGE_DEBUG", "  [Coil] SUCCESS: $imageUrl")
        is AsyncImagePainter.State.Error   -> Log.e("LISTIMAGE_DEBUG", "  [Coil] ERROR: $imageUrl - ${state.result.throwable}")
        is AsyncImagePainter.State.Empty   -> Log.d("LISTIMAGE_DEBUG", "  [Coil] EMPTY (url not set): $imageUrl")
    }

    Box(
        modifier = androidx.compose.ui.Modifier
            .padding(4.dp)
            .aspectRatio(1f)
            .background(Color.LightGray)
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = androidx.compose.ui.Modifier.fillMaxSize()
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
        Box(
            modifier = androidx.compose.ui.Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            TopBar(toProfile = { /*TODO*/ }, toFriend = { /*TODO*/ }, userIDModel = userModel)
        }

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