package com.praptechie.serverdrivenuicompose.ui_elements

import androidx.compose.runtime.Composable
import com.praptechie.serverdrivenuicompose.data_models.ButtonComponent
import com.praptechie.serverdrivenuicompose.data_models.CardComponent
import com.praptechie.serverdrivenuicompose.data_models.ChipGroupComponent
import com.praptechie.serverdrivenuicompose.data_models.ColumnComponent
import com.praptechie.serverdrivenuicompose.data_models.GridComponent
import com.praptechie.serverdrivenuicompose.data_models.IconButtonComponent
import com.praptechie.serverdrivenuicompose.data_models.ImageComponent
import com.praptechie.serverdrivenuicompose.data_models.LazyColumnComponent
import com.praptechie.serverdrivenuicompose.data_models.LazyVerticalStaggeredGridComponent
import com.praptechie.serverdrivenuicompose.data_models.RowComponent
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.data_models.TextComponent
import com.praptechie.serverdrivenuicompose.data_models.UIComponent
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import kotlinx.serialization.json.JsonObject

@Composable
 internal fun RenderComponent(
    component: UIComponent,
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    when (component) {
        is ColumnComponent -> RenderColumn(component, dataJson, state, onEvent)
        is RowComponent -> RenderRow(component, dataJson, state, onEvent)
        is TextComponent -> RenderText(component, dataJson, state, onEvent)
        is ImageComponent -> RenderImage(component, dataJson, state, onEvent)
        is ButtonComponent -> RenderButton(component, dataJson, state, onEvent)
        is CardComponent -> RenderCard(component, dataJson, state, onEvent)
        is ChipGroupComponent -> RenderChipGroup(component, dataJson, state, onEvent)
        is GridComponent -> RenderGrid(component, dataJson, state, onEvent)
        is LazyColumnComponent -> RenderLazyColumn(component, dataJson, state, onEvent)
        is IconButtonComponent -> RenderIconButton(component, dataJson, state, onEvent )
        is LazyVerticalStaggeredGridComponent -> RenderLazyVerticalStaggeredGrid(component, dataJson, state, onEvent)
    }
}