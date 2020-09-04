package com.duke.xial.elliot.kim.kotlin.egglotto.retrofit2

import com.duke.xial.elliot.kim.kotlin.egglotto.models.LottoNumberModel
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


object DailyHoroscope {
    const val BASE_URL = "https://m.search.naver.com"
    const val GET_URL = "p/csearch/dcontent/external_api/json_todayunse_v2.naver?_callback=window.__jindo2_callback._fortune_my_0"

    interface DailyHoroscopeService {
        @GET(GET_URL)
        fun requestLottoNumber(
            @Query("gender") gender: String,
            @Query("birth") birth: String,
            @Query("solarCal") solarCal: String
        ): Call<LottoNumberModel>
    }
    //base url - https://m.search.naver.com/p/csearch/dcontent/external_api/json_todayunse_v2.naver?_callback=window.__jindo2_callback._fortune_my_0
    // &gender=f&birth=19940910&solarCal=solar&time=0
    // gender에 m, f
    // birth 19940910 같은거.
    // solarCal에 solar/lularGeneral, lunarLeap
}

object DailyHoroscopeApisRequest {

    private fun getRetrofitAddedHeader() : Retrofit {
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Access-Control-Allow-Origin", "*").build()
            chain.proceed(request)
        }

        return Retrofit.Builder().baseUrl(DailyHoroscope.BASE_URL)
            .client(okHttpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    fun getDailyHoroscopeService(): DailyHoroscope.DailyHoroscopeService =
        getRetrofitAddedHeader().create(DailyHoroscope.DailyHoroscopeService::class.java)

}

object SolarCalendar {
    const val solar = "solar"
    const val lunarGeneral = "lunarGeneral"
    const val lunarLeap = "lunarLeap"
}