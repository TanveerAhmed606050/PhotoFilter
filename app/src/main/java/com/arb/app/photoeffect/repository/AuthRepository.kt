package com.arb.app.photoeffect.repository

import android.net.wifi.hotspot2.pps.Credential
import com.arb.app.photoeffect.network.ApiInterface
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiInterface: ApiInterface
) :
    BaseRepository() {
    suspend fun userLogin(userCredential: Credential.UserCredential) = loadResource {
        apiInterface.userLogin(userCredential)
    }
}