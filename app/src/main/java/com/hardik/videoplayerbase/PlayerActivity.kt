package com.hardik.videoplayerbase

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.opengl.Visibility
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hardik.videoplayerbase.databinding.ActivityPlayerBinding
import com.hardik.videoplayerbase.databinding.MoreFeaturesBinding
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var runnable: Runnable
    private var isSubtitle: Boolean = true

    companion object{
        private lateinit var player : SimpleExoPlayer
        lateinit var playerList: ArrayList<Video>
        var position: Int = -1
        var repeat:Boolean = false
        private var isFullscreen: Boolean = false
        private var isLocked: Boolean = false
        @SuppressLint("StaticFieldLeak")
        lateinit var trackSelector: DefaultTrackSelector
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //hide top title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //for knock display
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setTheme(R.style.playerActivityTheme)
        setContentView(binding.root)
        // for immersive mode (fullscreen mode) this for bottom button of android
        WindowCompat.setDecorFitsSystemWindows(window,false)
        WindowInsetsControllerCompat(window,binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
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
        if (repeat) binding.repeatBtn.setImageResource(R.drawable.repeat_icon_one)
        else binding.repeatBtn.setImageResource(R.drawable.repeat_icon_all)
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
        binding.repeatBtn.setOnClickListener {
            if (repeat){
                repeat = false
                player.repeatMode = Player.REPEAT_MODE_OFF
                binding.repeatBtn.setImageResource(R.drawable.repeat_icon_off)
            }else{
                repeat = true
                player.repeatMode = Player.REPEAT_MODE_ONE
                binding.repeatBtn.setImageResource(R.drawable.repeat_icon_one)
            }
        }
        binding.fullScreenBtn.setOnClickListener {
            if (isFullscreen){
                isFullscreen = false
                playInFullscreen(enable = false)
            }else{
                isFullscreen = true
                playInFullscreen(enable = true)
            }
        }

        binding.lockButton.setOnClickListener {
            if(!isLocked){
                //for hide control
                isLocked = true
                binding.playerView.hideController()
                binding.playerView.useController = false
                binding.lockButton.setImageResource(R.drawable.lock_close_icon)
            }else{
                //for show control
                isLocked = false
                binding.playerView.useController = true
                binding.playerView.showController()
                binding.lockButton.setImageResource(R.drawable.lock_open_icon)

            }
        }

        binding.moreFeatures.setOnClickListener {
            pauseVideo()//first pause video
            val customDialog = LayoutInflater.from(this).inflate(R.layout.more_features,binding.root,false)
            val bindingMF = MoreFeaturesBinding.bind(customDialog)
            val dialog = MaterialAlertDialogBuilder(this).setView(customDialog)
                .setOnCancelListener { playVideo() }
                .setBackground(ColorDrawable(0x803700B3.toInt()))
                .create()
            dialog.show()

            //for audio Track
            bindingMF.audioTrack.setOnClickListener{
                dialog.dismiss()
                playVideo()

                val audioTrack = ArrayList<String>()
                 for (i in 0 until player.currentTrackGroups.length){
                      if (player.currentTrackGroups.get(i).getFormat(0).selectionFlags == C.SELECTION_FLAG_DEFAULT){//if that track is selectable so select it.
                          audioTrack.add(Locale(player.currentTrackGroups.get(i).getFormat(0).language.toString()).displayLanguage)//Locale is short form to long form convert

                      }
                 }
                val tempTracks = audioTrack.toArray(arrayOfNulls<CharSequence>(audioTrack.size))//convert arrayList to CharSequence
                MaterialAlertDialogBuilder(this,R.style.alertDialog)
                    .setTitle("Select Language")
                    .setOnCancelListener { playVideo() }
                    .setBackground(ColorDrawable(0x803700B3.toInt()))
                    .setItems(tempTracks){_, position ->
                        Toast.makeText(this,audioTrack[position]+ " selected", Toast.LENGTH_SHORT).show()
                        trackSelector.setParameters(trackSelector.buildUponParameters().setPreferredAudioLanguage(audioTrack[position]))// change audio track on video
                    }
                    .create()
                    .show()
            }

            bindingMF.subtitlesBtn.setOnClickListener{
                if (isSubtitle){
                    //is on
                    trackSelector.parameters = DefaultTrackSelector.ParametersBuilder(this).setRendererDisabled(
                        C.TRACK_TYPE_VIDEO,true
                    ).build()
                    Toast.makeText(this,"Subtitles Off", Toast.LENGTH_SHORT).show()
                    isSubtitle = false
                }
                else{
                    //is off
                    trackSelector.parameters = DefaultTrackSelector.ParametersBuilder(this).setRendererDisabled(
                        C.TRACK_TYPE_VIDEO,false
                    ).build()
                    Toast.makeText(this,"Subtitles On", Toast.LENGTH_SHORT).show()
                    isSubtitle = true
                }
                dialog.dismiss()
                playVideo()
            }
        }
    }

    private fun createPlayer(){
        try {
            player.release()//for release all old resource in side stored
        }catch (e:Exception){
            e.printStackTrace()
        }
        trackSelector = DefaultTrackSelector(this)
        binding.videoTitle.text = playerList[position].title
        binding.videoTitle.isSelected = true
        player = SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build()
        binding.playerView.player = player
        val mediaItem = MediaItem.fromUri(playerList[position].artUri)//directly play
        player.setMediaItem(mediaItem)
        player.prepare()
        playVideo()

        //add completion listener
        player.addListener(object : Player.Listener{
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_ENDED){//when video finish
                    nextPrevVideo()
                }
            }
        })

        playInFullscreen(enable = isFullscreen)//when value is contain isFullscreen val,that is set.

        setVisibility()
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
        if (!repeat){
            if (isIncrement){
                if (playerList.size -1 == position) position = 0 //if list size is last item so set 0 index
                else ++position
            }else{
                if (position == 0)
                    position = playerList.size -1 //set last position of list
                else --position
            }
        }
    }

    private fun playInFullscreen(enable : Boolean){
        if(enable){
            binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            binding.fullScreenBtn.setImageResource(R.drawable.fullscreen_exit_icon)
        }else{

            binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
            binding.fullScreenBtn.setImageResource(R.drawable.fullscreen_icon)
        }
    }

    private fun setVisibility(){
        runnable = Runnable {
            if (binding.playerView.isControllerVisible) changeVisibility(View.VISIBLE)
            else changeVisibility(View.INVISIBLE)
            Handler(Looper.getMainLooper()).postDelayed(runnable,200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)
    }

    private fun changeVisibility(visibility: Int){
        binding.topController.visibility = visibility
        binding.bottomController.visibility = visibility
        binding.playPauseBtn.visibility = visibility

        //when lock is close that time it's not hide but always show
        if (isLocked) binding.lockButton.visibility = View.VISIBLE
        // when lock is open that time it's work
        else binding.lockButton.visibility = visibility
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}