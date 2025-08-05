package com.o7solutions.braingames.BottomNav.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.api.Context
import com.o7solutions.braingames.DataClasses.Auth.UserResponse.UserData
import com.o7solutions.braingames.Model.Repository
import com.o7solutions.braingames.Model.StateClass
import com.o7solutions.braingames.utils.AppFunctions
import kotlinx.coroutines.launch

class ProfileViewModel(val repo: Repository): ViewModel() {

    private val _userLiveData = MutableLiveData<StateClass<UserData>>()
    val userLiveData: LiveData<StateClass<UserData>> get() = _userLiveData
//    private var hasFetchedUser = false

//    init {
//        val userId = AppFunctions.getUserId(context).toString()
//        getUserById(userId)
//    }
    fun getUserById(userId: String) {
//        if (hasFetchedUser) return
//        hasFetchedUser = true
        viewModelScope.launch {
            try {
                _userLiveData.value = StateClass.Loading
                val response = repo.getUserData(userId)
                _userLiveData.value = response
//
//                if (response.isSuccessful && response.body() != null) {
//                    _userLiveData.value = StateClass.Success(response.body()!!.data)
//                } else {
//                    _userLiveData.value = StateClass.Error("Error: ${response.message()}")
//                }

            } catch (e: Exception) {
                _userLiveData.value = StateClass.Error("Exception: ${e.localizedMessage}")
            }
        }
    }
}