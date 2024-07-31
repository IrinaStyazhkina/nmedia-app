package ru.netology.nmedia.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.model.Action
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.model.PushMessage
import java.util.logging.Logger
import kotlin.random.Random

class FCMService: FirebaseMessagingService() {

    private val channelId = "remote"
    private val gson = Gson()
    private val logger = Logger.getLogger(FCMService::class.java.name)

    override fun onCreate() {
        super.onCreate()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val recipient = Gson().fromJson(message.data["content"], PushMessage::class.java).recipientId
        val userId = AppAuth.getInstance().authState.value.id
        when (recipient) {
            userId, null -> {
                message.data["action"]?.let {
                    when(Action.lookup(it)) {
                        Action.LIKE -> handleLike(gson.fromJson(message.data["content"], Like::class.java))
                        Action.NEW_POST -> handleNewPost(gson.fromJson(message.data["content"], NewPost::class.java))
                        Action.UNKNOWN -> logger.warning("Unknown Action type received")
                    }
                }
            }
            else -> {
                AppAuth.getInstance().sendPushToken()
            }
        }
    }

    override fun onNewToken(token: String) {
        AppAuth.getInstance().sendPushToken(token)
    }

    private fun handleLike(content: Like) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_liked,
                    content.userName,
                    content.postAuthor,
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notify(notification)
    }

    private fun handleNewPost(content: NewPost) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_added_post,
                    content.author,
                )
            )
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(content.content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notify(notification)
    }

    private fun notify(notification: Notification) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || checkSelfPermission(
            Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this).notify(
                Random.nextInt(100_000), notification
            )
        }
    }

    data class Like(
        val userId: Long,
        val userName: String,
        val postId: Long,
        val postAuthor: String,
    )

    data class NewPost(
        val author: String,
        val content: String,
    )
}