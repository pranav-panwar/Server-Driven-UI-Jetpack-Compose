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

@Composable
internal fun RenderDivider(
    component: DividerComponent,
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 4.dp),
        thickness = component.thickness.dp,
        color = try { Color(android.graphics.Color.parseColor(component.color)) } catch (e: Exception) { Color.LightGray }
    )
}
