package com.android.ashdeep.timerapp_android_kotlin

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.android.ashdeep.timerapp_android_kotlin.util.NotificationUtil
import com.android.ashdeep.timerapp_android_kotlin.util.PrefUtil

import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.android.synthetic.main.content_timer.*
import java.util.*

class TimerActivity : AppCompatActivity() {
    companion object {
        fun setAlarm(context: Context,nowSeconds:Long,secondsRemaining:Long):Long{
            val wakeUpTime=(nowSeconds+secondsRemaining)*1000
            val alarmManager=context.getSystemService(Context.ALARM_SERVICE)as AlarmManager
            var intent=Intent(context,TimerExpired::class.java)//defines what happens when alarm goes off
            val pendingIntent=PendingIntent.getBroadcast(context,0,intent,0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,wakeUpTime,pendingIntent)
            PrefUtil.setAlarmSetTime(nowSeconds,context)
            return wakeUpTime
        }

        fun removeAlarm(context: Context)
        {
            val intent=Intent(context,TimerExpired::class.java)
            val pendingIntent=PendingIntent.getBroadcast(context,0,intent,0)
            val alarmManager=context.getSystemService(Context.ALARM_SERVICE)as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmSetTime(0,context)

        }

        val nowSeconds:Long
            get() = Calendar.getInstance().timeInMillis/1000
    }
    enum class TimerState{
        Stopped,Paused,Running
    }
    private  lateinit var  timer: CountDownTimer
    private var timerLengthSeconds =0L
    private var timerState=TimerState.Stopped
    private var secondsremaing=0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        setSupportActionBar(toolbar)

        supportActionBar?.setIcon(R.drawable.ic_timer);

        supportActionBar?.title="    TIMER";

        fab_start.setOnClickListener { v ->
            startTimer()
            timerState = TimerState.Running
            updateButtons()
        };

        fab_pause.setOnClickListener { v ->
            timer.cancel()
            timerState = TimerState.Paused
            updateButtons()
        }

        fab_stop.setOnClickListener { v ->
            timer.cancel()
            onTimerFinished()
        }
    }

    override fun onResume() {
        super.onResume()
        initTimer()
        Log.d("mytag","OnResume")
        removeAlarm(this)
        //hide notification
        NotificationUtil.hideTimerNotification(this)
    }

    override fun onPause() {
        super.onPause()
        if(timerState==TimerState.Running)
        {
            timer.cancel()
            val wakeUpTime= setAlarm(this, nowSeconds,secondsremaing)

            //show notification
            NotificationUtil.showTimerRunning(this,wakeUpTime)
        }
        else if(timerState==TimerState.Paused)
        {
            //show notification
            NotificationUtil.showTimerPaused(this)
        }

        PrefUtil.setPreviousTimerLegthSeconds(timerLengthSeconds,this);
        PrefUtil.setSecondsRemaining(secondsremaing,this);
        PrefUtil.setTimerState(timerState,this);
    }

    private fun startTimer()
    {
        timerState=TimerState.Running;

        timer=object :CountDownTimer(secondsremaing*1000,1000){
            override fun onFinish() =onTimerFinished();
            override fun onTick(millisUntilFinished: Long) {
               // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                secondsremaing=millisUntilFinished/1000
                updateCountdownUI()
            }
        }.start()
    }

    private fun setNewTimerLength()
    {
        val lengthInMinutes=PrefUtil.getTimerLength(this);
        timerLengthSeconds=(lengthInMinutes*60L)
        progress_countdown.max=timerLengthSeconds.toInt();
    }

    private fun setPreviousTimerLength()
    {
        timerLengthSeconds=PrefUtil.getPreviousTimerLegthSeconds(this);
        progress_countdown.max=timerLengthSeconds.toInt();
    }

    private fun updateCountdownUI()
    {
        val minutesUntilFinished=secondsremaing/60;
        val secondsInMinutesUntilFinished=secondsremaing-minutesUntilFinished*60;
        val secondsStr=secondsInMinutesUntilFinished.toString()
        textView_countdown.text="$minutesUntilFinished:${
        if(secondsStr.length==2)
        secondsStr
        else
            "0"+secondsStr
        }"
        progress_countdown.progress=(timerLengthSeconds-secondsremaing).toInt()
    }

    private fun updateButtons()
    {
        when(timerState)
        {
            TimerState.Running->{
                fab_start.isEnabled=false
                fab_pause.isEnabled=true
                fab_stop.isEnabled=true
            }
            TimerState.Stopped->{
                fab_start.isEnabled=true
                fab_pause.isEnabled=false
                fab_stop.isEnabled=false
            }
            TimerState.Paused->{
                fab_start.isEnabled=true
                fab_pause.isEnabled=false
                fab_stop.isEnabled=true
            }
        }
    }
    private fun onTimerFinished()
    {
        timerState=TimerState.Stopped;

        setNewTimerLength();

        progress_countdown.progress=0;

        PrefUtil.setSecondsRemaining(timerLengthSeconds,this);
        secondsremaing=timerLengthSeconds

        updateButtons()
        updateCountdownUI()
    }

    private fun initTimer()
    {
        timerState=PrefUtil.getTimerState(this);
        //Log.d("mytag","init timer timerstate"+timerState)
        if(timerState==TimerState.Stopped)
        {
            setNewTimerLength();
        }
        else
        {
            setPreviousTimerLength();
        }

        secondsremaing=if(  timerState==TimerState.Running||timerState==TimerState.Paused)
            PrefUtil.getSecondsRemaining(this)
        else
            timerLengthSeconds

        //change secondsRemaining according to where the background timer stopped
        val alarmSetTime=PrefUtil.getAlarmSetTime(this);

        if(alarmSetTime>0)
        {
            secondsremaing-= nowSeconds-alarmSetTime
        }

        if(secondsremaing<=0)
            onTimerFinished()
        else if(timerState==TimerState.Running)
            startTimer()

        updateButtons()
        updateCountdownUI()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_timer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
