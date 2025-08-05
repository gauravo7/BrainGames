package com.o7solutions.braingames.BottomNav.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.o7solutions.braingames.DataClasses.GameFetchData
import com.o7solutions.braingames.Model.Repository
import com.o7solutions.braingames.Model.StateClass
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: Repository): ViewModel() {

    private val _gameState = MutableLiveData<StateClass<List<GameFetchData.Data>>>()
    val gameState: LiveData<StateClass<List<GameFetchData.Data>>> = _gameState

    fun getGames() {
        if (_gameState.value != null) return  // Already has data; skip fetch

        _gameState.value = StateClass.Loading
        viewModelScope.launch {
            val result = repo.fetchGames()
            _gameState.value = result
        }
    }
}