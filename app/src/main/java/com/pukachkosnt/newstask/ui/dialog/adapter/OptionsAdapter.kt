package com.pukachkosnt.newstask.ui.dialog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pukachkosnt.newstask.R
import com.pukachkosnt.newstask.model.Option
import com.pukachkosnt.newstask.ui.listnews.ArticleHolder

class OptionsAdapter(
    private val layoutInflater: LayoutInflater,
    private val items: List<Option>
) : RecyclerView.Adapter<OptionHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionHolder {
        val view = layoutInflater.inflate(R.layout.option_item, parent, false)
        return OptionHolder(view)
    }

    override fun onBindViewHolder(holder: OptionHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}