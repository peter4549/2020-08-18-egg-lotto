package com.duke.xial.elliot.kim.kotlin.egglotto.dialog_fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.duke.xial.elliot.kim.kotlin.egglotto.activities.MainActivity
import com.duke.xial.elliot.kim.kotlin.egglotto.R
import com.duke.xial.elliot.kim.kotlin.egglotto.goToPlayStore
import com.google.android.gms.ads.AdListener
import kotlinx.android.synthetic.main.fragment_exit_dialog.view.*


class EndDialogFragment: DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = requireActivity().layoutInflater.inflate(R.layout.fragment_exit_dialog, null)
        builder.setView(view)

        val adListener = object : AdListener() {
            override fun onAdFailedToLoad(p0: Int) {
                println("$TAG: onAdFailedToLoad")
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                println("$TAG: onAdLoaded")
            }
        }


        view.ad_view.adListener = adListener
        view.ad_view.loadAd((requireActivity() as MainActivity).adRequest)

        view.button_go_to_review.setOnClickListener {
            goToPlayStore(requireContext())
        }

        view.button_ok.setOnClickListener {
            dismiss()
            (requireActivity() as MainActivity).finish()
        }

        return builder.create()
    }

    companion object {
        private const val TAG = "ExitApplicationDialogFragment"
    }
}