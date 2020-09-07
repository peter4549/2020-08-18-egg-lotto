package com.duke.xial.elliot.kim.kotlin.egglotto.activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.duke.xial.elliot.kim.kotlin.egglotto.*
import com.duke.xial.elliot.kim.kotlin.egglotto.adapters.FragmentStateAdapter
import com.duke.xial.elliot.kim.kotlin.egglotto.broadcast_receiver.AlarmReceiver
import com.duke.xial.elliot.kim.kotlin.egglotto.broadcast_receiver.DeviceBootReceiver
import com.duke.xial.elliot.kim.kotlin.egglotto.dialog_fragment.EndDialogFragment
import com.duke.xial.elliot.kim.kotlin.egglotto.error_handler.ErrorHandler
import com.duke.xial.elliot.kim.kotlin.egglotto.fragments.EggBreakingFragment
import com.duke.xial.elliot.kim.kotlin.egglotto.fragments.HistoryFragment
import com.duke.xial.elliot.kim.kotlin.egglotto.fragments.SettingsFragment
import com.duke.xial.elliot.kim.kotlin.egglotto.fragments.TodayHoroscopeFragment
import com.duke.xial.elliot.kim.kotlin.egglotto.view_model.ViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_drawer.*
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: ViewModel
    private lateinit var alarmManager: AlarmManager
    private lateinit var endDialogFragment: EndDialogFragment
    private lateinit var mediaPlayer: MediaPlayer
    private var soundState = true
    private var weeklyAlarmState = false
    private val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.DAY_OF_WEEK, WEEKLY_ALARM_DAY_OF_WEEK)
        set(Calendar.HOUR_OF_DAY, WEEKLY_ALARM_HOUR_OF_DAY)
        set(Calendar.MINUTE, WEEKLY_ALARM_MINUTE)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    private val tabTexts = arrayOf(
        "달걀깨기", "오늘운세", "당첨결과", "기록"
    )
    val eggBreakingFragment = EggBreakingFragment()
    val historyFragment = HistoryFragment()
    val todayHoroscopeFragment = TodayHoroscopeFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_drawer)

        setupTimber()

        endDialogFragment = EndDialogFragment(this)
        errorHandler = ErrorHandler(this)

        alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mediaPlayer = MediaPlayer.create(this, R.raw.egg_cracking_sound)

        val viewModelFactory =
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        viewModel = ViewModelProvider(this, viewModelFactory)[ViewModel::class.java]
        viewModel.getAll().observe(this, androidx.lifecycle.Observer { lottoNumbersList ->
            when (viewModel.state) {
                ViewModel.INITIALIZE -> {
                    historyInitialized = true
                    historyFragment.lottoNumbersRecyclerViewAdapter.insertAll(lottoNumbersList)
                    viewModel.state = ViewModel.INITIALIZED
                }
                ViewModel.INSERT -> {
                    historyFragment.lottoNumbersRecyclerViewAdapter.insert(
                        lottoNumbersList[lottoNumbersList.size - 1], 0 //lottoNumbersList.size - 1
                    )
                    ++historiesAddedRange
                }
                ViewModel.DELETE -> historyFragment.lottoNumbersRecyclerViewAdapter.remove(viewModel.changedLottoNumbers)
            }
        })

        // For testing
        // viewModel.nukeTable()

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)
        toolbar.setNavigationIcon(R.drawable.ic_raw_egg_32)

        initializeTabLayoutViewPager(tab_layout, view_pager)

        soundState = getSoundState()
        weeklyAlarmState = getWeeklyAlarmState()

        switch_sound.isChecked = soundState
        switch_sound.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showToast(this, getString(R.string.sound_on))
                text_view_sound_state.text = getString(R.string.sound_on)
            }
            else {
                showToast(this, getString(R.string.sound_off))
                text_view_sound_state.text = getString(R.string.sound_off)
            }
            soundState = isChecked
        }

        switch_weekly_alarm.isChecked = weeklyAlarmState
        switch_weekly_alarm.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showToast(this, getString(R.string.weekly_alarm_on))
                setWeeklyAlarm()
            }
            else {
                showToast(this, getString(R.string.weekly_alarm_off))
                cancelWeeklyAlarm()
            }
            weeklyAlarmState = isChecked
        }

        text_view_settings.setOnClickListener {
            startSettingsFragment()
        }

        text_view_qr_code.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setPrompt(getString(R.string.have_the_qr_code_in_the_square))
            integrator.setBeepEnabled(false)
            integrator.setBarcodeImageEnabled(true)
            integrator.captureActivity = CaptureActivity::class.java
            integrator.initiateScan()
        }

        text_view_share.setOnClickListener {
            shareApplication(this)
        }
    }

    private fun setupTimber() {
        Timber.plant(Timber.DebugTree())
    }

    private fun initializeTabLayoutViewPager(tabLayout: TabLayout, viewPager: ViewPager2) {
        viewPager.adapter = FragmentStateAdapter(this)
        view_pager.isUserInputEnabled = true

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.tag = position
            tab.text = tabTexts[position]
        }.attach()
    }

    override fun onBackPressed() {
        if (drawer_layout_activity_main.isDrawerOpen(GravityCompat.END))
            drawer_layout_activity_main.closeDrawer(GravityCompat.END)
        else
            when {
                supportFragmentManager.findFragmentByTag(TAG_LICENSES_FRAGMENT) != null -> super.onBackPressed()
                supportFragmentManager.findFragmentByTag(TAG_SETTINGS_FRAGMENT) != null -> super.onBackPressed()
                supportFragmentManager.findFragmentByTag(TAG_WEB_VIEW_FRAGMENT) != null -> super.onBackPressed()
                else -> endDialogFragment.show(supportFragmentManager, TAG)
            }
    }

    override fun onPause() {
        saveOptionStates()
        super.onPause()
    }

    private fun saveOptionStates() {
        val preferences = getSharedPreferences(PREFERENCES_OPTIONS, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean(KEY_SOUND_STATE, soundState)
        editor.putBoolean(KEY_WEEKLY_ALARM_STATE, weeklyAlarmState)
        editor.apply()
    }

    private fun getSoundState() =
        getSharedPreferences(PREFERENCES_OPTIONS, Context.MODE_PRIVATE)
            .getBoolean(KEY_SOUND_STATE, true)

    private fun getWeeklyAlarmState() =
        getSharedPreferences(PREFERENCES_OPTIONS, Context.MODE_PRIVATE)
            .getBoolean(KEY_WEEKLY_ALARM_STATE, false)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_options -> {
                drawer_layout_activity_main.openDrawer(GravityCompat.END)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setWeeklyAlarm() {
        val switchedTime = Calendar.getInstance().timeInMillis
        if (switchedTime > calendar.timeInMillis)
            setBlockNotification()
        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT
        )

        val receiver = ComponentName(this, DeviceBootReceiver::class.java)
        packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7, pendingIntent
        )
    }

    private fun setBlockNotification() {
        getSharedPreferences(PREFERENCES_OPTIONS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_BLOCK_NOTIFICATION, true).apply()
    }

    private fun cancelWeeklyAlarm() {
        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0, alarmIntent, PendingIntent.FLAG_NO_CREATE
        )
        try {
            alarmManager.cancel(pendingIntent)
        } catch (e: NullPointerException) {
            e.printStackTrace()
            println("$TAG: notification not set")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode != RESULT_CANCELED) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    showToast(this, getString(R.string.failed_to_load_web_page))
                    println("$TAG: result.contents is null")
                } else {
                    val url = result.contents
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
                }
            } else {
                println("$TAG: result is null")
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    fun startSound() {
        if(soundState)
            mediaPlayer.start()
    }

    private fun startSettingsFragment() {
        drawer_layout_activity_main.closeDrawer(GravityCompat.END)
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .setCustomAnimations(
                R.anim.anim_slide_in_from_bottom,
                R.anim.anim_slide_out_to_top,
                R.anim.anim_slide_in_from_top,
                R.anim.anim_slide_out_to_bottom
            ).replace(
                R.id.constraint_layout_activity_main,
                SettingsFragment(),
                TAG_SETTINGS_FRAGMENT
            ).commit()
    }

    companion object {
        const val KEY_BLOCK_NOTIFICATION = "key_block_notification"
        const val KEY_WEEKLY_ALARM_STATE = "key_weekly_alarm_state"
        const val PREFERENCES_OPTIONS = "preferences_options"
        const val TAG_LICENSES_FRAGMENT = "tag_licenses_fragment"
        lateinit var errorHandler: ErrorHandler
        private const val KEY_SOUND_STATE = "key_sound_state"
        private const val TAG = "MainActivity"
        private const val TAG_SETTINGS_FRAGMENT = "tag_settings_fragment"
        private const val TAG_WEB_VIEW_FRAGMENT = "tag_web_view_fragment"
        var historyInitialized = false
        var historiesAddedRange = 0
    }
}