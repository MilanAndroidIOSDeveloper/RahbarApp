package com.android.rahbar.advisor.coursedetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.rahbar.advisor.R
import com.android.rahbar.advisor.course.Course
import com.android.rahbar.advisor.databinding.ActivityCourseDetailBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class CourseDetailActivity : AppCompatActivity() {

    private val repository =
        CourseAddRepository(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance())
    private lateinit var binding: ActivityCourseDetailBinding

    companion object {
        private const val EXTRA_COURSE = "SelectedCourse"

        fun newIntent(context: Context, course: Course): Intent {
            return Intent(context, CourseDetailActivity::class.java).apply {
                putExtra(EXTRA_COURSE, course)
            }
        }

        fun getCourseExtra(intent: Intent): Course? {
            return intent.getParcelableExtra(EXTRA_COURSE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val course = getCourseExtra(intent)
        if (course != null) {
            setupToolbar()
            displayCourseDetails(course)
            checkEnrollmentStatus(course)
            setupEnrollButton(course)
        } else {
            finish()
        }
    }

    private fun setupToolbar() {
        supportActionBar?.title = getString(R.string.course_details)
    }

    private fun setupEnrollButton(course: Course) {
        binding.enrollTv.setOnClickListener { enrollOrShowAlreadyEnrolled(course) }
    }

    private fun checkEnrollmentStatus(course: Course) {
        lifecycleScope.launch {
            try {
                val isEnrolled = repository.isEnrolledInCourse(course.id.toString())
                if (isEnrolled) {
                    binding.enrollTv.text = getString(R.string.enrolled)
                    binding.enrollTv.isEnabled = false
                } else {
                    binding.enrollTv.text = getString(R.string.enroll_now)
                    binding.enrollTv.isEnabled = true
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@CourseDetailActivity,
                    getString(R.string.failed_to_check_enrollment_status, e.message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun enrollOrShowAlreadyEnrolled(course: Course) {
        lifecycleScope.launch {
            try {
                repository.storeSelectedCourse(course)
                binding.enrollTv.text = getString(R.string.enrolled)
                binding.enrollTv.isEnabled = false

            } catch (e: Exception) {
                Toast.makeText(
                    this@CourseDetailActivity,
                    getString(R.string.failed_to_add_course, e.message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun displayCourseDetails(course: Course) {
        binding.apply {
            title.text = course.title
            authorName.text = course.visible_instructors[0].title
            descriptions.text = course.headline

            Glide.with(this@CourseDetailActivity)
                .load(course.image_480x270)
                .error(R.drawable.no_img_available)
                .centerCrop()
                .into(courseImageView)

            Glide.with(this@CourseDetailActivity)
                .load(course.visible_instructors[0].image_100x100)
                .error(R.drawable.no_img_available)
                .centerCrop()
                .into(authorImageview)
        }
    }


//    private val repository = CourseAddRepository()
//    private lateinit var binding: ActivityCourseDetailBinding
//
//
//    companion object {
//        private const val EXTRA_COURSE = "SelectedCourse"
//
//        fun newIntent(context: Context, course: Course): Intent {
//            val intent = Intent(context, CourseDetailActivity::class.java)
//            intent.putExtra(EXTRA_COURSE, course)
//            return intent
//        }
//
//        fun getCourseExtra(intent: Intent): Course? {
//            return intent.getParcelableExtra(EXTRA_COURSE)
//        }
//    }
//
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityCourseDetailBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        setSupportActionBar(binding.toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?.setTitle("Course Details")
//
//        val course = getCourseExtra(intent)
//        if (course != null) {
//            displayCourseDetails(binding, course)
//            checkEnrollmentStatus(course)
//        } else {
//            finish()
//        }
//
//        binding.enrollTv.setOnClickListener {
//            addCourse(course)
//
//        }
//
//    }
//
//    private fun addCourse(course: Course?) {
//        repository.storeSelectedCourse(course!!)
//        binding.enrollTv.text = "Enrolled"
//        binding.enrollTv.isEnabled = false
//    }
//
//    private fun checkEnrollmentStatus(course: Course) {
//        repository.isEnrolledInCourse(course.id.toString()) { isEnrolled ->
//            if (isEnrolled) {
//                // User is enrolled in the course
//                binding.enrollTv.text = "Enrolled"
//                binding.enrollTv.isEnabled = false
//            } else {
//                // User is not enrolled in the course
//                binding.enrollTv.text = "Enroll Now"
//                binding.enrollTv.isEnabled = true
//            }
//        }
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//
//        if (item.itemId == android.R.id.home) {
//            finish()
//        }
//
//        return super.onOptionsItemSelected(item)
//    }
//    private fun displayCourseDetails(binding: ActivityCourseDetailBinding, course: Course) {
//        binding.title.text = course.title
//        binding.authorName.text = course.visible_instructors[0].title
//        binding.descriptions.text = course.headline
//
//        Glide.with(this@CourseDetailActivity)
//            .load(course.image_480x270)
//            .error(R.drawable.no_img_available)
//            .centerCrop()
//            .into(binding.courseImageView)
//
//        Glide.with(this@CourseDetailActivity)
//            .load(course.visible_instructors[0].image_100x100)
//            .error(R.drawable.no_img_available)
//            .centerCrop()
//            .into(binding.authorImageview)
//    }
}