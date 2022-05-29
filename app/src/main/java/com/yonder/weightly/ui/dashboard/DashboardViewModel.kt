package com.yonder.weightly.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarEntry
import com.yonder.weightly.data.local.WeightDao
import com.yonder.weightly.data.repository.WeightRepository
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.ui.home.HomeViewModel
import com.yonder.weightly.utils.extensions.endOfDay
import com.yonder.weightly.utils.extensions.orZero
import com.yonder.weightly.utils.extensions.startOfDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    var weightDao: WeightDao,
    private var weightRepository: WeightRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun fetchInsights() = viewModelScope.launch(Dispatchers.IO) {

        val averageWeight = weightDao.getAverage()
        val maxWeight = weightDao.getMax()
        val minWeight = weightDao.getMin()
        _uiState.update {
            it.copy(
                averageWeight = "$averageWeight",
                minWeight = "$minWeight",
                maxWeight = "$maxWeight"
            )
        }
    }

    fun getWeightHistories() = viewModelScope.launch(Dispatchers.IO) {
        weightRepository.invoke().collectLatest { weightHistories ->
            _uiState.update {
                it.copy(
                    histories = weightHistories,
                    barEntries = weightHistories.mapIndexed { index, weight ->
                        BarEntry(index.toFloat(), weight?.value.orZero())
                    }
                )
            }
        }
    }

    data class UiState(
        var maxWeight: String? = null,
        var minWeight: String? = null,
        var averageWeight: String? = null,
        var histories: List<WeightUIModel?> = emptyList(),
        var barEntries: List<BarEntry> = emptyList(),
    )


}