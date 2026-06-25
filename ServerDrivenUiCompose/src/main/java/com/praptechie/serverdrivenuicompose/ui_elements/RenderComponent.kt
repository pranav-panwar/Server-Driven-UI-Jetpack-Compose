package com.praptechie.serverdrivenuicompose.ui_elements

import androidx.compose.runtime.Composable
import com.praptechie.serverdrivenuicompose.data_models.BottomBarComponent
import com.praptechie.serverdrivenuicompose.data_models.BoxComponent
import com.praptechie.serverdrivenuicompose.data_models.ButtonComponent
import com.praptechie.serverdrivenuicompose.data_models.CardComponent
import com.praptechie.serverdrivenuicompose.data_models.ChipGroupComponent
import com.praptechie.serverdrivenuicompose.data_models.ColumnComponent
import com.praptechie.serverdrivenuicompose.data_models.DividerComponent
import com.praptechie.serverdrivenuicompose.data_models.GridComponent
import com.praptechie.serverdrivenuicompose.data_models.IconButtonComponent
import com.praptechie.serverdrivenuicompose.data_models.ImageComponent
import com.praptechie.serverdrivenuicompose.data_models.LazyColumnComponent
import com.praptechie.serverdrivenuicompose.data_models.LazyRowComponent
import com.praptechie.serverdrivenuicompose.data_models.LazyVerticalStaggeredGridComponent
import com.praptechie.serverdrivenuicompose.data_models.RowComponent
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.data_models.SpacerComponent
import com.praptechie.serverdrivenuicompose.data_models.TextComponent
import com.praptechie.serverdrivenuicompose.data_models.UIComponent
import com.praptechie.serverdrivenuicompose.data_models.TextInputComponent
import com.praptechie.serverdrivenuicompose.data_models.ConditionalComponent
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import kotlinx.serialization.json.JsonObject

@Composable
 internal fun RenderComponent(
    component: UIComponent,
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    val windowSize = com.praptechie.serverdrivenuicompose.LocalFireUiWindowSize.current
    val windowSizeStr = when (windowSize) {
        com.praptechie.serverdrivenuicompose.FireUiWindowSizeClass.COMPACT -> "compact"
        com.praptechie.serverdrivenuicompose.FireUiWindowSizeClass.MEDIUM -> "medium"
        com.praptechie.serverdrivenuicompose.FireUiWindowSizeClass.EXPANDED -> "expanded"
    }

    if (component.visibleOn != null && !component.visibleOn!!.contains(windowSizeStr)) {
        return // Do not render on this breakpoint
    }
    val slotName = component.slot
    if (slotName != null) {
        val slotContent = com.praptechie.serverdrivenuicompose.ServerDrivenUiHandler.getSlotContent(slotName)
        if (slotContent != null) {
            val context = mapOf<String, Any?>(
                "dataJson" to dataJson,
                "stateMap" to state.stateMap,
                "component" to component
            )
            slotContent(context)
        } else {
            // "render nothing in that slot's position (collapse to zero size) rather than crashing"
            // we could report error, but we don't have direct access to onError here.
        }
        return
    }

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
        is BoxComponent -> RenderBox(component, dataJson, state, onEvent)
        is LazyRowComponent -> RenderLazyRow(component, dataJson, state, onEvent)
        is BottomBarComponent -> RenderBottomBarUi(component, dataJson, state, onEvent)
        is SpacerComponent -> RenderSpacer(component, dataJson, state, onEvent)
        is DividerComponent -> RenderDivider(component, dataJson, state, onEvent)
        is TextInputComponent -> RenderTextInput(component, dataJson, state, onEvent)
        is ConditionalComponent -> RenderConditional(component, dataJson, state, onEvent)
    }
}

@Composable
internal fun RenderTextInput(
    component: TextInputComponent,
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    // Stub renderer
}

@Composable
internal fun RenderConditional(
    component: ConditionalComponent,
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    // Stub renderer
}