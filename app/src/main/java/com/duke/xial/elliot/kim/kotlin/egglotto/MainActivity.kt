package com.duke.xial.elliot.kim.kotlin.egglotto

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.TransitionDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.duke.xial.elliot.kim.kotlin.egglotto.broadcast_receiver.AlarmReceiver
import com.duke.xial.elliot.kim.kotlin.egglotto.broadcast_receiver.DeviceBootReceiver
import com.duke.xial.elliot.kim.kotlin.egglotto.error_handler.ErrorHandler
import com.duke.xial.elliot.kim.kotlin.egglotto.error_handler.ResponseFailedException
import com.duke.xial.elliot.kim.kotlin.egglotto.models.LottoNumberModel
import com.duke.xial.elliot.kim.kotlin.egglotto.retrofit2.LottoNumberApisRequest
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_drawer.*
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var alarmManager: AlarmManager
    private lateinit var generatedLottoNumbers: IntArray
    private lateinit var mediaPlayer: MediaPlayer
    private val errorHandler = ErrorHandler(this)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_drawer)

        alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mediaPlayer = MediaPlayer.create(this, R.raw.egg_cracking_sound)

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)
        toolbar.setNavigationIcon(R.drawable.ic_raw_egg_32)

        initializeSpinner(spinner)
        generateLottoNumber()

        button_generate_lotto_number.setOnClickListener {
            generateLottoNumber()
            returnAllToEggs()
        }

        button_break_at_once.setOnClickListener {
            if (soundState)
                mediaPlayer.start()
            openAtOnce()
        }

        button_winning_confirmation.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setPrompt(getString(R.string.have_the_qr_code_in_the_square))
            integrator.setBeepEnabled(false)
            integrator.setBarcodeImageEnabled(true)
            integrator.captureActivity = com.journeyapps.barcodescanner.CaptureActivity::class.java
            integrator.initiateScan()
        }

        text_view_generated_lotto_number_01.setOnClickListener(eggTextViewClickListener)
        text_view_generated_lotto_number_02.setOnClickListener(eggTextViewClickListener)
        text_view_generated_lotto_number_03.setOnClickListener(eggTextViewClickListener)
        text_view_generated_lotto_number_04.setOnClickListener(eggTextViewClickListener)
        text_view_generated_lotto_number_05.setOnClickListener(eggTextViewClickListener)
        text_view_generated_lotto_number_06.setOnClickListener(eggTextViewClickListener)
        text_view_generated_lotto_number_bonus.setOnClickListener(eggTextViewClickListener)

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

        MobileAds.initialize(this)
        ad_view.loadAd(AdRequest.Builder().build())
        val adListener = object : AdListener() {
            override fun onAdFailedToLoad(p0: Int) {
                println("$TAG: onAdFailedToLoad")
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                println("$TAG: onAdLoaded")
            }
        }

        ad_view.adListener = adListener
    }

    override fun onBackPressed() {
        if (drawer_layout_activity_main.isDrawerOpen(GravityCompat.END))
            drawer_layout_activity_main.closeDrawer(GravityCompat.END)
        else
            super.onBackPressed()
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
        val pendingIntent = PendingIntent.getBroadcast(this,
            0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        showToast(this, longTimeToString(calendar.timeInMillis))

        val receiver = ComponentName(this, DeviceBootReceiver::class.java)
        packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_HALF_HOUR/6, pendingIntent)//AlarmManager.INTERVAL_DAY * 7
    }

    private fun setBlockNotification() {
        getSharedPreferences(PREFERENCES_OPTIONS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_BLOCK_NOTIFICATION, true).apply()
    }

    private fun cancelWeeklyAlarm() {
        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this,
            0, alarmIntent, PendingIntent.FLAG_NO_CREATE)
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
                    // showToast(this, getString(R.string.failed_to_load_web_page))
                    println("$TAG: result.contents is null")
                } else {
                    var url = result.contents
                    if (!url.startsWith("http://")) {
                        url = "http://$url"
                    } else if (!url.startsWith("https://")) {
                        url = "https://$url"
                    }

                    showToast(this, url)
                    startWebViewFragment(url)
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private val eggTextViewClickListener = View.OnClickListener { view ->
        if(soundState)
            mediaPlayer.start()

        val lottoNumber = when(view.id) {
            R.id.text_view_generated_lotto_number_01 -> generatedLottoNumbers[0]
            R.id.text_view_generated_lotto_number_02 -> generatedLottoNumbers[1]
            R.id.text_view_generated_lotto_number_03 -> generatedLottoNumbers[2]
            R.id.text_view_generated_lotto_number_04 -> generatedLottoNumbers[3]
            R.id.text_view_generated_lotto_number_05 -> generatedLottoNumbers[4]
            R.id.text_view_generated_lotto_number_06 -> generatedLottoNumbers[5]
            R.id.text_view_generated_lotto_number_bonus -> generatedLottoNumbers[6]
            else -> throw IllegalStateException("invalid view id")
        }

        showLottoNumberWithAnimation(view as TextView, lottoNumber)
    }

    private fun startWebViewFragment(url: String) {
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .setCustomAnimations(
                R.anim.anim_slide_in_from_bottom,
                R.anim.anim_slide_out_to_top,
                R.anim.anim_slide_in_from_top,
                R.anim.anim_slide_out_to_bottom
            ).replace(
                R.id.constraint_layout_activity_main,
                WebViewFragment.newInstance(url),
                TAG_WEB_VIEW_FRAGMENT
            ).commitAllowingStateLoss()
    }

    private fun generateLottoNumber() {
        val randomNumbers = mutableSetOf<Int>()
        while (randomNumbers.size < 7) {
            randomNumbers.add((1..45).random())
        }

        generatedLottoNumbers = randomNumbers.toIntArray()
    }

    private fun initializeSpinner(spinner: Spinner) {
        val arrayAdapter = ArrayAdapter<Int>(
            this@MainActivity,
            R.layout.support_simple_spinner_dropdown_item
        )
        val currentRound = getCurrentRound()

        for (i in  currentRound downTo 1)
            arrayAdapter.add(i)

        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                val round = spinner.selectedItem as Int
                var text = "${spinner.selectedItem}${getString(R.string.winning_number)}"
                if (round == currentRound)
                    text += getString(R.string.recency)
                text_view_round.text = text
                setPastRoundLottoNumber(round)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {  }
        }
    }

    private fun getCurrentRound(): Int = runBlocking(Dispatchers.IO) {
        Jsoup.connect(getString(R.string.game_result_url)).get().let { document ->
            val crawlResults = document.body().getElementsByClass("win_result")
                .select("h4").select("strong").toString()

            return@runBlocking Regex("[^0-9]").replace(crawlResults, "").toInt()
        }
    }

    private fun setPastRoundLottoNumber(round: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            LottoNumberApisRequest.getLottoNumberService()
                .requestLottoNumber(round.toString()).enqueue(object :
                    Callback<LottoNumberModel> {
                    override fun onFailure(call: Call<LottoNumberModel>, t: Throwable) {
                        errorHandler.errorHandling(t, "로또 번호를 읽어오는데 실패했습니다.")
                    }

                    override fun onResponse(
                        call: Call<LottoNumberModel>,
                        response: Response<LottoNumberModel>
                    ) {
                        if (response.isSuccessful) {
                            val lottoNumberModel = response.body()
                            if (lottoNumberModel != null) {
                                val firstWinAmount = "%,d".format(lottoNumberModel.firstWinamnt)
                                val text = "${getString(R.string.winning_amount)} $firstWinAmount ${getString(R.string.monetary_unit)}"
                                text_view_winning_amount.text = text
                                showPastRoundLottoNumber(getLottoNumbers(lottoNumberModel))
                            } else {
                                errorHandler.errorHandling(
                                    NullPointerException("failed to get lotto number, lottoNumberModel is null"),
                                    "로또 번호를 읽어오는데 실패했습니다."
                                )
                            }
                        } else
                            response.errorBody()?.let { errorBody ->
                                errorHandler.errorHandling(
                                    ResponseFailedException(
                                        "failed to get lotto number",
                                        errorBody
                                    ),
                                    "로또 번호를 읽어오는데 실패했습니다."
                                )
                            } ?: run {
                                errorHandler.errorHandling(
                                    NullPointerException("failed to get lotto number, response.errorBody() is null"),
                                    null, true
                                )
                            }
                    }
                })
        }
    }

    private fun getLottoNumbers(lottoNumber: LottoNumberModel): IntArray {
        return IntArray(7).apply {
            set(0, lottoNumber.drwtNo1.toInt())
            set(1, lottoNumber.drwtNo2.toInt())
            set(2, lottoNumber.drwtNo3.toInt())
            set(3, lottoNumber.drwtNo4.toInt())
            set(4, lottoNumber.drwtNo5.toInt())
            set(5, lottoNumber.drwtNo6.toInt())
            set(6, lottoNumber.bnusNo.toInt())
        }
    }

    private fun showPastRoundLottoNumber(lottoNumber: IntArray) {
        showLottoNumber(text_view_lotto_number_01, lottoNumber[0])
        showLottoNumber(text_view_lotto_number_02, lottoNumber[1])
        showLottoNumber(text_view_lotto_number_03, lottoNumber[2])
        showLottoNumber(text_view_lotto_number_04, lottoNumber[3])
        showLottoNumber(text_view_lotto_number_05, lottoNumber[4])
        showLottoNumber(text_view_lotto_number_06, lottoNumber[5])
        showLottoNumber(text_view_lotto_number_bonus, lottoNumber[6])
    }

    private fun showLottoNumberWithAnimation(textView: TextView, lottoNumber: Int) {
        if (textView.text.isNotBlank())
            return

        textView.isEnabled = false
        CoroutineScope(Dispatchers.Main).launch {
            val transitionDrawable = (textView.background as TransitionDrawable)
            transitionDrawable.isCrossFadeEnabled = true
            transitionDrawable.startTransition(200)
            delay(200L)
            showLottoNumber(textView, lottoNumber)
        }
    }

    private fun showLottoNumber(textView: TextView, number: Int) {
        val drawableId = when(number) {
            in 1..10 -> R.drawable.ic_circle_yellow_32
            in 11..20 -> R.drawable.ic_circle_blue_32
            in 21..30 -> R.drawable.ic_circle_red_32
            in 31..40 -> R.drawable.ic_circle_black_32
            else -> R.drawable.ic_circle_green_32
        }

        CoroutineScope(Dispatchers.Main).launch {
            textView.apply {
                animate().alpha(0F)
                    .setDuration(200L)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            visibility = View.INVISIBLE
                            textView.apply {
                                setBackgroundResource(drawableId)
                                text = number.toString()
                                alpha = 0F
                                visibility = View.VISIBLE
                                animate().alpha(1F)
                                    .setDuration(200L)
                                    .setListener(null)
                            }
                        }
                    })
            }
        }
    }

    private fun returnAllToEggs() {
        returnToEgg(text_view_generated_lotto_number_01)
        returnToEgg(text_view_generated_lotto_number_02)
        returnToEgg(text_view_generated_lotto_number_03)
        returnToEgg(text_view_generated_lotto_number_04)
        returnToEgg(text_view_generated_lotto_number_05)
        returnToEgg(text_view_generated_lotto_number_06)
        returnToEgg(text_view_generated_lotto_number_bonus)
    }

    private fun returnToEgg(textView: TextView) {
        textView.apply {
            isEnabled = true
            animate().alpha(0F)
                .setDuration(200L)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.INVISIBLE
                        textView.apply {
                            setBackgroundResource(R.drawable.transition_egg)
                            text = ""
                            alpha = 0F
                            visibility = View.VISIBLE
                            animate().alpha(1F)
                                .setDuration(200L)
                                .setListener(null)
                        }
                    }
                })
        }
    }

    private fun openAtOnce() {
        showLottoNumberWithAnimation(text_view_generated_lotto_number_01, generatedLottoNumbers[0])
        showLottoNumberWithAnimation(text_view_generated_lotto_number_02, generatedLottoNumbers[1])
        showLottoNumberWithAnimation(text_view_generated_lotto_number_03, generatedLottoNumbers[2])
        showLottoNumberWithAnimation(text_view_generated_lotto_number_04, generatedLottoNumbers[3])
        showLottoNumberWithAnimation(text_view_generated_lotto_number_05, generatedLottoNumbers[4])
        showLottoNumberWithAnimation(text_view_generated_lotto_number_06, generatedLottoNumbers[5])
        showLottoNumberWithAnimation(text_view_generated_lotto_number_bonus, generatedLottoNumbers[6])
    }

    private fun longTimeToString(time: Long, pattern: String  = "yyyy.MM.dd hh:mm:ss"): String =
        SimpleDateFormat(pattern, Locale.getDefault()).format(time)

    companion object {
        const val KEY_BLOCK_NOTIFICATION = "key_block_notification"
        const val KEY_WEEKLY_ALARM_STATE = "key_weekly_alarm_state"
        const val PREFERENCES_OPTIONS = "preferences_options"
        private const val KEY_SOUND_STATE = "key_sound_state"
        private const val TAG = "MainActivity"
        private const val TAG_WEB_VIEW_FRAGMENT = "tag_web_view_fragment"
    }
}