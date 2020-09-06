package com.duke.xial.elliot.kim.kotlin.egglotto

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun showToast(context: Context, text: String, duration: Int = Toast.LENGTH_LONG) {
    CoroutineScope(Dispatchers.Main).launch {
        Toast.makeText(context, text, duration).show()
    }
}

fun View.expand() {
    val matchParentMeasureSpec: Int = View.MeasureSpec.makeMeasureSpec(
        (this.parent as View).width,
        View.MeasureSpec.EXACTLY
    )

    val wrapContentMeasureSpec: Int =
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    this.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
    val targetHeight: Int = this.measuredHeight
    val originHeight = this.height

    // Older versions of Android (prior to API 21) cancel the animation of 0 height views.
    this.layoutParams.height = originHeight
    this.visibility = View.VISIBLE
    val animation: Animation = object : Animation() {
        override fun applyTransformation(
            interpolatedTime: Float,
            t: Transformation?
        ) {
            if (interpolatedTime == 1F)
                this@expand.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            else
                this@expand.layoutParams.height =
                    if ((targetHeight * interpolatedTime).toInt() < originHeight)
                        originHeight
                else
                    (targetHeight * interpolatedTime).toInt()
            this@expand.requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    // 1dp/ms expansion rate
    //animation.duration = (targetHeight / this.context.resources.displayMetrics.density).toLong()
    animation.duration = 320L
    this.startAnimation(animation)
}

fun View.collapse(targetHeight: Int) {
    val initialHeight: Int = this.measuredHeight
    val animation: Animation = object : Animation() {
        override fun applyTransformation(
            interpolatedTime: Float,
            t: Transformation?
        ) {
            if (interpolatedTime == 1F) {
                this@collapse.layoutParams.height = targetHeight
            } else {
                this@collapse.layoutParams.height =
                    if ((initialHeight - (initialHeight * interpolatedTime).toInt()) > targetHeight)
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    else
                        targetHeight
            }

            this@collapse.requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    // 1dp/ms collapse rate
    // animation.duration = (initialHeight / this.context.resources.displayMetrics.density).toLong()

    animation.duration = 320L
    this.startAnimation(animation)
}

fun getVersionName(context: Context): String {
    return try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        return "version info not found"
    }
}

fun goToPlayStore(context: Context) {
    try {
        context.startActivity(
            Intent (
                Intent.ACTION_VIEW,
            Uri.parse("market://details?id=${context.packageName}"))
        )
    } catch (e: ActivityNotFoundException) {
        context.startActivity(
            Intent (
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
            )
        )
    }
}

fun View.fadeIn(duration: Int = resources.getInteger(android.R.integer.config_shortAnimTime),
                animatorListenerAdapter: AnimatorListenerAdapter? = null) {
    this.apply {
        alpha = 0F
        visibility = View.VISIBLE
    }

    animate()
        .alpha(1F)
        .setDuration(duration.toLong())
        .setListener(animatorListenerAdapter)
}

fun View.fadeOut(duration: Int = resources.getInteger(android.R.integer.config_shortAnimTime)) {

    this.animate()
        .alpha(0F)
        .setDuration(duration.toLong())
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                this@fadeOut.visibility = View.GONE
            }
        })
}

/*
private fun startTimer() {
    timerTask = timer(period = 60000L) {
        if (timerCount > 0) {
            runOnUiThread {
                interstitialAd.loadAd(AdRequest.Builder().build())
            }

            showInterstitialAd = true
            timerCount = -1
        }
        timerCount++
    }
}
 */