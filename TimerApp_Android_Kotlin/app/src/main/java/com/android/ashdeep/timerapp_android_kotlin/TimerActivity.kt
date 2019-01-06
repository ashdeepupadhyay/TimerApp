package com.android.ashdeep.timerapp_android_kotlin

import android.os.Bundle
import android.os.CountDownTimer
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import com.android.ashdeep.timerapp_android_kotlin.util.PrefUtil

import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.android.synthetic.main.content_timer.*

class TimerActivity : AppCompatActivity() {
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
        //ToDo:remove background timer and notification

    }

    override fun onPause() {
        super.onPause()
        if(timerState==TimerState.Running)
        {
            timer.cancel()
            //ToDo:start background timer and notification
        }
        else if(timerState==TimerState.Paused)
        {
            //ToDo:show notification
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

        if(timerState==TimerState.Stopped)
        {
            setNewTimerLength();
        }
        else
        {
            setPreviousTimerLength();
        }

        secondsremaing=if(  timerState==TimerState.Paused||timerState==TimerState.Running)
            PrefUtil.getSecondsRemaining(this)
        else
            timerLengthSeconds

        //ToDO:change secondsRemaining according to where the background timer stopped

        //Resume where we left off

        if(timerState==TimerState.Running)
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
