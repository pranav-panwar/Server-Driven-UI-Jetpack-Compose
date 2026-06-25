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
import androidx.compose.material3.SheetValue
import androidx.compose.ui.window.DialogProperties
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

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.MutableState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.praptechie.serverdrivenuicompose.handler_processors.DataConverter
import androidx.compose.foundation.layout.fillMaxHeight

enum class FireUiWindowSizeClass { COMPACT, MEDIUM, EXPANDED }

val LocalFireUiTheme = compositionLocalOf<com.praptechie.serverdrivenuicompose.data_models.FireUiTheme> { 
    com.praptechie.serverdrivenuicompose.data_models.FireUiTheme.SYSTEM 
}
val LocalFireUiWindowSize = compositionLocalOf<FireUiWindowSizeClass> { 
    FireUiWindowSizeClass.COMPACT 
}

class FireUIScope(
    private val showBottomSheetState: MutableState<Boolean>,
    private val showDialogState: MutableState<Boolean>,
    private val activeSlotState: MutableState<String?>
) {
    fun showBottomSheet(slotKey: String? = null) {
        activeSlotState.value = slotKey
        showBottomSheetState.value = true
    }

    fun dismissBottomSheet() {
        showBottomSheetState.value = false
    }

    fun showDialog(slotKey: String? = null) {
        activeSlotState.value = slotKey
        showDialogState.value = true
    }

    fun dismissDialog() {
        showDialogState.value = false
    }
}

val LocalFireUIScope = staticCompositionLocalOf<FireUIScope> {
    error("No FireUIScope provided. Make sure to wrap your content in FireUI {}")
}


class ServerDrivenUiHandler() {

    internal val defaultUiJsons = java.util.concurrent.ConcurrentHashMap<String, String>()
    internal val defaultDataJsons = java.util.concurrent.ConcurrentHashMap<String, String>()
    internal var fetchIntervalSeconds: Long = 3600
    internal var cohortContext: Map<String, String> = emptyMap()
    internal var variantResolverUrl: String? = null

    internal val uiFlows = java.util.concurrent.ConcurrentHashMap<String, MutableStateFlow<String>>()
    internal val dataFlows = java.util.concurrent.ConcurrentHashMap<String, MutableStateFlow<String>>()

    internal val globalUiFlow = MutableStateFlow<String>("")
    internal val globalDataFlow = MutableStateFlow<String>("")

    private var lastActiveScreenKey: String? = null

    fun getUiFlow(screenKey: String): MutableStateFlow<String> {
        return uiFlows.getOrPut(screenKey) {
            MutableStateFlow(globalUiFlow.value.ifEmpty { defaultUiJsons[screenKey] ?: "" })
        }
    }

    fun getDataFlow(screenKey: String): MutableStateFlow<String> {
        return dataFlows.getOrPut(screenKey) {
            MutableStateFlow(globalDataFlow.value.ifEmpty { defaultDataJsons[screenKey] ?: "" })
        }
    }

    fun loadUI(json: String) {
        val activeKey = lastActiveScreenKey
        if (activeKey != null) {
            loadUI(activeKey, json)
        } else {
            globalUiFlow.value = json
        }
    }

    fun loadUI(screenKey: String, json: String) {
        uiFlows.getOrPut(screenKey) { MutableStateFlow("") }.value = json
    }

    fun loadData(data: Any) {
        val activeKey = lastActiveScreenKey
        val jsonStr = convertDataToString(data)
        if (activeKey != null) {
            loadData(activeKey, data)
        } else {
            globalDataFlow.value = jsonStr
        }
    }

    fun loadData(screenKey: String, data: Any) {
        val jsonStr = convertDataToString(data)
        dataFlows.getOrPut(screenKey) { MutableStateFlow("") }.value = jsonStr
    }

    private fun convertDataToString(data: Any): String {
        return when (data) {
            is String -> data
            else -> {
                try {
                    DataConverter.toJsonObject(data).toString()
                } catch (e: Exception) {
                    ""
                }
            }
        }
    }

    internal fun setActiveScreen(screenKey: String) {
        lastActiveScreenKey = screenKey
    }

    class Builder {
        private val defaultUiJsons = mutableMapOf<String, String>()
        private val defaultDataJsons = mutableMapOf<String, String>()
        private var fetchIntervalSeconds: Long = 3600
        private var cohortContext: Map<String, String> = emptyMap()
        private var variantResolverUrl: String? = null

        fun defaultUiJson(screenKey: String, json: String) = apply {
            defaultUiJsons[screenKey] = json
        }

