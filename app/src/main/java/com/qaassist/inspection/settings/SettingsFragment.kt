package com.qaassist.inspection.settings

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.qaassist.inspection.databinding.FragmentSettingsBinding
import com.qaassist.inspection.utils.SaveLocation
import com.qaassist.inspection.utils.SharedPrefsUtil
import com.qaassist.inspection.utils.ThemeMode

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSaveLocationOptions()
        loadSaveLocationPreference()
        setupThemeOptions()
        loadThemePreference()
    }

    private fun setupSaveLocationOptions() {
        if (!isSdCardAvailable()) {
            binding.radioButtonSdCard.isEnabled = false
            binding.radioButtonSdCard.text = "SD Card (Not available)"
        }

        binding.radioGroupSaveLocation.setOnCheckedChangeListener { _, checkedId ->
            val selectedLocation = when (checkedId) {
                binding.radioButtonDeviceStorage.id -> SaveLocation.DEVICE_STORAGE
                binding.radioButtonSdCard.id -> SaveLocation.SD_CARD
                else -> SaveLocation.DEVICE_STORAGE
            }
            SharedPrefsUtil.setSaveLocation(requireContext(), selectedLocation)
        }
    }

    private fun loadSaveLocationPreference() {
        val saveLocation = SharedPrefsUtil.getSaveLocation(requireContext())
        when (saveLocation) {
            SaveLocation.DEVICE_STORAGE -> binding.radioButtonDeviceStorage.isChecked = true
            SaveLocation.SD_CARD -> binding.radioButtonSdCard.isChecked = true
        }
    }

    private fun setupThemeOptions() {
        binding.radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            val selectedTheme = when (checkedId) {
                binding.radioButtonLight.id -> ThemeMode.LIGHT
                binding.radioButtonDark.id -> ThemeMode.DARK
                binding.radioButtonSystem.id -> ThemeMode.SYSTEM
                else -> ThemeMode.SYSTEM
            }
            SharedPrefsUtil.setTheme(requireContext(), selectedTheme)
        }
    }

    private fun loadThemePreference() {
        val theme = SharedPrefsUtil.getTheme(requireContext())
        when (theme) {
            ThemeMode.LIGHT -> binding.radioButtonLight.isChecked = true
            ThemeMode.DARK -> binding.radioButtonDark.isChecked = true
            ThemeMode.SYSTEM -> binding.radioButtonSystem.isChecked = true
        }
    }

    private fun isSdCardAvailable(): Boolean {
        val storageManager = requireContext().getExternalFilesDirs(null)
        return storageManager.size > 1 && storageManager[1] != null && Environment.isExternalStorageRemovable(storageManager[1])
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}