package com.qaassist.inspection.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qaassist.inspection.R
import com.qaassist.inspection.adapters.HistoryAdapter
import com.qaassist.inspection.database.entities.InspectionEntity
import com.qaassist.inspection.databinding.FragmentHistoryBinding
import com.qaassist.inspection.viewmodels.HistoryViewModel
import com.qaassist.inspection.viewmodels.InspectionViewModel
import java.io.File

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val historyViewModel: HistoryViewModel by viewModels()
    private val inspectionViewModel: InspectionViewModel by activityViewModels() // Shared ViewModel

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
        setupBackButton()
    }

    private fun setupUI() {
        historyAdapter = HistoryAdapter(
            onClick = { inspection -> openExcel(inspection) },
            onLongClick = { inspection -> editInspection(inspection) }
        )

        binding.recyclerViewHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val inspection = historyAdapter.currentList[position]
                    showDeleteConfirmationDialog(inspection, viewHolder)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewHistory)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                historyViewModel.searchInspections(newText ?: "")
                return true
            }
        })

        setupFilterDropdowns()

        binding.btnSortDate.setOnClickListener { historyViewModel.sortBy("date") }
        binding.btnSortProject.setOnClickListener { historyViewModel.sortBy("project") }
        binding.btnSortType.setOnClickListener { historyViewModel.sortBy("inspectionType") }
        binding.btnSortEquipment.setOnClickListener { historyViewModel.sortBy("equipmentId") }

        binding.swipeRefresh.setOnRefreshListener {
            historyViewModel.refreshInspections()
        }
    }

    private fun editInspection(inspection: InspectionEntity) {
        inspectionViewModel.loadInspectionForEdit(inspection)
        // Navigation should be handled by the activity observing a shared event
        Toast.makeText(context, "Loading inspection for editing...", Toast.LENGTH_SHORT).show()
    }

    private fun showDeleteConfirmationDialog(inspection: InspectionEntity, viewHolder: RecyclerView.ViewHolder) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Inspection")
            .setMessage("Are you sure you want to delete this inspection and all its files?")
            .setPositiveButton("Delete") { _, _ ->
                historyViewModel.deleteInspectionAndFiles(inspection)
            }
            .setNegativeButton("Cancel") { _, _ ->
                if (viewHolder.bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    historyAdapter.notifyItemChanged(viewHolder.bindingAdapterPosition)
                }
            }
            .setOnCancelListener {
                if (viewHolder.bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    historyAdapter.notifyItemChanged(viewHolder.bindingAdapterPosition)
                }
            }
            .show()
    }

    private fun setupFilterDropdowns() {
        val projectAdapter = ArrayAdapter<String>(requireContext(), R.layout.dropdown_item)
        binding.spinnerFilterProject.setAdapter(projectAdapter)
        val types = arrayOf("All", "GLB/CSP", "PED", "AERIAL", "DROP/NID", "DROP/PROP LINE")
        val typeAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, types)
        binding.spinnerFilterType.setAdapter(typeAdapter)

        val oltAdapter = ArrayAdapter<String>(requireContext(), R.layout.dropdown_item)
        binding.spinnerFilterOlt.setAdapter(oltAdapter)
        val fsaAdapter = ArrayAdapter<String>(requireContext(), R.layout.dropdown_item)
        binding.spinnerFilterFsa.setAdapter(fsaAdapter)
        val asBuiltAdapter = ArrayAdapter<String>(requireContext(), R.layout.dropdown_item)
        binding.spinnerFilterAsBuilt.setAdapter(asBuiltAdapter)

        binding.spinnerFilterProject.setOnItemClickListener { _, _, position, _ ->
            val selectedProject = projectAdapter.getItem(position) ?: ""
            historyViewModel.filterByField("project", selectedProject)
        }
        binding.spinnerFilterType.setOnItemClickListener { _, _, position, _ ->
            val selectedType = types[position]
            historyViewModel.filterByField("inspectionType", selectedType)
        }
        binding.etFilterDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                historyViewModel.filterByField("date", s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.spinnerFilterOlt.setOnItemClickListener { _, _, position, _ ->
            val selectedOlt = oltAdapter.getItem(position) ?: ""
            historyViewModel.filterByField("olt", selectedOlt)
        }
        binding.spinnerFilterFsa.setOnItemClickListener { _, _, position, _ ->
            val selectedFsa = fsaAdapter.getItem(position) ?: ""
            historyViewModel.filterByField("fsa", selectedFsa)
        }
        binding.spinnerFilterAsBuilt.setOnItemClickListener { _, _, position, _ ->
            val selectedAsBuilt = asBuiltAdapter.getItem(position) ?: ""
            historyViewModel.filterByField("asBuilt", selectedAsBuilt)
        }
    }

    private fun setupObservers() {
        historyViewModel.filteredInspections.observe(viewLifecycleOwner) { inspections ->
            historyAdapter.submitList(inspections)
            binding.textEmptyState.visibility = if (inspections.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerViewHistory.visibility = if (inspections.isEmpty()) View.GONE else View.VISIBLE
        }

        historyViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }

        historyViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }

        historyViewModel.inspections.observe(viewLifecycleOwner) { inspections ->
            updateFilterDropdowns(inspections)
        }
    }

    private fun setupBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                isEnabled = false
                if (isAdded) {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun updateFilterDropdowns(inspections: List<InspectionEntity>) {
        val projects = listOf("All") + inspections.asSequence().mapNotNull { it.project }.distinct().sorted().toList()
        val projectAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, projects)
        binding.spinnerFilterProject.setAdapter(projectAdapter)
        binding.spinnerFilterProject.setText(historyViewModel.currentFilters["project"] ?: "All", false)

        binding.spinnerFilterType.setText(historyViewModel.currentFilters["inspectionType"] ?: "All", false)

        val olts = listOf("All") + inspections.asSequence().mapNotNull { it.olt }.filter { it.isNotBlank() }.distinct().sorted().toList()
        val oltAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, olts)
        binding.spinnerFilterOlt.setAdapter(oltAdapter)
        binding.spinnerFilterOlt.setText(historyViewModel.currentFilters["olt"] ?: "All", false)

        val fsas = listOf("All") + inspections.asSequence().mapNotNull { it.fsa }.filter { it.isNotBlank() }.distinct().sorted().toList()
        val fsaAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, fsas)
        binding.spinnerFilterFsa.setAdapter(fsaAdapter)
        binding.spinnerFilterFsa.setText(historyViewModel.currentFilters["fsa"] ?: "All", false)

        val asBuilts = listOf("All") + inspections.asSequence().mapNotNull { it.asBuilt }.filter { it.isNotBlank() }.distinct().sorted().toList()
        val asBuiltAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, asBuilts)
        binding.spinnerFilterAsBuilt.setAdapter(asBuiltAdapter)
        binding.spinnerFilterAsBuilt.setText(historyViewModel.currentFilters["asBuilt"] ?: "All", false)
    }

    private fun openExcel(inspection: InspectionEntity) {
        val uriString = inspection.excelUri
        if (uriString.isBlank()) {
            Toast.makeText(context, "Excel file not available for this inspection.", Toast.LENGTH_SHORT).show()
            return
        }

        val excelUri: Uri = try {
            val rawUri = uriString.toUri()
            if (rawUri.scheme == "file") {
                val authority = requireContext().packageName + ".fileprovider"
                FileProvider.getUriForFile(requireContext(), authority, File(rawUri.path!!))
            } else {
                rawUri
            }
        } catch (e: Exception) {
            Log.e("HistoryFragment", "Error creating shareable URI for Excel file: $uriString", e)
            Toast.makeText(context, "Error accessing Excel file.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(excelUri, "application/vnd.ms-excel")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No Excel viewer found. Please install an Excel viewer app.", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error opening Excel file: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("HistoryFragment", "Error opening Excel file", e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}