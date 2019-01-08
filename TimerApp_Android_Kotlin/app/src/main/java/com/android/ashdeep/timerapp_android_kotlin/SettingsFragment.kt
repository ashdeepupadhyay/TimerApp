package com.android.ashdeep.timerapp_android_kotlin

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat

class SettingsFragment:PreferenceFragmentCompat() {
    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        addPreferencesFromResource(R.xml.preferences)
    }
}