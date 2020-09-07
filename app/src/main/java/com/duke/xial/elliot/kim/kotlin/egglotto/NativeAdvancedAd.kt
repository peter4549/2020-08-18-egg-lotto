package com.duke.xial.elliot.kim.kotlin.egglotto

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.widget.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import timber.log.Timber

class NativeAdvancedAd {

    var currentNativeAd: UnifiedNativeAd? = null

    private fun populateUnifiedNativeAdView(nativeAd: UnifiedNativeAd, adView: UnifiedNativeAdView) {
        currentNativeAd?.destroy()
        currentNativeAd = nativeAd

        adView.mediaView = adView.findViewById(R.id.ad_media)
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView.setMediaContent(nativeAd.mediaContent)

        if (adView.bodyView != null) {
            if (nativeAd.body == null) {
                adView.bodyView.visibility = View.GONE
            } else {
                (adView.bodyView as TextView).text = nativeAd.body
                adView.bodyView.visibility = View.VISIBLE
            }
        }

        if (adView.callToActionView != null) {
            if (nativeAd.callToAction == null) {
                adView.callToActionView.visibility = View.INVISIBLE
            } else {
                adView.callToActionView.visibility = View.VISIBLE
                (adView.callToActionView as Button).text = nativeAd.callToAction
            }
        }

        if (adView.iconView != null) {
            if (nativeAd.icon == null) {
                adView.iconView.visibility = View.GONE
            } else {
                (adView.iconView as ImageView).setImageDrawable(
                    nativeAd.icon.drawable
                )
                adView.iconView.visibility = View.VISIBLE
            }
        }

        if (adView.priceView != null) {
            if (nativeAd.price == null) {
                adView.priceView.visibility = View.GONE
            } else {
                (adView.priceView as TextView).text = nativeAd.price
                adView.priceView.visibility = View.VISIBLE
            }
        }

        if (adView.storeView != null) {
            if (nativeAd.store == null) {
                adView.storeView.visibility = View.GONE
            } else {
                (adView.storeView as TextView).text = nativeAd.store
                adView.storeView.visibility = View.VISIBLE
            }
        }

        if (adView.starRatingView != null) {
            if (nativeAd.starRating == null) {
                adView.starRatingView.visibility = View.INVISIBLE
            } else {
                (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
                adView.starRatingView.visibility = View.VISIBLE
            }
        }

        if (adView.advertiserView != null) {
            if (nativeAd.advertiser == null) {
                adView.advertiserView.visibility = View.INVISIBLE
            } else {
                (adView.advertiserView as TextView).text = nativeAd.advertiser
                adView.advertiserView.visibility = View.VISIBLE
            }
        }

        adView.setNativeAd(nativeAd)
    }

    fun refreshAd(activity: Activity?, ad_frame: FrameLayout?) {
        val builder = AdLoader.Builder(activity, activity?.getString(R.string.native_advanced_ad_unit_id))

        builder.forUnifiedNativeAd { unifiedNativeAd ->
            @SuppressLint("InflateParams")
            val adView =
                activity?.layoutInflater?.inflate(R.layout.unified_native_ad_view, null) as UnifiedNativeAdView
            populateUnifiedNativeAdView(unifiedNativeAd, adView)
            ad_frame?.removeAllViews()
            ad_frame?.addView(adView)
        }

        val videoOptions = VideoOptions.Builder()
            .setStartMuted(true)
            .build()

        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()

        builder.withNativeAdOptions(adOptions)

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
                Timber.d("failed to load native advanced ad: $errorCode")
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun destroyNativeAd() {
        currentNativeAd?.destroy()
    }
}