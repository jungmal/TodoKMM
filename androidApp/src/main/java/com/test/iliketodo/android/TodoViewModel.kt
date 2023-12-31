package com.test.iliketodo.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.TODOItem
import com.test.iliketodo.TodoUseCase
import com.test.iliketodo.presentation.ContentViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    val todoUseCase: TodoUseCase
) : ViewModel() {
    val todoState = MutableStateFlow(ContentViewState())

    init {
        viewModelScope.launch {
            todoUseCase.getAllItemFlow().collect {
                todoState.value = todoState.value.copy(itemList = it)
            }
        }
    }

    fun deleteItem(todoItem: TODOItem) {
        todoUseCase.deleteItem(todoItem.id, todoItem.title, todoItem.imageUrl, todoItem.isFinish, System.currentTimeMillis())
    }

    fun addItem(title: String, imageUrl: String) {
        todoUseCase.insertItem(title, imageUrl)
    }

    fun updateItem(id: Long, checked: Boolean) {
        todoUseCase.updateCheck(checked, id)
    }

}