package com.praptechie.serverdrivenuicompose.ui_elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.praptechie.serverdrivenuicompose.data_models.ColumnComponent
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toHorizontalAlignment
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toModifier
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toVerticalArrangement
import kotlinx.serialization.json.JsonObject


@Composable
internal fun RenderColumn(
    component: ColumnComponent,
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(component.margin?.toModifier() ?: Modifier)  // ← USE MARGIN
            .then(component.style?.modifier?.toModifier() ?: Modifier),
        verticalArrangement = component.style?.columnStyle?.verticalArrangement.toVerticalArrangement(),
        horizontalAlignment = component.style?.columnStyle?.horizontalAlignment.toHorizontalAlignment()
    ) {
        component.children?.forEach { child ->
            RenderComponent(child, dataJson, state, onEvent)
            component.style?.columnStyle?.spaceBy?.let {
                if (it > 0) Spacer(modifier = Modifier.height(it.dp))
            }
        }
    }
}