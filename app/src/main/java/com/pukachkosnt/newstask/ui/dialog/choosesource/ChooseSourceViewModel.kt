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
    override val _listOptionsLiveData: MutableLiveData<MutableList<Source>> = MutableLiveData()
    private lateinit var listOptions: MutableList<Source>

    init {
        viewModelScope.launch {
            listOptions = getNewsSourcesUseCase.getNewsSources().map {
                it.mapToUiModel()
            }.toMutableList()
            _listOptionsLiveData.postValue(listOptions)
        }
    }

    fun saveFavSources(): Boolean {
        var isDifferent = false
        val startingSources: Set<String> = sourcesIdsRepository.getNewsSources()
        val sourcesToSave: MutableSet<String> = mutableSetOf()
        listOptions.forEach {
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

    override fun refreshSources(option: Source, isChecked: Boolean) {
        val index = listOptions.indexOfFirst { it.id == option.id }
        listOptions[index] = option.copy(isFav = isChecked)
    }
}