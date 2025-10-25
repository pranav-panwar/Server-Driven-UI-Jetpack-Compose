package com.praptechie.serverdrivenuicompose.handler_processors

import androidx.compose.runtime.mutableStateMapOf
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlin.collections.set


internal class ServerDrivenState {
    private val _stateMap = mutableStateMapOf<String, JsonElement>()
    val stateMap: Map<String, JsonElement> get() = _stateMap

    fun update(key: String, value: JsonElement) {
        _stateMap[key] = value
    }

    fun update(key: String, value: String) {
        _stateMap[key] = JsonPrimitive(value)
    }

    fun update(key: String, value: Int) {
        _stateMap[key] = JsonPrimitive(value)
    }

    fun get(key: String): JsonElement? = _stateMap[key]
}

