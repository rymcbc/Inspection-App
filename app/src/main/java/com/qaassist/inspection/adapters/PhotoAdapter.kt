package com.qaassist.inspection.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.qaassist.inspection.databinding.ItemPhotoBinding
import com.qaassist.inspection.models.PhotoItem

class PhotoAdapter : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    private var photos: MutableList<PhotoItem> = mutableListOf()
    private val selectedPhotos = mutableSetOf<PhotoItem>()

    fun updatePhotos(newPhotos: List<PhotoItem>) {
        val diffCallback = PhotoDiffCallback(photos, newPhotos)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        photos = newPhotos.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }
    
    fun getSelectedPhotos(): Set<PhotoItem> {
        return selectedPhotos
    }

    fun getCurrentPhotos(): List<PhotoItem> {
        return photos
    }

    fun deleteSelectedPhotos() {
        if (selectedPhotos.isEmpty()) return
        
        val newPhotos = photos.filter { !selectedPhotos.contains(it) }
        updatePhotos(newPhotos)
        selectedPhotos.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount(): Int = photos.size

    inner class PhotoViewHolder(private val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(photo: PhotoItem) {
            val isSelected = selectedPhotos.contains(photo)
            binding.imageView.alpha = if (isSelected) 0.5f else 1.0f
            
            val requestOptions = RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(android.R.drawable.ic_menu_camera)
                .error(android.R.drawable.ic_menu_report_image)

            Glide.with(binding.root.context)
                .load(photo.uri)
                .apply(requestOptions)
                .into(binding.imageView)

            binding.root.setOnClickListener {
                if (selectedPhotos.contains(photo)) {
                    selectedPhotos.remove(photo)
                    binding.imageView.alpha = 1.0f
                } else {
                    selectedPhotos.add(photo)
                    binding.imageView.alpha = 0.5f
                }
            }
        }
    }

    private class PhotoDiffCallback(
        private val oldList: List<PhotoItem>,
        private val newList: List<PhotoItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].path == newList[newItemPosition].path
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}