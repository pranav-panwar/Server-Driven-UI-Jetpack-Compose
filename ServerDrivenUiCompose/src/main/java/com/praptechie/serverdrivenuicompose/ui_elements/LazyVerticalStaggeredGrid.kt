package com.praptechie.serverdrivenuicompose.ui_elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.praptechie.serverdrivenuicompose.data_models.LazyVerticalStaggeredGridComponent
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import com.praptechie.serverdrivenuicompose.handler_processors.TemplateProcessor
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject


@Composable
internal fun RenderLazyVerticalStaggeredGrid(
    component: LazyVerticalStaggeredGridComponent, // your custom data class
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    if(component.dataBinding==null)
        return

    val items = TemplateProcessor.resolveDataSource(
        component.dataBinding.removePrefix("@"),
        dataJson,
        state.stateMap
    )
    // Use your custom staggered grid composable
    LazyVerticalStaggeredGrid(
        horizontalArrangement = Arrangement.spacedBy((component.horizontalSpacing?:0).dp),
        verticalItemSpacing = (component.verticalSpacing?:0).dp,
        modifier = Modifier.fillMaxSize(),
        columns = StaggeredGridCells.Fixed(component.columns?:2),
        content = {
            itemsIndexed(items) { index, item ->
                val itemContext = buildJsonObject {
                    put("item", item)
                    put("index", JsonPrimitive(index))
                }
                RenderComponent(component.itemTemplate, itemContext, state, onEvent)
            }
        }
    )
}
