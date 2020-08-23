package com.duke.xial.elliot.kim.kotlin.egglotto

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
        var positiveButtonClicked = false
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
            .setPositiveButton(getString(R.string.update)) { _, _ ->
                positiveButtonClicked = true
                goToPlayStore(this)
            }
            .setNegativeButton(
                if (exitWhenNotUpdating) getString(R.string.exit)
                else getString(R.string.later)) { _, _ ->

                if (exitWhenNotUpdating)
                    finish()
                else {
                    positiveButtonClicked = false
                }
            }
            .setOnDismissListener {
                if (!positiveButtonClicked) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        builder.create().show()
    }

    companion object {
        private const val TAG = "SplashActivity"
    }
}