package com.arb.app.photoeffect.network

import com.arb.app.photoeffect.util.Constant.BASE_URL
import com.arb.app.photoeffect.util.Constant.BASE_URL2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitClient {
    @Provides
    @Singleton
    fun getRetrofit(): ApiInterface {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .connectTimeout(600, TimeUnit.SECONDS)
            .readTimeout(600, TimeUnit.SECONDS)
            .writeTimeout(600, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiInterface::class.java)
    }
}

//@Module
//@InstallIn(SingletonComponent::class)
//object RetrofitModule {
//    private fun getClient(): OkHttpClient {
//        val logging = HttpLoggingInterceptor()
//        logging.level = HttpLoggingInterceptor.Level.BODY
//        return OkHttpClient.Builder()
//            .connectTimeout(600, TimeUnit.SECONDS)
//            .readTimeout(600, TimeUnit.SECONDS)
//            .writeTimeout(600, TimeUnit.SECONDS)
//            .addInterceptor(logging)
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideApi8000(): ApiInterface {
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(getClient())
//            .build()
//            .create(ApiInterface::class.java)
//    }
//
//    @Provides
//    @Singleton
//    fun provideApi8001(): ApiInterface8003 {
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL2)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(getClient())
//            .build()
//            .create(ApiInterface8003::class.java)
//    }
//}
