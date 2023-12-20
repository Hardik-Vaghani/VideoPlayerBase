package com.hardik.videoplayerbase

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.hardik.videoplayerbase.databinding.ActivityFoldersBinding
import java.io.File

class FoldersActivity : AppCompatActivity() {

    companion object{
        lateinit var currentFolderVideo: ArrayList<Video>
    }
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPinkNav)
//        setContentView(R.layout.activity_folders)
        val binding = ActivityFoldersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val position = intent.getIntExtra("position",0)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = MainActivity.folderList[position].folderName
        currentFolderVideo = getAllVideos(MainActivity.folderList[position].id)//get find folder id and pass to function it's give list of videos

        binding.videoRVFA.setHasFixedSize(true)
        binding.videoRVFA.setItemViewCacheSize(10)
        binding.videoRVFA.layoutManager = LinearLayoutManager(this@FoldersActivity)
        try {
            binding.videoRVFA.adapter = VideoAdapter(this@FoldersActivity, currentFolderVideo, isFolder = true)
            binding.totalVideosFA.text = "Total Videos: ${currentFolderVideo.size}"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    @SuppressLint("InLinedApi", "Recycle", "Range")
    private fun getAllVideos(folderId:String): ArrayList<Video> {
        val tempList = ArrayList<Video>()
        val selection = MediaStore.Video.Media.BUCKET_ID +" LIKE? "//SQL query passing in side cursor query
        //which type data do you want mention here
        val projection = arrayOf(
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,//folder name
            MediaStore.Video.Media.BUCKET_ID,//folder id
            MediaStore.Video.Media.DATA,//uri
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DURATION
        )
        //request to cursor i want to data when "MediaStore.Video.Media.EXTERNAL_CONTENT_URI" and which type of "projection" and set Order "DESC"
        val cursor = this.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, selection, arrayOf(folderId),
            MediaStore.Video.Media.DATE_ADDED + " DESC"
        )

        //Now get data from the cursor
        if (cursor != null) {
            if (cursor.moveToNext()) {
                do {
                    val titleC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                    val folderC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
                    val sizeC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                    val durationC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                            .toLong()

                    try {
                        val file = File(pathC)
                        val artUriC = Uri.fromFile(file)//get artUric form the file
                        val video = Video(
                            id = idC,
                            title = titleC,
                            duration = durationC,
                            folderName = folderC,
                            size = sizeC,
                            path = pathC,
                            artUri = artUriC
                        )
                        if (file.exists()) tempList.add(video)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } while (cursor.moveToNext())
                cursor.close()//when it's not null and complete it's work done
            }
        }
        return tempList
    }
}