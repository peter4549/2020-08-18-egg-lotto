package com.duke.xial.elliot.kim.kotlin.egglotto.license

data class LicenseModel(val name: String,
                        val copyright: String,
                        val link: String,
                        val license: String)

internal val licenses = arrayListOf(
    LicenseModel("Retrofit",
        "Copyright 2013 Square, Inc.",
        "https://square.github.io/retrofit/",
        APACHE_LICENSE),
    LicenseModel("Kotlin Standard Library",
        "Copyright 2000-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.",
        "https://kotlinlang.org/api/latest/jvm/stdlib/",
        APACHE_LICENSE)
)