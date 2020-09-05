package com.duke.xial.elliot.kim.kotlin.egglotto.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.duke.xial.elliot.kim.kotlin.egglotto.GridLayoutManagerWrapper
import com.duke.xial.elliot.kim.kotlin.egglotto.R
import com.duke.xial.elliot.kim.kotlin.egglotto.adapters.BaseRecyclerViewAdapter
import com.duke.xial.elliot.kim.kotlin.egglotto.database.LottoNumbersModel
import kotlinx.android.synthetic.main.fragment_egg_breaking.view.*
import kotlinx.android.synthetic.main.item_view_lotto_numbers.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryFragment: Fragment() {

    val lottoNumbersRecyclerViewAdapter = LottoNumbersRecyclerViewAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        view.recycler_view_numbers.apply {
            adapter = lottoNumbersRecyclerViewAdapter
            layoutManager = GridLayoutManagerWrapper(requireContext(), 1)
        }
        return view
    }

    private fun getLuckyNumbers() {

    }

    inner class LottoNumbersRecyclerViewAdapter():
        RecyclerView.Adapter<LottoNumbersRecyclerViewAdapter.ViewHolder>() {

        private var lottoNumbersList: ArrayList<LottoNumbersModel> = arrayListOf()

        inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(layoutInflater.inflate(R.layout.item_view_lotto_numbers, parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val lottoNumbers = lottoNumbersList[position]
            showLottoNumber(holder.view.text_view_lotto_number_01, lottoNumbers.lottoNumbers[0])
            showLottoNumber(holder.view.text_view_lotto_number_02, lottoNumbers.lottoNumbers[1])
            showLottoNumber(holder.view.text_view_lotto_number_03, lottoNumbers.lottoNumbers[2])
            showLottoNumber(holder.view.text_view_lotto_number_04, lottoNumbers.lottoNumbers[3])
            showLottoNumber(holder.view.text_view_lotto_number_05, lottoNumbers.lottoNumbers[4])
            showLottoNumber(holder.view.text_view_lotto_number_06, lottoNumbers.lottoNumbers[5])
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
            notifyItemChanged(position)
        }

        fun remove(lottoNumbers: LottoNumbersModel) {
            val position = lottoNumbersList.indexOf(lottoNumbers)
            lottoNumbersList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}