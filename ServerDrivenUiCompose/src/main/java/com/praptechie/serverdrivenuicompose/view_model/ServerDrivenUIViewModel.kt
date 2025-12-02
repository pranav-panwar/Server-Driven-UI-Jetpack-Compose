package com.praptechie.serverdrivenuicompose.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praptechie.serverdrivenuicompose.data_models.UIDefinition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

val ServerDrivenUILogTag = "ServerDrivenUiLog"

internal class ServerDrivenUIViewModel : ViewModel() {

    private val json = Json {
        classDiscriminator = "type"
    }

    private val _uiDefinition = MutableStateFlow<UIDefinition?>(null)
    val uiDefinition: StateFlow<UIDefinition?> = _uiDefinition.asStateFlow()

    private val _dataJson = MutableStateFlow<JsonObject?>(null)
    val dataJson: StateFlow<JsonObject?> = _dataJson.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadUI(uiJsonString: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val definition = json.decodeFromString<UIDefinition>(uiJsonString)
                _uiDefinition.value = definition

            } catch (e: Exception) {
                _error.value = "Failed to parse UI: ${e.message}"
                Log.e(ServerDrivenUILogTag, "Failed to parse UI: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

   fun loadData(dataJsonString: String) {
        viewModelScope.launch {
            try {
                val data = json.parseToJsonElement(dataJsonString).jsonObject
                _dataJson.value = data
            } catch (e: Exception) {
                _error.value = "Failed to parse data: ${e.message}"
            }
        }
    }
}