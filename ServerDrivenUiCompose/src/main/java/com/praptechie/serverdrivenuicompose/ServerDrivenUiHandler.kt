package com.praptechie.serverdrivenuicompose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.data_models.UIDefinition
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import com.praptechie.serverdrivenuicompose.ui_elements.RenderComponent
import com.praptechie.serverdrivenuicompose.view_model.ServerDrivenUIViewModel
import kotlinx.serialization.json.JsonObject

class ServerDrivenUiHandler {

    @Composable
    fun ServerDrivenContainer(
        uiJsonString: String,
        dataJsonString: String,
        modifier: Modifier = Modifier,
        onEvent: (ServerDrivenEvent) -> Unit = {}
    ) {
        MainScreen(
            uiJsonString = uiJsonString,
            dataJsonString = dataJsonString,
            modifier = modifier,
            onEvent = onEvent
        )
    }

    @Composable
    internal fun MainScreen(
        uiJsonString: String,
        dataJsonString: String,
        modifier: Modifier = Modifier,
        viewModel: ServerDrivenUIViewModel = viewModel(),
        onEvent: (ServerDrivenEvent) -> Unit = {}
    ) {

        LaunchedEffect(uiJsonString) {
            if (uiJsonString.isNotEmpty()) {
                viewModel.loadUI(uiJsonString)
            }
        }

        LaunchedEffect(dataJsonString) {
            if (dataJsonString.isNotEmpty()) {
                viewModel.loadData(dataJsonString)
            }
        }

        val uiDefinition by viewModel.uiDefinition.collectAsState()
        val dataJson by viewModel.dataJson.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val error by viewModel.error.collectAsState()


        when {
            isLoading -> {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            uiDefinition != null && dataJson != null -> {
                val state = remember(uiDefinition, dataJson) { ServerDrivenState() }
                ServerDrivenContent(
                    uiDefinition = uiDefinition!!,
                    dataJson = dataJson!!,
                    state = state,
                    modifier = modifier,
                    onEvent = onEvent
                )
            }
        }
    }

    @Composable
    internal fun ServerDrivenContent(
        uiDefinition: UIDefinition,
        dataJson: JsonObject,
        state: ServerDrivenState,
        modifier: Modifier = Modifier,
        onEvent: (ServerDrivenEvent) -> Unit
    ) {
        if (uiDefinition.uiData.isNotEmpty() && uiDefinition.uiData != null) {

            Box(modifier = modifier.fillMaxSize()) {
                uiDefinition?.uiData?.forEach { component ->
                    RenderComponent(
                        component = component!!,
                        dataJson = dataJson,
                        state = state,
                        onEvent = onEvent
                    )
                }
            }
        }
    }
}