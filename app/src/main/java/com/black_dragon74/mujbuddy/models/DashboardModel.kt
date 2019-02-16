package com.black_dragon74.mujbuddy.models

class DashboardModel(
    val admDetails: AdmDetails,
    val eduQualifications: Array<EduQualifications>,
    val parentDetails: ParentDetails
)

class EduQualifications(
    val index: String,
    val obtainedMarks: String,
    val maxMarks: String,
    val year: String,
    val percentage: String,
    val board: String,
    val grade: String,
    val institution: String
)

class AdmDetails(
    val name: String,
    val acadYear: String,
    val regNo: String,
    val program: String
)

class ParentDetails(
    val mother: String,
    val father: String,
    val email: String,
    val mobileNo: String,
    val emergencyContact: String
)