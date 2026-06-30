package com.example.applicationsocket.data

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

data class modelNameUser(
    val firstName: String? = null,
    val lastName: String? = null,
    var imageProfileUser: String? = null
)
class UserSessionViewModel : ViewModel() {
    private val _userInformation = MutableLiveData<modelNameUser?>()

    val userInformation: LiveData<modelNameUser?> = _userInformation

    fun setUserInformation(userInformation: modelNameUser) {
        _userInformation.value = userInformation
    }

    fun logOut() {
        FirebaseAuth.getInstance().signOut()
        _userInformation.value = null
    }
}
fun getUserName(email: String, context: Context, userViewModel: UserSessionViewModel, togoHome: () -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val userRef = database.getReference("users").child(email).child("information")

    userRef.get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val userInformation = task.result?.getValue(modelNameUser::class.java)
            if (userInformation != null) {
                userViewModel.setUserInformation(userInformation)
                togoHome()
            } else {
                Toast.makeText(context, "Không có thông tin người dùng", Toast.LENGTH_SHORT).show()
            }
        } else {
            val errorMessage = task.exception?.message ?: "Lỗi không xác định"
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
}
