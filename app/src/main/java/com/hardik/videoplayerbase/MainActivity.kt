package com.hardik.videoplayerbase

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Video
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.hardik.videoplayerbase.databinding.ActivityMainBinding
import java.io.File
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    companion object {
        lateinit var videoList: ArrayList<com.hardik.videoplayerbase.Video>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.coolPinkNav)
        setContentView(binding.root)

        //for NavDrawer
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (requestRuntimePermission()) {
            videoList = getAllVideos()
            setFragment(VideosFragment())
        }
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.video_view -> setFragment(VideosFragment())
                R.id.folders_view -> setFragment(FoldersFragment())
            }
            return@setOnItemSelectedListener true
        }
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.feedbackNav -> Toast.makeText(this, "Feedback", Toast.LENGTH_SHORT).show()
                R.id.themesNav -> Toast.makeText(this, "Theme", Toast.LENGTH_SHORT).show()
                R.id.sortOrderNav -> Toast.makeText(this, "Sort Order", Toast.LENGTH_SHORT).show()
                R.id.aboutNav -> Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()
                R.id.exitNav -> exitProcess(1)//close app, parameter value doesn't matter

            }
            return@setNavigationItemSelectedListener true
        }
    }

    private fun setFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentFL, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }

    //for requesting permission at the 9 sdk till not 10
    private fun requestRuntimePermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), 13)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show()
            else
                ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), 13)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("InLinedApi", "Recycle", "Range")
    private fun getAllVideos(): ArrayList<com.hardik.videoplayerbase.Video> {
        var tempList = ArrayList<com.hardik.videoplayerbase.Video>()
        //which type data do you want mention here
        val projection = arrayOf(
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DURATION
        )
        //request to cursor i want to data when "MediaStore.Video.Media.EXTERNAL_CONTENT_URI" and which type of "projection" and set Order "DESC"
        val cursor = this.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null,
            MediaStore.Video.Media.DATE_ADDED + " DESC"
        )

        //Now get data from the cursor
        if (cursor != null) {
            if (cursor.moveToNext()) {
                do {
                    val titleC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                    val folderC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
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