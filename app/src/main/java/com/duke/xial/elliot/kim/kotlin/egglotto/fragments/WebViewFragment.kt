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

class WebViewFragment : Fragment() {

    private lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (::fragmentView.isInitialized)
            return fragmentView

        fragmentView = inflater.inflate(R.layout.fragment_web_view, container, false)

        val webSettings: WebSettings = fragmentView.web_view.settings
        @SuppressLint("SetJavaScriptEnabled")
        webSettings.javaScriptEnabled = true
        fragmentView.web_view.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                println("$TAG: page loaded, url: $url")
            }
        }

        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.web_view.loadUrl(WINNING_RESULT_URL)
    }

    companion object {
        private const val TAG = "WebViewFragment"
        private const val WINNING_RESULT_URL = "https://m.dhlottery.co.kr/gameResult.do?method=byWin&wiselog=M_A_1_8"
    }
}