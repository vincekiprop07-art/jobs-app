package com.example.jobsapp.model

import com.google.gson.annotations.SerializedName

data class Job(
    @SerializedName("id") val id: String? = null,
    @SerializedName("slug") val slug: String? = null,
    @SerializedName("company") val company: String? = null,
    @SerializedName("company_logo") val companyLogo: String? = null,
    @SerializedName("position") val position: String? = null,
    @SerializedName("tags") val tags: List<String>? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("salary_min") val salaryMin: Long? = null,
    @SerializedName("salary_max") val salaryMax: Long? = null,
    @SerializedName("url") val url: String? = null,
    @SerializedName("date") val date: String? = null
)
