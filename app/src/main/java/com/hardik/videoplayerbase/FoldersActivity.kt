package com.hardik.videoplayerbase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.hardik.videoplayerbase.databinding.ActivityFoldersBinding

class FoldersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPinkNav)
//        setContentView(R.layout.activity_folders)
        val binding = ActivityFoldersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val position = intent.getIntExtra("position",0)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = MainActivity.folderList[position].folderName
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}