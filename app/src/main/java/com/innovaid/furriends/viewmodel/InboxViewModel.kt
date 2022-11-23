package com.innovaid.furriends.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InboxViewModel: ViewModel()  {
    private val _text = MutableLiveData<String>().apply {
        value = "You have no messages"
    }
    val text: LiveData<String> = _text
}