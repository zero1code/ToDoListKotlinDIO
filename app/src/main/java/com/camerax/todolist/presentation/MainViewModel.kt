package com.camerax.todolist.presentation

import androidx.lifecycle.*
import com.camerax.todolist.data.model.TaskResponseValue
import com.camerax.todolist.domain.DeleteTaskUseCase
import com.camerax.todolist.domain.ListTaskUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class MainViewModel(
    private val listTaskUseCase: ListTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel(), LifecycleObserver {
    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun getTasks() {
        viewModelScope.launch {
            listTaskUseCase()
                .flowOn(Dispatchers.Main)
                .onStart {
                    _state.value = State.Loading
                }
                .catch {
                    _state.value = State.Error(it)
                }
                .collect {
                    _state.value = State.Success(it)
                }
        }
    }

     fun deleteTask(id: Long) {
        viewModelScope.launch {
            deleteTaskUseCase(id)
                .flowOn(Dispatchers.Main)
                .onStart {
                    _state.value = State.Loading
                }
                .catch {
                    _state.value = State.Error(it)
                }
                .collect {
                    _state.value = State.Deleted(listOf())
                }
        }
    }

    sealed class State {
        object Loading : State()

        data class Success(val list: List<TaskResponseValue>) : State()
        data class Deleted(val list: List<TaskResponseValue>) : State()
        data class Error(val error: Throwable) : State()
    }
}