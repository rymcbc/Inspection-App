package com.qaassist.inspection.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qaassist.inspection.database.entities.InspectionEntity
import com.qaassist.inspection.databinding.ItemHistoryBinding

class HistoryAdapter(
    private val onItemClick: (InspectionEntity) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    
    private var inspections: List<InspectionEntity> = emptyList()
    
    fun updateInspections(newInspections: List<InspectionEntity>) {
        inspections = newInspections
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(inspections[position])
    }
    
    override fun getItemCount(): Int = inspections.size
    
    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(inspection: InspectionEntity) {
            binding.apply {
                textDate.text = inspection.date
                textProject.text = inspection.project
                textInspectionType.text = inspection.inspectionType
                textEquipmentId.text = inspection.equipmentId
                textAddress.text = inspection.address
                textOlt.text = "OLT: ${inspection.olt}"
                textFsa.text = "FSA: ${inspection.fsa}"
                
                root.setOnClickListener {
                    onItemClick(inspection)
                }
            }
        }
    }
}