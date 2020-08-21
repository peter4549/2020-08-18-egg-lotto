package com.duke.xial.elliot.kim.kotlin.egglotto.license

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.duke.xial.elliot.kim.kotlin.egglotto.R
import kotlinx.android.synthetic.main.activity_main_drawer.*
import kotlinx.android.synthetic.main.fragment_licenses.view.*
import kotlinx.android.synthetic.main.item_view_license.view.*

class LicensesFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_licenses, container, false)
        view.recycler_view_licenses.apply {

        }
        return view
    }

    inner class LicensesRecyclerViewAdapter(private val licenses: ArrayList<LicenseModel>):
        RecyclerView.Adapter<LicensesRecyclerViewAdapter.ViewHolder>() {
        inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_license, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = licenses.count()

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val license = licenses[position]

            holder.view.text_view_name.text = license.name
            holder.view.text_view_copyright.text = license.copyright
            holder.view.text_view_link.text = license.link
            holder.view.text_view_license.text = license.license
        }
    }
}