package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var glideDownloadID: Long = 0
    private var loadAppDownloadID: Long = 0
    private var retrofitDownloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private var url: String? = null

    companion object {
        private const val CHANNEL_ID = "files_channel"
        private const val CHANNEL_NAME = "Files"
        private const val NOTIFICATION_ID = 0
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/master.zip"
        private const val LOADAPP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/master.zip"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        notificationManager = getSystemService(
            NotificationManager::class.java
        )

        createChannel(
            CHANNEL_ID,
            CHANNEL_NAME
        )

        radioButton.setOnClickListener {
            url = GLIDE_URL
        }

        radioButton2.setOnClickListener {
            url = LOADAPP_URL
        }

        radioButton3.setOnClickListener {
            url = RETROFIT_URL
        }

        custom_button.setOnClickListener {
            if (url == null) {
                Toast.makeText(this, getString(R.string.not_file_selected), Toast.LENGTH_LONG)
                    .show()
            } else {
                val downloadID = download()
                when (url) {
                    GLIDE_URL -> glideDownloadID = downloadID
                    LOADAPP_URL -> loadAppDownloadID = downloadID
                    RETROFIT_URL -> retrofitDownloadID = downloadID
                    else -> Unit
                }
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val repository = when (id) {
                glideDownloadID -> getString(R.string.description_glide_url)
                loadAppDownloadID -> getString(R.string.description_loadapp_url)
                retrofitDownloadID -> getString(R.string.description_retrofit_url)
                else -> ""
            }
            val status = when (intent?.getIntExtra(DownloadManager.COLUMN_STATUS, -1)) {
                DownloadManager.STATUS_SUCCESSFUL -> "Successful"
                else -> "Failure"
            }

            val detailIntent = Intent(context, DetailActivity::class.java)
            detailIntent.putExtra("repository", repository)
            detailIntent.putExtra("status", status)
            pendingIntent = PendingIntent.getActivity(
                applicationContext,
                NOTIFICATION_ID,
                detailIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            action = NotificationCompat.Action(
                R.drawable.ic_assistant_black_24dp,
                getString(R.string.notification_button),
                pendingIntent
            )
            sendNotification(
                getString(R.string.notification_description),
                context!!
            )
        }
    }

    private fun download(): Long {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        return downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download files"

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun sendNotification(messageBody: String, applicationContext: Context) {

        val builder = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(
                applicationContext
                    .getString(R.string.notification_title)
            )
            .setContentText(messageBody)
            .setAutoCancel(true)
            .addAction(action)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}
