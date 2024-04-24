package com.android.rahbar.advisor.course

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.rahbar.advisor.R
import com.android.rahbar.advisor.coursedetail.CourseDetailActivity
import com.android.rahbar.advisor.databinding.CoursesItemListBinding
import com.bumptech.glide.Glide

class CourseListAdapter : RecyclerView.Adapter<CourseListAdapter.CourseViewHolder>() {


    private val courses = mutableListOf<Course>()


    fun addNewCourse(newCourses: List<Course>) {
        courses.addAll(newCourses)
        notifyDataSetChanged()
    }

    fun addSearchCourse(newCourses: List<Course>) {
        courses.clear()
        courses.addAll(newCourses)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = CoursesItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]
        holder.bind(course)
    }

    override fun getItemCount(): Int = courses.size

    inner class CourseViewHolder(private val binding: CoursesItemListBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val course = courses[position]
                    val intent = CourseDetailActivity.newIntent(it.context, course)
                    it.context.startActivity(intent)
                }
            }
        }

        fun bind(course: Course) {
            binding.apply {
                title.text = course.title
                authorName.text = course.visible_instructors.firstOrNull()?.title ?: ""
                descriptions.text = course.headline

                Glide.with(itemView)
                    .load(course.image_480x270)
                    .error(R.drawable.no_img_available)
                    .centerCrop()
                    .into(courseImageView)

                Glide.with(itemView)
                    .load(course.visible_instructors.firstOrNull()?.image_100x100)
                    .error(R.drawable.no_img_available)
                    .centerCrop()
                    .into(authorImageview)
            }
        }
    }
}