        fun defaultDataJson(screenKey: String, json: String) = apply {
            defaultDataJsons[screenKey] = json
        }

        fun fetchIntervalSeconds(seconds: Long) = apply {
            fetchIntervalSeconds = seconds
        }

        fun cohortContext(context: Map<String, String>) = apply {
            cohortContext = context
        }

        fun variantResolverUrl(url: String?) = apply {
            variantResolverUrl = url
        }

        fun build(): ServerDrivenUiHandler {
            val handler = ServerDrivenUiHandler()
            handler.defaultUiJsons.putAll(defaultUiJsons)
            handler.defaultDataJsons.putAll(defaultDataJsons)
            handler.fetchIntervalSeconds = fetchIntervalSeconds
            handler.cohortContext = cohortContext
            handler.variantResolverUrl = variantResolverUrl
            return handler
        }
    }


    companion object {
        private val dataProviders = java.util.concurrent.ConcurrentHashMap<String, () -> List<JsonObject>>()
        private var customActionHandler: ((Action, JsonObject, ServerDrivenState) -> Boolean)? = null
        private val slotContents = java.util.concurrent.ConcurrentHashMap<String, @Composable (Map<String, Any?>) -> Unit>()
        private val fontRegistry = java.util.concurrent.ConcurrentHashMap<String, androidx.compose.ui.text.font.FontFamily>()
        
        internal var currentTheme = mutableStateOf(com.praptechie.serverdrivenuicompose.data_models.FireUiTheme.SYSTEM)

        @JvmStatic
        fun setTheme(theme: com.praptechie.serverdrivenuicompose.data_models.FireUiTheme) {
            currentTheme.value = theme
        }

        @JvmStatic
        fun registerFont(name: String, fontFamily: androidx.compose.ui.text.font.FontFamily) {
            fontRegistry[name] = fontFamily
        }

        internal fun getFontFamily(name: String): androidx.compose.ui.text.font.FontFamily? {
            return fontRegistry[name]
        }

        @JvmStatic
        fun registerDataProvider(key: String, provider: () -> List<JsonObject>) {
            dataProviders[key.removePrefix("@")] = provider
        }

        @JvmStatic
        fun registerCustomActionHandler(handler: (Action, JsonObject, ServerDrivenState) -> Boolean) {
            customActionHandler = handler
        }

        @JvmStatic
        fun registerSlotContent(slotName: String, content: @Composable (slotContext: Map<String, Any?>) -> Unit) {
            slotContents[slotName] = content
        }

        internal fun getSlotContent(slotName: String): (@Composable (Map<String, Any?>) -> Unit)? {
            return slotContents[slotName]
        }

        internal fun getDataFromProvider(key: String): List<JsonObject>? {
            return dataProviders[key.removePrefix("@")]?.invoke()
        }

        internal fun executeCustomAction(action: Action, dataJson: JsonObject, state: ServerDrivenState): Boolean {
            return customActionHandler?.invoke(action, dataJson, state) ?: false
        }
    }

    @Deprecated(
        message = "Use FireUI wrapper composable instead",
        replaceWith = ReplaceWith("FireUI(screenKey = screenKey, onEvent = onEvent)")
    )
    @Composable
    fun ServerDrivenRemoteScreen(
        screenKey: String,
        dataJsonString: String,
        modifier: Modifier = Modifier,
        defaultUiJson: String? = null,
        fetchIntervalSeconds: Long = 3600,
        cohortContext: Map<String, String> = emptyMap(),
        variantResolverUrl: String? = null,
        onEvent: (ServerDrivenEvent) -> Unit = {},
        onError: ((String) -> Unit)? = null
    ) {
        LaunchedEffect(screenKey, defaultUiJson, dataJsonString) {
            defaultUiJsons[screenKey] = defaultUiJson ?: ""
            loadData(screenKey, dataJsonString)
        }

        LaunchedEffect(fetchIntervalSeconds, cohortContext, variantResolverUrl) {
            this@ServerDrivenUiHandler.fetchIntervalSeconds = fetchIntervalSeconds
            this@ServerDrivenUiHandler.cohortContext = cohortContext
            this@ServerDrivenUiHandler.variantResolverUrl = variantResolverUrl
        }

        FireUI(
            screenKey = screenKey,
            onEvent = onEvent,
            modifier = modifier,
            onError = onError
        ) {}
    }

