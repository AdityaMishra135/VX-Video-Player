@file:Suppress("PrivatePropertyName", "unused")

package com.example.proplayer

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.Menu
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proplayer.adapter.FolderAdapter
import com.example.proplayer.data.MediaFiles
import com.example.proplayer.databinding.ActivityFolderBinding


@Suppress("UNUSED_PARAMETER",
    "KotlinRedundantDiagnosticSuppress", "DEPRECATION", "ControlFlowWithEmptyBody"
)
class FolderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFolderBinding
    private var totalVideos:Int?=null
    private lateinit var folderNames:String
    private var STORAGE_PERMISSION_REQUEST_CODE = 100
    private lateinit var folderAdapter: FolderAdapter
    private lateinit var folderList:ArrayList<String>
    private lateinit var mediaFilesArray:ArrayList<MediaFiles>
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // arrayList
        folderList = ArrayList()
        mediaFilesArray = ArrayList()

        // set Permission code
        checkStoragePermission(this, STORAGE_PERMISSION_REQUEST_CODE)
        // setToolbar code
        setSupportActionBar(binding.materialToolbar)

        mediaFilesArray = fetchAllVideoFolder()
        setLayoutCode()
    }

    @SuppressLint("Recycle", "Range")
    private fun fetchAllVideoFolder(): ArrayList<MediaFiles> {
        val contentResolver:ContentResolver = contentResolver
        val folderArrayList= ArrayList<MediaFiles>()
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val cursor = contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )
       if(cursor != null && cursor.moveToNext()){
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

               val index = path.lastIndexOf("/")
               val subString = path.substring(0,index)
               if(!folderList.contains(subString)){
                   folderList.add(subString)
               }
               folderArrayList.add(mediaFiles)
           }while (cursor.moveToNext())
       }
        return folderArrayList
    }

    private fun checkStoragePermission(activity: AppCompatActivity, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= TIRAMISU) {
            // Check if READ_MEDIA_IMAGES and READ_MEDIA_VIDEO permissions are already granted
            val hasReadImagesPermission = ContextCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
            val hasReadVideoPermission = ContextCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED

            val permissionsToRequest = mutableListOf<String>()
            if (!hasReadImagesPermission) {
                permissionsToRequest.add(android.Manifest.permission.READ_MEDIA_IMAGES)
            }
            if (!hasReadVideoPermission) {
                permissionsToRequest.add(android.Manifest.permission.READ_MEDIA_VIDEO)
            }
            if (permissionsToRequest.isNotEmpty()) {
                // Request permissions if not already granted
                ActivityCompat.requestPermissions(
                    activity,
                    permissionsToRequest.toTypedArray(),
                    requestCode
                )
            } else {
                // Permission already granted, proceed with accessing media
                // (Use MediaStore or SAF here)

            }
        } else {
            // No need to request permission for lower versions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Check if READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE permissions are already granted
                val hasStoragePermission = ContextCompat.checkSelfPermission(
                    activity,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    activity,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED

                val permissionsToRequest = mutableListOf<String>()
                if (!hasStoragePermission) {
                    permissionsToRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    permissionsToRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }

                if (permissionsToRequest.isNotEmpty()) {
                    // Request permissions if not already granted
                    ActivityCompat.requestPermissions(
                        activity,
                        permissionsToRequest.toTypedArray(),
                        requestCode
                    )
                } else {
                    // Permission already granted, proceed with accessing media

                }
            } else {
                // No need to request permission for lower versions (consider informing user about limitations)
                // Might not work as expected due to Scoped Storage
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with accessing media
            } else {
                showDialog()
            }
        }
    }

    private fun showDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_layout, null)
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(dialogView)

        val alertDialog = alertDialogBuilder.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()

        dialogView.findViewById<Button>(R.id.button).setOnClickListener {
            openAppSettings()
            alertDialog.dismiss()
        }

        dialogView.findViewById<TextView>(R.id.textView2).setOnClickListener {
            finish()
        }
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 10)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.tool_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }



    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10) {
            // Permission settings changed
            // Check if permission is now granted or not
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {

            } else {

                showDialog()
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun setLayoutCode(){
        binding.folderRecyclerView.layoutManager = LinearLayoutManager(this)
        folderAdapter = FolderAdapter(this,folderList,mediaFilesArray)
        binding.folderRecyclerView.adapter = folderAdapter
        folderAdapter.notifyDataSetChanged()
    }
}



