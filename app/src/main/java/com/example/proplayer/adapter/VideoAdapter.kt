package com.example.proplayer.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.persistableBundleOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.proplayer.VideoPlayActivity
import com.example.proplayer.data.MediaFiles
import com.example.proplayer.databinding.VideoItemBinding
import java.io.File
import java.time.Duration

class VideoAdapter(private val context: Context, private val videoList: ArrayList<MediaFiles>) :
    RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: VideoItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(VideoItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.videoTitle.text = videoList[position].disPlaysName
        holder.binding.videoTime.text =
            DateUtils.formatElapsedTime(videoList[position].duration / 1000)
        holder.binding.resolutionOfVideo.text = videoList[position].quality
        Glide.with(context)
            .load(videoList[position].path)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.binding.thumbImage)

        // video clickListener
        holder.itemView.setOnClickListener {
            val intent = Intent(context,VideoPlayActivity::class.java)
            intent.putExtra("name",videoList[position].disPlaysName)
            intent.putExtra("pos",position)
            val bundle = Bundle()
            bundle.putParcelableArrayList("videoList",videoList)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }
}




