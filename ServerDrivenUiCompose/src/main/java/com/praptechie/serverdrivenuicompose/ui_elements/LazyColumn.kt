package com.praptechie.serverdrivenuicompose.ui_elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.praptechie.serverdrivenuicompose.data_models.LazyColumnComponent
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import com.praptechie.serverdrivenuicompose.handler_processors.TemplateProcessor
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject


// LazyColumn
@Composable
internal fun RenderLazyColumn(
    component: LazyColumnComponent,
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    val items = TemplateProcessor.resolveDataSource(
        component.dataBinding.removePrefix("@"),
        dataJson,
        state.stateMap
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy((component.spacing?:8).dp)
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
