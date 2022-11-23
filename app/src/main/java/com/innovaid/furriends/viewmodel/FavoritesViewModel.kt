package com.innovaid.furriends.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoritesViewModel: ViewModel()  {

    private val _text = MutableLiveData<String>().apply {
        value = "You have not add any pet to your favorites"
    }
    val text: LiveData<String> = _text
}