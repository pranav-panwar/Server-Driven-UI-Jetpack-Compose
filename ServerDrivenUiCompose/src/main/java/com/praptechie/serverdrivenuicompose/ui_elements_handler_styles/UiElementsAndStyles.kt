package com.praptechie.serverdrivenuicompose.ui_elements_handler_styles

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.praptechie.serverdrivenuicompose.data_models.Action
import com.praptechie.serverdrivenuicompose.data_models.ItemSize
import com.praptechie.serverdrivenuicompose.data_models.Margin
import com.praptechie.serverdrivenuicompose.data_models.ModifierStyle
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.data_models.TextStyle
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import com.praptechie.serverdrivenuicompose.handler_processors.TemplateProcessor
import kotlinx.serialization.json.JsonObject


// Action Handler
 internal fun handleAction(
    action: Action,
    onEvent: (ServerDrivenEvent) -> Unit,
    dataJson: JsonObject,
    state: ServerDrivenState
) {
    val processedParams = action.parameters.mapValues { (_, value) ->
        TemplateProcessor.replaceVars(value, dataJson, state.stateMap)
    }

    when (action.perform) {
        "navigate" -> {
            val screen = processedParams["screen"] ?: ""
            onEvent(ServerDrivenEvent.NavigationRequested(screen, processedParams))
        }
        "update_state" -> {
            val key = processedParams["key"] ?: return
            val value = processedParams["value"] ?: return
            state.update(key, value)
            onEvent(ServerDrivenEvent.StateUpdateRequested(key, value))
        }
        "button_click" -> {
            onEvent(ServerDrivenEvent.ButtonClicked(action.perform, processedParams))
        }
        else -> {
            onEvent(ServerDrivenEvent.ButtonClicked(action.perform, processedParams))
        }
    }
}
// Add these extension functions to your file

 internal fun ModifierStyle?.toModifier(): Modifier {
    if (this == null) return Modifier

    var modifier : Modifier= Modifier

    // Apply padding
    this.padding?.let {
        modifier = modifier.padding(
            top = (it.top ?: it.all ?: 0).dp,
            bottom = (it.bottom ?: it.all ?: 0).dp,
            start = (it.left ?: it.all ?: 0).dp,
            end = (it.right ?: it.all ?: 0).dp
        )
    }

    // Apply background color
    this.backgroundColor?.let {
        try {
            modifier = modifier.background(Color(android.graphics.Color.parseColor(it)))
        } catch (e: Exception) {
            Log.e("ServerDrivenUILogTag", "ModifierStyle Exception - Invalid color: $it")
        }
    }

    // Apply clip shape
    this.clip?.let {
        if (it.shape == "rounded") {
            modifier = modifier.clip(RoundedCornerShape(it.radius.dp))
        }
        else if(it.shape=="circle")
            modifier = modifier.clip(CircleShape)
    }

    return modifier
}




internal fun Margin?.toModifier(): Modifier {
    if (this == null) return Modifier
    return Modifier.padding(
        top = (this.top ?: this.all ?: 0).dp,
        bottom = (this.bottom ?: this.all ?: 0).dp,
        start = (this.left ?: this.all ?: 0).dp,
        end = (this.right ?: this.all ?: 0).dp
    )
}

internal fun ItemSize?.toModifier(): Modifier {
    if (this == null) return Modifier
    var modifier: Modifier = Modifier

    if (this.widthPercent != null && this.widthPercent > 0.0f) {
        modifier = modifier.fillMaxWidth(this.widthPercent)
    } else if (this.width != null) {
        modifier = modifier.width(this.width.dp)
    }

    if (this.heightPercent != null && this.heightPercent > 0.0f) {
        modifier = modifier.fillMaxHeight(this.heightPercent)
    } else if (this.height != null) {
        modifier = modifier.height(this.height.dp)
    }
    return modifier
}


internal fun TextStyle.toTextStyle(): androidx.compose.ui.text.TextStyle {
    return androidx.compose.ui.text.TextStyle(
        fontSize = (fontSize?:16).sp,
        color = Color(android.graphics.Color.parseColor(textColor)),
        fontWeight = when (fontWeight?.lowercase()) {
            "bold" -> FontWeight.Bold
            "medium" -> FontWeight.Medium
            "light" -> FontWeight.Light
            else -> FontWeight.Normal
        }
    )
}

internal fun String?.toVerticalArrangement(): Arrangement.Vertical {
    return when (this?.lowercase()) {
        "center" -> Arrangement.Center
        "bottom" -> Arrangement.Bottom
        "spacebetween" -> Arrangement.SpaceBetween
        "spacearound" -> Arrangement.SpaceAround
        "spaceevenly" -> Arrangement.SpaceEvenly
        else -> Arrangement.Top
    }
}

internal fun String?.toHorizontalAlignment(): Alignment.Horizontal {
    return when (this?.lowercase()) {
        "center" -> Alignment.CenterHorizontally
        "end" -> Alignment.End
        else -> Alignment.Start
    }
}

internal fun String?.toHorizontalArrangement(): Arrangement.Horizontal {
    return when (this?.lowercase()) {
        "center" -> Arrangement.Center
        "end" -> Arrangement.End
        "spacebetween" -> Arrangement.SpaceBetween
        "spacearound" -> Arrangement.SpaceAround
        "spaceevenly" -> Arrangement.SpaceEvenly
        else -> Arrangement.Start
    }
}

internal fun String?.toVerticalAlignment(): Alignment.Vertical {
    return when (this?.lowercase()) {
        "center" -> Alignment.CenterVertically
        "bottom" -> Alignment.Bottom
        else -> Alignment.Top
    }
}

internal fun String?.convertToIntColor(): Int {
    return android.graphics.Color.parseColor(this)
}

internal fun Int.convertToColor(): Color {
    return Color(this)
}
