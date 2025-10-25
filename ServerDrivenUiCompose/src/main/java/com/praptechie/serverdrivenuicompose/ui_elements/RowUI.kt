package com.praptechie.serverdrivenuicompose.ui_elements

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.praptechie.serverdrivenuicompose.data_models.RowComponent
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toHorizontalArrangement
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toModifier
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toVerticalAlignment
import kotlinx.serialization.json.JsonObject

// Row
@Composable
internal fun RenderRow(
    component: RowComponent,
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    val modifier = if (component.itemSize != null) {
        Modifier
            .width(component.itemSize.width?.dp ?: 100.dp)
            .height(component.itemSize.height?.dp ?: 100.dp)  // ← USE itemSize
    } else {
        Modifier.fillMaxWidth()
    }
    Row(
        modifier = modifier

            .then(component.style?.modifier?.toModifier() ?: Modifier),
        horizontalArrangement = component.style?.rowStyle?.horizontalArrangement.toHorizontalArrangement(),
        verticalAlignment = component.style?.rowStyle?.verticalAlignment.toVerticalAlignment()
    ) {
        component.children.forEach { child ->
            RenderComponent(child, dataJson, state, onEvent)
            component.style?.rowStyle?.spaceBy?.let {
                if(it>0) Spacer(modifier = Modifier.width(it.dp))
            }
        }
    }
}