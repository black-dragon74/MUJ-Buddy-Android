package com.black_dragon74.mujbuddy.models

data class CaptchaModel (
    val sessionid: String,
    val generator: String,
    val encodedImage: String
)