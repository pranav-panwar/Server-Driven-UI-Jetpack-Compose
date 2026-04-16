package com.praptechie.serverdrivenuicompose.ui_elements

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.data_models.SpacerComponent
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import kotlinx.serialization.json.JsonObject

@Composable
internal fun RenderSpacer(
    component: SpacerComponent,
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    Spacer(modifier = Modifier.height(component.size.dp).width(component.size.dp))
}
