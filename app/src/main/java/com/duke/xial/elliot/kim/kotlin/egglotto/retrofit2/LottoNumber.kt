package com.duke.xial.elliot.kim.kotlin.egglotto.retrofit2

import com.duke.xial.elliot.kim.kotlin.egglotto.models.LottoNumberModel
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object LottoNumberApis {
    interface LottoNumberService {
        @GET("common.do?method=getLottoNumber")
        fun requestLottoNumber(
            @Query("drwNo") drwNo: String
        ): Call<LottoNumberModel>
    }
}

object LottoNumberApisRequest {

    private fun getRetrofitAddedHeader() : Retrofit {
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Access-Control-Allow-Origin", "*").build()
            chain.proceed(request)
        }

        return Retrofit.Builder().baseUrl("https://www.dhlottery.co.kr")
            .client(okHttpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    fun getLottoNumberService(): LottoNumberApis.LottoNumberService =
        getRetrofitAddedHeader().create(LottoNumberApis.LottoNumberService::class.java)

}


