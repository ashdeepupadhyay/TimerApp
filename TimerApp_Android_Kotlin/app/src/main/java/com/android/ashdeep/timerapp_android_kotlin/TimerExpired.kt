package com.android.ashdeep.timerapp_android_kotlin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.android.ashdeep.timerapp_android_kotlin.util.NotificationUtil
import com.android.ashdeep.timerapp_android_kotlin.util.PrefUtil

class TimerExpired : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        //showNotification
        NotificationUtil.showTimerExpired(context)
        PrefUtil.setTimerState(TimerActivity.TimerState.Stopped,context)
        PrefUtil.setAlarmSetTime(0,context)
    }
}
