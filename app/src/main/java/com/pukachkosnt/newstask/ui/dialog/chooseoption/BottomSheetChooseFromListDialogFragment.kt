package com.pukachkosnt.newstask.ui.dialog.chooseoption

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pukachkosnt.newstask.databinding.FragmentChooseSourceBinding
import com.pukachkosnt.newstask.model.Option

/*
 * A general fragment to all fragment where user must choose the one option from a list
 */

abstract class BottomSheetChooseFromListDialogFragment<T : Option>
    : BottomSheetDialogFragment(), OptionHolder.Actions<T> {
    protected lateinit var binding: FragmentChooseSourceBinding
    protected abstract val viewModel: ChooseFromListViewModel<T>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseSourceBinding.inflate(layoutInflater)
        binding.recyclerViewSource.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }

        viewModel.listOptionsLiveData.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.textViewNothingFoundSources.isVisible = true
            } else {
                binding.recyclerViewSource.adapter = OptionsAdapter(layoutInflater, it, this)
            }

            binding.progressBarLoadSource.isVisible = false
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
    }

    override fun onChooseOption(option: T, isChecked: Boolean) {
        viewModel.refreshSources(option, isChecked)
    }

    companion object {
        const val CHOOSE_FROM_LIST_DIALOG_TAG = "BottomSheetChooseFromListDialogFragment"
    }
}