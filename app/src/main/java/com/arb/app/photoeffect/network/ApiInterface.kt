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
        @Part image: MultipartBody.Part
    ): ResponseBody

    @Multipart
    @POST("api1/api/face_enhance")
    suspend fun enhanceQuality(
        @Part image: MultipartBody.Part
    ): ResponseBody

    @Multipart
    @POST("api1/api/solid")
    suspend fun solidBg(
        @Part image: MultipartBody.Part,
        @Part("color") color: RequestBody
    ): ResponseBody

    @Multipart
    @POST("api1/api/gradient")
    suspend fun gradientBg(
        @Part image: MultipartBody.Part,
        @Part("color1") color1: RequestBody,
        @Part("color2") color2: RequestBody
    ): ResponseBody

    @Multipart
    @POST("api2/api/colorize/")
    suspend fun colorizeImage(
        @Part image: MultipartBody.Part
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
