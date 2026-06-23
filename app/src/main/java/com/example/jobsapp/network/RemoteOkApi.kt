package com.example.jobsapp.network

import com.example.jobsapp.model.Job
import retrofit2.http.GET

interface RemoteOkApi {
    // RemoteOK's public JSON feed. No API key required.
    // The first element of the array is a legal notice, not a job -
    // callers should filter out entries with a null/blank "position".
    @GET("api")
    suspend fun getJobs(): List<Job>
}
