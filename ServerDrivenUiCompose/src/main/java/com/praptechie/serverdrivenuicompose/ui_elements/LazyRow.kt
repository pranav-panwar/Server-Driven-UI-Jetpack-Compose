package com.praptechie.serverdrivenuicompose.ui_elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.praptechie.serverdrivenuicompose.data_models.LazyRowComponent
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import com.praptechie.serverdrivenuicompose.handler_processors.TemplateProcessor
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toHorizontalAlignment
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toHorizontalArrangement
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toModifier
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toVerticalAlignment
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

// LazyColumn
@Composable
internal fun RenderLazyRow(
    component: LazyRowComponent,
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    val items = TemplateProcessor.resolveDataSource(
        component.dataBinding.removePrefix("@"),
        dataJson,
        state.stateMap
    )
    val modifier = if (component.itemSize != null) {
        component.itemSize.toModifier()
    } else {
        Modifier.fillMaxWidth()
    }

    LazyRow (
        modifier = modifier.then(component.style?.modifier.toModifier()),
        horizontalArrangement = if(((component.spacing?:0)>0))Arrangement.spacedBy(space=(component.spacing?:8).dp, alignment = component.style?.columnStyle?.horizontalAlignment.toHorizontalAlignment()) else component.style?.columnStyle?.horizontalAlignment.toHorizontalArrangement(),
        verticalAlignment = component.style?.columnStyle?.verticalArrangement.toVerticalAlignment()
    ) {
        itemsIndexed(items) { index, item ->
            val itemContext = buildJsonObject {
                put("item", item)
                put("index", JsonPrimitive(index))
            }

            RenderComponent(component.itemTemplate, itemContext, state, onEvent)
        }
    }
}
