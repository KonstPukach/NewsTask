package com.pukachkosnt.newstask.ui.dialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pukachkosnt.domain.repository.SourcesIdsRepository
import com.pukachkosnt.domain.usecases.GetNewsSourcesUseCase
import com.pukachkosnt.newstask.model.Source
import com.pukachkosnt.newstask.extensions.mapToUiModel
import com.pukachkosnt.newstask.ui.dialog.chooseoption.ChooseFromListViewModel
import kotlinx.coroutines.launch

class ChooseSourceViewModel(
    private val getNewsSourcesUseCase: GetNewsSourcesUseCase,
    private val sourcesIdsRepository: SourcesIdsRepository
) : ChooseFromListViewModel<Source>() {
    override val _listOptions: MutableLiveData<MutableList<Source>> = MutableLiveData()

    init {
        viewModelScope.launch {
            _listOptions.postValue(getNewsSourcesUseCase.getNewsSources().map {
                it.mapToUiModel()
            }.toMutableList())
        }
    }

    fun saveFavSources() {
        val sourcesToSave: MutableSet<String> = mutableSetOf()
        _listOptions.value?.forEach {
            if (it.checked)
                sourcesToSave.add(it.id)
        }
        sourcesIdsRepository.saveNewsSources(sourcesToSave)
    }

    override fun refreshSources(option: Source) {
        val index = _listOptions.value?.indexOfFirst { it.id == option.id }
        index?.let {
            _listOptions.value?.set(it, option.copy(isFav = !option.checked))
        }
    }
}