package com.pukachkosnt.newstask.ui.dialog.chooseoption

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pukachkosnt.newstask.R
import com.pukachkosnt.newstask.model.Option

class OptionsAdapter<T : Option>(
    private val layoutInflater: LayoutInflater,
    private val items: List<T>,
    private val actions: OptionHolder.Actions<T>
) : RecyclerView.Adapter<OptionHolder<T>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionHolder<T> {
        val view = layoutInflater.inflate(R.layout.option_item, parent, false)
        return OptionHolder(view, actions)
    }

    override fun onBindViewHolder(holder: OptionHolder<T>, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}