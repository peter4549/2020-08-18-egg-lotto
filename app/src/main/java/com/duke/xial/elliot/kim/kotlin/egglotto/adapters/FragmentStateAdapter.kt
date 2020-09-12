package com.duke.xial.elliot.kim.kotlin.egglotto.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.duke.xial.elliot.kim.kotlin.egglotto.activities.MainActivity
import com.duke.xial.elliot.kim.kotlin.egglotto.fragments.EggBreakingFragment
import com.duke.xial.elliot.kim.kotlin.egglotto.fragments.WebViewFragment

class FragmentStateAdapter(private val fragmentActivity: FragmentActivity):
    androidx.viewpager2.adapter.FragmentStateAdapter(fragmentActivity) {
    private val pageCount = 4

    override fun getItemCount(): Int = pageCount

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> (fragmentActivity as MainActivity).eggBreakingFragment
            1 -> (fragmentActivity as MainActivity).todayHoroscopeFragment
            2 -> (fragmentActivity as MainActivity).webViewFragment
            3 -> (fragmentActivity as MainActivity).historyFragment
            else -> throw Exception("invalid fragment")
        }
    }
}