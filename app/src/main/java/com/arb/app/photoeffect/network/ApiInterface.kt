package com.arb.app.photoeffect.network

import android.net.wifi.hotspot2.pps.Credential
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiInterface {

    @POST("user/login")
    suspend fun userLogin(
        @Body userCredential: Credential.UserCredential
    ): Credential.UserCredential

    @Multipart
    @POST("api1/api/cartoonize")
    suspend fun cartoonize(
        @Part file: MultipartBody.Part
    ): ResponseBody

    @Multipart
    @POST("api1/api/portrait")
    suspend fun portrait(
        @Part file: MultipartBody.Part
    ): ResponseBody

    @Multipart
    @POST("api1/api/face_enhance")
    suspend fun enhanceQuality(
        @Part file: MultipartBody.Part
    ): ResponseBody

    @Multipart
    @POST("api3/api/restore/")
    suspend fun removeScratch(
        @Part file: MultipartBody.Part,
        @Part("gpu") gpu: RequestBody,
        @Part("with_scratch") withScratch: RequestBody,
        @Part("hr") hr: RequestBody
    ): ResponseBody

    @Multipart
    @POST("api1/api/solid")
    suspend fun solidBg(
        @Part file: MultipartBody.Part,
        @Part("color") color: RequestBody
    ): ResponseBody

    @Multipart
    @POST("api1/api/gradient")
    suspend fun gradientBg(
        @Part file: MultipartBody.Part,
        @Part("color1") color1: RequestBody,
        @Part("color2") color2: RequestBody
    ): ResponseBody

    @Multipart
    @POST("api2/api/colorize/")
    suspend fun colorizeImage(
        @Part file: MultipartBody.Part
    ): ResponseBody
}

//interface ApiInterface8003 {
//    @Multipart
//    @POST("api/colorize/")
//    suspend fun colorizeImage(
//        @Part image: MultipartBody.Part
//    ): ResponseBody
//
//}
