package com.duke.xial.elliot.kim.kotlin.egglotto.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.duke.xial.elliot.kim.kotlin.egglotto.activities.MainActivity
import com.duke.xial.elliot.kim.kotlin.egglotto.R
import com.duke.xial.elliot.kim.kotlin.egglotto.license.LicensesFragment
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        (requireActivity() as MainActivity).setSupportActionBar(view.toolbar)
        (requireActivity() as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)

        view.text_view_open_source_licenses.setOnClickListener {
            startLicensesFragment()
        }

        return view
    }

    private fun startLicensesFragment() {
        (requireActivity() as MainActivity).supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .setCustomAnimations(
                R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left,
                R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right
            ).replace(
                R.id.constraint_layout_activity_main, LicensesFragment(),
                MainActivity.TAG_LICENSES_FRAGMENT
            ).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            requireActivity().onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}