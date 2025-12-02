package com.praptechie.serverdrivenuicompose.ui_elements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.praptechie.serverdrivenuicompose.data_models.ButtonComponent
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import com.praptechie.serverdrivenuicompose.handler_processors.TemplateProcessor
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.convertToColor
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.convertToIntColor
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.handleAction
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toModifier
import kotlinx.serialization.json.JsonObject

@Composable
internal fun RenderButton(
    component: ButtonComponent,
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    val buttonStyle = component.style?.buttonStyle
    val text = if (component.dataBinding != null) {
        TemplateProcessor.replaceVars(component.dataBinding.removePrefix("@"), dataJson, state.stateMap)
    } else {
        TemplateProcessor.replaceVars(component.text, dataJson, state.stateMap)
    }
    val modifier = if (component.itemSize != null) {
        component.itemSize.toModifier()
    } else {
        Modifier.fillMaxWidth()
    }
    val buttonShape=when(buttonStyle?.buttonShape){"ROUNDED"-> RoundedCornerShape(component.style.buttonStyle.buttonRounded?:0)
        "CIRCLE" -> CircleShape
        else ->RoundedCornerShape(0)
    }

    Button(
        shape = buttonShape,
        colors = ButtonDefaults.buttonColors(containerColor =  buttonStyle?.buttonColor?.convertToIntColor()?.convertToColor()?:"#ffffff".convertToIntColor().convertToColor()),
        onClick = {
            handleAction(component.action, onEvent, dataJson, state)
        },
        modifier =modifier.then( component.style?.modifier?.toModifier() ?: Modifier)
    ) {
        Text(text = text, fontSize = buttonStyle?.buttonTextSize?.sp?:12.sp, color = buttonStyle?.buttonTextColor.convertToIntColor().convertToColor() )
    }
}