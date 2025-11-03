package com.qaassist.inspection.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.qaassist.inspection.databinding.ItemPhotoBinding
import com.qaassist.inspection.models.PhotoItem

class PhotoAdapter(
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {
    
    private var photos: List<PhotoItem> = emptyList()
    
    fun updatePhotos(newPhotos: List<PhotoItem>) {
        photos = newPhotos
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photos[position], position)
    }
    
    override fun getItemCount(): Int = photos.size
    
    inner class PhotoViewHolder(private val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(photo: PhotoItem, position: Int) {
            Glide.with(binding.root.context)
                .load(photo.uri)
                .centerCrop()
                .into(binding.imageView)
            
            binding.btnDelete.setOnClickListener {
                onDeleteClick(position)
            }
        }
    }
}