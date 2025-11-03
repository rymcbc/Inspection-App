package com.qaassist.inspection.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.qaassist.inspection.R
import com.qaassist.inspection.adapters.HistoryAdapter
import com.qaassist.inspection.databinding.FragmentHistoryBinding
import com.qaassist.inspection.viewmodels.HistoryViewModel
import java.io.File

class HistoryFragment : Fragment() {
    
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var historyAdapter: HistoryAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
    }
    
    private fun setupUI() {
        // Setup RecyclerView
        historyAdapter = HistoryAdapter { inspection ->
            openPDF(inspection.pdfPath)
        }
        
        binding.recyclerViewHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
        }
        
        // Setup Search
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchInspections(newText ?: "")
                return true
            }
        })
        
        // Setup Filter Spinners
        setupFilterSpinners()
        
        // Setup Sort Buttons
        setupSortButtons()
        
        // Setup Refresh
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadInspections()
        }
    }
    
    private fun setupFilterSpinners() {
        // Project filter
        val projects = arrayOf("All", "5080562", "5080564", "5080567", "5080568", "5080569", "5080570", "5080571", "5080572", "5080585")
        val projectAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, projects)
        binding.spinnerFilterProject.setAdapter(projectAdapter)
        binding.spinnerFilterProject.setOnItemClickListener { _, _, position, _ ->
            val selectedProject = if (position == 0) "" else projects[position]
            viewModel.filterByField("project", selectedProject)
        }
        
        // Inspection Type filter
        val types = arrayOf("All", "GLB/CSP", "PED", "AERIAL", "DROP/NID", "DROP/PROP LINE")
        val typeAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, types)
        binding.spinnerFilterType.setAdapter(typeAdapter)
        binding.spinnerFilterType.setOnItemClickListener { _, _, position, _ ->
            val selectedType = if (position == 0) "" else types[position]
            viewModel.filterByField("inspectionType", selectedType)
        }
    }
    
    private fun setupSortButtons() {
        binding.btnSortDate.setOnClickListener { viewModel.sortBy("date") }
        binding.btnSortProject.setOnClickListener { viewModel.sortBy("project") }
        binding.btnSortType.setOnClickListener { viewModel.sortBy("inspectionType") }
        binding.btnSortEquipment.setOnClickListener { viewModel.sortBy("equipmentId") }
    }
    
    private fun setupObservers() {
        viewModel.filteredInspections.observe(viewLifecycleOwner) { inspections ->
            historyAdapter.updateInspections(inspections)
            binding.textEmptyState.visibility = if (inspections.isEmpty()) View.VISIBLE else View.GONE
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }
    }
    
    private fun openPDF(pdfPath: String) {
        try {
            val file = File(pdfPath)
            if (file.exists()) {
                val uri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.fileprovider",
                    file
                )
                
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                startActivity(Intent.createChooser(intent, "Open PDF"))
            } else {
                Toast.makeText(context, "PDF file not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error opening PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}