package com.android.ashdeep.timerapp_android_kotlin.util

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.android.ashdeep.timerapp_android_kotlin.AppConstants
import com.android.ashdeep.timerapp_android_kotlin.R
import com.android.ashdeep.timerapp_android_kotlin.TimerActivity
import com.android.ashdeep.timerapp_android_kotlin.TimerNotificationActionReceiver
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*

class NotificationUtil {
    companion object {
        private const val CHANNEL_ID_TIMER="Menu_Timer"
        private const val CHANNEL_NAME_TIMER="Timer_App_Timer"
        private const val TIMER_ID=0

        fun showTimerExpired(context: Context)
        {
            val startIntent= Intent(context, TimerNotificationActionReceiver::class.java)
            startIntent.action=AppConstants.ACTION_START
            val startPendingIntent=PendingIntent.getBroadcast(context,0,startIntent,PendingIntent.FLAG_UPDATE_CURRENT)

            val nBuilder=getBasicNotificationBuilder(context, CHANNEL_ID_TIMER,true)
            nBuilder.setContentTitle("Timer Expired!")
                .setContentText("Start Again?")
                .setContentIntent(getPendingIntentWithStack(context,TimerActivity::class.java))
                .addAction(R.drawable.ic_play,"START",startPendingIntent)//icon,title,and when clicked we want this to be called

            //Notification Manager
            val nManager=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //as we are supporting Oreo and newer Android version we need to create notification channel
            nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER,true)

            nManager.notify(TIMER_ID,nBuilder.build())

        }

        fun showTimerRunning(context: Context,wakeUpTime:Long)
        {
            val stopIntent= Intent(context, TimerNotificationActionReceiver::class.java)
            stopIntent.action=AppConstants.ACTION_STOP
            val stopPendingIntent=PendingIntent.getBroadcast(context,0,stopIntent,PendingIntent.FLAG_UPDATE_CURRENT)

            val pauseIntent= Intent(context, TimerNotificationActionReceiver::class.java)
            pauseIntent.action=AppConstants.ACTION_PAUSE
            val pausePendingIntent=PendingIntent.getBroadcast(context,0,pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT)

            val df=SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)

            val nBuilder=getBasicNotificationBuilder(context, CHANNEL_ID_TIMER,true)
            nBuilder.setContentTitle("Timer is Running!")
                .setContentText("End:${df.format(Date(wakeUpTime))}")
                .setContentIntent(getPendingIntentWithStack(context,TimerActivity::class.java))
                .setOngoing(true)//user cannot dismiss it manually need to remove it from code
                .addAction(R.drawable.ic_stop,"STOP",stopPendingIntent)//icon,title,and when clicked we want this to be called
                .addAction(R.drawable.ic_pause,"PAUSE",pausePendingIntent)
            //Notification Manager
            val nManager=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //as we are supporting Oreo and newer Android version we need to create notification channel
            nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER,true)

            nManager.notify(TIMER_ID,nBuilder.build())

        }

        fun showTimerPaused(context: Context)
        {
            val resumeIntent= Intent(context, TimerNotificationActionReceiver::class.java)
            resumeIntent.action=AppConstants.ACTION_START
            val resumePendingIntent=PendingIntent.getBroadcast(context,0,resumeIntent,PendingIntent.FLAG_UPDATE_CURRENT)

            val nBuilder=getBasicNotificationBuilder(context, CHANNEL_ID_TIMER,true)
            nBuilder.setContentTitle("Timer is Paused!")
                .setContentText("Resume?")
                .setContentIntent(getPendingIntentWithStack(context,TimerActivity::class.java))
                .setOngoing(true)
                .addAction(R.drawable.ic_play,"RESUME",resumePendingIntent)//icon,title,and when clicked we want this to be called

            //Notification Manager
            val nManager=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //as we are supporting Oreo and newer Android version we need to create notification channel
            nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER,true)

            nManager.notify(TIMER_ID,nBuilder.build())

        }

        fun hideTimerNotification(context: Context)
        {
            //Notification Manager
            val nManager=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManager.cancel(TIMER_ID)
        }

        private fun getBasicNotificationBuilder(context: Context, channelID: String, playSound: Boolean): NotificationCompat.Builder
        {
            val NotificationSound:Uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val nBuilder=NotificationCompat.Builder(context,channelID)
                .setSmallIcon(R.drawable.ic_timer)
                .setAutoCancel(true)//when user clicks on notification it will dismiss the notifcation

            if(playSound)nBuilder.setSound(NotificationSound)
            return nBuilder;
        }

        private fun <T> getPendingIntentWithStack(context:Context,javaClass: Class<T>):PendingIntent
        {
            val resultIntent=Intent(context,javaClass)
            resultIntent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP //if the activity is open it will not open again

            val stackBuilder=TaskStackBuilder.create(context)
            stackBuilder.addParentStack(javaClass)
            stackBuilder.addNextIntent(resultIntent)//the activity which we want to open

            return stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT)
        }

        @TargetApi(26)
        //ExtensionFunction
        private fun NotificationManager.createNotificationChannel(channelID: String,channelName:String,playSound: Boolean)
        {
         if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
         {
             val channelImportance=if(playSound)NotificationManager.IMPORTANCE_DEFAULT else NotificationManager.IMPORTANCE_LOW

             //creating a Notification Channel
             val nChannel=NotificationChannel(channelID,channelName,channelImportance)
             nChannel.enableLights(true);
             nChannel.lightColor=Color.BLUE;
             this.createNotificationChannel(nChannel)

         }
        }
    }
}