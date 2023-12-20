package com.hardik.videoplayerbase

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hardik.videoplayerbase.databinding.FolderViewBinding

class FoldersAdapter (private val context: Context, private var foldersList: ArrayList<Folder>) :
    RecyclerView.Adapter<FoldersAdapter.MyHolder>() {
    class MyHolder(binding: FolderViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val folderName = binding.folderNameFV
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(FolderViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.folderName.text = foldersList[position].folderName
    }

    override fun getItemCount(): Int {
        return foldersList.size
    }
}