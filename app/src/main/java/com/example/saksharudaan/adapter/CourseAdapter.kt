package com.example.saksharudaan.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.saksharudaan.EditProfileActivity
import com.example.saksharudaan.PlaylistActivity
import com.example.saksharudaan.databinding.HomeRvItemBinding
import com.example.saksharudaan.model.UserModel
import com.example.saksharudaan.model.CourseModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CourseAdapter(
    private val context: Context,
    private val courseList: MutableList<CourseModel> = mutableListOf()
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {
    val database = FirebaseDatabase.getInstance()
    val auth = FirebaseAuth.getInstance()
    private lateinit var instructorName: String


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding =
            HomeRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CourseViewHolder(binding)
    }

    override fun getItemCount(): Int = courseList.size

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {

        holder.bind(position)
        holder.itemView.setOnClickListener{
            val courseItem = courseList[position]
            val intent = Intent(context, PlaylistActivity::class.java)
            intent.putExtra("postId",courseItem.postId)
            intent.putExtra("courseTitle",courseItem.courseTitle)
            intent.putExtra("postedBy",instructorName)
            intent.putExtra("coursePrice",courseItem.coursePrice)
            intent.putExtra("courseDuration",courseItem.courseDuration)
            intent.putExtra("courseDescription",courseItem.courseDescription)
            intent.putExtra("introVideoUrl",courseItem.courseVideoUrl)
            context.startActivity(intent)
        }
    }

    inner class CourseViewHolder(private val binding: HomeRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val courseItem = courseList[position]
            binding.apply {
                tvCourseTitle.text = courseItem.courseTitle
                tvPrice.text = courseItem.coursePrice.toString()
                val uriString = courseItem.courseThumbnailUrl.toString()
                val uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(courseImage)

                // Fetch instructor(admin) data associated with this course
                if (courseItem.postedBy != null) {
                    database.reference.child("admin").child(courseItem.postedBy!!).child("profile")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(userSnapshot: DataSnapshot) {
                                val user = userSnapshot.getValue(UserModel::class.java)
                                user?.let {
                                    Glide.with(context).load(Uri.parse(it.imageUri)).into(imgProfile)
                                    binding.tvInstructorName.text = it.name
                                    instructorName = it.name.toString()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show()
                            }
                        })
                }
            }
        }

    }
}

class FullScreenVideoView(context: Context, attrs: AttributeSet) : VideoView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }
}