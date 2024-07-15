package com.example.applicationsocket
import java.util.Properties
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
class SeverMyEmail {
    fun sendEmail(to: String, subject: String, body: String, callback: (Boolean) -> Unit) {
        val username = "dohuunhan321@gmail.com" // Thay bằng email của bạn
        val password = "ioho cipk uxtc bzqm" // Thay bằng mật khẩu của bạn

        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
        }

        val session = Session.getInstance(props, object : javax.mail.Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(username, password)
            }
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(username))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
                this.subject = subject
                setText(body)
            }

            Thread {
                try {
                    Transport.send(message)
                    callback(true)
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback(false)
                }
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
            callback(false)
        }
    }
}