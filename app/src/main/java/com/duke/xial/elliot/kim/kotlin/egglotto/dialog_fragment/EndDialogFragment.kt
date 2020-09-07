package com.duke.xial.elliot.kim.kotlin.egglotto.dialog_fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.duke.xial.elliot.kim.kotlin.egglotto.NativeAdvancedAd
import com.duke.xial.elliot.kim.kotlin.egglotto.activities.MainActivity
import com.duke.xial.elliot.kim.kotlin.egglotto.R
import com.duke.xial.elliot.kim.kotlin.egglotto.goToPlayStore
import kotlinx.android.synthetic.main.fragment_end_dialog.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("InflateParams")
class EndDialogFragment(private var activity: AppCompatActivity? = null): DialogFragment() {

    private lateinit var alertDialog: AlertDialog
    private val nativeAdvancedAd = NativeAdvancedAd()
    private var dialogView: View? = null


    init {
        CoroutineScope(Dispatchers.IO).launch {
            if (activity != null) {

                dialogView = activity?.layoutInflater?.inflate(
                    R.layout.fragment_end_dialog,
                    null
                )
                nativeAdvancedAd.refreshAd(activity, dialogView?.ad_frame)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (::alertDialog.isInitialized)
            return alertDialog

        val builder = AlertDialog.Builder(requireActivity())

        if (dialogView != null)
            builder.setView(dialogView)
        else {
            dialogView = requireActivity().layoutInflater.inflate(
                R.layout.fragment_end_dialog,
                null
            )
            nativeAdvancedAd.refreshAd(requireActivity(), dialogView?.ad_frame)
            builder.setView(dialogView)
        }

        dialogView?.button_go_to_review?.setOnClickListener {
            goToPlayStore(requireContext())
            nativeAdvancedAd.destroyNativeAd()
            Thread.sleep(200L)
            (requireActivity() as MainActivity).finish()
        }

        dialogView?.button_ok?.setOnClickListener {
            dismiss()
            nativeAdvancedAd.destroyNativeAd()
            Thread.sleep(200L)
            (requireActivity() as MainActivity).finish()
        }

        alertDialog = builder.create()

        return alertDialog
    }

    override fun onDetach() {
        activity = null
        super.onDetach()
    }
}