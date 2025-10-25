package com.praptechie.serverdrivenuicompose.handler_processors

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put


internal object DataConverter {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Convert any data type to JsonObject
     */
    fun toJsonObject(data: Any): JsonObject {
        return when (data) {
            is String -> {
                try {
                    json.parseToJsonElement(data).jsonObject
                } catch (e: Exception) {
                    buildJsonObject {
                        put("value", data)
                    }
                }
            }
            is Map<*, *> -> mapToJsonObject(data)
            is List<*> -> buildJsonObject {
                put("items", listToJsonArray(data))
            }
            is JsonObject -> data
            else -> {
                try {
                    json.parseToJsonElement(json.encodeToString(kotlinx.serialization.serializer(), data)).jsonObject
                } catch (e: Exception) {
                    buildJsonObject { }
                }
            }
        }
    }

    private fun mapToJsonObject(map: Map<*, *>): JsonObject {
        return buildJsonObject {
            map.forEach { (key, value) ->
                val keyStr = key.toString()
                when (value) {
                    is String -> put(keyStr, value)
                    is Number -> put(keyStr, JsonPrimitive(value))
                    is Boolean -> put(keyStr, value)
                    is List<*> -> put(keyStr, listToJsonArray(value))
                    is Map<*, *> -> put(keyStr, mapToJsonObject(value))
                    null -> put(keyStr, JsonNull)
                    else -> {
                        try {
                            put(keyStr, json.parseToJsonElement(value.toString()))
                        } catch (e: Exception) {
                            put(keyStr, value.toString())
                        }
                    }
                }
            }
        }
    }

    private fun listToJsonArray(list: List<*>): JsonArray {
        return buildJsonArray {
            list.forEach { item ->
                when (item) {
                    is String -> add(buildJsonObject { put("text", item) })
                    is Number -> add(JsonPrimitive(item))
                    is Boolean -> add(item)
                    is Map<*, *> -> add(mapToJsonObject(item))
                    null -> add(JsonNull)
                    else -> {
                        try {
                            add(json.parseToJsonElement(item.toString()))
                        } catch (e: Exception) {
                            add(buildJsonObject { put("text", item.toString()) })
                        }
                    }
                }
            }
        }
    }
}