    @Deprecated(
        message = "Use FireUI wrapper composable instead",
        replaceWith = ReplaceWith("FireUI(screenKey = screenKey, onEvent = onEvent)")
    )
    @Composable
    fun ServerDrivenContainer(
        uiJsonString: String,
        dataJsonString: String,
        modifier: Modifier = Modifier,
        fallbackContent: @Composable (() -> Unit)? = null,
        screenKey: String? = null,
        onEvent: (ServerDrivenEvent) -> Unit = {},
        onError: ((String) -> Unit)? = null
    ) {
        val finalScreenKey = screenKey ?: "deprecated_container_key"

        LaunchedEffect(finalScreenKey, uiJsonString, dataJsonString) {
            loadUI(finalScreenKey, uiJsonString)
            loadData(finalScreenKey, dataJsonString)
        }

        FireUI(
            screenKey = finalScreenKey,
            onEvent = onEvent,
            modifier = modifier,
            onError = onError,
            fallbackContent = fallbackContent
        ) {}
    }

    @Composable
    internal fun MainScreen(
        uiJsonString: String,
        dataJsonString: String,
        modifier: Modifier = Modifier,
        fallbackContent: @Composable (() -> Unit)? = null,
        viewModel: ServerDrivenUIViewModel = viewModel(),
        screenKey: String? = null,
        onEvent: (ServerDrivenEvent) -> Unit = {},
        onError: ((String) -> Unit)? = null
    ) {

        val context = LocalContext.current

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
                LaunchedEffect(uiDefinition, screenKey) {
                    if (screenKey != null) {
                        SduiRemoteConfig.markLastKnownGood(context, screenKey, uiJsonString)
                    }
                    onEvent(ServerDrivenEvent.ScreenRendered(screenKey ?: "unknown", null, uiDefinition?.uiData?.size ?: 0))
                }

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
                val collapsible = state.stateMap["sdui_bottom_sheet_collapsible"]?.jsonPrimitive?.contentOrNull != "false"
                val dismissOnOutsideClick = state.stateMap["sdui_bottom_sheet_dismissOnOutsideClick"]?.jsonPrimitive?.contentOrNull != "false"
                val sheetSize = state.stateMap["sdui_bottom_sheet_sheetSize"]?.jsonPrimitive?.contentOrNull ?: ""
                
                val sheetState = rememberModalBottomSheetState(
                    skipPartiallyExpanded = !collapsible,
                    confirmValueChange = {
                        if (it == SheetValue.Hidden && !dismissOnOutsideClick) false else true
                    }
                )
                
                ModalBottomSheet(
                    onDismissRequest = {
                        state.update("sdui_bottom_sheet_visible", "false")
                    },
                    sheetState = sheetState
                ) {
                    var modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                    
                    if (sheetSize.isNotEmpty()) {
                        when (sheetSize) {
                            "compact" -> modifier = modifier.fillMaxHeight(0.3f)
                            "half" -> modifier = modifier.fillMaxHeight(0.5f)
                            "full" -> modifier = modifier.fillMaxHeight(0.95f)
                            else -> {
                                val dpSize = sheetSize.toIntOrNull()
                                if (dpSize != null) {
                                    modifier = modifier.height(dpSize.dp)
                                }
                            }
                        }
                    }

                    Column(
                        modifier = modifier
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
                val dismissOnOutsideClick = state.stateMap["sdui_dialog_dismissOnOutsideClick"]?.jsonPrimitive?.contentOrNull != "false"
                val dismissOnBackPress = state.stateMap["sdui_dialog_dismissOnBackPress"]?.jsonPrimitive?.contentOrNull != "false"

                AlertDialog(
                    onDismissRequest = {
                        if (dismissOnOutsideClick) {
                            state.update("sdui_dialog_visible", "false")
                        }
                    },
                    properties = DialogProperties(
                        dismissOnClickOutside = dismissOnOutsideClick,
                        dismissOnBackPress = dismissOnBackPress
                    ),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerDrivenUiHandler.FireUI(
    screenKey: String,
    onEvent: (ServerDrivenEvent) -> Unit = {},
    modifier: Modifier = Modifier,
    onError: ((String) -> Unit)? = null,
    fallbackContent: @Composable (() -> Unit)? = null,
    content: @Composable FireUIScope.() -> Unit
) {
    val context = LocalContext.current
    setActiveScreen(screenKey)

    // Retrieve flows
    val uiFlow = getUiFlow(screenKey)
    val dataFlow = getDataFlow(screenKey)

    val uiJsonString by uiFlow.collectAsState()
    val dataJsonString by dataFlow.collectAsState()

    // Fetch Remote Config internally
    val currentVariant = remember(screenKey) { mutableStateOf<String?>(null) }
    LaunchedEffect(screenKey) {
        val defaultUi = defaultUiJsons[screenKey] ?: ""
        SduiRemoteConfig.Builder(context)
            .screenKey(screenKey)
            .defaultJson(defaultUi)
            .fetchIntervalSeconds(fetchIntervalSeconds)
            .cohortContext(cohortContext)
            .onUpdate { fetchedJson ->
                if (fetchedJson.isNotBlank()) {
                    loadUI(screenKey, fetchedJson)
                }
            }
            .onVariantResolved { _, variant ->
                currentVariant.value = variant
            }
            .build()
            .fetchWithVariantResolution(variantResolverUrl)
    }

    val wrappedOnEvent: (ServerDrivenEvent) -> Unit = { event ->
        val enrichedEvent = when (event) {
            is ServerDrivenEvent.ButtonClicked -> event.copy(screenKey = screenKey, variant = currentVariant.value)
            is ServerDrivenEvent.ItemClicked -> event.copy(screenKey = screenKey, variant = currentVariant.value)
            is ServerDrivenEvent.ChipSelected -> event.copy(screenKey = screenKey, variant = currentVariant.value)
            is ServerDrivenEvent.NavigationRequested -> event.copy(screenKey = screenKey, variant = currentVariant.value)
            is ServerDrivenEvent.StateUpdateRequested -> event.copy(screenKey = screenKey, variant = currentVariant.value)
            is ServerDrivenEvent.ScreenRendered -> event.copy(screenKey = screenKey, variant = currentVariant.value)
        }
        onEvent(enrichedEvent)
    }

    val configuration = LocalConfiguration.current
    val windowSizeClass = remember(configuration.screenWidthDp) {
        when {
            configuration.screenWidthDp < 600 -> FireUiWindowSizeClass.COMPACT
            configuration.screenWidthDp < 840 -> FireUiWindowSizeClass.MEDIUM
            else -> FireUiWindowSizeClass.EXPANDED
        }
    }
    
    val isSystemDark = isSystemInDarkTheme()
    val theme = remember(ServerDrivenUiHandler.currentTheme.value, isSystemDark) {
        when (ServerDrivenUiHandler.currentTheme.value) {
            com.praptechie.serverdrivenuicompose.data_models.FireUiTheme.SYSTEM -> if (isSystemDark) com.praptechie.serverdrivenuicompose.data_models.FireUiTheme.DARK else com.praptechie.serverdrivenuicompose.data_models.FireUiTheme.LIGHT
            else -> ServerDrivenUiHandler.currentTheme.value
        }
    }

    // Imperative overlay states
    val showBottomSheet = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }
    val activeSlot = remember { mutableStateOf<String?>(null) }
    
    val scope = remember(showBottomSheet, showDialog, activeSlot) {
        FireUIScope(showBottomSheet, showDialog, activeSlot)
    }

    CompositionLocalProvider(
        LocalFireUiTheme provides theme,
        LocalFireUiWindowSize provides windowSizeClass,
        LocalFireUIScope provides scope
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            // Render user's screen first
            scope.content()

            // Fetch and render the server-driven UI on top
            MainScreen(
                uiJsonString = uiJsonString,
                dataJsonString = dataJsonString,
                modifier = Modifier.fillMaxSize(),
                fallbackContent = fallbackContent,
                viewModel = viewModel(key = screenKey),
                screenKey = screenKey,
                onEvent = wrappedOnEvent,
                onError = onError
            )

            // Render Imperative Overlays (triggered via FireUIScope)
            if (showBottomSheet.value) {
                val slotKey = activeSlot.value
                val slotContent = slotKey?.let { ServerDrivenUiHandler.getSlotContent(it) }

                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet.value = false
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        if (slotContent != null) {
                            slotContent(emptyMap())
                        } else {
                            Text("No content registered for slot: $slotKey")
                        }
                    }
                }
            }

            if (showDialog.value) {
                val slotKey = activeSlot.value
                val slotContent = slotKey?.let { ServerDrivenUiHandler.getSlotContent(it) }

                AlertDialog(
                    onDismissRequest = {
                        showDialog.value = false
                    },
                    confirmButton = {
                        TextButton(onClick = { showDialog.value = false }) {
                            Text("Close")
                        }
                    },
                    text = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            if (slotContent != null) {
                                slotContent(emptyMap())
                            } else {
                                Text("No content registered for slot: $slotKey")
                            }
                        }
                    }
                )
            }
        }
    }
}