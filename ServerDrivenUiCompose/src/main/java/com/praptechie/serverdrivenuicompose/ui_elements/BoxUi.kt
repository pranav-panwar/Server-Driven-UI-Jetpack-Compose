package com.praptechie.serverdrivenuicompose.ui_elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.praptechie.serverdrivenuicompose.data_models.BoxComponent
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.boxContentAlignment
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.handleAction
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toModifier
import kotlinx.serialization.json.JsonObject

@Composable
internal fun RenderBox(
    component: BoxComponent,
    dataJson: JsonObject, state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {

    val modifier = if (component.itemSize != null) {
        component.itemSize.toModifier()
    } else {
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    }

    Box(
        modifier = modifier
            .then(component.style?.modifier?.toModifier() ?: Modifier)
            .then(
                if (component.action != null) {
                    Modifier.clickable {
                        handleAction(component.action, onEvent, dataJson, state)
                    }
                } else Modifier),
        contentAlignment = component.style?.boxContentAlignment.boxContentAlignment()
    ) {
        RenderComponent(component.itemTemplate, dataJson, state, onEvent)
    }

}