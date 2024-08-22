package com.example.saksharudaan

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.saksharudaan.databinding.ActivityPlaylistBinding
import com.google.firebase.database.collection.LLRBNode.Color

class PlaylistActivity : AppCompatActivity() {
    private val binding: ActivityPlaylistBinding by lazy {
        ActivityPlaylistBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(binding.root)
        //by default playlist button and recycler view playlist is visible

        handelSwitchButton()

        val courseTitle = intent.getStringExtra("courseTitle")
        val instructorName = intent.getStringExtra("postedBy")

        binding.tvCourseTitle.text = courseTitle
        binding.tvInstructorName.text = instructorName


    }

    private fun handelSwitchButton() {
        binding.rvPlaylist.visibility = View.VISIBLE
        binding.btnDescription.visibility = View.INVISIBLE
        binding.cvDescription.visibility = View.GONE

        // Set up the buttons to switch views
        binding.tvPlaylistBtn.setOnClickListener {
            binding.rvPlaylist.visibility = View.VISIBLE
            binding.cvDescription.visibility = View.GONE
            binding.btnPlaylist.visibility = View.VISIBLE
            binding.btnDescription.visibility = View.INVISIBLE
        }


        binding.tvDescriptionBtn.setOnClickListener {
            binding.rvPlaylist.visibility = View.GONE
            binding.cvDescription.visibility = View.VISIBLE
            binding.btnPlaylist.visibility = View.INVISIBLE
            binding.btnDescription.visibility = View.VISIBLE
        }
    }
}


class FullScreenVideoView(context: Context, attrs: AttributeSet) : VideoView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width,height)
    }
}