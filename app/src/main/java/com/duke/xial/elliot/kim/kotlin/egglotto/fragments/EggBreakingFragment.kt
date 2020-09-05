package com.duke.xial.elliot.kim.kotlin.egglotto.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.duke.xial.elliot.kim.kotlin.egglotto.GridLayoutManagerWrapper
import com.duke.xial.elliot.kim.kotlin.egglotto.R
import com.duke.xial.elliot.kim.kotlin.egglotto.activities.MainActivity
import com.duke.xial.elliot.kim.kotlin.egglotto.adapters.BaseRecyclerViewAdapter
import com.duke.xial.elliot.kim.kotlin.egglotto.database.LottoNumbersModel
import com.duke.xial.elliot.kim.kotlin.egglotto.showToast
import kotlinx.android.synthetic.main.fragment_egg_breaking.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.Collections.shuffle
import kotlin.collections.ArrayList

class EggBreakingFragment: Fragment() {

    private lateinit var eggsRecyclerViewAdapter: EggsRecyclerViewAdapter
    private lateinit var numbersRecyclerViewAdapter: NumbersRecyclerViewAdapter
    private var numberCount = 0
    private var obtainedNumbers = arrayListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_egg_breaking, container, false)

        view.button_generate_lotto_number.setOnClickListener {
            refreshEggs()
        }

        view.button_break_at_once.setOnClickListener {
            if (numberCount < 6) {
                eggsRecyclerViewAdapter.breakAtOnce()
            }
        }

        eggsRecyclerViewAdapter = EggsRecyclerViewAdapter(
            R.layout.item_view_egg,
            generateLottoNumbers().toList() as ArrayList<Int>
        )
        view.recycler_view_eggs.apply {
            adapter = eggsRecyclerViewAdapter
            layoutManager = GridLayoutManagerWrapper(requireContext(), 3).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
        }

        numbersRecyclerViewAdapter = NumbersRecyclerViewAdapter(
            R.layout.item_view_egg,
            arrayListOf()
        )
        view.recycler_view_numbers.apply {
            adapter = numbersRecyclerViewAdapter
            layoutManager = GridLayoutManagerWrapper(requireContext(), 1).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
        }

        return view
    }

    private fun generateLottoNumbers(): IntArray {
        val randomNumbers = mutableSetOf<Int>()
        while (randomNumbers.size < 18) {
            randomNumbers.add((1..45).random())
        }

        return randomNumbers.toIntArray()
    }

    private fun breakEgg(textView: TextView, number: Int) {
        if (textView.text.isNotBlank())
            return

        textView.isEnabled = false
        CoroutineScope(Dispatchers.Main).launch {
            val transitionDrawable = (textView.background as TransitionDrawable)
            transitionDrawable.isCrossFadeEnabled = true
            transitionDrawable.startTransition(200)
            delay(200L)
            showLottoNumber(textView, number)
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

    private fun refreshEggs() {
        eggsRecyclerViewAdapter.clear()
        eggsRecyclerViewAdapter.items.addAll(generateLottoNumbers().toList() as ArrayList<Int>)
        //eggsRecyclerViewAdapter.notifyDataSetChanged()

        numbersRecyclerViewAdapter.clear()
        //numbersRecyclerViewAdapter.itemsarrayListOf()

        numberCount = 0
    }

    private fun updateNumber(number: Int) {
        numbersRecyclerViewAdapter.insert(number, numberCount++)

        if (numberCount > 5) {
            showToast(requireContext(), "최고의 번호입니다.")
            (requireActivity() as MainActivity).viewModel
                .insert(LottoNumbersModel(creationTime = getCurrentTime(),
                    lottoNumbers = obtainedNumbers.toTypedArray()))
            return
        }
    }

    inner class EggsRecyclerViewAdapter(layoutId: Int, numbers: ArrayList<Int>)
        : BaseRecyclerViewAdapter(layoutId, numbers) {

        private val brokenEggs = arrayListOf<TextView>()
        private val boundViews = arrayListOf<TextView>()

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val number = items[position]

            holder.view.setOnClickListener {
                if (numberCount < 6) {
                    breakEgg(it as TextView, number)
                    brokenEggs.add(it)
                    obtainedNumbers.add(number)
                    updateNumber(number)
                }
            }

            boundViews.add(holder.view as TextView)
        }

        fun breakAtOnce() {
            val remainingNumbers = (items - obtainedNumbers)
                .map { items.indexOf(it) }
            shuffle(remainingNumbers)
            for (number in remainingNumbers.take(6 - numberCount)) {
                breakEgg(boundViews[number], items[number])
                brokenEggs.add(boundViews[number])
                obtainedNumbers.add(number)
                updateNumber(items[number])
            }
        }

        fun clear() {
            for (view in brokenEggs) {
                returnToEgg(view)
            }

            boundViews.clear()

            val itemCount = items.count()
            items.clear()
            notifyItemRangeRemoved(0, itemCount)
        }
    }

    private fun getCurrentTime() = SimpleDateFormat("yyyy.MM.dd. hh:mm:ss",
        Locale.getDefault()).format(System.currentTimeMillis())

    inner class NumbersRecyclerViewAdapter(layoutId: Int, numbers: ArrayList<Int>)
        : BaseRecyclerViewAdapter(layoutId, numbers) {

        private val brokenEggs = arrayListOf<TextView>()

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val number = items[position]
            breakEgg(holder.view as TextView, number)
            brokenEggs.add(holder.view as TextView)
        }

        fun clear() {
            for (view in brokenEggs) {
                returnToEgg(view)
            }

            val itemCount = items.count()
            items.clear()
            notifyItemRangeRemoved(0, itemCount)
        }
    }
}