package com.example.saksharudaan

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saksharudaan.databinding.ActivityPlaylistBinding
import com.example.saksharudaan.model.PlaylistModel
import com.example.saksharudaanadmin.adapter.PlaylistAdapter
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PlaylistActivity : AppCompatActivity() {
    private val binding: ActivityPlaylistBinding by lazy {
        ActivityPlaylistBinding.inflate(layoutInflater)
    }
    private lateinit var simpleExoPlayer: SimpleExoPlayer
    private lateinit var playlist: ArrayList<PlaylistModel>
    private lateinit var database: FirebaseDatabase
    private lateinit var playlistAdapter: PlaylistAdapter

    private var isFullscreen = false
    private var originalLayoutParams: ViewGroup.LayoutParams? = null
    private val handler = Handler(Looper.getMainLooper())
    private val hideFullscreenButtonRunnable = Runnable {
        binding.btnFullscreen.visibility = View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize database
        database = FirebaseDatabase.getInstance()

        handleSwitchButton()

        val postId = intent.getStringExtra("postId")
        val courseTitle = intent.getStringExtra("courseTitle")
        val instructorName = intent.getStringExtra("postedBy")
        val coursePrice = intent.getStringExtra("coursePrice")
        val courseDuration = intent.getStringExtra("courseDuration")
        val courseDescription = intent.getStringExtra("courseDescription")
        val introVideoUrl = intent.getStringExtra("introVideoUrl")

        binding.apply {
            tvCourseTitle.text = courseTitle
            tvInstructorName.text = instructorName
            tvCoursePrice.text = "Rs. $coursePrice"
            tvCourseDuration.text = "$courseDuration Hours"
            tvCourseDescription.text = courseDescription
        }

        playIntroVideo(introVideoUrl.toString())
        retrievePlaylistAndSetAdapter()


        binding.btnFullscreen.setOnClickListener {
            toggleFullscreen()
        }




    }

    private fun toggleFullscreen() {
        if (isFullscreen) {
            // Exit fullscreen
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            // Restore original layout params
            if (originalLayoutParams != null) {
                binding.videoPlayer.layoutParams = originalLayoutParams
            }

            // Set the icon for fullscreen mode
            binding.btnFullscreen.setImageResource(R.drawable.ic_fullscreen)
        } else {
            // Enter fullscreen
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            // Save original layout params
            if (originalLayoutParams == null) {
                originalLayoutParams = binding.videoPlayer.layoutParams
            }

            // Set layout params for fullscreen
            binding.videoPlayer.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            // Set the icon for exit fullscreen mode
            binding.btnFullscreen.setImageResource(R.drawable.ic_fullscreen_exit)
        }
        isFullscreen = !isFullscreen
        binding.videoPlayer.requestLayout()

    }

    private fun handleSwitchButton() {
        // By default, playlist button and recycler view playlist are visible
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

    private fun retrievePlaylistAndSetAdapter() {
        playlist = arrayListOf()
        val postId = intent.getStringExtra("postId")
        val databaseRef = postId?.let { database.reference.child("course").child(it).child("playlist") }
        if (databaseRef != null) {
            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (list in snapshot.children) {
                        val listData = list.getValue(PlaylistModel::class.java)
                        listData?.let {
                            playlist.add(it)
                        }
                    }
                    setAdapter(playlist)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@PlaylistActivity, "Failed to retrieve playlist: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun setAdapter(playlist: ArrayList<PlaylistModel>) {
        playlistAdapter = PlaylistAdapter(playlist) { position, videoUrl, size ->
            playPlaylistVideo(videoUrl)
        }
        binding.rvPlaylist.layoutManager = LinearLayoutManager(this)
        binding.rvPlaylist.adapter = playlistAdapter
        playlistAdapter.notifyDataSetChanged()
    }

    private fun playIntroVideo(videoUrl: String) {
        try {
            // Create an ExoPlayer instance using ExoPlayer.Builder
            simpleExoPlayer = SimpleExoPlayer.Builder(this@PlaylistActivity).build()

            // Bind the player to the view.
            binding.videoPlayer.player = simpleExoPlayer

            // Build the media item.
            val mediaItem = MediaItem.fromUri(videoUrl)

            // Set the media item to be played.
            simpleExoPlayer.setMediaItem(mediaItem)

            // Prepare the player (if you want to load the complete video first).
            simpleExoPlayer.prepare()


            // Start the playback.
            simpleExoPlayer.playWhenReady = true


            simpleExoPlayer.next()

            binding.videoPlayer.setShowBuffering(StyledPlayerView.SHOW_BUFFERING_ALWAYS)

            // Synchronize the fullscreen button visibility with the ExoPlayer controls
            binding.videoPlayer.setControllerVisibilityListener { visibility ->
                binding.btnFullscreen.visibility = visibility
            }

        } catch (e: Exception) {
            showToast(e.localizedMessage)
        }
    }

    private fun playPlaylistVideo(videoUrl: String) {
        try {
            if (!::simpleExoPlayer.isInitialized) {
                // Create an ExoPlayer instance using ExoPlayer.Builder
                simpleExoPlayer = SimpleExoPlayer.Builder(this@PlaylistActivity).build()
                binding.videoPlayer.player = simpleExoPlayer
            }

            // Build the media item.
            val mediaItem = MediaItem.fromUri(videoUrl)

            // Set the media item to be played.
            simpleExoPlayer.setMediaItem(mediaItem)

            // Prepare the player (if you want to load the complete video first).
            simpleExoPlayer.prepare()

            // Play video
            simpleExoPlayer.playWhenReady = true

            binding.videoPlayer.setShowBuffering(StyledPlayerView.SHOW_BUFFERING_ALWAYS)

            // Synchronize the fullscreen button visibility with the ExoPlayer controls
            binding.videoPlayer.setControllerVisibilityListener { visibility ->
                binding.btnFullscreen.visibility = visibility
            }


        } catch (e: Exception) {
            showToast(e.localizedMessage)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Handle layout changes based on the new orientation
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Handle landscape orientation changes
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Handle portrait orientation changes
        }
    }

    override fun onBackPressed() {
        // Check if the current orientation is landscape
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Switch to portrait mode
            toggleFullscreen()
        } else {
            // If already in portrait mode, proceed with normal back press
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::simpleExoPlayer.isInitialized) {
            simpleExoPlayer.release()
        }
        // Clean up the handler callbacks on destroy
        handler.removeCallbacks(hideFullscreenButtonRunnable)
    }

    override fun onPause() {
        super.onPause()
        if (::simpleExoPlayer.isInitialized) {
            simpleExoPlayer.playWhenReady = false
        }
        // Remove callbacks when the activity is paused to avoid memory leaks
        handler.removeCallbacks(hideFullscreenButtonRunnable)
    }

    private fun showToast(msg: String) {
        Toast.makeText(this@PlaylistActivity, msg, Toast.LENGTH_SHORT).show()
    }
}
