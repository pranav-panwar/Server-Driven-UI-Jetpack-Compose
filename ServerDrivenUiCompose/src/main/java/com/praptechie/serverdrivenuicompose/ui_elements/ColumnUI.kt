package com.praptechie.serverdrivenuicompose.ui_elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.praptechie.serverdrivenuicompose.data_models.ColumnComponent
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toHorizontalAlignment
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toModifier
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toVerticalAlignment
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toVerticalArrangement
import kotlinx.serialization.json.JsonObject


@Composable
internal fun RenderColumn(
    component: ColumnComponent,
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    val modifier = if (component.itemSize != null) {
        component.itemSize.toModifier()
    } else {
        Modifier.fillMaxWidth()
    }

val scrollModifier = if(component.style?.columnStyle?.enableScroll!=true) Modifier else {
    val scrollState= rememberScrollState()
    Modifier.verticalScroll(scrollState)
}
    Column(
        modifier = modifier
            .then(component.style?.modifier?.toModifier() ?: Modifier)
            .then(scrollModifier),
        verticalArrangement = if(((component.spacing?:0)>0))Arrangement.spacedBy(space=(component.spacing?:8).dp, alignment = component.style?.columnStyle?.verticalArrangement.toVerticalAlignment()) else component.style?.columnStyle?.verticalArrangement.toVerticalArrangement(),
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