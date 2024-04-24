package com.android.rahbar.advisor.coursedetail

import android.util.Log
import com.android.rahbar.advisor.R
import com.android.rahbar.advisor.Rahbarapplication
import com.android.rahbar.advisor.course.Course
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CourseAddRepository(private val db: FirebaseFirestore, private val auth: FirebaseAuth) {

    suspend fun storeSelectedCourse(course: Course) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userCoursesRef = db.collection("users").document(userId).collection("courses")
            try {
                userCoursesRef.add(course).await()
                Log.d(TAG, Rahbarapplication.appContext.getString(R.string.course_added))
            } catch (e: Exception) {
                Log.w(TAG, Rahbarapplication.appContext.getString(R.string.error_adding_course), e)
                throw e
            }
        }
    }

    suspend fun isEnrolledInCourse(courseId: String): Boolean {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userCoursesRef = db.collection("users").document(userId).collection("courses")
            try {
                val querySnapshot = userCoursesRef.get().await()
                for (document in querySnapshot.documents) {
                    val courseData = document.data
                    if (courseData?.get("id").toString() == courseId) {
                        return true
                    }
                }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    Rahbarapplication.appContext.getString(R.string.error_checking_course_enrollment),
                    e
                )
                throw e
            }
        }
        return false
    }

    companion object {
        private const val TAG = "CourseAddRepository"
    }
}