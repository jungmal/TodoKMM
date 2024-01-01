package com.test.iliketodo.android

import com.test.DeletedTODOItem
import com.test.TODOItem

data class TodoViewState(
    val itemList: List<TODOItem> = emptyList(),
    val lastDeletedItem: DeletedTODOItem? = null,
    val deletedCount: Int = 0,
    val titleText: String = "",
    val imageUrlText: String = ""
)
