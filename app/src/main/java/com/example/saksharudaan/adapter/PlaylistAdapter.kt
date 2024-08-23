package com.example.saksharudaanadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.persistableBundleOf
import androidx.recyclerview.widget.RecyclerView
import com.example.saksharudaan.databinding.PlaylistItemBinding
import com.example.saksharudaan.model.PlaylistModel
import com.example.saksharudaan.databinding.HomeRvItemBinding
import java.util.stream.Stream

class PlaylistAdapter(private var playlist: ArrayList<PlaylistModel>, private val listener: videoListener):RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    inner class PlaylistViewHolder(private val binding: PlaylistItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val playlistItem = playlist[position]
            binding.tvVideoTitle.text = playlistItem.playlistVideoTitle
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistAdapter.PlaylistViewHolder {
        val binding = PlaylistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistAdapter.PlaylistViewHolder, position: Int) {
        holder.bind(position)
        val playlistItem = playlist[position]
        holder.itemView.setOnClickListener {
            playlistItem.playlistVideoUrl?.let {
                url->
                listener.onClick(position, url, playlist.size)
            }
        }
    }

    override fun getItemCount(): Int = playlist.size

    fun interface videoListener{
        fun onClick(position: Int, videoUrl: String, size: Int)
    }
}