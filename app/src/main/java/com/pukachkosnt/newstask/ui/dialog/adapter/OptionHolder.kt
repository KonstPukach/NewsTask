package com.pukachkosnt.newstask.ui.dialog.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.pukachkosnt.newstask.databinding.OptionItemBinding
import com.pukachkosnt.newstask.model.Option

class OptionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding: OptionItemBinding = OptionItemBinding.bind(itemView)

    fun bind(item: Option) {
        binding.checkedTextViewOptionTitle.text = item.title
    }
}