package com.example.mvvmtodolist.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.mvvmtodolist.data.PreferencesManager
import com.example.mvvmtodolist.data.SortOrder
import com.example.mvvmtodolist.data.Task
import com.example.mvvmtodolist.data.TaskDao
import com.example.mvvmtodolist.ui.ADD_TASK_RESULT_OK
import com.example.mvvmtodolist.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TasksViewModel @ViewModelInject constructor (
        private val taskDao: TaskDao,
        private val preferencesManager: PreferencesManager,
        @Assisted private val state: SavedStateHandle
) : ViewModel() {

        val searchQuery = state.getLiveData("searchQuery","")

        val preferencesFlow = preferencesManager.preferencesFlow

        private val taskEventChannel = Channel<TasksEvent>()
        val taskEvent = taskEventChannel.receiveAsFlow()

        private val tasksFlow = combine(
                searchQuery.asFlow(),
                preferencesFlow
        ) { query, filterPreferences ->
                Pair(query, filterPreferences)
        }.flatMapLatest { (query, filterPreferences) ->
                taskDao.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
        }

        val tasks = tasksFlow.asLiveData()

        fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
                preferencesManager.updateSortOrder(sortOrder)
        }
        fun onHideCompletedClick(hideCompleted : Boolean) = viewModelScope.launch {
                preferencesManager.updateHideCompleted(hideCompleted)
        }

        fun  onTaskSelected(task: Task) = viewModelScope.launch {
                taskEventChannel.send(TasksEvent.NavigateToEditTaskScreen(task))
        }
        fun onTaskCheckedChanged(task: Task,isChecked: Boolean)= viewModelScope.launch {
                taskDao.update(task.copy(completed = isChecked))
        }

        fun onTaskSwiped(task: Task) = viewModelScope.launch {
                taskDao.delete(task)
                taskEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))

        }

        fun onUndoDeleteCLick(task: Task)= viewModelScope.launch {
                taskDao.insert(task)
        }
        fun onAddNewTaskClick() = viewModelScope.launch {
                taskEventChannel.send(TasksEvent.NavigateToAddTaskScreen)
        }
        fun onAddEditResult(result :Int){
                when(result){
                        ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task added")
                        EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task updated")
                }
        }

        private fun showTaskSavedConfirmationMessage(text : String) = viewModelScope.launch {
                taskEventChannel.send(TasksEvent.ShowTaskSavedConfirmationMessage(text))
        }
        fun onDeleteAllCompletedClick() = viewModelScope.launch {
                taskEventChannel.send(TasksEvent.NavigateToDeleteAllCompletedScreen)
        }

        sealed class  TasksEvent {
                object NavigateToAddTaskScreen : TasksEvent()
                data class NavigateToEditTaskScreen (val task : Task ) : TasksEvent()
                //when we don't want to pass data we create object class, it doesn't create unnecessary instances
                //sealed class is like enum but can hold data
                data class ShowUndoDeleteTaskMessage(val task : Task ) : TasksEvent()
                data class ShowTaskSavedConfirmationMessage(val msg : String) : TasksEvent()
                object NavigateToDeleteAllCompletedScreen : TasksEvent()
        }



}
