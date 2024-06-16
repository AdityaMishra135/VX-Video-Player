package com.example.proplayer

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proplayer.adapter.VideoAdapter
import com.example.proplayer.data.MediaFiles
import com.example.proplayer.databinding.ActivityVideoBinding

class VideoActivity : AppCompatActivity() {
    private lateinit var binding:ActivityVideoBinding
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var videoList:ArrayList<MediaFiles>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        videoList = ArrayList()
        setLayoutCodes()
        // set custom toolbar
        setSupportActionBar(binding.materialToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val folderName = intent.getStringExtra("folderName")
        binding.materialToolbar.setTitle(folderName)

        videoList = fetchAllVideos(folderName!!)

    }

    @SuppressLint("Range", "Recycle")
    private fun fetchAllVideos(name:String): ArrayList<MediaFiles> {
        val contentResolver:ContentResolver = contentResolver
        val getVideoList = ArrayList<MediaFiles>()

        val selection = "${MediaStore.Video.Media.DATA} like? "
        val selectionArgs = arrayOf("%$name%")

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.RESOLUTION
        )

        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val cursor = contentResolver.query(
               uri,
            projection,
            selection,
            selectionArgs,
            sortOrder
            )

        if (cursor != null && cursor.moveToNext()) {
            do {
                val id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
                val displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME))
                val size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
                val duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                val dateAdded = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED))
                val quality = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.RESOLUTION))
                val mediaFiles = MediaFiles(id,title,displayName,size,path,duration,dateAdded,quality)
                videoList.add(mediaFiles)
            } while (cursor.moveToNext())
        }

        return getVideoList
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun setLayoutCodes(){
        binding.videoRecyclerView.layoutManager = LinearLayoutManager(this)
        videoAdapter = VideoAdapter(this,videoList);
        binding.videoRecyclerView.adapter = videoAdapter
         videoAdapter.notifyDataSetChanged()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}