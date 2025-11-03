package com.qaassist.inspection.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qaassist.inspection.database.entities.InspectionEntity
import com.qaassist.inspection.databinding.ItemHistoryBinding

class HistoryAdapter(
    private val onClick: (InspectionEntity) -> Unit,
    private val onLongClick: (InspectionEntity) -> Unit // Added for long press
) : ListAdapter<InspectionEntity, HistoryAdapter.HistoryViewHolder>(InspectionDiffCallback) {

    inner class HistoryViewHolder(
        private val binding: ItemHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(inspection: InspectionEntity) {
            binding.textDate.text = inspection.date
            binding.textProject.text = inspection.project
            binding.textInspectionType.text = inspection.inspectionType
            binding.textOlt.text = if (inspection.olt.isNullOrEmpty()) "OLT: N/A" else "OLT: ${inspection.olt}"
            binding.textFsa.text = if (inspection.fsa.isNullOrEmpty()) "FSA: N/A" else "FSA: ${inspection.fsa}"
            binding.textAsBuilt.text = if (inspection.asBuilt.isNullOrEmpty()) "As-Built: N/A" else "As-Built: ${inspection.asBuilt}"
            binding.textAddress.text = inspection.address // Keep existing address binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val inspection = getItem(position)
        holder.bind(inspection)
        holder.itemView.setOnClickListener {
            onClick(inspection)
        }
        holder.itemView.setOnLongClickListener { // Added long click listener
            onLongClick(inspection)
            true // Consume the long click
        }
    }
}

object InspectionDiffCallback : DiffUtil.ItemCallback<InspectionEntity>() {
    override fun areItemsTheSame(oldItem: InspectionEntity, newItem: InspectionEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: InspectionEntity, newItem: InspectionEntity): Boolean {
        return oldItem == newItem
    }
}