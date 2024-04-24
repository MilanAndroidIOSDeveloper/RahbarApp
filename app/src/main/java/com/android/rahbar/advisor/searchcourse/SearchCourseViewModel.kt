package com.android.rahbar.advisor.searchcourse

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.rahbar.advisor.BuildConfig
import com.android.rahbar.advisor.Rahbarapplication
import com.android.rahbar.advisor.course.Course
import com.android.rahbar.advisor.course.CourseRepository
import com.android.rahbar.advisor.course.CourseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchCourseViewModel @Inject constructor(private val repository: CourseRepository) : ViewModel() {

    private val _courses = MutableLiveData<List<Course>>()
    val courses: LiveData<List<Course>> = _courses

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var currentPage = 1
    private var pageSize = 9999

    fun fetchSearchCourseList(searchText: String) {
        if (_isLoading.value == true) return
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.searchCourses(searchText, currentPage, pageSize)
                handleResponse(response)
            } catch (e: Exception) {
                handleErrorResponse(e)
            }
        }
    }

    private fun handleResponse(response: CourseResponse) {
        val coursesList = response.results
        _courses.postValue(coursesList)
        currentPage++
        _isLoading.postValue(false)
    }

    private fun handleErrorResponse(error: Throwable) {
        Toast.makeText(Rahbarapplication.appContext, error.message, Toast.LENGTH_SHORT).show()
        _isLoading.postValue(false)
    }

}