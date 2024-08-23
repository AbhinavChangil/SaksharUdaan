package com.example.saksharudaan

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.SimpleAdapter
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saksharudaan.databinding.ActivityPlaylistBinding
import com.example.saksharudaan.model.PlaylistModel
import com.example.saksharudaanadmin.adapter.PlaylistAdapter
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.collection.LLRBNode.Color
import com.norulab.exofullscreen.preparePlayer

class PlaylistActivity : AppCompatActivity() {
    private val binding: ActivityPlaylistBinding by lazy {
        ActivityPlaylistBinding.inflate(layoutInflater)
    }
    private lateinit var simpleExoPlayer: SimpleExoPlayer
    private lateinit var playlist: ArrayList<PlaylistModel>
    private lateinit var database: FirebaseDatabase
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(binding.root)

        //initialize database
        database = FirebaseDatabase.getInstance()

        handelSwitchButton()

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

    }

    private fun handelSwitchButton() {

        //by default playlist button and recycler view playlist is visible
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
                    for(list in snapshot.children){
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
        playlistAdapter = PlaylistAdapter(playlist){
            position,videoUrl,size->
            playPlaylistVideo(videoUrl)
        }
        binding.rvPlaylist.layoutManager = LinearLayoutManager(this)
        binding.rvPlaylist.adapter = playlistAdapter
        playlistAdapter.notifyDataSetChanged()
    }

    private fun playIntroVideo(videoUrl: String) {
        try {
            //create an ExoPlayer instance using ExoPlayer.Builder
            simpleExoPlayer = SimpleExoPlayer.Builder(this@PlaylistActivity).build()
            // Bind the player to the view.
            binding.videoPlayer.player = simpleExoPlayer
            // Build the media item.
            val mediaItem = MediaItem.fromUri(videoUrl)
            // Set the media item to be played.
            simpleExoPlayer.setMediaItem(mediaItem)

            // Prepare the player (id you want to load the complete video first.
//            simpleExoPlayer.prepare()
            simpleExoPlayer.preparePlayer(binding.videoPlayer, true)

            // Start the playback.
            simpleExoPlayer.playWhenReady = true
            binding.videoPlayer.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)


        }catch (e:Exception){
            showToast(e.localizedMessage)
        }
    }

    private fun playPlaylistVideo(videoUrl: String) {
        try {

            if(!::simpleExoPlayer.isInitialized){
                //create an ExoPlayer instance using ExoPlayer.Builder
                simpleExoPlayer = SimpleExoPlayer.Builder(this@PlaylistActivity).build()

                binding.videoPlayer.player = simpleExoPlayer
            }

            // Build the media item.
            val mediaItem = MediaItem.fromUri(videoUrl)
            // Set the media item to be played.
            simpleExoPlayer.setMediaItem(mediaItem)

            // Prepare the player (id you want to load the complete video first).
            simpleExoPlayer.preparePlayer(binding.videoPlayer, true)

            //play video
            simpleExoPlayer.playWhenReady = true


            binding.videoPlayer.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)



        }catch (e:Exception){
            showToast(e.localizedMessage)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::simpleExoPlayer.isInitialized) {
            simpleExoPlayer.release()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::simpleExoPlayer.isInitialized) {
            simpleExoPlayer.playWhenReady = false
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}


class FullScreenVideoView(context: Context, attrs: AttributeSet) : VideoView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width,height)
    }
}