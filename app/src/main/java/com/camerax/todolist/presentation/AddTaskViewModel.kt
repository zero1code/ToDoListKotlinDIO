package com.camerax.todolist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camerax.todolist.data.model.TaskResponseValue
import com.camerax.todolist.domain.SaveTaskUseCase
import com.camerax.todolist.domain.UpdateTaskUseCase
import com.camerax.todolist.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class AddTaskViewModel(
    private val saveTaskUseCase: SaveTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase
) : ViewModel() {
    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    fun saveTask(task: TaskResponseValue) {
        viewModelScope.launch {
            saveTaskUseCase(task)
                .flowOn(Dispatchers.Main)
                .onStart {
                    _state.value = State.Loading
                }
                .catch {
                    _state.value = State.Error(it)
                }
                .collect {
                    _state.value = State.Saved
                }
        }
    }

    fun updateTask(task: TaskResponseValue) {
        viewModelScope.launch {
            updateTaskUseCase(task)
                .flowOn(Dispatchers.Main)
                .onStart {
                    _state.value = State.Loading
                }
                .catch {
                    _state.value = State.Error(it)
                }
                .collect {
                    _state.value = State.Updated
                }
        }
    }

    sealed class State {
        object Loading : State()
        object Saved : State()
        object Updated : State()

        data class Success(val task: TaskResponseValue) : State()
        data class Error(val error: Throwable) : State()
    }
}