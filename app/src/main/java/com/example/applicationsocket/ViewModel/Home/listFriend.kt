package com.example.applicationsocket.ViewModel.Home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
//import androidx.room.util.query
import com.example.applicationsocket.R
import com.example.applicationsocket.ViewModel.ProfileUser.CircularImage
import com.example.applicationsocket.ViewModel.ProfileUser.bodyScreen
import com.example.applicationsocket.ViewModel.ProfileUser.getImageProfileUser
import com.example.applicationsocket.ViewModel.ProfileUser.headScreen
import com.example.applicationsocket.ViewModel.ProfileUser.uploadData
import com.example.applicationsocket.ViewModel.ProfileUser.uploadPass
import com.example.applicationsocket.data.UserIDModel
import com.example.applicationsocket.data.UserSessionViewModel
import com.example.applicationsocket.data.addFriend
import com.example.applicationsocket.data.modelNameUser
import com.example.applicationsocket.data.modelUser
import com.example.applicationsocket.encodeEmail
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


@Composable
fun bodyListFriendScreen(userIdModel: UserIDModel, userid: String){
    Column(modifier = Modifier.fillMaxSize()) {
        // Các thành phần khác của BodyScreen

        Spacer(modifier = Modifier.weight(1f)) // Đẩy các thành phần khác lên trên

        // Tích hợp FriendScreen vào cuối BodyScreen
        FriendScreen(userIdModel, userid)
    }
}

@Composable
fun FriendScreen(userIdModel: UserIDModel, userid: String) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabs = listOf("Danh sách bạn bè", " Tìm kiếm bạn bè")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTabIndex == index) Color.Black else Color.Gray
                        )
                    }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> ListFriendScreen(userIdModel, userid)
            1 -> SearchFriendScreen(userIdModel, userid)
        }
    }
}
fun fetchFriendRequests(email: String, onSuccess: (List<Pair<String, Boolean>>) -> Unit) {
    val database = FirebaseDatabase.getInstance().reference
    val userEncoded = encodeEmail(email)

    val friendListRef = database.child("users").child(userEncoded).child("friendList")

    friendListRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                Log.d("FirebaseDatabase", "Snapshot exists")
                val friendRequests = snapshot.children.mapNotNull {
                    val friendEmail = it.child("email").getValue(String::class.java)
                    val isAccepted = it.child("status").getValue(Boolean::class.java) ?: false
                    Log.d("FirebaseDatabaseFriend", "Friend Email: $friendEmail, Status: $isAccepted")
                    if (friendEmail != null) Pair(friendEmail, isAccepted) else null
                }
                onSuccess(friendRequests)
            } else {
                Log.d("FirebaseDatabase", "No friend requests found")
                onSuccess(emptyList())
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("FirebaseDatabase", "Error fetching friend requests", error.toException())
            onSuccess(emptyList())
        }
    })
}





@Composable
fun ListFriendScreen(userIdModel: UserIDModel, userEmail: String) {
    var friendRequests by remember { mutableStateOf<List<Pair<String, Boolean>>>(emptyList()) }

    Log.e("ListFriendScreen", "ListFriendScreen: $userEmail")
    // Fetch friend requests when the composable is launched
    LaunchedEffect(userEmail) {
        fetchFriendRequests(userEmail) { requests ->
            friendRequests = requests
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(friendRequests) { request ->
            val friendEmail = request.first
            val isAccepted = request.second

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(85.dp)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = friendEmail,
                    color = if (isAccepted) Color.Green else Color.Gray,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )

                if (!isAccepted) {
                    Button(
                        onClick = {
                            acceptFriendRequest(userEmail, friendEmail) {
                                // Update the list to reflect the change
                                friendRequests = friendRequests.map {
                                    if (it.first == friendEmail) it.copy(second = true) else it
                                }
                            }
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                    ) {
                        Text("Accept")
                    }
                }

                Button(
                    onClick = {
                        deleteFriendRequest(userEmail, friendEmail) {
                            // Remove the friend request from the list
                            friendRequests = friendRequests.filter { it.first != friendEmail }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    )
                ) {
                    Text("Delete")
                }
            }
        }
    }
}


fun acceptFriendRequest(userEmail: String, friendEmail: String, onComplete: () -> Unit) {
    val database = FirebaseDatabase.getInstance().reference
    val userEncoded = encodeEmail(userEmail)
    val friendEncoded = encodeEmail(friendEmail)

    val friendRequestRef = database.child("users").child(userEncoded).child("friendList").child(friendEmail)

    // Set the status to true
    friendRequestRef.child("status").setValue(true).addOnCompleteListener {
        onComplete()
    }
}

fun deleteFriendRequest(userEmail: String, friendEmail: String, onComplete: () -> Unit) {
    val database = FirebaseDatabase.getInstance().reference
    val userEncoded = encodeEmail(userEmail)
    val friendEncoded = encodeEmail(friendEmail)

    val userFriendRequestRef = database.child("users").child(userEncoded).child("friendList").child(friendEncoded)
    val friendFriendRequestRef = database.child("users").child(friendEncoded).child("friendList").child(userEncoded)

    // Remove the friend request for both users
    userFriendRequestRef.removeValue().addOnCompleteListener {
        friendFriendRequestRef.removeValue().addOnCompleteListener {
            onComplete()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFriendScreen(userIdModel: UserIDModel, userid: String) {
    val userInformation = userIdModel.userID.observeAsState().value
    var search by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<modelUser>>(emptyList()) }
    val context = LocalContext.current
    val searchEmail = remember { mutableStateOf("") }

    Column {
        Row(modifier = Modifier.padding(16.dp)) {
            TextField(
                value = search,
                onValueChange = {
                    search = it
                },
                label = { Text("Tìm kiếm", color = Color(0xFFb4b4b4)) },
                modifier = Modifier
                    .weight(0.8f) // Chiếm 80% chiều rộng
                    .padding(end = 8.dp),
                textStyle = TextStyle(color = Color.White),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFF616161),
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
            )
            FloatingActionButton(
                onClick = {
                    if(search == userInformation?.email){
                        Toast.makeText(context, "Không thể tìm kiếm chính mình", Toast.LENGTH_SHORT).show()
                        return@FloatingActionButton
                    }else if(search != userInformation?.email){
                        searchEmail.value = search
                        searchForUsers(search) { results -> searchResults = results }
                    }


                },
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.CenterVertically), // Align the FAB vertically
                containerColor = Color(0xFF111111),
                contentColor = Color.White
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = "Search"
                )
            }
        }
        listFriend(searchResults,searchEmail.value,userIdModel.userID.value?.email.toString())

    }
}


