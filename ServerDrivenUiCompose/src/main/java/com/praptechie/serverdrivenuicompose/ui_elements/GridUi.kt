package com.praptechie.serverdrivenuicompose.ui_elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.praptechie.serverdrivenuicompose.data_models.GridComponent
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import com.praptechie.serverdrivenuicompose.handler_processors.TemplateProcessor
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toModifier
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
    val modifier = if (component.itemSize != null) {
        component.itemSize.toModifier()
    } else {
        Modifier.fillMaxWidth()
    }
    val windowSize = com.praptechie.serverdrivenuicompose.LocalFireUiWindowSize.current
    var resolvedColumns = component.columns ?: 2
    component.responsiveColumns?.let {
        when (windowSize) {
            com.praptechie.serverdrivenuicompose.FireUiWindowSizeClass.COMPACT -> it.compact?.let { c -> resolvedColumns = c }
            com.praptechie.serverdrivenuicompose.FireUiWindowSizeClass.MEDIUM -> it.medium?.let { c -> resolvedColumns = c }
            com.praptechie.serverdrivenuicompose.FireUiWindowSizeClass.EXPANDED -> it.expanded?.let { c -> resolvedColumns = c }
        }
    }

    LazyVerticalGrid(
        modifier=modifier.then(component.style?.modifier.toModifier()),
        columns = GridCells.Fixed(resolvedColumns),
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