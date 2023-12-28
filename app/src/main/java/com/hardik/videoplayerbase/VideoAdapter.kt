package com.hardik.videoplayerbase

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.text.SpannableStringBuilder
import android.text.format.DateUtils
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hardik.videoplayerbase.databinding.DetailsViewBinding
import com.hardik.videoplayerbase.databinding.RenameFieldBinding
import com.hardik.videoplayerbase.databinding.VideoMoreFeaturesBinding
import com.hardik.videoplayerbase.databinding.VideoViewBinding
import java.io.File

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

    @SuppressLint("NotifyDataSetChanged")
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
                val customDialogRF = LayoutInflater.from(context).inflate(R.layout.rename_field, holder.root, false)
                val bindingRF = RenameFieldBinding.bind(customDialogRF)
                val dialogRF = MaterialAlertDialogBuilder(context).setView(customDialogRF)
                    .setCancelable(false)
                    .setPositiveButton("Rename"){self,_ ->
                        val currentFile = File(videoList[position].path)
                        val newName = bindingRF.renameField.text
                        if(newName != null && currentFile.exists() && newName.toString().isNotEmpty()){
                            val newFile = File(currentFile.parentFile,newName.toString()+"."+currentFile.extension)
                            if (currentFile.renameTo(newFile )){
                                MediaScannerConnection.scanFile(context, arrayOf(newFile.toString()), arrayOf("video/*"),null)
                                when{
                                    MainActivity.search -> {
                                        MainActivity.searchList[position].title = newName.toString()
                                        MainActivity.searchList[position].path = newFile.path
                                        MainActivity.searchList[position].artUri = Uri.fromFile(newFile)
                                        notifyItemChanged(position)
                                    }
                                    isFolder -> {
                                        FoldersActivity.currentFolderVideo[position].title = newName.toString()
                                        FoldersActivity.currentFolderVideo[position].path = newFile.path
                                        FoldersActivity.currentFolderVideo[position].artUri = Uri.fromFile(newFile)
                                        notifyItemChanged(position)
                                        MainActivity.dataChanged = true
                                    }
                                    else -> {
                                        MainActivity.videoList[position].title = newName.toString()
                                        MainActivity.videoList[position].path = newFile.path
                                        MainActivity.videoList[position].artUri = Uri.fromFile(newFile)
                                        notifyItemChanged(position)
                                    }
                                }
                            }
                                else {
                                    Toast.makeText(context,"Permission Denied!",Toast.LENGTH_LONG).show()
                            }
                        }
                        self.dismiss()
                    }
                    .setNegativeButton("Cancel"){self,_ ->
                        self.dismiss()
                    }
                    .create()
                dialogRF.show()
                bindingRF.renameField.text = SpannableStringBuilder(videoList[position].title)//string convert in editable string using SpannableStringBuilder
                dialogRF.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(MaterialColors.getColor(context,R.attr.themeColor,Color.RED))//set themes color/and default color red
                dialogRF.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(MaterialColors.getColor(context,R.attr.themeColor,Color.RED))//set themes color/and default color red
//                requestWriteR()
            }

            //video sharing
            bindingVMF.shareBtn.setOnClickListener{
                dialog.dismiss()
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "video/*"
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(videoList[position].path))
                ContextCompat.startActivity(context, Intent.createChooser(shareIntent, "Sharing Video File!!"), null)
            }

            //Information of video
            bindingVMF.infoBtn.setOnClickListener {
                dialog.dismiss()
                val customDialogIF = LayoutInflater.from(context).inflate(R.layout.details_view, holder.root, false)
                val bindingIF = DetailsViewBinding.bind(customDialogIF)
                val dialogIF = MaterialAlertDialogBuilder(context).setView(customDialogIF)
                    .setCancelable(false)
                    .setPositiveButton("Ok"){self, _ ->
                        self.dismiss()
                    }
                    .create()
                dialogIF.show()
                val infoText = SpannableStringBuilder().bold { append("DETAILS\n\nName: ") }.append(videoList[position].title)
                    .bold { append("\n\nDuration: ") }.append(DateUtils.formatElapsedTime(videoList[position].duration/1000))//pass seconds and itself set time duration
                    .bold { append("\n\nFile Size: ") }.append(Formatter.formatShortFileSize(context, videoList[position].size.toLong()))//pass size and convert in to mb/gb size
                    .bold { append("\n\nLocation: ") }.append(videoList[position].path)

                bindingIF.detailTV.text = infoText
                dialogIF.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(
                    MaterialColors.getColor(context, R.attr.themeColor, Color.RED)
                )
            }

            bindingVMF.deleteBtn.setOnClickListener {
                requestPermissionR()
                dialog.dismiss()

                val builder = MaterialAlertDialogBuilder(context)
                builder.setTitle("Delete Video?")
                    .setMessage(videoList[position].title)
                    .setPositiveButton("Yes"){ self, _ ->
                        val file = File(videoList[position].path)
                        if(file.exists() && file.delete()){
                            MediaScannerConnection.scanFile(context, arrayOf(file.path), arrayOf("video/*"), null)
                            MainActivity.videoList.removeAt(position)
                            notifyDataSetChanged()
                            when{
                                MainActivity.search -> {
                                    MainActivity.dataChanged = true
                                    videoList.removeAt(position)
                                    notifyDataSetChanged()
                                }
                                isFolder -> {
                                    MainActivity.dataChanged = true
                                    FoldersActivity.currentFolderVideo.removeAt(position)
                                    notifyDataSetChanged()
                                }
                                else -> {
                                    MainActivity.videoList.removeAt(position)
                                    notifyDataSetChanged()
                                }
                            }
                        }else{
                            Toast.makeText(context,"Permission Denied!",Toast.LENGTH_LONG).show()
                        }
                        self.dismiss()
                    }
                    .setNegativeButton("No"){self, _ -> self.dismiss() }
                val delDialog = builder.create()
                delDialog.show()
                delDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(
                    MaterialColors.getColor(context, R.attr.themeColor, Color.RED)
                )
                delDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(
                    MaterialColors.getColor(context, R.attr.themeColor, Color.RED)
                )
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

    //for requesting android 11 or higher storage permission
    private fun requestPermissionR(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if(!Environment.isExternalStorageManager()){
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data = Uri.parse("package:${context.applicationContext.packageName}")
                    ContextCompat.startActivity(context, intent, null)
                }
            }
    }
}