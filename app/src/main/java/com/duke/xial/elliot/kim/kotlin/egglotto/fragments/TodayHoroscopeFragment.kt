package com.duke.xial.elliot.kim.kotlin.egglotto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.duke.xial.elliot.kim.kotlin.egglotto.R
import com.duke.xial.elliot.kim.kotlin.egglotto.activities.MainActivity
import com.duke.xial.elliot.kim.kotlin.egglotto.error_handler.ResponseFailedException
import com.duke.xial.elliot.kim.kotlin.egglotto.models.LottoNumberModel
import com.duke.xial.elliot.kim.kotlin.egglotto.retrofit2.DailyHoroscopeApisRequest
import com.duke.xial.elliot.kim.kotlin.egglotto.retrofit2.LottoNumberApisRequest
import com.duke.xial.elliot.kim.kotlin.egglotto.showToast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_today_horoscope.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class TodayHoroscopeFragment: Fragment() {

    private lateinit var textViewDesc: TextView
    private lateinit var textViewKeyword: TextView
    private val calendars = arrayOf("양력", "음력 평달", "음력 윤달")
    private val calendarMap = mapOf(
        1 to 1
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_today_horoscope, container, false)

        textViewDesc = view.text_view_desc
        textViewKeyword = view.text_view_keyword

        val adapter = ArrayAdapter(requireContext(), R.layout.item_view_calendar, calendars)
        (view.text_input_layout_calendar.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        (view.text_input_layout_calendar.editText as? AutoCompleteTextView)?.setText(adapter.getItem(1),false)

        view.button_show_horoscope.setOnClickListener {
            setTodayHoroscope()
        }

        return view
    }

    private fun setTodayHoroscope() {
        CoroutineScope(Dispatchers.IO).launch {
            DailyHoroscopeApisRequest.getDailyHoroscopeService().requestLottoNumber(
                gender = "m",
                birth = "19940910",
                solarCal = "solar"
            ).enqueue(object :
                    Callback<String> {
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        MainActivity.errorHandler.errorHandling(t, t.message)
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
                                        errorBody), null)
                            } ?: run {
                                MainActivity.errorHandler.errorHandling(
                                    NullPointerException("failed to get today horoscope, response.errorBody() is null"),
                                    null, true
                                )
                            }
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
    }
}