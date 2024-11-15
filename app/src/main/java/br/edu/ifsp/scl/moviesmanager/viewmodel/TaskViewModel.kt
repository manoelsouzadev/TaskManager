package br.edu.ifsp.scl.moviesmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import br.edu.ifsp.scl.moviesmanager.database.TaskDatabase
import br.edu.ifsp.scl.moviesmanager.database.TaskEntry
import br.edu.ifsp.scl.moviesmanager.database.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application): AndroidViewModel(application) {

    private val taskDao = TaskDatabase.getDatabase(application).taskDao()
    private val repository: TaskRepository

    val getAllTasks: LiveData<List<TaskEntry>>
    val getAllPriotityTasks: LiveData<List<TaskEntry>>

    init {
        repository = TaskRepository(taskDao)
        getAllTasks = repository.getAllTasks()
        getAllPriotityTasks = repository.getAllPriorityTasks()
    }

    fun insert(taskEntry: TaskEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(taskEntry)
        }
    }

    fun delete(taskEntry: TaskEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(taskEntry)
        }
    }

    fun update(taskEntry: TaskEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(taskEntry)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }

    fun searchDatabase(searchQuery: String): LiveData<List<TaskEntry>> {
        return repository.searchDatabase(searchQuery)
    }
}