package com.black_dragon74.mujbuddy.models

class FeeModel(
    val success: Boolean,
    val paid: PaidFee?,
    val unpaid: UnpaidFee?
)

class PaidFee(
    val semester1: String?,
    val total: String?
)

class UnpaidFee(
    val total: String?
)