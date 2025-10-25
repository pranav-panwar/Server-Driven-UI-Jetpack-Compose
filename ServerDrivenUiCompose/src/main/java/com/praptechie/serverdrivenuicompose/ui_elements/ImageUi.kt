package com.praptechie.serverdrivenuicompose.ui_elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.praptechie.serverdrivenuicompose.data_models.ImageComponent
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import com.praptechie.serverdrivenuicompose.handler_processors.TemplateProcessor
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.handleAction
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toModifier
import kotlinx.serialization.json.JsonObject

@Composable
internal fun RenderImage(
    component: ImageComponent,
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    val imageUrl = when {
        component.dataBinding != null -> {
            val binding = component.dataBinding.removePrefix("@")
            TemplateProcessor.replaceVars(binding, dataJson, state.stateMap)
        }
        component.imageUrl != null -> {
            TemplateProcessor.replaceVars(component.imageUrl, dataJson, state.stateMap)
        }
        else -> ""
    }

    val modifier = if (component.itemSize != null) {
        Modifier
            .width(component.itemSize.width?.dp ?: 100.dp)
            .height(component.itemSize.height?.dp ?: 100.dp)  // ← USE itemSize
    } else {
        Modifier.fillMaxWidth()
    }

    if (imageUrl.isNotEmpty()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = modifier
                .then(component.style?.modifier?.toModifier() ?: Modifier)
                .then(if (component.action != null) {
                    Modifier.clickable {
                        handleAction(component.action, onEvent, dataJson, state)
                    }
                } else Modifier),
            contentScale = ContentScale.Crop
        )
    }
}