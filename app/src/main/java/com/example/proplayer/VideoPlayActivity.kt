@file:Suppress("DEPRECATION")

package com.example.proplayer

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.SimpleExoPlayer
import androidx.media3.exoplayer.source.ConcatenatingMediaSource
import com.example.proplayer.data.MediaFiles
import com.example.proplayer.databinding.ActivityVideoPlayBinding
import java.io.File

@Suppress("UNUSED_ANONYMOUS_PARAMETER", "ComplexRedundantLet")
class VideoPlayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoPlayBinding

    @SuppressLint("UnsafeOptInUsageError")
    private lateinit var exoPlayer: SimpleExoPlayer
    private var pos: Int = 0
    private var isLocked = false
      private lateinit var videoTitle:String
    @SuppressLint("UnsafeOptInUsageError")
    private lateinit var concatenatingMediaSource: ConcatenatingMediaSource
    private var listOfVideos = ArrayList<MediaFiles>()
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVideoPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // status bar remove
        statusBarRemove()
        // keyBroadRemove Button
        onWindowFocusChanged(true)
        // back Button function
        backButton()

        // videoLookAndUnLook
      //  videoLockAndUnLook()


        fun onWindowFocusChanged(hasFocus: Boolean) {
            super.onWindowFocusChanged(hasFocus)
            if (hasFocus) {
                val decorView = window.decorView
                decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }
        }

        listOfVideos = ArrayList()

        videoTitle = intent.getStringExtra("name")!!
        pos = intent.getIntExtra("pos", 1)
        listOfVideos = intent.extras?.getParcelableArrayList("videoList")!!

        val name = findViewById<TextView>(R.id.video_name)
        name.text = listOfVideos[pos].titleName


        //   initializePlayer()
        videoPlay()


        findViewById<ImageButton>(R.id.btn_play_next).setOnClickListener {
              playNextVideo()
            videoTitleUpdate(pos)
        }
        findViewById<ImageButton>(R.id.btn_play_prev).setOnClickListener {
            prevPlayVideo()
                videoTitleUpdate(pos)
        }

    }
    @SuppressLint("UnsafeOptInUsageError")
    private fun prevPlayVideo() {
       if (pos > 0){
           exoPlayer.stop()
           pos--
           videoPlay()
       } else {
           Toast.makeText(this, "No Video More 😏😏😏", Toast.LENGTH_SHORT).show()
       }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun playNextVideo() {
       if (pos < listOfVideos.size -1){
           exoPlayer.stop()
           pos++
           videoPlay()
       } else{
           Toast.makeText(this, "No Video More 😏😏😏", Toast.LENGTH_SHORT).show()
       }
    }

    @SuppressLint("UnsafeOptInUsageError")
    @OptIn(UnstableApi::class)
    private fun videoPlay() {
        val path = listOfVideos[pos].path
        val file = File(path)
        val uri = Uri.parse(file.toString())
        exoPlayer = SimpleExoPlayer.Builder(this).build()
        binding.exoPlayer.player = exoPlayer
        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    @Deprecated("Deprecated in Java")
    @OptIn(UnstableApi::class) override fun onBackPressed() {
        super.onBackPressed()
        if (exoPlayer.isPlaying){
            exoPlayer.stop()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onResume() {
        super.onResume()
       exoPlayer.playWhenReady = true
        exoPlayer.playbackState
    }
    @SuppressLint("UnsafeOptInUsageError")
    override fun onPause() {
        super.onPause()
        exoPlayer.playWhenReady = false
        exoPlayer.playbackState
    }

 @OptIn(UnstableApi::class) override fun onDestroy() {
        super.onDestroy()
         exoPlayer.stop()
     }
    @SuppressLint("ObsoleteSdkInt")
    private fun statusBarRemove(){
        // Hide status bar for API level 11 (Honeycomb MR1) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            val decorView = window.decorView
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }
    private fun videoTitleUpdate(position:Int){
      val name = findViewById<TextView>(R.id.video_name)
        name.text = listOfVideos[position].titleName
    }
    private fun backButton(){
        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }
    }
}
