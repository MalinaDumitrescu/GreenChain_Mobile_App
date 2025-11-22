package com.greenchain.feature.homepage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenchain.feature.homepage.data.HomeQuoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeQuoteRepository: HomeQuoteRepository
) : ViewModel() {

    private val _quoteText = MutableStateFlow<String?>(null)
    val quoteText: StateFlow<String?> = _quoteText.asStateFlow()

    init {
        loadRandomQuote()
    }

    fun loadRandomQuote() {
        viewModelScope.launch {
            _quoteText.value = homeQuoteRepository.getRandomQuote().text
        }
    }
}
