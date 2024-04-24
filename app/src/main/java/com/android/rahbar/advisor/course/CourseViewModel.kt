package com.android.rahbar.advisor.course

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(private val repository: CourseRepository) : ViewModel() {

    private val _course = MutableLiveData<List<Course>>()
    val course: LiveData<List<Course>> = _course

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var currentPage = 1
    private var pageSize = 50

    init {
        fetchCourseList()
    }

    private fun fetchCourseList() {
        if (_isLoading.value == true) return
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.getCourses(currentPage, pageSize)
                handleResponse(response)
            } catch (e: Exception) {
                handleErrorResponse(e)
            }
        }
    }

    private fun handleResponse(response: CourseResponse) {
        if (response.results.isNotEmpty()) {
            val newCourse = response.results
            _course.postValue(newCourse)
            currentPage++
        } else {
            handleErrorResponse(Exception("Api Error"))
        }
        _isLoading.value = false
    }

    private fun handleErrorResponse(error: Throwable) {
        _isLoading.value = false
    }

    fun getMoreCourse() {
        if (_isLoading.value == true) return
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.getCourses(currentPage, pageSize)
                handleResponse(response)
            } catch (e: Exception) {
                handleErrorResponse(e)
            }
        }
    }
}