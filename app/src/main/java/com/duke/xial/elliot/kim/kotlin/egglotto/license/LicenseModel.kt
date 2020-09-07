package com.duke.xial.elliot.kim.kotlin.egglotto.license

data class LicenseModel(val name: String,
                        val link: String,
                        val copyright: String?)

internal val licenses = arrayListOf(
    LicenseModel("Retrofit",
        "https://square.github.io/retrofit/",
        "retrofit.txt"),
    LicenseModel("Kotlin",
        "https://github.com/JetBrains/kotlin/blob/master/license/README.md",
        "kotlin.txt"),
    LicenseModel("Kotlinx Coroutines",
        "https://github.com/JetBrains/kotlin/blob/master/license/README.md",
        "kotlin.txt"),
    LicenseModel("AndroidX AppCompat",
        "https://developer.android.com/topic/libraries/support-library",
        "android.txt"),
    LicenseModel("AndroidX ConstraintLayout",
        "https://developer.android.com/topic/libraries/support-library",
        "android.txt"),
    LicenseModel("AndroidX Core",
        "https://developer.android.com/topic/libraries/support-library",
        "android.txt"),
    LicenseModel("Android Material Components",
        "https://developer.android.com/topic/libraries/support-library",
        "android.txt"),
    LicenseModel("Gson",
        "https://github.com/google/gson",
        "gson.txt"),
    LicenseModel("Glide",
        "https://github.com/bumptech/glide/blob/master/LICENSE",
        "glide.txt"),
    LicenseModel("ZXing",
        "https://zxing.github.io/zxing/licenses.html",
        null)
)