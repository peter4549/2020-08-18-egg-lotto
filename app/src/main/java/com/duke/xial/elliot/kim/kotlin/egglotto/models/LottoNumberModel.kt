package com.duke.xial.elliot.kim.kotlin.egglotto.models

@Suppress("SpellCheckingInspection")
data class LottoNumberModel (val totSellamnt: Long,
                             val returnValue: String, // success, fail 두개.
                             val drwNoDate: String,
                             val firstWinamnt: Long, // 1명당 상금.
                             val drwtNo6: Long,
                             val drwtNo4: Long,
                             val firstPrzwnerCo: Long, // 1등 당첨자 수
                             val drwtNo5: Long,
                             val bnusNo: Long,
                             val firstAccumamnt: Long, // 1등의 총 당첨금 합..
                             val drwNo: Long,
                             val drwtNo2: Long,
                             val drwtNo3: Long,
                             val drwtNo1: Long)