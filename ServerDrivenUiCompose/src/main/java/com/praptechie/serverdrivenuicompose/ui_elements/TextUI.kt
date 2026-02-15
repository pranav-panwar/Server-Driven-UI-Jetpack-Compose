package com.praptechie.serverdrivenuicompose.ui_elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.data_models.TextComponent
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import com.praptechie.serverdrivenuicompose.handler_processors.TemplateProcessor
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.handleAction
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toModifier
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toTextStyle
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive


// Text
@Composable
internal fun RenderText(
    component: TextComponent,
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    val modifier = if (component.itemSize != null) {
        component.itemSize.toModifier()
    } else {
        Modifier.wrapContentWidth()
    }
    val text = when {
        component.dataBinding != null -> {
            val binding = component.dataBinding.removePrefix("@")
            val items = TemplateProcessor.resolveDataSource(binding, dataJson, state.stateMap)
            if (items.isNotEmpty()) {
                Column {
                    items.forEach { item ->
                        val textValue = item["text"]?.jsonPrimitive?.content
                            ?: item["title"]?.jsonPrimitive?.content
                            ?: ""
                        Text(
                            text = textValue,
                            style = component.style?.textStyle?.toTextStyle() ?: LocalTextStyle.current,
                            modifier = Modifier
                                .then(component.style?.modifier?.toModifier() ?: Modifier)
                                .then(if (component.action != null) {
                                    Modifier.clickable {
                                        handleAction(component.action, onEvent, dataJson, state)
                                    }
                                } else Modifier)
                        )
                    }
                }
                return
            } else {
                TemplateProcessor.replaceVars(binding, dataJson, state.stateMap)
            }
        }
        component.content.isNotEmpty() -> {
            TemplateProcessor.replaceVars(component.content[0].text, dataJson, state.stateMap)
        }
        else -> ""
    }

    Text(
        text = text,
        style = component.style?.textStyle?.toTextStyle() ?: LocalTextStyle.current,
        modifier = modifier
            .then(component.style?.modifier?.toModifier() ?: Modifier)
            .then(if (component.action != null) {
                Modifier.clickable {
                    handleAction(component.action, onEvent, dataJson, state)
                }
            } else Modifier)
    )
}
