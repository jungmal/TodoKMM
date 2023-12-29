package com.test.iliketodo.presentation

import com.test.TODOItem

sealed class ContentViewEvent {
    data class AddItem(val title: String, val imageUrl: String) : ContentViewEvent()
    data class DeleteItem(val item: TODOItem) : ContentViewEvent()
    data class UpdateItem(val item: TODOItem, val checked: Boolean) : ContentViewEvent()
    object LoadAllData : ContentViewEvent()
}
