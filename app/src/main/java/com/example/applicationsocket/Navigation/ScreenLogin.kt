package com.example.applicationsocket.Navigation

object ScreenLogin {
    const val createEmail = "createEmail"
    const val createOTP = "createOTP/{email}"
    const val createPass = "createPass"
    const val Login = "Login"

    fun createOTP(email: String) = "createOTP/$email"
}