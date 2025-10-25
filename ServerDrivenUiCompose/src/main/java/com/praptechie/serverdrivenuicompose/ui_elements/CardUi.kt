package com.praptechie.serverdrivenuicompose.ui_elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.praptechie.serverdrivenuicompose.data_models.Action
import com.praptechie.serverdrivenuicompose.data_models.CardComponent
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.handleAction
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toModifier
import kotlinx.serialization.json.JsonObject

@Composable
internal fun RenderCard(
    component: CardComponent,
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    val clickAction = component.style?.modifier?.onClick?.action ?: component.action
    val paddingValues = component.style?.cardStyle?.cardPadding?.let{
        PaddingValues(  top = (it.top ?: it.all ?: 0).dp,
            bottom = (it.bottom ?: it.all ?: 0).dp,
            start = (it.left ?: it.all ?: 0).dp,
            end = (it.right ?: it.all ?: 0).dp)
    }?: PaddingValues(0.dp)

    Card(
        modifier = Modifier
            .then(component.margin?.toModifier() ?: Modifier)  // ← USE MARGIN
            .then(component.style?.modifier?.toModifier() ?: Modifier)
            .then(if (clickAction != null) {
                Modifier.clickable {
                    handleAction(clickAction as Action, onEvent, dataJson, state)
                }
            } else Modifier),
        shape = component.style?.cardStyle?.cardShape?.let {
            RoundedCornerShape(it.dp)
        } ?: RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor =Color( android.graphics.Color.parseColor(component?.style?.cardStyle?.cardContainerColor?:"#fff000")))
    ) {
        Column(modifier = Modifier.padding(paddingValues)){
            component.children.forEach { child ->
                RenderComponent(child, dataJson, state, onEvent)
            }
        }
    }
}