package com.android.rahbar.advisor.mycourses

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.rahbar.advisor.R
import com.android.rahbar.advisor.Rahbarapplication
import com.android.rahbar.advisor.course.Course
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyCourseViewModel @Inject constructor() : ViewModel() {

    private val fireStore = FirebaseFirestore.getInstance()
    private val usersCollection = fireStore.collection("users")
    private val _courses = MutableLiveData<List<Course>>()
    val courses: LiveData<List<Course>> get() = _courses

    init {
        fetchUserCourses()
    }

    private fun fetchUserCourses() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            usersCollection.document(user.uid).collection("courses").get()
                .addOnSuccessListener { documents ->
                    val coursesList = mutableListOf<Course>()
                    for (document in documents) {
                        try {
                            val course = document.toObject(Course::class.java)
                            coursesList.add(course)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    _courses.value = coursesList
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, Rahbarapplication.appContext.getString(R.string.error_fetching_user_courses, exception))
                }
        }
    }

    companion object {
        private const val TAG = "MyCourseViewModel"
    }
}