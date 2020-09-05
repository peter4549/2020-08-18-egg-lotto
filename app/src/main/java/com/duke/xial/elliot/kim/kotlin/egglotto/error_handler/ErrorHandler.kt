package com.duke.xial.elliot.kim.kotlin.egglotto.error_handler

import android.content.Context
import com.duke.xial.elliot.kim.kotlin.egglotto.R
import com.duke.xial.elliot.kim.kotlin.egglotto.showToast
import okhttp3.ResponseBody

class ErrorHandler(private val context: Context) {

    private var hasSpecificMessage = false

    fun errorHandling(e: Exception, toastMessage: String? = null, throwing: Boolean = false) {
        println("${context.getString(R.string.app_name)} Error")
        e.printStackTrace()

        if (throwing)
            throw e

        when(e) {
            is ResponseFailedException -> println("Error Body: ${e.responseBody}")
        }

        if (toastMessage != null && !hasSpecificMessage)
            showToast(context, toastMessage)
    }

    fun errorHandling(t: Throwable, toastMessage: String? = null, throwing: Boolean = false) {
        t.printStackTrace()

        if (throwing)
            throw t

        if (toastMessage != null)
            showToast(context, toastMessage)
    }
}

class ResponseFailedException(message: String, val responseBody: ResponseBody): Exception(message)