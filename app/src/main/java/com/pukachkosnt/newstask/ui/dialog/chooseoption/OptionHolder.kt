package com.pukachkosnt.newstask.ui.dialog.chooseoption

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.pukachkosnt.newstask.databinding.OptionItemBinding
import com.pukachkosnt.newstask.model.Option

class OptionHolder<T : Option>(
    itemView: View,
    private val actions: Actions<T>
) : RecyclerView.ViewHolder(itemView) {
    private val binding: OptionItemBinding = OptionItemBinding.bind(itemView)

    fun bind(item: T) {
        binding.checkedTextViewOptionTitle.text = item.title
        binding.checkedTextViewOptionTitle.isChecked = item.checked

        itemView.setOnClickListener {
            actions.onChooseOption(item)
        }
    }

    interface Actions<T : Option> {
        fun onChooseOption(option: T)
    }
}