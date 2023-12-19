package com.hardik.videoplayerbase

import android.content.Context
import android.text.format.DateUtils
import android.util.TimeUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hardik.videoplayerbase.databinding.VideoViewBinding

class VideoAdapter(private val context: Context, private var videoList: ArrayList<Video>) :
    RecyclerView.Adapter<VideoAdapter.MyHolder>() {
    class MyHolder(binding: VideoViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.videoName
        val folder = binding.folderName
        val duration = binding.duration
        val image = binding.videoImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(VideoViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.title.text = videoList[position].title
        holder.folder.text = videoList[position].folderName
        holder.duration.text = DateUtils.formatElapsedTime(videoList[position].duration/1000)
        Glide.with(context)
            .asBitmap()
            .load(videoList[position].artUri)
            .centerCrop()
            .placeholder(R.mipmap.ic_video_player)
            .error(R.mipmap.ic_video_player)
            .into(holder.image)

    }

    override fun getItemCount(): Int {
        return videoList.size
    }
}