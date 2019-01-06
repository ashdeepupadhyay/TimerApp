package com.android.ashdeep.timerapp_android_kotlin.util

import android.content.Context
import android.os.health.TimerStat
import android.preference.Preference
import android.preference.PreferenceManager
import com.android.ashdeep.timerapp_android_kotlin.TimerActivity

class PrefUtil {
    companion object {
        fun getTimerLength(context: Context):Int{
            //place holder
            return 1;
        }

        private const val PREVIOUS_TIMER_LEGTH_SECONDS_ID="com.ashdeep.timer.previous_timer_length";

        fun getPreviousTimerLegthSeconds(context: Context):Long{
            val prefrences=PreferenceManager.getDefaultSharedPreferences(context);
            return prefrences.getLong(PREVIOUS_TIMER_LEGTH_SECONDS_ID,0);
        }

        fun setPreviousTimerLegthSeconds(seconds:Long,context: Context){
            val editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putLong(PREVIOUS_TIMER_LEGTH_SECONDS_ID,seconds);
            editor.apply();
        }
        private const val TIMER_STATE_ID="com.ashdeep.timer.timer_state";

        fun getTimerState(context: Context):TimerActivity.TimerState{
            val prefrences=PreferenceManager.getDefaultSharedPreferences(context);
            val ordinal=prefrences.getInt(TIMER_STATE_ID,0);
            return TimerActivity.TimerState.values()[ordinal];
        }

        fun setTimerState(state:TimerActivity.TimerState,context: Context){
            val editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
            val ordinal=state.ordinal;
            editor.putInt(TIMER_STATE_ID,ordinal);
            editor.apply();
        }


        private const val SECONDS_REMAINING_ID="com.ashdeep.timer.seconds_remaining";

        fun getSecondsRemaining(context: Context):Long{
            val prefrences=PreferenceManager.getDefaultSharedPreferences(context);
            return prefrences.getLong(SECONDS_REMAINING_ID,0);
        }

        fun setSecondsRemaining(seconds:Long,context: Context){
            val editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putLong(SECONDS_REMAINING_ID,seconds);
            editor.apply();
        }
    }
}