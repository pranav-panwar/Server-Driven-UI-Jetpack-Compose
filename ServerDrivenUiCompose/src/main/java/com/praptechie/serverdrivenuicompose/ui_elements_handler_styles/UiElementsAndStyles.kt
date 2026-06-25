package com.praptechie.serverdrivenuicompose.ui_elements_handler_styles

import androidx.compose.runtime.Composable
import com.praptechie.serverdrivenuicompose.LocalFireUiTheme
import com.praptechie.serverdrivenuicompose.LocalFireUiWindowSize
import com.praptechie.serverdrivenuicompose.FireUiWindowSizeClass
import com.praptechie.serverdrivenuicompose.data_models.FireUiTheme
import com.praptechie.serverdrivenuicompose.data_models.ColorValue
import com.praptechie.serverdrivenuicompose.ServerDrivenUiHandler
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

    val processedAction = action.copy(parameters = processedParams)
    if (com.praptechie.serverdrivenuicompose.ServerDrivenUiHandler.executeCustomAction(processedAction, dataJson, state)) {
        return
    }

    when (action.perform) {
        "show_bottom_sheet" -> {
            val title = processedParams["title"] ?: ""
            val content = action.parameters["content"] ?: ""
            state.update("sdui_bottom_sheet_title", title)
            state.update("sdui_bottom_sheet_content", content)
            state.update("sdui_bottom_sheet_sheetSize", processedParams["sheetSize"] ?: "")
            state.update("sdui_bottom_sheet_expandable", processedParams["expandable"] ?: "true")
            state.update("sdui_bottom_sheet_collapsible", processedParams["collapsible"] ?: "true")
            state.update("sdui_bottom_sheet_dismissOnOutsideClick", processedParams["dismissOnOutsideClick"] ?: "true")
            state.update("sdui_bottom_sheet_initialState", processedParams["initialState"] ?: "")
            state.update("sdui_bottom_sheet_visible", "true")
        }
        "hide_bottom_sheet" -> {
            state.update("sdui_bottom_sheet_visible", "false")
        }
        "show_dialog" -> {
            val title = processedParams["title"] ?: ""
            val message = processedParams["message"] ?: ""
            val content = action.parameters["content"] ?: ""
            state.update("sdui_dialog_title", title)
            state.update("sdui_dialog_message", message)
            state.update("sdui_dialog_content", content)
            state.update("sdui_dialog_dismissOnOutsideClick", processedParams["dismissOnOutsideClick"] ?: "true")
            state.update("sdui_dialog_dismissOnBackPress", processedParams["dismissOnBackPress"] ?: "true")
            state.update("sdui_dialog_visible", "true")
        }
        "hide_dialog" -> {
            state.update("sdui_dialog_visible", "false")
        }
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

@Composable
internal fun ModifierStyle?.toModifier(): Modifier {
    if (this == null) return Modifier

    val isDark = LocalFireUiTheme.current == FireUiTheme.DARK
    val windowSize = LocalFireUiWindowSize.current
    
    var effectiveStyle: ModifierStyle = this
    this.responsive?.let { resp ->
        when (windowSize) {
            FireUiWindowSizeClass.COMPACT -> resp.compact?.let { effectiveStyle = it }
            FireUiWindowSizeClass.MEDIUM -> resp.medium?.let { effectiveStyle = it }
            FireUiWindowSizeClass.EXPANDED -> resp.expanded?.let { effectiveStyle = it }
        }
    }

    var modifier: Modifier = Modifier

    effectiveStyle.padding?.let {
        modifier = modifier.padding(
            top = (it.top ?: it.all ?: 0).dp,
            bottom = (it.bottom ?: it.all ?: 0).dp,
            start = (it.left ?: it.all ?: 0).dp,
            end = (it.right ?: it.all ?: 0).dp
        )
    }

    effectiveStyle.backgroundColor?.let {
        try {
            modifier = modifier.background(Color(android.graphics.Color.parseColor(it.resolve(isDark))))
        } catch (e: Exception) {
            Log.e("ServerDrivenUILogTag", "ModifierStyle Exception - Invalid color: ${it.resolve(isDark)}")
        }
    }

    effectiveStyle.clip?.let {
        if (it.shape == "rounded") {
            modifier = modifier.clip(RoundedCornerShape(it.radius.dp))
        } else if(it.shape == "circle") {
            modifier = modifier.clip(CircleShape)
        }
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


@Composable
internal fun TextStyle.toTextStyle(): androidx.compose.ui.text.TextStyle {
    val isDark = LocalFireUiTheme.current == FireUiTheme.DARK
    val colorStr = textColor?.resolve(isDark) ?: "#000000"
    val resolvedFontFamily = fontFamily?.let { ServerDrivenUiHandler.getFontFamily(it) }
    
    return androidx.compose.ui.text.TextStyle(
        fontSize = (fontSize?:16).sp,
        color = try { Color(android.graphics.Color.parseColor(colorStr)) } catch(e:Exception){ Color.Black },
        fontWeight = when (fontWeight?.lowercase()) {
            "bold" -> FontWeight.Bold
            "medium" -> FontWeight.Medium
            "light" -> FontWeight.Light
            else -> FontWeight.Normal
        },
        fontFamily = resolvedFontFamily ?: androidx.compose.ui.text.font.FontFamily.Default
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

internal fun String?.boxContentAlignment():Alignment{
    return when (this?.lowercase()){
        "center" -> Alignment.Center
        "top_end" -> Alignment.TopEnd
        "top_center" -> Alignment.TopCenter
        "top_start" -> Alignment.TopStart
        "center_end" -> Alignment.CenterEnd
        "center_start" -> Alignment.CenterStart
        "bottom_center" -> Alignment.BottomCenter
        "bottom_end" -> Alignment.BottomEnd
        "bottom_start" -> Alignment.BottomStart
        else -> Alignment.TopCenter
    }
}

internal fun String?.convertToIntColor(): Int {
    if (this.isNullOrBlank()) return android.graphics.Color.BLACK
    return try { android.graphics.Color.parseColor(this) } catch(e:Exception){ android.graphics.Color.BLACK }
}

@Composable
internal fun ColorValue?.convertToIntColor(): Int {
    if (this == null) return android.graphics.Color.BLACK
    val isDark = LocalFireUiTheme.current == FireUiTheme.DARK
    return try { android.graphics.Color.parseColor(this.resolve(isDark)) } catch(e:Exception){ android.graphics.Color.BLACK }
}

internal fun Int.convertToColor(): Color {
    return Color(this)
}

@Composable
internal fun ColorValue?.convertToColor(): Color {
    if (this == null) return Color.Transparent
    val isDark = LocalFireUiTheme.current == FireUiTheme.DARK
    return try { Color(android.graphics.Color.parseColor(this.resolve(isDark))) } catch(e:Exception){ Color.Transparent }
}
