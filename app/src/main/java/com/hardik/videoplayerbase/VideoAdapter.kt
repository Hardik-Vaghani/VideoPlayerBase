package com.hardik.videoplayerbase

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hardik.videoplayerbase.databinding.VideoMoreFeaturesBinding
import com.hardik.videoplayerbase.databinding.VideoViewBinding

class VideoAdapter(private val context: Context, private var videoList: ArrayList<Video>, private val isFolder:Boolean = false) :
    RecyclerView.Adapter<VideoAdapter.MyHolder>() {

    private var newPosition = 0
//    private lateinit var dialogRF: androidx.appcompat.app.AlertDialog

    class MyHolder(binding: VideoViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.videoName
        val folder = binding.folderName
        val duration = binding.duration
        val image = binding.videoImage
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(VideoViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: MyHolder, @SuppressLint("RecyclerView") position: Int) {
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
        holder.root.setOnClickListener{
            when{
                videoList[position].id == PlayerActivity.nowPlayingId ->{
                    sendIntent(pos = position, ref = "NowPlaying")
                }
                isFolder->{
                    PlayerActivity.pipStatus = 1
                    sendIntent(pos = position, ref = "FolderActivity")
                }
                MainActivity.search -> {
                    PlayerActivity.pipStatus = 2
                    sendIntent(pos = position, ref = "SearchedVideos")
                }
                else -> {
                    PlayerActivity.pipStatus = 3
                    sendIntent(pos = position, ref = "AllVideos")
                }
            }
        }
        holder.root.setOnLongClickListener {
            newPosition = position

            val customDialog = LayoutInflater.from(context).inflate(R.layout.video_more_features, holder.root, false)
            val bindingVMF = VideoMoreFeaturesBinding.bind(customDialog)
            val dialog = MaterialAlertDialogBuilder(context).setView(customDialog)
//                .setBackground(ColorDrawable(0x22334455.toInt()))
                .create()
            dialog.show()

            bindingVMF.renameBtn.setOnClickListener {
                dialog.dismiss()
//                requestWriteR()
            }
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    private fun sendIntent(pos: Int, ref: String){
        PlayerActivity.position = pos//directly set on playerActivity
        val intent = Intent(context,PlayerActivity::class.java)
        intent.putExtra("class",ref)//intent passing
        ContextCompat.startActivity(context,intent,null)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(searchList: ArrayList<Video>){
        this.videoList = ArrayList()
        this.videoList.addAll(searchList)
        notifyDataSetChanged()

    }
}