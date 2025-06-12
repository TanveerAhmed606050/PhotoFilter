package com.arb.app.photoeffect.network

import android.net.wifi.hotspot2.pps.Credential
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {

    @POST("user/login")
    suspend fun userLogin(
        @Body userCredential: Credential.UserCredential
    ): Credential.UserCredential

}