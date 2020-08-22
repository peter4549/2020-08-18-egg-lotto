package com.duke.xial.elliot.kim.kotlin.egglotto.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.duke.xial.elliot.kim.kotlin.egglotto.R
import kotlinx.android.synthetic.main.fragment_web_view.view.*


class WebViewFragment(private val url: String? = null) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_web_view, container, false)

        val webSettings: WebSettings = view.web_view.settings
        @SuppressLint("SetJavaScriptEnabled")
        webSettings.javaScriptEnabled = true
        view.web_view.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                println("$TAG: page loaded, url: $url")
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (url != null)
            view.web_view.loadUrl(url)
    }

    companion object {
        @JvmStatic
        fun newInstance(url: String) =
            WebViewFragment(
                url
            )

        private const val TAG = "WebViewFragment"
    }
}