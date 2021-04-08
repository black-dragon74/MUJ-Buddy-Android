package com.black_dragon74.mujbuddy.models

import com.google.gson.annotations.SerializedName

data class GPAModel(
        @SerializedName("gpa_semester_i")
        val semester_1: String?,

        @SerializedName("gpa_semester_ii")
        val semester_2: String?,

        @SerializedName("gpa_semester_iii")
        val semester_3: String?,

        @SerializedName("gpa_semester_iv")
        val semester_4: String?,

        @SerializedName("gpa_semester_v")
        val semester_5: String?,

        @SerializedName("gpa_semester_vi")
        val semester_6: String?,

        @SerializedName("gpa_semester_vii")
        val semester_7: String?,

        @SerializedName("gpa_semester_viii")
        val semester_8: String?
)