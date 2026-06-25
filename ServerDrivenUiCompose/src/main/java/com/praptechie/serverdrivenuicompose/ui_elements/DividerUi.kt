package com.praptechie.serverdrivenuicompose.ui_elements

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.praptechie.serverdrivenuicompose.data_models.DividerComponent
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import kotlinx.serialization.json.JsonObject
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.convertToColor

@Composable
internal fun RenderDivider(
    component: DividerComponent,
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    val dividerColor = component.color.convertToColor()
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 4.dp),
        thickness = component.thickness.dp,
        color = if (dividerColor == Color.Transparent) Color.LightGray else dividerColor
    )
}
