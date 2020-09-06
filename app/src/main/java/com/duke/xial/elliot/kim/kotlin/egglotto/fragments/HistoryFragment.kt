package com.duke.xial.elliot.kim.kotlin.egglotto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.duke.xial.elliot.kim.kotlin.egglotto.GridLayoutManagerWrapper
import com.duke.xial.elliot.kim.kotlin.egglotto.R
import com.duke.xial.elliot.kim.kotlin.egglotto.activities.MainActivity
import com.duke.xial.elliot.kim.kotlin.egglotto.database.LottoNumbersModel
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.fragment_history.view.*
import kotlinx.android.synthetic.main.item_view_lotto_numbers.view.*

class HistoryFragment: Fragment() {

    private lateinit var fragmentView: View
    private val numberCountMap = mutableMapOf<Int, Int?>()
    val lottoNumbersRecyclerViewAdapter = LottoNumbersRecyclerViewAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (::fragmentView.isInitialized)
            return fragmentView

        fragmentView = inflater.inflate(R.layout.fragment_history, container, false)

        fragmentView.recycler_view_history.apply {
            adapter = lottoNumbersRecyclerViewAdapter
            layoutManager = GridLayoutManagerWrapper(requireContext(), 1)
        }

        return fragmentView
    }

    override fun onResume() {
        lottoNumbersRecyclerViewAdapter.notifyDataSetChanged()
        setLuckyNumbers(lottoNumbersRecyclerViewAdapter.lottoNumbersList)
        super.onResume()
    }

    private fun setLuckyNumbers(lottoNumbersList: MutableList<LottoNumbersModel>) {
        for (i in 1 .. 45)
            numberCountMap[i] = 0

        for (lottoNumbers in lottoNumbersList) {
            for (lottoNumber in lottoNumbers.lottoNumbers) {
                numberCountMap[lottoNumber] = numberCountMap[lottoNumber]?.plus(1)
            }
        }
        displayLuckyNumbers(getMostNumbers(numberCountMap))
    }

    private fun updateLuckyNumbers(lottoNumbers: LottoNumbersModel) {
        for (lottoNumber in lottoNumbers.lottoNumbers) {
            numberCountMap[lottoNumber] = numberCountMap[lottoNumber]?.minus(1)
        }

        displayLuckyNumbers(getMostNumbers(numberCountMap))
    }

    private fun getMostNumbers(numberCountMap: Map<Int, Int?>): Array<Pair<Int, Int?>> {
        val sortedNumberCounts = numberCountMap.toList().sortedBy { (_, value) -> value}.toMutableList()
        sortedNumberCounts.reverse()

        val mostNumbers: Array<Pair<Int, Int?>> = Array(3) { Pair(0, 0) }
        mostNumbers[0] = sortedNumberCounts[0]
        mostNumbers[1] = sortedNumberCounts[1]
        mostNumbers[2] = sortedNumberCounts[2]

        return mostNumbers
    }

    private fun displayLuckyNumbers(mostNumbers: Array<Pair<Int, Int?>>) {
        val first = mostNumbers[0]
        val second = mostNumbers[1]
        val third = mostNumbers[2]

        if (first.second == 0) {
            text_lucky_number_01.text = ""
            text_lucky_number_count_01.text = ""
        } else {
            text_lucky_number_01.text = first.first.toString()
            text_lucky_number_count_01.text = first.second.toString()
        }

        if (second.second == 0) {
            text_lucky_number_02.text = ""
            text_lucky_number_count_02.text = ""
        } else {
            text_lucky_number_02.text = second.first.toString()
            text_lucky_number_count_02.text = second.second.toString()
        }

        if (third.second == 0) {
            text_lucky_number_03.text = ""
            text_lucky_number_count_03.text = ""
        } else {
            text_lucky_number_03.text = third.first.toString()
            text_lucky_number_count_03.text = third.second.toString()
        }
    }

    inner class LottoNumbersRecyclerViewAdapter:
        RecyclerView.Adapter<LottoNumbersRecyclerViewAdapter.ViewHolder>() {

        var lottoNumbersList: ArrayList<LottoNumbersModel> = arrayListOf()

        inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(layoutInflater.inflate(R.layout.item_view_lotto_numbers, parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val lottoNumbers = lottoNumbersList[position]
            holder.view.text_creation_time.text = lottoNumbers.creationTime
            showLottoNumber(holder.view.text_view_lotto_number_01, lottoNumbers.lottoNumbers[0])
            showLottoNumber(holder.view.text_view_lotto_number_02, lottoNumbers.lottoNumbers[1])
            showLottoNumber(holder.view.text_view_lotto_number_03, lottoNumbers.lottoNumbers[2])
            showLottoNumber(holder.view.text_view_lotto_number_04, lottoNumbers.lottoNumbers[3])
            showLottoNumber(holder.view.text_view_lotto_number_05, lottoNumbers.lottoNumbers[4])
            showLottoNumber(holder.view.text_view_lotto_number_06, lottoNumbers.lottoNumbers[5])
            holder.view.image_delete.setOnClickListener {
                (requireActivity() as MainActivity).viewModel.delete(lottoNumbers)
            }
        }

        override fun getItemCount(): Int = lottoNumbersList.count()

        private fun showLottoNumber(textView: TextView, number: Int) {
            val drawableId = when(number) {
                in 1..10 -> R.drawable.ic_circle_yellow_32
                in 11..20 -> R.drawable.ic_circle_blue_32
                in 21..30 -> R.drawable.ic_circle_red_32
                in 31..40 -> R.drawable.ic_circle_black_32
                else -> R.drawable.ic_circle_green_32
            }

            textView.apply {
                setBackgroundResource(drawableId)
                text = number.toString()
            }
        }

        fun insertAll(lottoNumbersList: MutableList<LottoNumbersModel>) {
            this.lottoNumbersList = lottoNumbersList as ArrayList<LottoNumbersModel>
        }

        fun insert(lottoNumbers: LottoNumbersModel, position: Int = 0) {
            lottoNumbersList.add(position, lottoNumbers)
        }

        fun remove(lottoNumbers: LottoNumbersModel) {
            val position = lottoNumbersList.indexOf(lottoNumbers)
            lottoNumbersList.removeAt(position)
            updateLuckyNumbers(lottoNumbers)
            notifyItemRemoved(position)
        }
    }
}