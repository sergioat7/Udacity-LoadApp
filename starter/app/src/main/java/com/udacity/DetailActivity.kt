package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val notificationManager = getSystemService(
            NotificationManager::class.java
        )
        notificationManager.cancelAll()

        textViewFilename.text = intent.getStringExtra("repository")
        textViewStatus.text = intent.getStringExtra("status")
        button.setOnClickListener {
            onBackPressed()
        }
    }
}
