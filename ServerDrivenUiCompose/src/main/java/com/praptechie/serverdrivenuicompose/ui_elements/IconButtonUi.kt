package com.praptechie.serverdrivenuicompose.ui_elements

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.praptechie.serverdrivenuicompose.data_models.IconButtonComponent
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.handler_processors.IconResolver
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import com.praptechie.serverdrivenuicompose.handler_processors.TemplateProcessor
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.handleAction
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toModifier
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlin.collections.plus

@Composable
internal fun RenderIconButton(
    component: IconButtonComponent,
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    val clickAction = component.style?.modifier?.onClick?.action ?: component.action
    val style = component.style?.iconButtonStyle

    var isToggled by remember { mutableStateOf(false) }

    // Handle data changes dynamically
    LaunchedEffect(dataJson, state.stateMap) {
        isToggled = when {
            component.stateKey != null -> {
                (state.get(component.stateKey) as? JsonPrimitive)?.booleanOrNull ?: false
            }
            style?.toggleStateFrom != null -> {
                val path = style.toggleStateFrom.removePrefix("@")
                val resolved = TemplateProcessor.resolveValue(path, dataJson)
                Log.e("asdfasda","path = $path, res = $resolved")
                (resolved as? JsonPrimitive)?.booleanOrNull ?: false
            }
            else -> false
        }
    }

    val iconName = if (isToggled && style?.toggledIconName != null)
        style.toggledIconName else style?.iconName ?: "favorite_border"
    val icon = IconResolver.getIcon(iconName)

    val iconColor = try {
        if (isToggled && style?.toggledTint != null)
            Color(android.graphics.Color.parseColor(style.toggledTint))
        else if (style?.tint != null)
            Color(android.graphics.Color.parseColor(style.tint))
        else Color.Black
    } catch (e: Exception) {
        Color.Black
    }

    IconButton(
        onClick = {
            // Toggle state immediately
            isToggled = !isToggled
            if (component.stateKey != null) {
                state.update(component.stateKey, JsonPrimitive(isToggled))
            }

            // Provide event for parent or repository logic (e.g., toggleLike)
            if (clickAction != null) {
                val enhancedAction = clickAction.copy(
                    parameters = clickAction.parameters + mapOf(
                        "isToggled" to isToggled.toString(),
                        "newState" to isToggled.toString()
                    )
                )
                handleAction(enhancedAction, onEvent, dataJson, state)
            }

            onEvent(ServerDrivenEvent.StateUpdateRequested(component.stateKey ?: "", isToggled.toString()))
        },
        modifier = Modifier
            .then(Modifier.size((component.size?:0).dp))
            .then(component.style?.modifier?.toModifier() ?: Modifier)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = iconName,
            tint = iconColor,
            modifier = Modifier.size(component.size.dp)
        )
    }
}