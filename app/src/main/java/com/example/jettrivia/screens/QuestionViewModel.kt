package com.example.jettrivia.screens

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jettrivia.data.DataOrException
import com.example.jettrivia.model.QuestionItem
import com.example.jettrivia.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class QuestionViewModel @Inject constructor(private val repository: QuestionRepository) : ViewModel() {

     val dataOrException : MutableState<DataOrException<ArrayList<QuestionItem>, Boolean, Exception>>
        = mutableStateOf(DataOrException(null, true, Exception(" ")))

    init {
        getAllQuestions()
    }

    private fun getAllQuestions(){
        viewModelScope.launch {
            dataOrException.value.loading = true
            dataOrException.value = repository.getAllQuestions()
            if (dataOrException.value.data.toString().isNotEmpty()) dataOrException.value.loading = false
        }
    }
}

