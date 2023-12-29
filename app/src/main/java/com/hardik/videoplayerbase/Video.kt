package com.hardik.videoplayerbase

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import java.io.File

data class Video(
    val id: String,
    var title: String,
    val duration: Long = 0,
    val folderName: String,
    val size: String,
    var path: String,
    var artUri: Uri
)

data class Folder(val id: String, val folderName: String)


@SuppressLint("InLinedApi", "Recycle", "Range")
fun getAllVideos(context: Context): ArrayList<Video> {
    val sortEditor = context.getSharedPreferences("Sorting", AppCompatActivity.MODE_PRIVATE)
    MainActivity.sortValue = sortEditor.getInt("sortValue", 0)

    var tempList = ArrayList<Video>()
    var tempFolderList = ArrayList<String>()
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
    val cursor = context.contentResolver.query(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null,
        MainActivity.sortList[MainActivity.sortValue]
    )

    //Now get data from the cursor
    if (cursor != null) {
        if (cursor.moveToNext()) {
            do {
                val titleC =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
                val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                val folderC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
                val folderIdC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID))
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

                    //for adding folder
                    if (!tempFolderList.contains(folderC))
                    {
                        tempFolderList.add(folderC)
                        MainActivity.folderList.add(Folder(id=folderIdC, folderName = folderC))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } while (cursor.moveToNext())
            cursor.close()//when it's not null and complete it's work done
        }
    }
    return tempList
}