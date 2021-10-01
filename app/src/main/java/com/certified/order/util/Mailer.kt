package com.certified.order.util

import io.reactivex.Completable
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object Mailer {

    // NOTE : if you will use google email as admin you need to enable
    //  less secure app from security of the gmail account
    // https://www.google.com/settings/security/lesssecureapps


//    @SuppressLint("CheckResult")
    fun sendMail(email: String, subject: String, message: String): Completable {

        return Completable.create { emitter ->

            val props: Properties = Properties().also {
                it["mail.smtp.host"] = "smtp.gmail.com"
                it["mail.smtp.socketFactory.port"] = "465"
                it["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
                it["mail.smtp.auth"] = "true"
                it["mail.smtp.port"] = "465"
            }

            val session = Session.getDefaultInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(Config.ADMIN_EMAIL, Config.ADMIN_PASSWORD)
                }
            })

            try {
                MimeMessage(session).let { mime ->
                    mime.setFrom(InternetAddress(Config.ADMIN_EMAIL))
                    mime.addRecipient(Message.RecipientType.TO, InternetAddress(email))
                    mime.subject = subject
                    mime.setText(message)
                    Transport.send(mime)
                }
            } catch (e: MessagingException) {
                emitter.onError(e)
            }
            emitter.onComplete()
        }
    }
}