package com.praptechie.serverdrivenuicompose.ui_elements

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.praptechie.serverdrivenuicompose.data_models.ChipGroupComponent
import com.praptechie.serverdrivenuicompose.data_models.ChipStyle
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import com.praptechie.serverdrivenuicompose.handler_processors.TemplateProcessor
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.handleAction
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.intOrNull
import kotlin.collections.plus

@Composable
internal fun RenderChipGroup(
    component: ChipGroupComponent,
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    val items = TemplateProcessor.resolveDataSource(
        component.dataBinding.removePrefix("@"),
        dataJson,
        state.stateMap
    )

    Log.e("asdfasdfasdf", "ChipGroup items: $items")

    val selectedIndex by remember {
        derivedStateOf {
            (state.get(component.selectedStateKey) as? JsonPrimitive)?.intOrNull ?: 0
        }
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 0.dp)
    ) {
        itemsIndexed(items) { index, item ->
            // Create item context with "item" key
            val itemContext = buildJsonObject {
                put("item", item)
                put("index", JsonPrimitive(index))
            }

            // Replace using item context, not the item directly
            val label = TemplateProcessor.replaceVars(
                component.chipTemplate.labelBinding,
                itemContext,  // ← Pass itemContext, not item
                state.stateMap
            )

            Log.e("asdfasdfasdf", "uidata - $item, label = $label")

            val style = component.chipTemplate.style ?: ChipStyle()
            val isSelected = index == selectedIndex

            Surface(
                shape = RoundedCornerShape(style.borderRadius.dp),
                color = if (isSelected) {
                    Color(android.graphics.Color.parseColor(style.selectedBackgroundColor))
                } else {
                    Color(android.graphics.Color.parseColor(style.backgroundColor))
                },
                modifier = Modifier.clickable {
                    state.update(component.selectedStateKey, index)
                    component.chipTemplate.action?.let { action ->
                        val processedAction = action.copy(
                            parameters = action.parameters + mapOf("index" to index.toString())
                        )
                        handleAction(processedAction, onEvent, dataJson, state)
                    }
                    onEvent(ServerDrivenEvent.ChipSelected(label, index))
                }
            ) {
                Text(
                    text = label ?: "Unknown",  // ← Add fallback
                    color = if (isSelected) {
                        Color(android.graphics.Color.parseColor(style.selectedTextColor))
                    } else {
                        Color(android.graphics.Color.parseColor(style.textColor))
                    },
                    modifier = Modifier.padding(
                        horizontal = style.paddingHorizontal.dp,
                        vertical = style.paddingVertical.dp
                    )
                )
            }
        }
    }
}
