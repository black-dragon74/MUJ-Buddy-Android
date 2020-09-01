package com.black_dragon74.mujbuddy.models

data class CaptchaResponseModel (
    val sessionid: String,
    val username: String?,
    val captchaFailed: Boolean,
    val loginSucceeded: Boolean,
    val credentialsError: Boolean
)