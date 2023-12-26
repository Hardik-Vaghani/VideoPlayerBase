package com.hardik.videoplayerbase

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hardik.videoplayerbase.databinding.ActivityMainBinding
import com.hardik.videoplayerbase.databinding.ThemeViewBinding
import java.io.File
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    companion object {
        lateinit var videoList: ArrayList<Video>
        lateinit var folderList: ArrayList<Folder>
        lateinit var searchList: ArrayList<Video>
        var search:Boolean = false
        var themeIndex: Int = 0
        val themesList = arrayOf(
            R.style.coolPinkNav, R.style.coolBlueNav, R.style.coolPurpleNav, R.style.coolGreenNav,
            R.style.coolRedNav, R.style.coolBlackNav
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val editor = getSharedPreferences("Themes", MODE_PRIVATE)
        themeIndex = editor.getInt("themeIndex", 0)
        setTheme(themesList[themeIndex])

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //for NavDrawer
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (requestRuntimePermission()) {
            folderList = ArrayList()//now initialize
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
//                R.id.feedbackNav -> Toast.makeText(this, "Feedback", Toast.LENGTH_SHORT).show()
                R.id.themesNav -> {
                    val customDialog = LayoutInflater.from(this).inflate(R.layout.theme_view, binding.root, false)
                    val bindingTV = ThemeViewBinding.bind(customDialog)
                    val dialog = MaterialAlertDialogBuilder(this).setView(customDialog)
                        .setTitle("Select Theme")
                        .create()
                    dialog.show()
                    when (themeIndex) {
                        0 -> bindingTV.themePink.setBackgroundColor(Color.YELLOW)
                        1 -> bindingTV.themeBlue.setBackgroundColor(Color.YELLOW)
                        2 -> bindingTV.themePurple.setBackgroundColor(Color.YELLOW)
                        3 -> bindingTV.themeGreen.setBackgroundColor(Color.YELLOW)
                        4 -> bindingTV.themeRed.setBackgroundColor(Color.YELLOW)
                        5 -> bindingTV.themeBlack.setBackgroundColor(Color.YELLOW)
                    }
                    bindingTV.themePink.setOnClickListener { saveTheme(0) }
                    bindingTV.themeBlue.setOnClickListener { saveTheme(1) }
                    bindingTV.themePurple.setOnClickListener { saveTheme(2) }
                    bindingTV.themeGreen.setOnClickListener { saveTheme(3) }
                    bindingTV.themeRed.setOnClickListener { saveTheme(4) }
                    bindingTV.themeBlack.setOnClickListener { saveTheme(5) }
                }
                R.id.sortOrderNav -> Toast.makeText(this, "Sort Order", Toast.LENGTH_SHORT).show()
                R.id.aboutNav -> startActivity(Intent(this,AboutActivity::class.java))
                R.id.exitNav -> exitProcess(1)//close app, parameter value doesn't matter

            }
            return@setNavigationItemSelectedListener true
        }
    }

    private fun saveTheme(index: Int){
        val editor = getSharedPreferences("Themes", MODE_PRIVATE).edit()
        editor.putInt("themeIndex", index)
        editor.apply()

        //for restarting app
        finish()
        startActivity(intent)
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
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show()
                //for first time app install
                folderList = ArrayList()//now initialize
                videoList = getAllVideos()
                setFragment(VideosFragment())
            }
            else
                ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), 13)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val gradientList = arrayOf(R.drawable.pink_gradient, R.drawable.blue_gradient, R.drawable.purple_gradient, R.drawable.green_gradient
            , R.drawable.red_gradient, R.drawable.black_gradient)

        findViewById<LinearLayoutCompat>(R.id.gradientLayout).setBackgroundResource(gradientList[themeIndex])

        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("InLinedApi", "Recycle", "Range")
    private fun getAllVideos(): ArrayList<Video> {
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
                            folderList.add(Folder(id=folderIdC, folderName = folderC))
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
}