package com.hardik.videoplayerbase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.hardik.videoplayerbase.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding

    companion object{
        private lateinit var player : SimpleExoPlayer
        lateinit var playerList: ArrayList<Video>
        var position: Int = -1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_player)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setTheme(R.style.coolPinkNav)
        setContentView(binding.root)
        initializeLayout()
        initializeBinding()
    }

    private fun initializeLayout(){
        when(intent.getStringExtra("class")){
            "AllVideos" ->{
                playerList = ArrayList()
                playerList.addAll(MainActivity.videoList)
                createPlayer()
            }
            "FolderActivity" ->{
                playerList = ArrayList()
                playerList.addAll(FoldersActivity.currentFolderVideo)
                createPlayer()
            }
        }
    }

    private fun initializeBinding(){
        binding.backBtn.setOnClickListener{
            finish()//when click this button activity close
        }
        binding.playPauseBtn.setOnClickListener{
            //if player is playing so pause either play
            if (player.isPlaying) pauseVideo()
            else playVideo()
        }
        binding.nextBtn.setOnClickListener{ nextPrevVideo() }
        binding.prevBtn.setOnClickListener{ nextPrevVideo(isNext = false) }
    }
    private fun createPlayer(){
        binding.videoTitle.text = playerList[position].title
        binding.videoTitle.isSelected = true
        player = SimpleExoPlayer.Builder(this).build()
        binding.playerView.player = player
        val mediaItem = MediaItem.fromUri(playerList[position].artUri)//directly play
        player.setMediaItem(mediaItem)
        player.prepare()
//        player.play()
        playVideo()
    }

    private fun playVideo(){
        binding.playPauseBtn.setImageResource(R.drawable.pause_icon)
        player.play()
    }

    private fun pauseVideo(){
        binding.playPauseBtn.setImageResource(R.drawable.play_icon)
        player.pause()
    }

    private fun nextPrevVideo(isNext:Boolean = true){
        if (isNext) setPosition()
        else setPosition(isIncrement = false)
        createPlayer()
    }
    private fun setPosition(isIncrement:Boolean = true){
        if (isIncrement){
            if (playerList.size -1 == position) position = 0 //if list size is last item so set 0 index
            else ++position
        }else{
            if (position == 0)
                position = playerList.size -1 //set last position of list
            else --position
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}