package com.example.citytour.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ammf_core.performance.MonitoringData
import com.example.citytour.model.Entry
import com.example.citytour.model.MonitoringOption
import com.example.citytour.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

    private val _viewState = MutableStateFlow(ViewState())
    val viewState: StateFlow<ViewState> get() = _viewState.asStateFlow()

    private val _event = MutableSharedFlow<Event>(replay = 1)
    val event: SharedFlow<Event> get() = _event.asSharedFlow()

    init {
        bindMonitoredData()
    }

    fun onIntent(action: Action) {
        when (action) {
            is Action.StartMonitoring -> {
                mainRepository.startMonitoring()
            }
            is Action.StopMonitoring -> {
                mainRepository.stopMonitoring()
            }
            is Action.UnbindService -> {
                mainRepository.unbindService()
            }
            is Action.BindService -> {
                mainRepository.bindService()
            }
            is Action.ClearData -> {
                _viewState.update { ViewState() }
                mainRepository.clearData()
            }
            is Action.Share -> {
                _event.tryEmit(Event.ShareData(_viewState.value.monitoringData))
            }
            is Action.SetMonitoringOption -> {
                _viewState.update { oldState ->
                    oldState.copy(monitoringOption = action.monitoringOption)
                }
                viewModelScope.launch {
                    if (action.monitoringOption == MonitoringOption.Monitor) {
                        mainRepository.startMonitoring()
                    } else {
                        mainRepository.stopMonitoring()
                    }
                }
            }
            is Action.GetEntries -> {
                viewModelScope.launch {
                    val response = mainRepository.getEntries()
                    if (response.isSuccessful) {
                        val entries = response.body()?.entries ?: emptyList()
                        _viewState.update { oldState ->
                            oldState.copy(entries = entries)
                        }
                    }
                }
            }
        }
    }

    private fun bindMonitoredData() {
        viewModelScope.launch {
            mainRepository.performanceState.collect { monitoringData ->
                _viewState.update { oldState ->
                    oldState.copy(
                        monitoringData = monitoringData
                    )
                }
            }
        }
    }

    data class ViewState(
        val isMonitoring: Boolean = false,
        val entries: List<Entry?> = emptyList(),
        val monitoringData: MonitoringData? = null,
        val monitoringOption: MonitoringOption = MonitoringOption.UnMonitor
    )

    sealed interface Event {
        data class ShareData(val monitoringData: MonitoringData?) : Event
    }

    sealed interface Action {
        data class SetMonitoringOption(val monitoringOption: MonitoringOption) : Action
        data object StartMonitoring: Action
        data object StopMonitoring: Action
        data object UnbindService: Action
        data object BindService: Action
        data object GetEntries: Action
        data object ClearData: Action
        data object Share: Action
    }
}