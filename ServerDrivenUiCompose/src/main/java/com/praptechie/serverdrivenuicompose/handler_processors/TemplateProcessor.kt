package com.praptechie.serverdrivenuicompose.handler_processors

import android.util.Log
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull


internal object TemplateProcessor {

    private val variableRegex = "\\{\\{([^}]+)\\}\\}".toRegex()

    /**
     * Replace {{variable}} placeholders in text with actual values from data
     */
    fun replaceVars(
        text: String,
        data: JsonObject,
        state: Map<String, JsonElement>
    ): String {
        if (text.isEmpty()) return text

        return variableRegex.replace(text) { match ->
            val path = match.groupValues[1].trim()

            Log.e("texefe", "path = $path, data keys = ${data.keys}")

            // First check state
            val stateValue = state[path]?.let { element ->
                when (element) {
                    is JsonPrimitive -> element.contentOrNull
                    else -> null
                }
            }

            if (stateValue != null) {
                stateValue
            } else {
                // Then resolve from data
                val value = resolveValue(path, data)
                Log.e("texefe", "resolved value = $value for path = $path")

                when (value) {
                    is JsonPrimitive -> value.contentOrNull ?: ""
                    JsonNull -> ""
                    else -> value.toString()
                }
            }
        }
    }

    fun resolveValue(path: String, data: JsonObject): JsonElement {
        val parts = path.split(".")
        var current: JsonElement = data

        for (part in parts) {
            Log.e("texefe", "resolving part = $part from current = $current")

            current = when (current) {
                is JsonObject -> {
                    current[part]?.also {
                        Log.e("texefe", "found $part in object")
                    } ?: JsonNull.also {
                        Log.e("texefe", "$part NOT found in object. Available keys: ${current.keys}")
                    }
                }
                is JsonArray -> {
                    part.toIntOrNull()?.let { idx ->
                        current.getOrNull(idx) ?: JsonNull
                    } ?: JsonNull
                }
                else -> {
                    Log.e("texefe", "current is not object/array: ${current.javaClass.simpleName}")
                    JsonNull
                }
            }
        }
        return current
    }


    /**
     * Resolve data source path to list of JsonObjects
     * Supports: "categories", "items", "categories.0.products"
     */
    fun resolveDataSource(
        source: String,
        data: JsonObject,
        state: Map<String, JsonElement>
    ): List<JsonObject> {
        // Remove @ prefix if present
        val cleanSource = source.removePrefix("@")

        // Handle state variables first
        val processedSource = if (cleanSource.contains("{{")) {
            replaceVars(cleanSource, data, state)
        } else {
            cleanSource
        }

        val parts = processedSource.split(".")
        var current: JsonElement = data

        for (part in parts) {
            current = when {
                current is JsonObject && current.containsKey(part) -> {
                    current[part]!!
                }

                current is JsonArray -> {
                    part.toIntOrNull()?.let { idx ->
                        current.getOrNull(idx) ?: return emptyList()
                    } ?: return emptyList()
                }

                else -> return emptyList()
            } as JsonElement
        }

        return when (current) {
            is JsonArray -> current.mapNotNull { it.jsonObjectOrNull }
            is JsonObject -> listOf(current)
            else -> emptyList()
        }
    }

    private val JsonElement.jsonObjectOrNull: JsonObject?
        get() = this as? JsonObject
}


