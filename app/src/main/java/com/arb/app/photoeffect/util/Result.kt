package com.arb.app.photoeffect.util

import java.lang.Exception

sealed class Result<out T> {
    data class Success<out R>(val data:R) : Result<R>()
    data class Failure(val exception: Exception) : Result<Nothing>()
}