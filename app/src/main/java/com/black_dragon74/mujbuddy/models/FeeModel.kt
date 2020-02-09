package com.black_dragon74.mujbuddy.models

data class FeeModel(
    val success: Boolean,
    val paid: PaidFee?,
    val unpaid: UnpaidFee?
)

data class PaidFee(
    val semester1: String?,
    val total: String?
)

data class UnpaidFee(
    val total: String?
)