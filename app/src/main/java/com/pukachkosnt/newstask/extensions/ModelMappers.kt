package com.pukachkosnt.newstask.extensions

import com.pukachkosnt.domain.models.SourceModel
import com.pukachkosnt.newstask.model.Source

fun SourceModel.mapToUiModel(): Source {
    return Source(
        sourceId = this.id,
        name = this.name,
        description = this.description,
        language = this.language,
        country = this.country,
        isFav = this.checked
    )
}