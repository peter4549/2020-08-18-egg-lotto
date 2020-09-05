package com.duke.xial.elliot.kim.kotlin.egglotto.license

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.duke.xial.elliot.kim.kotlin.egglotto.activities.MainActivity
import com.duke.xial.elliot.kim.kotlin.egglotto.R
import com.duke.xial.elliot.kim.kotlin.egglotto.collapse
import com.duke.xial.elliot.kim.kotlin.egglotto.expand
import kotlinx.android.synthetic.main.fragment_licenses.view.*
import kotlinx.android.synthetic.main.item_view_license.view.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class LicensesFragment: Fragment() {

    private var runOnlyOnce = true
    private var originHeight = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_licenses, container, false)

        (requireActivity() as MainActivity).setSupportActionBar(view.toolbar)
        (requireActivity() as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)

        Collections.sort(licenses, Comparator { o1: LicenseModel, o2: LicenseModel ->
            return@Comparator o1.name.compareTo(o2.name)
        })

        view.recycler_view_licenses.apply {
            adapter = LicensesRecyclerViewAdapter(licenses)
            layoutManager = GridLayoutManager(requireContext(), 1)
        }
        return view
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

    inner class LicensesRecyclerViewAdapter(private val licenses: ArrayList<LicenseModel>):
        RecyclerView.Adapter<LicensesRecyclerViewAdapter.ViewHolder>() {
        inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view)

        private val isExpandedMap = mutableMapOf<Int, Boolean>()

        init {
            for(i in 0 until licenses.count())
                isExpandedMap[i] = false
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_license, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = licenses.count()

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val license = licenses[position]

            holder.view.text_view_name.text = license.name
            holder.view.text_view_link.text = license.link

            if (runOnlyOnce) {
                originHeight = holder.view.text_view_copyright.layoutParams.height
                runOnlyOnce = false
            }
            holder.view.text_view_copyright.text = license.copyright?.let { getCopyright(it) }

            if (holder.view.text_view_copyright.text.isBlank())
                holder.view.text_view_copyright.isEnabled = false

            holder.view.text_view_copyright.setOnClickListener {
                if (isExpandedMap[position] != false) {
                    it.collapse(originHeight)
                    isExpandedMap[position] = false
                } else {
                    it.expand()
                    isExpandedMap[position] = true
                }
            }
        }

        private fun getCopyright(file: String): String {
            var reader: BufferedReader? = null
            val stringBuilder = StringBuilder()
            try {
                reader = BufferedReader(InputStreamReader(requireContext().assets.open("licenses/$file")))
                for (line in reader.readLines()) {
                    stringBuilder.append(line + "\n")
                }
            } catch (e: IOException) {
                MainActivity.errorHandler.errorHandling(e, getString(R.string.failed_to_load_file))
            } finally {
                if (reader != null) {
                    try {
                        reader.close()
                    } catch (e: IOException) {
                        println("$TAG: failed to close buffered reader")
                    }
                }
            }

            return stringBuilder.toString()
        }
    }

    companion object {
        private const val TAG = "LicenseFragment"
    }
}