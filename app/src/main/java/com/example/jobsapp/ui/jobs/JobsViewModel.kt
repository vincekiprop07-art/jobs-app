package com.example.jobsapp.ui.jobs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobsapp.model.Job
import com.example.jobsapp.network.RetrofitClient
import kotlinx.coroutines.launch

class JobsViewModel : ViewModel() {

    private val _jobs = MutableLiveData<List<Job>>()
    val jobs: LiveData<List<Job>> = _jobs

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadJobs() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val result = RetrofitClient.api.getJobs()
                _jobs.value = result.filter { !it.position.isNullOrBlank() }
            } catch (e: Exception) {
                _error.value = "Couldn't load jobs: ${e.message ?: "unknown error"}"
            } finally {
                _loading.value = false
            }
        }
    }
}
