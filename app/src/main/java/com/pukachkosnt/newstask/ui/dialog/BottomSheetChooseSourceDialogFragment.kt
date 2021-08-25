package com.pukachkosnt.newstask.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResult
import com.pukachkosnt.newstask.model.Source
import com.pukachkosnt.newstask.ui.dialog.chooseoption.BottomSheetChooseFromListDialogFragment
import com.pukachkosnt.newstask.ui.listnews.all.ListNewsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class BottomSheetChooseSourceDialogFragment : BottomSheetChooseFromListDialogFragment<Source>() {
    override val viewModel: ChooseSourceViewModel by viewModel()
    private var closeType = CloseType.CANCEL

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imgBtnSaveSources.setOnClickListener {
            viewModel.saveFavSources()
            closeType = CloseType.SAVE
            this.dismiss()
        }
        binding.imgBtnCancelSources.setOnClickListener {
            this.dismiss()
        }
    }

    override fun onStop() {
        super.onStop()
        setFragmentResult(
            ListNewsFragment.F_RESULT_SOURCES,
            Bundle().apply {
                putSerializable(
                    KEY_CLOSE_TYPE,
                    closeType
                )
            }
        )
    }

    enum class CloseType {
        CANCEL,
        SAVE
    }

    companion object {
        const val KEY_CLOSE_TYPE = "KEY_CLOSE_TYPE"
    }
}