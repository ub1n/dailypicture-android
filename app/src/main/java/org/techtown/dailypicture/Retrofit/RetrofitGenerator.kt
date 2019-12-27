package org.techtown.kotlin_todolist

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



object RetrofitGenerator {
    val builder = OkHttpClient.Builder()
    init {
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }       // Retrofit 에서 통신 과정의 로그를 확인하기 위함. 로그의 level 을 지정
        builder.addInterceptor(httpLoggingInterceptor)

    }
    val okHttpClient = builder
        .build()






    private val retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("http://dailypicture-cname.ap-northeast-2.elasticbeanstalk.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    /*private val retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("http://dailypicture-backend-test-dev.ap-northeast-2.elasticbeanstalk.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()*/
    fun create(): RetrofitService = retrofit.create(RetrofitService::class.java)
}


