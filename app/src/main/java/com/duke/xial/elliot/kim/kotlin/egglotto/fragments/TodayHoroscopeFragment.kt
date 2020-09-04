package com.duke.xial.elliot.kim.kotlin.egglotto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.duke.xial.elliot.kim.kotlin.egglotto.R
import kotlinx.android.synthetic.main.fragment_today_horoscope.view.*

class TodayHoroscopeFragment: Fragment() {

    private val calendars = arrayOf("양력", "음력 평달", "음력 윤달")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_today_horoscope, container, false)

        val adapter = ArrayAdapter(requireContext(), R.layout.item_view_calendar, calendars)
        (view.text_input_layout_calendar.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        return view
    }
}