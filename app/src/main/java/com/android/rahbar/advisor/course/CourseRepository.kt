package com.android.rahbar.advisor.course

import com.android.rahbar.advisor.BuildConfig
import com.android.rahbar.advisor.retrofits.ApiService

class CourseRepository(private val apiService: ApiService) {

    suspend fun getCourses(page: Int, pageSize: Int): CourseResponse =
        apiService.getCourseList(BuildConfig.AUTH_TOKEN, page, pageSize)

    suspend fun searchCourses(searchText: String, page: Int, pageSize: Int): CourseResponse =
        apiService.getSearchCourseList(BuildConfig.AUTH_TOKEN, page, pageSize, searchText)

}