package com.praptechie.serverdrivenuicompose.ui_elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.praptechie.serverdrivenuicompose.data_models.BottomBarComponent
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.handler_processors.IconResolver
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.convertToColor
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.convertToIntColor
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.handleAction
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toModifier
import kotlinx.serialization.json.JsonObject

@Composable
internal fun RenderBottomBarUi(
    component: BottomBarComponent,
    dataJson: JsonObject, state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    val modifier = if (component.itemSize != null) {
        component.itemSize.toModifier()
    } else {
        Modifier
            .fillMaxWidth()
            .height(60.dp)
    }
    val style = component.style?.bottomBarStyle

    Row(
        modifier = modifier
            .then(component.style?.modifier?.toModifier() ?: Modifier)) {

        component.bottomBarItems?.forEach {bbItem->
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f) .then(
                        if (bbItem.action != null) {
                            Modifier.clickable {
                                handleAction(bbItem.action, onEvent, dataJson, state)
                            }
                        } else Modifier),
                verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                bbItem.iconName?.let{name->
                    Icon(
                        imageVector = IconResolver.getIcon(name),
                        "bottom bar icon",
                        modifier = Modifier.size(25.dp),
                        tint = style?.iconColor?.convertToIntColor()?.convertToColor()
                            ?: "#ffffff".convertToIntColor().convertToColor()
                    )
                }
                bbItem.text?.let{text->
                    Text(
                        text = text,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp,
                        color = style?.textColor?.convertToIntColor()?.convertToColor()
                            ?: "#ffffff".convertToIntColor().convertToColor()
                    )
                }

            }
        }

    }
}