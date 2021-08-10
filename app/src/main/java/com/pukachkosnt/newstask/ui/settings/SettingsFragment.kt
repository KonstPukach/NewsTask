package com.pukachkosnt.newstask.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import com.pukachkosnt.newstask.R
import com.pukachkosnt.notifications.PeriodicIntentGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class SettingsFragment : PreferenceFragmentCompat() {
    private var notificationsPref: SwitchPreferenceCompat? = null
    private var notificationIntervalPref: SeekBarPreference? = null

    private val notifWorkManagerUtil: PeriodicIntentGenerator by inject()

    private val flowTimeInterval: MutableSharedFlow<Int> = MutableSharedFlow(0)

    @FlowPreview
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        val activityNews = (context as AppCompatActivity)
        activityNews.supportActionBar?.setDisplayShowHomeEnabled(true)
        activityNews.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        notificationsPref = findPreference(getString(R.string.notifications_pref))
        notificationIntervalPref = findPreference(getString(R.string.time_interval_pref))

        // it's not bound to lifecycleScope to be sure settings are saved and a workRequest is started
        CoroutineScope(Dispatchers.Default).launch {
            flowTimeInterval.debounce(FLOW_TIMEOUT_MILLS).collect {
                notifWorkManagerUtil.changeInterval(it * MILLS_IN_MINUTE)
            }
        }

        notificationsPref?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean) {
                notifWorkManagerUtil.changeInterval(
                    (notificationIntervalPref?.value ?: 0) * MILLS_IN_MINUTE
                )
            } else {
                notifWorkManagerUtil.cancel()
            }
            true
        }

        notificationIntervalPref?.setOnPreferenceChangeListener { _, newValue ->
            lifecycleScope.launch { flowTimeInterval.emit(newValue as Int) }
            true
        }
    }

    companion object {
        fun newInstance() = SettingsFragment()
        private const val MILLS_IN_MINUTE = 1000 * 60L
        private const val TAG = "SettingsFragment"

        private const val FLOW_TIMEOUT_MILLS = 500L
    }
}