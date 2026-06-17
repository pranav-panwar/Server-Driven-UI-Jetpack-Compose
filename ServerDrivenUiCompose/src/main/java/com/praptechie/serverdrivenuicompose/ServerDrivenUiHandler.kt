package com.praptechie.serverdrivenuicompose

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.data_models.UIDefinition
import com.praptechie.serverdrivenuicompose.data_models.Action
import com.praptechie.serverdrivenuicompose.data_models.UIComponent
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import com.praptechie.serverdrivenuicompose.remote_config.SduiRemoteConfig
import com.praptechie.serverdrivenuicompose.ui_elements.RenderComponent
import com.praptechie.serverdrivenuicompose.view_model.ServerDrivenUIViewModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

class ServerDrivenUiHandler() {

    companion object {
        private val dataProviders = java.util.concurrent.ConcurrentHashMap<String, () -> List<JsonObject>>()
        private var customActionHandler: ((Action, JsonObject, ServerDrivenState) -> Boolean)? = null

        @JvmStatic
        fun registerDataProvider(key: String, provider: () -> List<JsonObject>) {
            dataProviders[key.removePrefix("@")] = provider
        }

        @JvmStatic
        fun registerCustomActionHandler(handler: (Action, JsonObject, ServerDrivenState) -> Boolean) {
            customActionHandler = handler
        }

        internal fun getDataFromProvider(key: String): List<JsonObject>? {
            return dataProviders[key.removePrefix("@")]?.invoke()
        }

        internal fun executeCustomAction(action: Action, dataJson: JsonObject, state: ServerDrivenState): Boolean {
            return customActionHandler?.invoke(action, dataJson, state) ?: false
        }
    }

    @Composable
    fun ServerDrivenRemoteScreen(
        screenKey: String,
        dataJsonString: String,
        modifier: Modifier = Modifier,
        defaultUiJson: String? = null,
        fetchIntervalSeconds: Long = 3600,
        onEvent: (ServerDrivenEvent) -> Unit = {},
        onError: ((String) -> Unit)? = null
    ) {
        val context = LocalContext.current
        val liveUiJson = remember(screenKey) { mutableStateOf(defaultUiJson ?: "") }

        LaunchedEffect(screenKey) {
            SduiRemoteConfig.Builder(context)
                .screenKey(screenKey)
                .defaultJson(defaultUiJson ?: "")
                .fetchIntervalSeconds(fetchIntervalSeconds)
                .onUpdate { fetchedJson ->
                    if (fetchedJson.isNotBlank()) {
                        liveUiJson.value = fetchedJson
                    }
                }
                .build()
                .fetch()
        }

        ServerDrivenContainer(
            uiJsonString = liveUiJson.value,
            dataJsonString = dataJsonString,
            modifier = modifier,
            onEvent = onEvent,
            onError = onError
        )
    }

    @Composable
    fun ServerDrivenContainer(
        uiJsonString: String,
        dataJsonString: String,
        modifier: Modifier = Modifier,
        fallbackContent: @Composable (() -> Unit)? = null,
        onEvent: (ServerDrivenEvent) -> Unit = {},
        onError: ((String) -> Unit)? = null
    ) {
        MainScreen(
            uiJsonString = uiJsonString,
            dataJsonString = dataJsonString,
            modifier = modifier,
            fallbackContent = fallbackContent,
            onEvent = onEvent,
            onError = onError
        )
    }

    @Composable
    internal fun MainScreen(
        uiJsonString: String,
        dataJsonString: String,
        modifier: Modifier = Modifier,
        fallbackContent: @Composable (() -> Unit)? = null,
        viewModel: ServerDrivenUIViewModel = viewModel(),
        onEvent: (ServerDrivenEvent) -> Unit = {},
        onError: ((String) -> Unit)? = null
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
                LaunchedEffect(error) {
                    error?.let { onError?.invoke(it) }
                }
                if (fallbackContent != null) {
                    fallbackContent()
                } else {
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
            }

            uiDefinition != null && !dataJson.isNullOrEmpty() -> {
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    internal fun ServerDrivenContent(
        uiDefinition: UIDefinition,
        dataJson: JsonObject,
        state: ServerDrivenState,
        modifier: Modifier = Modifier,
        onEvent: (ServerDrivenEvent) -> Unit
    ) {
        val bottomSheetVisible = state.stateMap["sdui_bottom_sheet_visible"]?.jsonPrimitive?.contentOrNull == "true"
        val dialogVisible = state.stateMap["sdui_dialog_visible"]?.jsonPrimitive?.contentOrNull == "true"

        Box(modifier = modifier.fillMaxSize()) {
            if (!uiDefinition.uiData.isNullOrEmpty()) {
                uiDefinition.uiData.forEach { component ->
                    RenderComponent(
                        component = component!!,
                        dataJson = dataJson,
                        state = state,
                        onEvent = onEvent
                    )
                }
            }

            if (bottomSheetVisible) {
                val bottomSheetTitle = state.stateMap["sdui_bottom_sheet_title"]?.jsonPrimitive?.contentOrNull ?: ""
                val bottomSheetContent = state.stateMap["sdui_bottom_sheet_content"]?.jsonPrimitive?.contentOrNull ?: ""
                val sheetState = rememberModalBottomSheetState()
                
                ModalBottomSheet(
                    onDismissRequest = {
                        state.update("sdui_bottom_sheet_visible", "false")
                    },
                    sheetState = sheetState
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        if (bottomSheetTitle.isNotEmpty()) {
                            Text(
                                text = bottomSheetTitle,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                        if (bottomSheetContent.isNotEmpty()) {
                            val parsedComponent = remember(bottomSheetContent) {
                                try {
                                    val compJson = Json { 
                                        classDiscriminator = "type"
                                        ignoreUnknownKeys = true 
                                    }
                                    compJson.decodeFromString<UIComponent>(bottomSheetContent)
                                } catch (e: Exception) {
                                    null
                                }
                            }
                            if (parsedComponent != null) {
                                RenderComponent(
                                    component = parsedComponent,
                                    dataJson = dataJson,
                                    state = state,
                                    onEvent = onEvent
                                )
                            } else {
                                Text(
                                    text = "Error parsing sheet content",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            if (dialogVisible) {
                val dialogTitle = state.stateMap["sdui_dialog_title"]?.jsonPrimitive?.contentOrNull ?: ""
                val dialogMessage = state.stateMap["sdui_dialog_message"]?.jsonPrimitive?.contentOrNull ?: ""
                val dialogContent = state.stateMap["sdui_dialog_content"]?.jsonPrimitive?.contentOrNull ?: ""

                AlertDialog(
                    onDismissRequest = {
                        state.update("sdui_dialog_visible", "false")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                state.update("sdui_dialog_visible", "false")
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    title = if (dialogTitle.isNotEmpty()) {
                        { Text(text = dialogTitle) }
                    } else null,
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (dialogMessage.isNotEmpty()) {
                                Text(text = dialogMessage)
                            }
                            if (dialogContent.isNotEmpty()) {
                                if (dialogMessage.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                val parsedComponent = remember(dialogContent) {
                                    try {
                                        val compJson = Json { 
                                            classDiscriminator = "type"
                                            ignoreUnknownKeys = true 
                                        }
                                        compJson.decodeFromString<UIComponent>(dialogContent)
                                    } catch (e: Exception) {
                                        null
                                    }
                                }
                                if (parsedComponent != null) {
                                    RenderComponent(
                                        component = parsedComponent,
                                        dataJson = dataJson,
                                        state = state,
                                        onEvent = onEvent
                                    )
                                } else {
                                    Text(
                                        text = "Error parsing dialog content",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}