package com.hardik.videoplayerbase

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.hardik.videoplayerbase.databinding.ActivityFoldersBinding

class FoldersActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPinkNav)
//        setContentView(R.layout.activity_folders)
        val binding = ActivityFoldersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tampList = ArrayList<Video>()
        tampList.add(MainActivity.videoList[0])

        val position = intent.getIntExtra("position",0)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = MainActivity.folderList[position].folderName

        binding.videoRVFA.setHasFixedSize(true)
        binding.videoRVFA.setItemViewCacheSize(10)
        binding.videoRVFA.layoutManager = LinearLayoutManager(this@FoldersActivity)
        try {
            binding.totalVideosFA.text = "${binding.totalVideosFA.text} ${MainActivity.folderList.size}"
            binding.videoRVFA.adapter = VideoAdapter(this@FoldersActivity, tampList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}