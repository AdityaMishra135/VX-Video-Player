package com.example.proplayer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proplayer.VideoActivity
import com.example.proplayer.data.MediaFiles
import com.example.proplayer.databinding.FolderItemBinding
import java.io.File

class FolderAdapter(private val context: Context, private val folderList:ArrayList<String>, val mediaFiles:ArrayList<MediaFiles>) :
    RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

    inner class ViewHolder(val binding:FolderItemBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FolderItemBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int {
        return folderList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      // get folder path
        val lastIndex = folderList[position].lastIndexOf("/")
        val nameOfFolder = folderList[position].substring(lastIndex+1)
        holder.binding.folderName.text = nameOfFolder
        holder.binding.folderPath.text = folderList[position]
        holder.binding.totalVideos.text = "${getTotalFiles(folderList[position])} Videos"

        holder.itemView.setOnClickListener {
            val intent = Intent(context,VideoActivity::class.java)
            intent.putExtra("folderName",nameOfFolder)
            context.startActivity(intent)
        }

    }
    fun getTotalFiles(folderPath: String): Int {
        val directory = File(folderPath)
        if (!directory.isDirectory) {
            return 0 // Not a directory
        }
        return directory.listFiles()?.size ?: 0 // Handles null case
    }
}