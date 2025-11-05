package com.qaassist.inspection.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.qaassist.inspection.R
import com.qaassist.inspection.adapters.PhotoAdapter
import com.qaassist.inspection.database.entities.InspectionEntity
import com.qaassist.inspection.databinding.FragmentInspectionBinding
import com.qaassist.inspection.models.InspectionForm
import com.qaassist.inspection.models.PhotoModel
import com.qaassist.inspection.models.isValid
import com.qaassist.inspection.utils.DateUtils
import com.qaassist.inspection.viewmodels.InspectionViewModel
import kotlinx.coroutines.launch

class InspectionFragment : Fragment() {

    private var _binding: FragmentInspectionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InspectionViewModel by activityViewModels() // Use activityViewModels
    private lateinit var photoAdapter: PhotoAdapter

    private var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    private val CUSTOM_PROJECT = "Custom Project"
    private val PROJECTS_TO_HIDE_OLT_FSA = listOf("5080572", "5080585")


    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    viewModel.addPhotoFromUri(uri)
                }
                Log.i("InspectionFragment", "Added ${clipData.itemCount} photos from gallery")
            } ?: result.data?.data?.let { uri ->
                viewModel.addPhotoFromUri(uri)
                Log.i("InspectionFragment", "Added single photo from gallery")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInspectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
        setupKeyboardListener()
    }

    private fun setupUI() {
        binding.dateText.text = DateUtils.getCurrentDisplayDate()
        setupStatusDropdown()
        setupProjectDropdown()
        setupMunicipalityDropdown()
        setupInspectionTypeDropdown()
        photoAdapter = PhotoAdapter()
        binding.photoRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = photoAdapter
        }
        binding.btnSelectPicture.setOnClickListener { openGallery() }
        binding.btnDeletePicture.setOnClickListener {
            val selected = photoAdapter.getSelectedPhotos()
            if (selected.isNotEmpty()) {
                photoAdapter.deleteSelectedPhotos()
                viewModel.updatePhotos(photoAdapter.getCurrentPhotos())
                Log.i("InspectionFragment", "Removed ${selected.size} photos")
            } else {
                Toast.makeText(context, "No photos selected", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnSave.setOnClickListener { saveInspection() }
        binding.btnClearForm.setOnClickListener { clearForm() }

        // Initial visibility based on the first project (or default state)
        val initialProject = binding.spinnerProject.text.toString()
        updateOltFsaVisibility(initialProject)
    }

    private fun setupStatusDropdown() {
        val statuses = arrayOf("Deficiencies Present", "Ready For Final")
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, statuses)
        binding.spinnerFilterStatus.setAdapter(adapter)
        binding.spinnerFilterStatus.setOnClickListener {
            binding.spinnerFilterStatus.showDropDown()
        }
    }

    private fun setupProjectDropdown() {
        val projects = arrayOf("5080562", "5080564", "5080567", "5080568", "5080569", "5080570", "5080571", "5080572", "5080585", CUSTOM_PROJECT)
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, projects)
        binding.spinnerProject.setAdapter(adapter)
        binding.spinnerProject.setOnClickListener {
            binding.spinnerProject.showDropDown()
        }
        binding.spinnerProject.setOnItemClickListener { parent, _, position, _ ->
            val selectedProject = parent.getItemAtPosition(position).toString()
            updateOltFsaVisibility(selectedProject)
            if (selectedProject == CUSTOM_PROJECT) {
                viewModel.showCustomProjectField.value = true
                viewModel.showCustomMunicipalityField.value = true
                binding.spinnerMunicipality.setText("", false)
            } else {
                viewModel.showCustomProjectField.value = false
                viewModel.showCustomMunicipalityField.value = false
                updateMunicipalityDropdown(selectedProject)
                binding.etCustomProject.text?.clear()
                binding.etCustomMunicipality.text?.clear()
            }
        }
    }

    private fun updateOltFsaVisibility(selectedProject: String) {
        val showOltFsa = selectedProject !in PROJECTS_TO_HIDE_OLT_FSA
        binding.llOltFsaContainer.isVisible = showOltFsa
        if (!showOltFsa) {
            binding.etOlt.text?.clear()
            binding.etFsa.text?.clear()
        }
    }

    private fun setupMunicipalityDropdown() {
        // Initial empty adapter, will be populated based on project selection
        val adapter = ArrayAdapter<String>(requireContext(), R.layout.dropdown_item, mutableListOf())
        binding.spinnerMunicipality.setAdapter(adapter)
        binding.spinnerMunicipality.setOnClickListener {
            binding.spinnerMunicipality.showDropDown()
        }
    }

    private fun updateMunicipalityDropdown(project: String) {
        val municipalities = viewModel.municipalityMap[project] ?: emptyList()
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, municipalities)
        binding.spinnerMunicipality.setAdapter(adapter)
        binding.spinnerMunicipality.setText("", false) // Clear previous selection
    }


    private fun setupInspectionTypeDropdown() {
        val types = arrayOf("GLB/CSP", "PED", "AERIAL", "DROP/NID", "DROP/PROP LINE")
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, types)
        binding.spinnerInspectionType.setAdapter(adapter)
        binding.spinnerInspectionType.setOnClickListener {
            binding.spinnerInspectionType.showDropDown()
        }
    }

    private fun setupObservers() {
        viewModel.photos.observe(viewLifecycleOwner) { photos ->
            photoAdapter.updatePhotos(photos)
            binding.photoRecyclerView.visibility = if (photos.isEmpty()) View.GONE else View.VISIBLE
        }
        viewModel.saveStatus.observe(viewLifecycleOwner) { success ->
            if (success) {
                clearForm()
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnSave.isEnabled = !isLoading
            binding.btnSave.text = if (isLoading) "Saving..." else "SAVE INSPECTION"
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Log.e("InspectionFragment", "Error: $it")
            }
        }

        viewModel.showCustomProjectField.observe(viewLifecycleOwner) { show ->
            binding.tilCustomProject.isVisible = show
        }

        viewModel.showCustomMunicipalityField.observe(viewLifecycleOwner) { show ->
            binding.tilCustomMunicipality.isVisible = show
            binding.tilMunicipality.isVisible = !show // This is the Municipality Dropdown Layout
        }
        
        viewModel.loadedInspection.observe(viewLifecycleOwner) { inspection ->
            inspection?.let {
                populateFormWithInspectionData(it)
                viewModel.onInspectionLoaded() // Reset after consuming
            }
        }
    }
    
    private fun populateFormWithInspectionData(inspection: InspectionEntity) {
        binding.apply {
            // Check if it's a custom project/municipality first
            val isCustomProject = !viewModel.municipalityMap.containsKey(inspection.project)
            
            if (isCustomProject) {
                spinnerProject.setText(CUSTOM_PROJECT, false)
                etCustomProject.setText(inspection.project)
                // etCustomMunicipality.setText(inspection.municipality) // Removed
                viewModel.showCustomProjectField.value = true
                viewModel.showCustomMunicipalityField.value = true
            } else {
                spinnerProject.setText(inspection.project, false)
                updateMunicipalityDropdown(inspection.project)
                // spinnerMunicipality.setText(inspection.municipality, false) // Removed
                viewModel.showCustomProjectField.value = false
                viewModel.showCustomMunicipalityField.value = false
            }
            
            updateOltFsaVisibility(inspection.project)
            etOlt.setText(inspection.olt)
            etFsa.setText(inspection.fsa)
            etAsBuilt.setText(inspection.asBuilt)
            spinnerInspectionType.setText(inspection.inspectionType, false)
            etEquipmentId.setText(inspection.equipmentId)
            etAddress.setText(inspection.address)
            etDrawing.setText(inspection.drawing)
            etObservations.setText(inspection.observations)
        }
        Toast.makeText(context, "Loaded inspection for editing", Toast.LENGTH_SHORT).show()
    }

    private fun openGallery() {
        try {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            galleryLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Gallery error: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("InspectionFragment", "Gallery error", e)
        }
    }

    private fun saveInspection() {
        val status = binding.spinnerFilterStatus.text.toString().trim()
        
        val projectName = if (viewModel.showCustomProjectField.value == true) {
            binding.etCustomProject.text.toString().trim()
        } else {
            binding.spinnerProject.text.toString().trim()
        }

        val municipalityName = if (viewModel.showCustomMunicipalityField.value == true) {
            binding.etCustomMunicipality.text.toString().trim()
        } else {
            binding.spinnerMunicipality.text.toString().trim()
        }

        val oltValue = if (projectName in PROJECTS_TO_HIDE_OLT_FSA || !binding.llOltFsaContainer.isVisible) {
            ""
        } else {
            binding.etOlt.text.toString().trim()
        }

        val fsaValue = if (projectName in PROJECTS_TO_HIDE_OLT_FSA || !binding.llOltFsaContainer.isVisible) {
            ""
        } else {
            binding.etFsa.text.toString().trim()
        }

        val photosForForm = (viewModel.photos.value ?: emptyList()).map { PhotoModel(path = it.path) }

        val form = InspectionForm(
            date = DateUtils.getCurrentDate(),
            project = projectName,
            municipality = municipalityName,
            olt = oltValue,
            fsa = fsaValue,
            asBuilt = binding.etAsBuilt.text.toString().trim(),
            inspectionType = binding.spinnerInspectionType.text.toString().trim(),
            equipmentId = binding.etEquipmentId.text.toString().trim(),
            address = binding.etAddress.text.toString().trim(),
            drawing = binding.etDrawing.text.toString().trim(),
            observations = binding.etObservations.text.toString().trim(),
            status = status,
            photos = photosForForm
        )
        if (!form.isValid(viewModel.showCustomProjectField.value ?: false, viewModel.showCustomMunicipalityField.value ?: false)) {
            Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        Log.i("InspectionFragment", "Saving inspection with ${form.photos.size} photos and status: $status")
        lifecycleScope.launch {
            viewModel.saveInspection(form, status)
        }
    }

    fun clearForm() {
        binding.apply {
            spinnerFilterStatus.text.clear()
            spinnerProject.text.clear()
            etCustomProject.text?.clear() 
            spinnerMunicipality.text.clear()
            etCustomMunicipality.text?.clear() 
            etOlt.text?.clear()
            etFsa.text?.clear()
            etAsBuilt.text?.clear()
            spinnerInspectionType.text?.clear() 
            etEquipmentId.text?.clear()
            etAddress.text?.clear()
            etDrawing.text?.clear()
            etObservations.text?.clear()
        }
        viewModel.clearPhotos()
        viewModel.showCustomProjectField.value = false
        viewModel.showCustomMunicipalityField.value = false
        
        if (binding.spinnerProject.adapter.count > 0) {
            val defaultProject = binding.spinnerProject.adapter.getItem(0).toString()
            binding.spinnerProject.setText(defaultProject, false)
            updateOltFsaVisibility(defaultProject) // Ensure OLT/FSA visibility is reset
            if (defaultProject != CUSTOM_PROJECT) {
                updateMunicipalityDropdown(defaultProject)
            } else {
                updateMunicipalityDropdown("") 
                viewModel.showCustomProjectField.value = true 
                viewModel.showCustomMunicipalityField.value = true
            }
        } else {
            updateMunicipalityDropdown("")
            updateOltFsaVisibility("") // Default to visible if no projects
        }
       
    }

    private fun setupKeyboardListener() {
        val activityRootView = requireActivity().window.decorView.rootView
        var wasKeyboardOpen = false

        globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            val r = Rect()
            activityRootView.getWindowVisibleDisplayFrame(r)
            val screenHeight = activityRootView.height
            val heightDiff = screenHeight - r.bottom
            val isKeyboardOpen = heightDiff > screenHeight * 0.15

            if (isKeyboardOpen && !wasKeyboardOpen) {
                val focused = view?.findFocus()
                if (focused != null) {
                    (view as? ScrollView)?.post {
                        val scrollY = getScrollAmountToCenter(focused)
                        (view as? ScrollView)?.smoothScrollTo(0, scrollY)
                    }
                }
            }
            wasKeyboardOpen = isKeyboardOpen
        }
        activityRootView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
    }

    private fun getScrollAmountToCenter(view: View): Int {
        val viewRect = Rect()
        view.getDrawingRect(viewRect)
        val scrollView = this.view as? ScrollView ?: return 0
        scrollView.offsetDescendantRectToMyCoords(view, viewRect)
        val scrollHeight = scrollView.height
        return viewRect.top - (scrollHeight / 2) + (view.height / 2)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        if (globalLayoutListener != null) {
            requireActivity().window.decorView.rootView.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
        }
        _binding = null
    }
}
