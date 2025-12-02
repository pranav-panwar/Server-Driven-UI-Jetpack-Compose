package com.praptechie.serverdrivenuicompose.ui_elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toHorizontalAlignment
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toHorizontalArrangement
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toModifier
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
    val modifier = if (component.itemSize != null) {
        component.itemSize.toModifier()
    } else {
        Modifier.fillMaxWidth()
    }
    // Use your custom staggered grid composable
    LazyVerticalStaggeredGrid(
        horizontalArrangement = if(((component.horizontalSpacing?:0)>0))Arrangement.spacedBy(space=(component.horizontalSpacing?:8).dp, alignment = component.style?.columnStyle?.horizontalAlignment.toHorizontalAlignment()) else component.style?.columnStyle?.horizontalAlignment.toHorizontalArrangement(),
        verticalItemSpacing = (component.verticalSpacing?:0).dp,
        modifier = modifier
            .then(component.style?.modifier?.toModifier() ?: Modifier),
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
