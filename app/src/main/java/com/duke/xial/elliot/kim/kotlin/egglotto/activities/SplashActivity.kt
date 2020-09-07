package com.duke.xial.elliot.kim.kotlin.egglotto.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.duke.xial.elliot.kim.kotlin.egglotto.R
import com.duke.xial.elliot.kim.kotlin.egglotto.getVersionName
import com.duke.xial.elliot.kim.kotlin.egglotto.goToPlayStore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class SplashActivity : AppCompatActivity() {

    private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig
    private lateinit var messageMap: Map<String, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        messageMap = mapOf(
            "new_version" to getString(R.string.message_new_version)
        )

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config)
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        println("$TAG: remote config activate successful")
                        val versionUpdate = firebaseRemoteConfig.getBoolean("version_update")
                        val versionName = firebaseRemoteConfig.getString("version_name")
                        if (versionUpdate && (versionName != getVersionName(this))) {
                            showConfigMessage()
                        }
                        else {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    } else {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
    }

    private fun showConfigMessage() {
        val message = messageMap[firebaseRemoteConfig.getString("message")] ?: firebaseRemoteConfig.getString("message")
        val exitWhenNotUpdating = firebaseRemoteConfig.getBoolean("exit_when_not_updating")
        var buttonClicked = false
        val builder = AlertDialog.Builder(this)
        builder
            .setTitle(getString(R.string.notice))
            .setMessage(message)
            .setPositiveButton(getString(R.string.update)) { _, _ ->
                buttonClicked = true
                goToPlayStore(this)
                finish()
            }.setNegativeButton(
                if (exitWhenNotUpdating) getString(R.string.exit)
                else getString(R.string.later)) { _, _ ->
                buttonClicked = true
                if (exitWhenNotUpdating)
                    finish()
                else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }.setOnDismissListener {
                // Back button pressed
                if (!buttonClicked)
                    finish()
            }
        builder.create()

        val dialog = builder.show()!!
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(false)

        val titleId = resources.getIdentifier("alertTitle", "id", packageName)
        var titleTextView: TextView? = null
        if (titleId > 0)
            titleTextView = dialog.findViewById(titleId)!!

        val messageTextView = dialog.findViewById<TextView>(android.R.id.message)!!
        val okButton = dialog.findViewById<Button>(android.R.id.button1)!!
        val cancelButton = dialog.findViewById<Button>(android.R.id.button2)!!

        val font = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            resources.getFont(R.font.font_family_cookie_run_regular)
        else ResourcesCompat.getFont(this, R.font.font_family_cookie_run_regular)

        if (titleTextView != null)
            titleTextView.typeface = font

        messageTextView.typeface = font
        okButton.typeface = font
        cancelButton.typeface = font
    }

    companion object {
        private const val TAG = "SplashActivity"
    }
}