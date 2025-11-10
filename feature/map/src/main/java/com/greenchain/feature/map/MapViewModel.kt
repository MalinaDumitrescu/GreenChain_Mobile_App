package com.greenchain.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenchain.feature.map.data.RecyclingPointRepository
import com.greenchain.model.RecyclingPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: RecyclingPointRepository
) : ViewModel() {

    private val _recyclingPoints = MutableStateFlow<List<RecyclingPoint>>(emptyList())
    val recyclingPoints: StateFlow<List<RecyclingPoint>> = _recyclingPoints

    init {
        loadRecyclingPoints()
    }

    private fun loadRecyclingPoints() {
        viewModelScope.launch {
            _recyclingPoints.value = repository.getRecyclingPoints()
        }
    }

    fun toggleRecyclingPointStatus(point: RecyclingPoint) {
        _recyclingPoints.update {
            it.map {
                if (it.id == point.id) {
                    it.copy(isActive = !it.isActive)
                } else {
                    it
                }
            }
        }
    }
}
