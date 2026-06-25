package com.praptechie.serverdrivenuicompose.data_models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

enum class FireUiTheme { LIGHT, DARK, SYSTEM }

@Serializable
data class ResponsiveValue<T>(
    val compact: T? = null,
    val medium: T? = null,
    val expanded: T? = null
)

@Serializable(with = ColorValueSerializer::class)
data class ColorValue(
    val light: String,
    val dark: String? = null
) {
    fun resolve(isDark: Boolean): String {
        return if (isDark && dark != null) dark else light
    }
}

object ColorValueSerializer : KSerializer<ColorValue> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ColorValue", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ColorValue) {
        // Not used for SDUI rendering
    }

    override fun deserialize(decoder: Decoder): ColorValue {
        val jsonDecoder = decoder as? JsonDecoder ?: throw IllegalStateException("Only JSON is supported")
        val element = jsonDecoder.decodeJsonElement()
        
        return if (element is JsonPrimitive && element.isString) {
            ColorValue(light = element.content)
        } else if (element is JsonObject) {
            val light = element["light"]?.jsonPrimitive?.content ?: "#000000"
            val dark = element["dark"]?.jsonPrimitive?.content
            ColorValue(light = light, dark = dark)
        } else {
            ColorValue(light = "#000000") // Fallback
        }
    }
}
