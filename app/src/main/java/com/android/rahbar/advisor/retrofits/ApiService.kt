package com.android.rahbar.advisor.retrofits

import com.android.rahbar.advisor.course.CourseResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {

	@GET("/api-2.0/courses/")
	suspend fun getCourseList(
		@Header("Authorization") authorization: String,
		@Query("page") page: Int,
		@Query("page_size") pageSize: Int
	): CourseResponse


	@GET("/api-2.0/courses/")
	suspend fun getSearchCourseList(
		@Header("Authorization") authorization: String,
		@Query("page") page: Int,
		@Query("page_size") pageSize: Int,
		@Query("search") searchText: String

	): CourseResponse
}