package com.example.applicationsocket.ViewModel.ShareModel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf

class SharedEmailModel : ViewModel() {
    // Mutable state để lưu trữ ID user
    private val _userId = mutableStateOf<String?>(null)

    // Getter cho trạng thái userId
    val userIdState = _userId

    // Phương thức để cập nhật ID user
    fun setUserId(userId: String) {
        _userId.value = userId
    }
}
