package com.praptechie.serverdrivenuicompose.ui_elements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.praptechie.serverdrivenuicompose.LocalFireUiTheme
import com.praptechie.serverdrivenuicompose.data_models.FireUiTheme
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.data_models.TopAppBarComponent
import com.praptechie.serverdrivenuicompose.handler_processors.IconResolver
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.convertToColor
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.handleAction
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toTextStyle
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toModifier
import kotlinx.serialization.json.JsonObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopAppBarUi(
    component: TopAppBarComponent,
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    val isDark = LocalFireUiTheme.current == FireUiTheme.DARK
    val style = component.style?.topAppBarStyle
    val titleText = component.title ?: ""
    val subtitleText = component.subtitle ?: ""
    val hasSubtitle = subtitleText.isNotEmpty()

    val containerColor = style?.backgroundColor?.convertToColor() ?: MaterialTheme.colorScheme.surface
    val elevation = style?.elevation?.dp ?: 0.dp
    val cornerRadius = style?.cornerRadius ?: 0f
    val isOverlay = style?.overlayContent == true

    val titleStyle = style?.titleTextStyle?.toTextStyle() ?: MaterialTheme.typography.titleLarge
    val subtitleStyle = style?.subtitleTextStyle?.toTextStyle() ?: MaterialTheme.typography.bodyMedium

    val behavior = when (style?.scrollBehavior?.lowercase()?.trim()) {
        "pinned" -> TopAppBarDefaults.pinnedScrollBehavior()
        "enteralways" -> TopAppBarDefaults.enterAlwaysScrollBehavior()
        "exituntilcollapsed" -> TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        else -> null
    }

    val navigationIconContent: @Composable () -> Unit = {
        component.navigationIcon?.let { navIcon ->
            val onClick: () -> Unit = {
                if (navIcon.type.lowercase().trim() == "back") {
                    onEvent(ServerDrivenEvent.NavigationRequested("back", emptyMap()))
                } else {
                    navIcon.action?.let { handleAction(it, onEvent, dataJson, state) }
                }
            }

            IconButton(onClick = onClick) {
                when (navIcon.type.lowercase().trim()) {
                    "back" -> {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = navIcon.contentDescription ?: "Back",
                            tint = navIcon.tintColor?.convertToColor() ?: MaterialTheme.colorScheme.onSurface
                        )
                    }
                    "close" -> {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = navIcon.contentDescription ?: "Close",
                            tint = navIcon.tintColor?.convertToColor() ?: MaterialTheme.colorScheme.onSurface
                        )
                    }
                    "menu" -> {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = navIcon.contentDescription ?: "Menu",
                            tint = navIcon.tintColor?.convertToColor() ?: MaterialTheme.colorScheme.onSurface
                        )
                    }
                    "image", "url", "svg" -> {
                        val imageUrl = navIcon.imageUrl ?: navIcon.svgUrl ?: ""
                        if (imageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = navIcon.contentDescription,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    val actionsContent: @Composable RowScope.() -> Unit = {
        component.actions?.take(3)?.forEachIndexed { index, actionItem ->
            var expanded by remember { mutableStateOf(false) }

            Box {
                IconButton(onClick = {
                    if (actionItem.type.lowercase().trim() == "dropdown_trigger") {
                        expanded = !expanded
                    } else {
                        actionItem.action?.let { handleAction(it, onEvent, dataJson, state) }
                    }
                }) {
                    when (actionItem.type.lowercase().trim()) {
                        "icon", "dropdown_trigger" -> {
                            actionItem.iconName?.let { name ->
                                Icon(
                                    imageVector = IconResolver.getIcon(name),
                                    contentDescription = actionItem.contentDescription ?: name,
                                    tint = actionItem.tintColor?.convertToColor() ?: MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        "image", "url", "svg" -> {
                            val imgUrl = actionItem.imageUrl ?: actionItem.svgUrl ?: ""
                            if (imgUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = imgUrl,
                                    contentDescription = actionItem.contentDescription,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        "text" -> {
                            actionItem.label?.let { text ->
                                Text(
                                    text = text,
                                    color = actionItem.tintColor?.convertToColor() ?: MaterialTheme.colorScheme.primary,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                if (actionItem.type.lowercase().trim() == "dropdown_trigger" && actionItem.dropdownMenu != null) {
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        actionItem.dropdownMenu.items.forEach { dropItem ->
                            DropdownMenuItem(
                                text = { Text(text = dropItem.label) },
                                onClick = {
                                    expanded = false
                                    dropItem.action?.let { handleAction(it, onEvent, dataJson, state) }
                                },
                                leadingIcon = if (dropItem.iconName != null) {
                                    {
                                        Icon(
                                            imageVector = IconResolver.getIcon(dropItem.iconName),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                } else null
                            )
                        }
                    }
                }
            }
        }
    }

    val titleContent: @Composable () -> Unit = {
        Column {
            Text(text = titleText, style = titleStyle)
            if (hasSubtitle) {
                Text(text = subtitleText, style = subtitleStyle)
            }
        }
    }

    val modifier = component.style?.modifier?.toModifier() ?: Modifier
    val clipModifier = if (cornerRadius > 0f) {
        Modifier.clip(RoundedCornerShape(bottomStart = cornerRadius.dp, bottomEnd = cornerRadius.dp))
    } else {
        Modifier
    }

    val topAppBarColors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor)
    val mediumAppBarColors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = containerColor)

    val appBarContent: @Composable () -> Unit = {
        if (hasSubtitle || style?.scrollBehavior?.lowercase()?.trim() == "exituntilcollapsed") {
            MediumTopAppBar(
                title = titleContent,
                navigationIcon = navigationIconContent,
                actions = actionsContent,
                scrollBehavior = behavior,
                colors = mediumAppBarColors,
                modifier = modifier.then(clipModifier)
            )
        } else {
            TopAppBar(
                title = titleContent,
                navigationIcon = navigationIconContent,
                actions = actionsContent,
                scrollBehavior = behavior,
                colors = topAppBarColors,
                modifier = modifier.then(clipModifier)
            )
        }
    }

    if (isOverlay) {
        Box(modifier = Modifier.zIndex(1f)) {
            appBarContent()
        }
    } else {
        appBarContent()
    }
}
