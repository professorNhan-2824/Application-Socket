package com.example.applicationsocket.data

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class modelUser(
    val email: String? = null,
    val password: String? = null,
)

class UserIDModel: ViewModel() {
    private val _userID = MutableLiveData<modelUser>()
    val userID: LiveData<modelUser?> = _userID

    fun setUserID(userID: modelUser) {
        _userID.value = userID
    }
}
fun getUserEmail(userID: String, userId: UserIDModel) {
    val database = FirebaseDatabase.getInstance()
    val userRef = database.getReference("users").child(userID)

    userRef.get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val userID = task.result?.getValue(modelUser::class.java)
            if (userID != null) {
                userId.setUserID(userID)
            } else {
//                Toast.makeText(context, "Không có thông tin người dùng", Toast.LENGTH_SHORT).show()
            }
        } else {
            val errorMessage = task.exception?.message ?: "Lỗi không xác định"
//            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
}


