package com.arb.app.photoeffect.network

import android.net.wifi.hotspot2.pps.Credential
import okhttp3.MultipartBody
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
    @POST("api/colorize/")
    suspend fun colorizeImage(
        @Part image: MultipartBody.Part
    ): ResponseBody

    @Multipart
    @POST("api/cartoonize")
    suspend fun cartoonize(
        @Part image: MultipartBody.Part
    ): ResponseBody

    @Multipart
    @POST("api/face_enhance")
    suspend fun enhanceQuality(
        @Part image: MultipartBody.Part
    ): ResponseBody
}