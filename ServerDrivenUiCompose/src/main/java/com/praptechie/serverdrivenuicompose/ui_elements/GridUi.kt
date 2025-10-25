package com.praptechie.serverdrivenuicompose.ui_elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.praptechie.serverdrivenuicompose.data_models.GridComponent
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import com.praptechie.serverdrivenuicompose.handler_processors.TemplateProcessor
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject


@Composable
internal fun RenderGrid(
    component: GridComponent,
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    val items = TemplateProcessor.resolveDataSource(
        component.dataBinding.removePrefix("@"),
        dataJson,
        state.stateMap
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(component.columns?:2),
        horizontalArrangement = Arrangement.spacedBy((component.spacing?:8).dp),
        verticalArrangement = Arrangement.spacedBy((component.spacing?:8).dp)
    ) {
        itemsIndexed(items) { index, item ->
            // Create item context
            val itemContext = buildJsonObject {
                put("item", item)
                put("index", JsonPrimitive(index))
            }

            // Pass itemContext to child renderers
            RenderComponent(component.itemTemplate, itemContext, state, onEvent)
        }
    }
}