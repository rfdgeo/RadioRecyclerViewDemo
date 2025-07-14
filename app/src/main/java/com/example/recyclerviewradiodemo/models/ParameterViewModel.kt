package com.example.recyclerviewradiodemo.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ParameterViewModel(application: Application) : AndroidViewModel(application) {
    private val _textData = MutableLiveData<String>()
    val textData: LiveData<String> = _textData

    fun updateTextData(newData: String) {
        _textData.value = newData
    }
}
