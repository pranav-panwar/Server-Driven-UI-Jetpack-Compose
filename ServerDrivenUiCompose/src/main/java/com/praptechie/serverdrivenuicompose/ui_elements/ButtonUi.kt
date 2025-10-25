package com.praptechie.serverdrivenuicompose.ui_elements

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.praptechie.serverdrivenuicompose.data_models.ButtonComponent
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import com.praptechie.serverdrivenuicompose.handler_processors.TemplateProcessor
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.handleAction
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toModifier
import kotlinx.serialization.json.JsonObject

@Composable
internal fun RenderButton(
    component: ButtonComponent,
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    val text = if (component.dataBinding != null) {
        TemplateProcessor.replaceVars(component.dataBinding.removePrefix("@"), dataJson, state.stateMap)
    } else {
        TemplateProcessor.replaceVars(component.text, dataJson, state.stateMap)
    }

    Button(
        onClick = {
            handleAction(component.action, onEvent, dataJson, state)
        },
        modifier = component.style?.modifier?.toModifier() ?: Modifier
    ) {
        Text(text = text)
    }
}