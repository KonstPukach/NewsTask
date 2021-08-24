package com.pukachkosnt.newstask.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pukachkosnt.newstask.databinding.FragmentChooseSourceBinding
import com.pukachkosnt.newstask.model.Option
import com.pukachkosnt.newstask.ui.dialog.adapter.OptionsAdapter

class BottomSheetChooseFromListDialogFragment() : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentChooseSourceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseSourceBinding.inflate(layoutInflater)

        val options = listOf(
            Option("BBC"), Option("Times"), Option("Forbes"),
            Option("BBC"), Option("Times"), Option("Forbes")
        )

        binding.recyclerViewSource.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = OptionsAdapter(layoutInflater, options)
        }
        return binding.root
    }

    companion object {
        const val CHOOSE_FROM_LIST_DIALOG_TAG = "BottomSheetChooseFromListDialogFragment"
    }
}