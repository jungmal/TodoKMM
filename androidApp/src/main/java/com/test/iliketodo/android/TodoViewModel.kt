package com.test.iliketodo.android

import androidx.lifecycle.ViewModel
import com.test.iliketodo.TodoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    val todoUseCase: TodoUseCase
) : ViewModel() {

}