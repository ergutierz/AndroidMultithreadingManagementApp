package com.example.citytour.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dagger.hilt.android.AndroidEntryPoint
import com.example.citytour.view.screens.MainScreen
import com.example.citytour.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onIntent(MainViewModel.Action.BindService)
        setContent {
            val viewState by viewModel.viewState.collectAsState()
            MainScreen(
                viewState = viewState,
                onIntent = viewModel::onIntent
            )
            LaunchedEffect("events", block = ::handleEvent)
        }
    }

    private fun handleEvent(coroutineScope: CoroutineScope) = with(coroutineScope) {
        viewModel.event.onEach { event ->
            when (event) {
                is MainViewModel.Event.ShareData -> {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, event.monitoringData.toString())
                    startActivity(Intent.createChooser(intent, "Share via"))
                }
            }
        }.launchIn(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onIntent(MainViewModel.Action.UnbindService)
    }
}