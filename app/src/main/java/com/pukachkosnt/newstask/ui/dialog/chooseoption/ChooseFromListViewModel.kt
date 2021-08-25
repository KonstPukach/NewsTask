package com.pukachkosnt.newstask.ui.dialog.chooseoption

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pukachkosnt.newstask.model.Option

abstract class ChooseFromListViewModel<T : Option> : ViewModel() {
    protected abstract val _listOptions: LiveData<MutableList<T>>

    val listOptions: LiveData<List<T>>
        get() = _listOptions as LiveData<List<T>>

    abstract fun refreshSources(option: T)
}