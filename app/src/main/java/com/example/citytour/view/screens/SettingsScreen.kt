package com.example.citytour.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.citytour.model.MonitoringOption
import com.example.citytour.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    viewState: MainViewModel.ViewState,
    onIntent: (MainViewModel.Action) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        MonitoringOption.entries.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                RadioButton(
                    selected = (option == viewState.monitoringOption),
                    onClick = {
                        onIntent(MainViewModel.Action.SetMonitoringOption(option))
                    }
                )
                Text(
                    text = option.name,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

