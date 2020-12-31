package com.a3acdhmwnotifyandbroadcast

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import com.a3acdhmwnotifyandbroadcast.databinding.ActivityMainBinding
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    companion object {
        private const val CLICK_BTN_TAG = "CLICK_BTN_TAG"
        private const val BUTTON_ACTION = "BUTTON_ACTION"
        private const val CHANNEL_ID = "MY_CHANNEL"
        private const val NOTIFY_ACTION = "NOTIFY_ACTION"
        private const val KEY_TEXT_REPLY = "KEY_TEXT_REPLY"

    }

    private lateinit var binding: ActivityMainBinding
    private val buttonClickReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(CLICK_BTN_TAG, "User tap on btn")
        }
    }

    private val notificationMsgReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            binding.tvTextFromReply.text =
                RemoteInput.getResultsFromIntent(intent)?.getCharSequence(KEY_TEXT_REPLY)

            /*val repliedNotification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText("Send!!!")
                .build()*/

            /*NotificationManagerCompat.from(applicationContext)
                .notify(3, repliedNotification)*/
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(buttonClickReceiver, IntentFilter(BUTTON_ACTION))
        registerReceiver(notificationMsgReceiver, IntentFilter(NOTIFY_ACTION))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        setupListeners()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(buttonClickReceiver)
        unregisterReceiver(notificationMsgReceiver)
    }

    private fun setupBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupListeners() {
        binding.btnOpenMyInfo.setOnClickListener{
            goToMyInfo()
        }

        binding.btnOpenMap.setOnClickListener{
            openMap()
        }

        binding.btnLog.setOnClickListener {
            Log.d(CLICK_BTN_TAG, "Button btnLog was clicked")
            startButtonClickReceiver()
        }

        binding.btnJustNotify.setOnClickListener {
            sendSimpleNotification()
        }

        binding.btnNotifyWithAction.setOnClickListener {
            sendNotificationWithButton()
        }

        binding.btnNotifyWithReply.setOnClickListener {
            sendNotificationWithReply()
        }

        binding.btnDownloadNotify.setOnClickListener {
            sendNotificationWithProgress()
        }
    }

    private fun openMap() {
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:50.4546600, 30.5238000"))
        mapIntent.setPackage("com.google.android.apps.maps")
        mapIntent.resolveActivity(packageManager)?.also {
            startActivity(mapIntent)
        }
    }

    private fun goToMyInfo() {
        MyInfoActivity.start(this, "Rodion Gra", 18)
    }

    private fun startButtonClickReceiver() {
        val intent = Intent()
        intent.action = BUTTON_ACTION
        sendBroadcast(intent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, "name", importance).apply {
                description = "descriptionText"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendSimpleNotification() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Simple notification")
            .setContentText("Some text <<<simple notification>>>")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        createNotificationChannel()
        NotificationManagerCompat.from(this).notify(1, builder.build())
    }

    private fun sendNotificationWithButton() {
        val intent = Intent(this, MainActivity::class.java).apply{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            2,
            intent,
            0
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Notification with button")
            .setContentText("Some text <<<notification with button>>>")
            .addAction(R.drawable.ic_launcher_foreground, "Open main activity", pendingIntent)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        createNotificationChannel()

        NotificationManagerCompat.from(this).notify(2, builder.build())
    }

    private fun sendNotificationWithReply() {
        val remoteInput: RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY).run {
            setLabel("Type text")
            build()
        }

        val intent = Intent(NOTIFY_ACTION)
        val replyPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                applicationContext,
                3,
                intent,
                0
            )


        // Create the reply action and add the remote input.
        val action: NotificationCompat.Action =
            NotificationCompat.Action.Builder(
                R.drawable.ic_launcher_foreground,
                getString(R.string.reply_action_title), replyPendingIntent)
                .addRemoteInput(remoteInput)
                .build()

        // Build the notification and add the action.
        val notificationWithReply = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Title notify with reply button")
                .setContentText("Text msg with reply button")
                .addAction(action)
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat.from(applicationContext)
            .notify(3, notificationWithReply)
    }

    private fun sendNotificationWithProgress() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle("Download Title")
            setContentText("Download in progress")
            setSmallIcon(R.drawable.ic_launcher_background)
            priority = NotificationCompat.PRIORITY_LOW
        }
        val progressMax = 100
        var progressCurrent = 0

        val executor = Executors.newSingleThreadExecutor()

        NotificationManagerCompat.from(this).apply {
            // Issue the initial notification with zero progress
            builder.setProgress(progressMax, progressCurrent, false)
            notify(4, builder.build())

            executor.execute{
                for(it in 1..11) {
                    progressCurrent += 10
                    builder.setProgress(100, progressCurrent, false)
                    builder.setContentText("Downloading... ${progressCurrent}%")
                    notify(4, builder.build())
                    Thread.sleep(300)
                }

                builder.setContentText("Download complete")
                    .setProgress(100, 100, false)
                notify(4, builder.build())
            }

        }

    }
}