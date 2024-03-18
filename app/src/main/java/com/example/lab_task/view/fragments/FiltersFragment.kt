package com.example.lab_task.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lab_task.R
import com.example.lab_task.databinding.FragmentFiltersBinding
import com.example.lab_task.model.other.FiltersData
import com.example.lab_task.model.other.FiltersDataRecievedListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FiltersFragment(val listener: FiltersDataRecievedListener) : BottomSheetDialogFragment() {
    lateinit var binding: FragmentFiltersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.clearButton.setOnClickListener {
            binding.withImageCheckbox.isChecked = false
            binding.sortRadioGroup.clearCheck()
            listener.onDataRevieved(null)
        }

        binding.applyButton.setOnClickListener {
            val sortType: Int = when(binding.sortRadioGroup.checkedRadioButtonId){
                R.id.decreasing_likes ->  0
                R.id.increasing_likes ->  1
                R.id.alphabet_sorted -> 2
                R.id.reverse_alphaber_sorted -> 3
                else -> -1
            }
            listener.onDataRevieved(
                FiltersData(
                    sortType,
                    binding.withImageCheckbox.isChecked,
                    binding.typeSearchSpinner.selectedItemPosition
                )
            )
            dismiss()
        }

    }
}