package com.test.iliketodo.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.TODOItem
import com.test.iliketodo.TodoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    val todoUseCase: TodoUseCase
) : ViewModel() {
    val todoState = MutableStateFlow(TodoViewState())

    init {
        viewModelScope.launch {
            todoUseCase.getAllItemFlow().collect {
                todoState.value = todoState.value.copy(itemList = it)
            }
        }
        viewModelScope.launch {
            todoUseCase.getFinishedItemCountFlow().collect {
                todoState.value = todoState.value.copy(
                    deletedCount = it.toInt()
                )
            }
        }
        viewModelScope.launch {
            todoUseCase.getLatestDeletedItemFlow().collect {
                todoState.value = todoState.value.copy(
                    lastDeletedItem = it
                )
            }
        }
    }

    fun updateTitleText(value: String) {
        todoState.value = todoState.value.copy(titleText = value)
    }

    fun updateImageUrlText(value: String) {
        todoState.value = todoState.value.copy(imageUrlText = value)
    }

    fun deleteItem(todoItem: TODOItem) {
        todoUseCase.deleteItem(todoItem.id, todoItem.title, todoItem.imageUrl, todoItem.isFinish, System.currentTimeMillis())
    }

    fun addItem() {
        todoUseCase.insertItem(todoState.value.titleText, todoState.value.imageUrlText)
    }

    fun updateItem(id: Long, checked: Boolean) {
        todoUseCase.updateCheck(checked, id)
    }

}