//hàm lấy ảnh  này còn lỗi chưa lấy được ảnh
fun getImageFriend(searchEmail: String, onSuccess: (modelNameUser?) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val encodedEmail = encodeEmail(searchEmail) // Encode email to be a valid Firebase path
    val userRef = database.getReference("users").child(encodedEmail).child("information")

    userRef.get()
        .addOnSuccessListener { snapshot ->
            val imageProfileUser = snapshot.child("imageProfileUser").getValue(String::class.java)
            val firstName = snapshot.child("firstName").getValue(String::class.java)
            val lastName = snapshot.child("lastName").getValue(String::class.java)

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
fun listFriend(searchResults: List<modelUser>,searchEmail : String, email: String) {
    var isButtonEnabled by remember { mutableStateOf(true) }
    var userImage by remember { mutableStateOf<modelNameUser?>(null) }
    val context = LocalContext.current
    LaunchedEffect(searchEmail) {
        getImageFriend(searchEmail) { image ->
            userImage = image
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(searchResults) { user ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        85.dp
                    )
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                    CircularImage(
                        userImage?.imageProfileUser,
                        "profileFriend"
                    )
                Text(
                    text = user.email ?: "",
                    color = Color.White,
                    modifier = Modifier
                        .weight(0.4f)
                        .padding(start = 8.dp)
                )
                Button(
                    onClick = {
                        if(isButtonEnabled){
                            sendEmailFriend(user.email ?: "",email)
                            Toast.makeText(context, "Đã gửi lời mời kết bạn", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                    },
                    modifier = Modifier
                        .width(78.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =  Color.Yellow,
                        contentColor =  Color.Black
                    ),
                    enabled = isButtonEnabled
                ) {
                    Text("Add")
                }
                }


            }
        }
}

//hàm này có chức khi add thì lưu id người add (false) và người được add (false) vào nhánh friendlist của nhau
fun sendEmailFriend(emailFriend: String, email: String){
    val database = FirebaseDatabase.getInstance().getReference()

    // Mã hóa địa chỉ email trước khi sử dụng chúng trong đường dẫn Firebase
    val emailFriendcode = encodeEmail(emailFriend)
    val emailcode = encodeEmail(email)

    // Sử dụng các biến mã hóa cho các tham chiếu đường dẫn
    val friendlitref = database.child("users").child(emailcode).child("friendList")
    val emaillistref = database.child("users").child(emailFriendcode).child("friendList")

    val senEmailFriend = addFriend(emailFriendcode, true)
    val sendEmail = addFriend(emailcode, false)

    // Sử dụng email mã hóa cho các khóa của bản ghi
    friendlitref.child(emailFriendcode).setValue(senEmailFriend)
    emaillistref.child(emailcode).setValue(sendEmail)
}


fun accepFriend(emailFriend: String,email: String){
    val database = FirebaseDatabase.getInstance().getReference()

    val emailFriendcode = encodeEmail(emailFriend)
    val emailcode = encodeEmail(email)

    val friendlitref = database.child("users").child(emailFriendcode).child("friendList")
    val emaillistref = database.child("users").child(emailcode).child("friendList")

//    val accepemailFriend = addFriend(emailFriendcode, true)
    val accepemail = addFriend(emailcode, true)
//    friendlitref.child(emailcode).setValue(accepemailFriend)
    emaillistref.child(emailFriendcode).setValue(accepemail)
}
//


fun searchForUsers(email: String, onSuccess: (List<modelUser>) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val usersRef = database.getReference("users")

    usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
        ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val users = snapshot.children.mapNotNull { it.getValue(modelUser::class.java) }
                onSuccess(users)
            } else {
                onSuccess(emptyList())
            }
        }

        override fun onCancelled(error: DatabaseError) {
            onSuccess(emptyList())
        }
    })
}

@Composable
fun mainListFriend( useridModel: UserIDModel, userID: String, comback: () -> Unit){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111111))
    ) {

        headScreen(conback = comback)
        bodyListFriendScreen(userIdModel = useridModel,userid = userID)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewListFriend(){
    mainListFriend(comback = {}, useridModel = UserIDModel(), userID = "123")
//    listFriend(searchResults = listOf(modelUser("123","dfsb")),searchEmail = "123", email = "123")
}