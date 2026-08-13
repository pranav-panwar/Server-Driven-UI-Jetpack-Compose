package com.praptechie.serverdrivenuicompose.ui_elements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.praptechie.serverdrivenuicompose.LocalFireUiTheme
import com.praptechie.serverdrivenuicompose.data_models.BottomBarComponent
import com.praptechie.serverdrivenuicompose.data_models.FireUiTheme
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.handler_processors.IconResolver
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.convertToColor
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.handleAction
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toModifier
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RenderBottomBarUi(
    component: BottomBarComponent,
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    val isDark = LocalFireUiTheme.current == FireUiTheme.DARK
    val style = component.style?.bottomBarStyle

    val barHeight = style?.height?.dp ?: 60.dp
    val containerColor = style?.backgroundColor?.convertToColor() ?: MaterialTheme.colorScheme.surface
    val selectedColor = style?.selectedColor?.convertToColor() ?: MaterialTheme.colorScheme.primary
    val unselectedColor = style?.unselectedColor?.convertToColor() ?: MaterialTheme.colorScheme.onSurfaceVariant
    val indicatorColor = style?.indicatorColor?.convertToColor() ?: MaterialTheme.colorScheme.secondaryContainer
    val elevation = style?.elevation?.dp ?: 8.dp
    val cornerRadiusVal = style?.cornerRadius?.dp ?: 0.dp
    val showLabels = style?.showLabels ?: (style?.type?.lowercase()?.trim() != "icon_only")
    val showIndicator = style?.showIndicator ?: (style?.type?.lowercase()?.trim() == "pill")
    val itemSpacing = style?.itemSpacing ?: 0f

    // Track selected index
    var localSelectedIndex by remember { mutableStateOf(0) }
    val selectedIndex = component.selectedStateKey?.let { key ->
        state.stateMap[key]?.jsonPrimitive?.contentOrNull?.toIntOrNull()
    } ?: localSelectedIndex

    // Top border helper
    val borderModifier = style?.borderColor?.convertToColor()?.let { color ->
        Modifier.drawBehind {
            drawLine(
                color = color,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 1.dp.toPx()
            )
        }
    } ?: Modifier

    val isFloating = style?.type?.lowercase()?.trim() == "floating"
    val outerModifier = if (isFloating) {
        Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(elevation, RoundedCornerShape(cornerRadiusVal))
            .background(containerColor, RoundedCornerShape(cornerRadiusVal))
            .clip(RoundedCornerShape(cornerRadiusVal))
    } else {
        Modifier
            .shadow(elevation)
            .background(containerColor)
            .then(borderModifier)
    }

    val componentModifier = if (component.itemSize != null) {
        component.itemSize.toModifier()
    } else {
        Modifier.fillMaxWidth().height(barHeight)
    }

    Surface(
        modifier = componentModifier
            .then(outerModifier)
            .then(component.style?.modifier?.toModifier() ?: Modifier),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(itemSpacing.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            component.bottomBarItems?.forEachIndexed { index, bbItem ->
                val isSelected = selectedIndex == index
                val itemColor = if (isSelected) selectedColor else unselectedColor

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable {
                            localSelectedIndex = index
                            component.selectedStateKey?.let { key ->
                                state.update(key, index.toString())
                                onEvent(ServerDrivenEvent.StateUpdateRequested(key, index.toString()))
                            }
                            bbItem.action?.let { handleAction(it, onEvent, dataJson, state) }
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val iconContent: @Composable () -> Unit = {
                        val mediaUrl = bbItem.imageUrl ?: bbItem.svgUrl ?: ""
                        if (mediaUrl.isNotEmpty()) {
                            AsyncImage(
                                model = mediaUrl,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        } else if (!bbItem.iconName.isNullOrEmpty()) {
                            Icon(
                                imageVector = IconResolver.getIcon(bbItem.iconName),
                                contentDescription = "bottom bar icon",
                                modifier = Modifier.size(24.dp),
                                tint = itemColor
                            )
                        }
                    }

                    val badgedIconContent: @Composable () -> Unit = {
                        if (!bbItem.badge.isNullOrEmpty()) {
                            BadgedBox(
                                badge = {
                                    val badgeBg = bbItem.badgeColor?.convertToColor() ?: MaterialTheme.colorScheme.error
                                    Badge(
                                        containerColor = badgeBg,
                                        contentColor = Color.White
                                    ) {
                                        Text(text = bbItem.badge, fontSize = 9.sp)
                                    }
                                }
                            ) {
                                iconContent()
                            }
                        } else {
                            iconContent()
                        }
                    }

                    if (style?.type?.lowercase()?.trim() == "pill") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (isSelected) indicatorColor else Color.Transparent)
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            badgedIconContent()
                        }
                    } else {
                        if (showIndicator && isSelected) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(indicatorColor)
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                badgedIconContent()
                            }
                        } else {
                            badgedIconContent()
                        }
                    }

                    if (showLabels && !bbItem.text.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = bbItem.text,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            fontSize = 11.sp,
                            color = itemColor,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}