package com.pukachkosnt.newstask.ui.dialog.choosesource

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

    fun saveFavSources(): Boolean {
        var isDifferent = false
        val startingSources: Set<String> = sourcesIdsRepository.getNewsSources()
        val sourcesToSave: MutableSet<String> = mutableSetOf()
        _listOptions.value?.forEach {
            if (it.checked) {
                sourcesToSave.add(it.id)
                if (!startingSources.contains(it.id)) {
                    isDifferent = true
                }
            }
        }
        isDifferent = isDifferent || startingSources.size != sourcesToSave.size
        if (isDifferent)
            sourcesIdsRepository.saveNewsSources(sourcesToSave)
        return isDifferent
    }

    override fun refreshSources(option: Source) {
        val index = _listOptions.value?.indexOfFirst { it.id == option.id }
        index?.let {
            _listOptions.value?.set(it, option.copy(isFav = !option.checked))
        }
    }
}