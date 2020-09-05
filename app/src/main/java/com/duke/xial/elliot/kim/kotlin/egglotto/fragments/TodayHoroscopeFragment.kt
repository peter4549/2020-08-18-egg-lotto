package com.duke.xial.elliot.kim.kotlin.egglotto.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.duke.xial.elliot.kim.kotlin.egglotto.R
import com.duke.xial.elliot.kim.kotlin.egglotto.activities.MainActivity
import com.duke.xial.elliot.kim.kotlin.egglotto.error_handler.ResponseFailedException
import com.duke.xial.elliot.kim.kotlin.egglotto.retrofit2.DailyHoroscopeApisRequest
import com.duke.xial.elliot.kim.kotlin.egglotto.showToast
import kotlinx.android.synthetic.main.fragment_today_horoscope.*
import kotlinx.android.synthetic.main.fragment_today_horoscope.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class TodayHoroscopeFragment: Fragment() {

    private lateinit var editTextBirth: com.google.android.material.textfield.TextInputEditText
    private lateinit var layoutBirth: com.google.android.material.textfield.TextInputLayout
    private lateinit var preferences: SharedPreferences
    private lateinit var textViewDesc: TextView
    private lateinit var textViewKeyword: TextView
    private val calendars = arrayOf("양력", "음력 평달", "음력 윤달")
    private val calendarMap = mapOf(
        calendars[0] to SolarCalendar.solar,
        calendars[1] to SolarCalendar.lunarGeneral,
        calendars[2] to SolarCalendar.lunarLeap
    )
    private val radioButtonIds = arrayOf(
        R.id.radio_button_male,
        R.id.radio_button_female
    )
    private var autoCompleteTextView: AutoCompleteTextView? = null
    private var calendar = calendars[0]
    private var checkedRadioButtonIndex = 0
    private var gender = "m"
    private var months30 = arrayOf(4, 6, 9, 11)
    private var months31 = arrayOf(1, 3, 5, 7, 8, 10, 12)
    private var resultMap = mutableMapOf<String, String>()
    private var validBirth = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_today_horoscope, container, false)

        textViewDesc = view.text_view_desc
        textViewKeyword = view.text_view_keyword
        editTextBirth = view.edit_text_birth
        layoutBirth = view.text_input_layout_birth

        editTextBirth.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                layoutBirth.error = null
                layoutBirth.isErrorEnabled = false
            }
        }

        preferences = requireContext().getSharedPreferences(
            PREFERENCES_TODAY_HOROSCOPE,
            Context.MODE_PRIVATE
        )
        restoreStates()

        view.radio_group.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_button_male -> checkedRadioButtonIndex = 0
                R.id.radio_button_female -> checkedRadioButtonIndex = 1
            }

            gender = if (checkedRadioButtonIndex == 0) "m"
            else "f"
        }
        view.radio_group.check(radioButtonIds[checkedRadioButtonIndex])

        val adapter = ArrayAdapter(requireContext(), R.layout.item_view_calendar, calendars)
        autoCompleteTextView = (view.text_input_layout_calendar.editText as? AutoCompleteTextView)
        autoCompleteTextView?.setAdapter(adapter)
        autoCompleteTextView?.setText(calendar, false)

        view.button_show_horoscope.setOnClickListener {
            it.isEnabled = false
            val birth = editTextBirth.text.toString()
            editTextBirth.clearFocus()
            hideKeyboard(view)

            if (checkBirth(birth)) {
                calendar = autoCompleteTextView?.text.toString()
                validBirth = birth
                storeCurrentStates()

                val results = checkResultExist("$gender$calendar$validBirth")
                if (results == null)
                    setTodayHoroscope(gender, birth, calendarMap[calendar] ?: calendarMap[calendars[0]].toString())
                else
                    restoreAndSetHoroscope(results)
            } else {
                view.text_view_keyword.text = ""
                view.text_view_desc.text = ""
                view.button_show_horoscope.isEnabled = true
            }
        }

        return view
    }


    private fun setTodayHoroscope(gender: String, birth: String, calendar: String) {
        CoroutineScope(Dispatchers.IO).launch {
            DailyHoroscopeApisRequest.getDailyHoroscopeService().requestLottoNumber(
                gender = gender,
                birth = birth,
                solarCal = calendar
            ).enqueue(object :
                Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    MainActivity.errorHandler.errorHandling(t, t.message)
                    button_show_horoscope.isEnabled = true
                }

                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful)
                        setHoroscopeText(getContents(response.body().toString()))
                    else
                        response.errorBody()?.let { errorBody ->
                            MainActivity.errorHandler.errorHandling(
                                ResponseFailedException(
                                    "failed to get today horoscope",
                                    errorBody
                                ), null
                            )
                        } ?: run {
                            MainActivity.errorHandler.errorHandling(
                                NullPointerException("failed to get today horoscope, response.errorBody() is null"),
                                null, true
                            )
                        }

                    button_show_horoscope.isEnabled = true
                }
            })
        }
    }

    private fun getContents(responseBody: String): Pair<String, String>? {
        return try {
            val result = responseBody.substringAfter("(").substringBeforeLast(")")
            val jsonArray = ((JSONObject(result).get("result") as JSONObject)
                .get("day") as JSONObject).get("content") as JSONArray
            val jsonObject = jsonArray.getJSONObject(0)
            val keyword = jsonObject.get("keyword").toString()
                .replace("<b>", "").replace("</b>", "")
            val desc = jsonObject.get("desc").toString()

            Pair(keyword, desc)
        } catch (e: Exception) {
            MainActivity.errorHandler.errorHandling(e)
            null
        }
    }

    private fun setHoroscopeText(contents: Pair<String, String>?) {
        textViewKeyword.text = contents?.first ?: "오늘 운세를 불러오지 못했습니다."
        textViewDesc.text = contents?.second ?: ""
        storeHoroscopeResult("$gender$calendar$validBirth",
            contents?.first ?: "", contents?.second ?: "")
    }

    private fun storeCurrentStates() {
        preferences.edit()
            .putInt(KEY_CHECKED_RADIO_BUTTON_INDEX, checkedRadioButtonIndex)
            .putString(KEY_BIRTH, validBirth)
            .putString(KEY_CALENDAR_POSITION, calendar)
            .apply()
    }

    private fun restoreStates() {
        checkedRadioButtonIndex = preferences.getInt(KEY_CHECKED_RADIO_BUTTON_INDEX, 0)
        gender = if (checkedRadioButtonIndex == 0) "m"
        else "f"
        calendar = preferences.getString(KEY_CALENDAR_POSITION, calendars[0]) ?: calendars[0]
        editTextBirth.setText(preferences.getString(KEY_BIRTH, "") ?: "")
    }

    private fun storeHoroscopeResult(key: String, keyword: String, desc: String) {
        resultMap[key] = "$keyword$SEPARATOR$desc"
    }

    private fun checkResultExist(key: String): String? = resultMap[key]

    private fun restoreAndSetHoroscope(results: String) {
        val keywordAndDesc = results.split(SEPARATOR)
        textViewKeyword.text = keywordAndDesc[0]
        textViewDesc.text = keywordAndDesc[1]
        button_show_horoscope.isEnabled = true
    }

    private fun checkBirth(birth: String): Boolean {
        layoutBirth.isErrorEnabled = true

        if (birth.isBlank()) {
            layoutBirth.error = "생년월일을 입력해주세요."
            return false
        }

        val errorMessage01 = "양식에 맞게 입력해주세요. ex. 19950111"

        if (birth.length < 8) {
            layoutBirth.error = errorMessage01
            return false
        }

        val year = birth.substring(0, 4).toInt()
        val yesterday = yesterday()
        val errorMessage02 = "19400101에서 ${yesterday}사이의 날짜를 입력해주세요."

        if (year < 1940 ||
            year > yesterday.substring(0, 4).toInt()) {
            layoutBirth.error = errorMessage02
            return false
        }

        val month = birth.substring(4, 6).toInt()
        if (month > 12) {
            layoutBirth.error = errorMessage01
            return false
        }

        val date = birth.substring(6, 8).toInt()
        if (month in months30 && date > 30) {
            layoutBirth.error = errorMessage01
            return false
        }

        if (month in months31 && date > 31) {
            layoutBirth.error = errorMessage01
            return false
        }

        if (month == 2) {
            if (year % 4 == 0 && date > 29) {
                layoutBirth.error = errorMessage01
                return false
            }

            if (date > 28) {
                layoutBirth.error = errorMessage01
                return false
            }
        }

        return true
    }

    private fun yesterday(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1)
        return SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(calendar.timeInMillis)
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        private const val PREFERENCES_TODAY_HOROSCOPE = "preferences_today_horoscope"
        private const val KEY_BIRTH = "key_birth"
        private const val KEY_CALENDAR_POSITION = "key_calendar_position"
        private const val KEY_CHECKED_RADIO_BUTTON_INDEX = "key_checked_radio_button_index"
        private const val SEPARATOR = "separator"
    }

    object SolarCalendar {
        const val solar = "solar"
        const val lunarGeneral = "lunarGeneral"
        const val lunarLeap = "lunarLeap"
    }
}