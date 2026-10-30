package com.example.project_farhangadoca_mobilee

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class OrderStatusNotifier(private val context: Context) {

    fun showStatusChanged(order: OrderRecord) {
        createNotificationChannel()

        val intent = Intent(context, OrderActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            data = Uri.parse("gadoca://orders/${Uri.encode(order.id)}")
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_24)
            .setContentTitle("Status pesanan diperbarui")
            .setContentText("${order.productName}: ${order.status}")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Pesanan ${order.productName} berubah status menjadi ${order.status}.")
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context).notify(order.id, NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Pembaruan pesanan",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifikasi perubahan status pesanan di perangkat ini."
        }
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "order_status_updates"
        private const val NOTIFICATION_ID = 0
    }
}
