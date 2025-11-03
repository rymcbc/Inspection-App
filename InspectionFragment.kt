package com.qaassist.inspection.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.qaassist.inspection.R
import com.qaassist.inspection.adapters.PhotoAdapter
import com.qaassist.inspection.databinding.FragmentInspectionBinding
import com.qaassist.inspection.models.InspectionForm
import com.qaassist.inspection.utils.DateUtils
import com.qaassist.inspection.viewmodels.InspectionViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class InspectionFragment : Fragment() {
    
    private var _binding: FragmentInspectionBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: InspectionViewModel by viewModels()
    private lateinit var photoAdapter: PhotoAdapter
    private var currentPhotoPath: String? = null
    
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            currentPhotoPath?.let { path ->
                viewModel.addPhoto(path)
            }
        }
    }
    
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    viewModel.addPhotoFromUri(uri)
                }
            } ?: result.data?.data?.let { uri ->
                viewModel.addPhotoFromUri(uri)
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
    }
    
    private fun setupUI() {
        // Set current date
        binding.dateText.text = DateUtils.getCurrentDate()
        
        // Setup dropdown adapters
        setupProjectDropdown()
        setupInspectionTypeDropdown()
        
        // Setup photo recycler view
        photoAdapter = PhotoAdapter(
            onDeleteClick = { position -> viewModel.removePhoto(position) }
        )
        binding.photoRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = photoAdapter
        }
        
        // Setup buttons
        binding.btnAddPicture.setOnClickListener { openCamera() }
        binding.btnSelectPicture.setOnClickListener { openGallery() }
        binding.btnSave.setOnClickListener { saveInspection() }
    }
    
    private fun setupProjectDropdown() {
        val projects = arrayOf(
            "5080562", "5080564", "5080567", "5080568", "5080569",
            "5080570", "5080571", "5080572", "5080585"
        )
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, projects)
        binding.spinnerProject.setAdapter(adapter)
    }
    
    private fun setupInspectionTypeDropdown() {
        val types = arrayOf("GLB/CSP", "PED", "AERIAL", "DROP/NID", "DROP/PROP LINE")
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, types)
        binding.spinnerInspectionType.setAdapter(adapter)
    }
    
    private fun setupObservers() {
        viewModel.photos.observe(viewLifecycleOwner) { photos ->
            photoAdapter.updatePhotos(photos)
        }
        
        viewModel.saveStatus.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "Inspection saved successfully!", Toast.LENGTH_SHORT).show()
                clearForm()
            } else {
                Toast.makeText(context, "Failed to save inspection", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun openCamera() {
        val photoFile = createImageFile()
        val photoURI = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile
        )
        
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        }
        
        cameraLauncher.launch(intent)
    }
    
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        galleryLauncher.launch(intent)
    }
    
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = requireContext().getExternalFilesDir("Pictures")
        
        return File.createTempFile(imageFileName, ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }
    
    private fun saveInspection() {
        val form = InspectionForm(
            date = DateUtils.getCurrentDate(),
            project = binding.spinnerProject.text.toString(),
            olt = binding.etOlt.text.toString(),
            fsa = binding.etFsa.text.toString(),
            asBuilt = binding.etAsBuilt.text.toString(),
            inspectionType = binding.spinnerInspectionType.text.toString(),
            equipmentId = binding.etEquipmentId.text.toString(),
            address = binding.etAddress.text.toString(),
            drawing = binding.etDrawing.text.toString(),
            observations = binding.etObservations.text.toString(),
            photos = viewModel.photos.value ?: emptyList()
        )
        
        lifecycleScope.launch {
            viewModel.saveInspection(form)
        }
    }
    
    private fun clearForm() {
        binding.apply {
            spinnerProject.text.clear()
            etOlt.text?.clear()
            etFsa.text?.clear()
            etAsBuilt.text?.clear()
            spinnerInspectionType.text.clear()
            etEquipmentId.text?.clear()
            etAddress.text?.clear()
            etDrawing.text?.clear()
            etObservations.text?.clear()
        }
        viewModel.clearPhotos()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}