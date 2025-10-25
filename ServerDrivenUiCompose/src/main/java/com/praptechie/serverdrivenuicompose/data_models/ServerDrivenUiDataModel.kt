package com.praptechie.serverdrivenuicompose.data_models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
internal data class UIDefinition(
    val version: String ?= "1.0.0",
    val uiData: List<UIComponent?> = emptyList()
)

@Serializable
internal sealed class UIComponent {
    abstract val type: String?
    abstract val margin: Margin? // ← ADD THIS from old
    abstract val style: ComponentStyle?
}

@Serializable
@SerialName("column")
internal data class ColumnComponent(
    override val type: String? = "column",
    override val margin: Margin? = null,  // ← ADD THIS
    override val style: ComponentStyle? = null,
    val children: List<UIComponent>? = emptyList()
) : UIComponent()

@Serializable
@SerialName("row")
internal data class RowComponent(
    override val type: String = "row",
    override val margin: Margin? = null,  // ← ADD THIS
    override val style: ComponentStyle? = null,
    val itemSize: ItemSize?=null,
    val children: List<UIComponent> = emptyList()
) : UIComponent()

@Serializable
@SerialName("text")
internal data class TextComponent(
    override val type: String = "text",
    override val margin: Margin? = null,  // ← ADD THIS
    val dataBinding: String? = null,
    val content: List<TextContent> = emptyList(),
    override val style: ComponentStyle? = null,
    val action: Action? = null
) : UIComponent()

@Serializable
internal data class TextContent(
    val text: String,
    val imageUrl: String? = null,  // ← ADD THIS from old
    val url: String? = null,  // ← ADD THIS from old
    val modifier: String? = null
)

@Serializable
@SerialName("image")
internal data class ImageComponent(
    override val type: String = "image",
    override val margin: Margin? = null,  // ← ADD THIS
    val dataBinding: String? = null,
    val imageUrl: String? = null,
    override val style: ComponentStyle? = null,
    val itemSize: ItemSize? = null,  // ← ADD THIS from old
    val action: Action? = null
) : UIComponent()


@Serializable
@SerialName("lazy_vertical_staggered_grid")
internal data class LazyVerticalStaggeredGridComponent(
    override val type: String = "lazy_vertical_staggered_grid",
    override val margin: Margin? = null,  // ← ADD THIS
    val dataBinding: String? = null,
    val imageUrl: String? = null,
    val columns:Int?=2,
    val verticalSpacing:Int?=0,
    val horizontalSpacing:Int?=0,
    override val style: ComponentStyle? = null,
    val itemTemplate: UIComponent,
    val itemSize: ItemSize? = null,  // ← ADD THIS from old
    val action: Action? = null
) : UIComponent()

@Serializable
@SerialName("button")
internal data class ButtonComponent(
    override val type: String = "button",
    override val margin: Margin? = null,  // ← ADD THIS
    val text: String,
    val dataBinding: String? = null,
    override val style: ComponentStyle? = null,
    val itemSize: ItemSize? = null,  // ← ADD THIS from old
    val action: Action
) : UIComponent()

@Serializable
@SerialName("card")
internal data class CardComponent(
    override val type: String = "card",
    override val margin: Margin? = null,  // ← ADD THIS
    override val style: ComponentStyle? = null,
    val children: List<UIComponent> = emptyList(),
    val action: Action? = null
) : UIComponent()

@Serializable
@SerialName("chip_group")
internal data class ChipGroupComponent(
    override val type: String = "chip_group",
    override val margin: Margin? = null,  // ← ADD THIS
    val dataBinding: String,
    val selectedStateKey: String = "selectedIndex",
    val chipTemplate: ChipTemplate,
    override val style: ComponentStyle? = null,  // ← REORDER
    val scrollable: Boolean = true
) : UIComponent()

@Serializable
@SerialName("icon_button")
internal data class IconButtonComponent(
    override val type: String = "icon_button",
    override val margin: Margin? = null,  // ← ADD THIS
    override val style: ComponentStyle? = null,
    val stateKey: String? = null,             // Key to check toggle state (e.g., "isLiked")
    val size: Int = 24,
    val action: Action? = null
) : UIComponent()

@Serializable
internal data class ChipTemplate(
    val labelBinding: String,
    val style: ChipStyle? = null,
    val action: Action? = null
)

@Serializable
@SerialName("grid")
internal data class GridComponent(
    override val type: String = "grid",
    override val margin: Margin? = null,  // ← ADD THIS
    val columns: Int?= 2,
    val spacing: Int?= 8,
    val dataBinding: String,
    override val style: ComponentStyle? = null,  // ← REORDER
    val itemTemplate: UIComponent
) : UIComponent()

@Serializable
@SerialName("lazy_column")
internal data class LazyColumnComponent(
    override val type: String = "lazy_column",
    override val margin: Margin? = null,  // ← ADD THIS
    val spacing: Int ?= 8,
    val dataBinding: String,
    override val style: ComponentStyle? = null,  // ← REORDER
    val itemTemplate: UIComponent
) : UIComponent()

// ============ Enhanced Styles ============

@Serializable
internal data class ComponentStyle(
    val modifier: ModifierStyle? = null,
    val textStyle: TextStyle? = null,
    val chipStyle: ChipStyle? = null,
    val columnStyle: ColumnStyle? = null,
    val rowStyle: RowStyle? = null,
    val cardStyle: CardStyle? = null,
    val iconButtonStyle: IconButtonStyle? = null,
    val boxContentAlignment: String? = null  // ← ADD THIS from old
)

@Serializable
internal data class ModifierStyle(
    val clip: ModifierClipData? = null,  // ← REORDER
    val backgroundColor: String? = null,
    val padding: Padding? = null,
    val onClick: OnClickData? = null  // ← ADD THIS from old
)

@Serializable
internal data class ModifierClipData(
    val shape: String = "rounded",
    val radius: Int = 8
)

@Serializable
internal data class OnClickData(
    val action: Action? = null
)

@Serializable
internal data class Padding(
    val all: Int? = null,
    val top: Int? = null,
    val bottom: Int? = null,
    val left: Int? = null,
    val right: Int? = null
)

@Serializable
internal data class TextStyle(
    val fontSize: Int = 16,
    val textColor: String = "#000000",
    val fontWeight: String = "normal"
)

@Serializable
internal data class ChipStyle(
    val backgroundColor: String = "#E0E0E0",
    val selectedBackgroundColor: String = "#6200EE",
    val textColor: String = "#000000",
    val selectedTextColor: String = "#FFFFFF",
    val borderRadius: Int = 20,
    val paddingHorizontal: Int = 16,
    val paddingVertical: Int = 8
)

@Serializable
internal data class ColumnStyle(
    val verticalArrangement: String? = "top",
    val horizontalAlignment: String? = "start",
    val spaceBy: Int? = 0
)

@Serializable
internal data class RowStyle(
    val horizontalArrangement: String? = "start",
    val verticalAlignment: String? = "center",
    val spaceBy: Int? = 0
)
@Serializable
internal data class CardStyle(
    val cardShape:Int?=8,
    val cardContainerColor:String?="#00ffff",
    val cardPadding:Padding?=null
)


@Serializable
internal data class IconButtonStyle(
    val iconName: String,
    val toggledIconName: String? = null,
    val iconSize: Int = 24,
    val tint: String? = "#666666",
    val toggledTint: String? = null,
    val toggleStateFrom: String? = null
)

@Serializable
internal data class Margin(
    val top: Int? = null,
    val left: Int? = null,
    val bottom: Int? = null,
    val right: Int? = null,
    val all: Int? = null
)

@Serializable
internal data class ItemSize(
    val height: Int? = null,
    val width: Int? = null
)

// ============ Actions ============

@Serializable
internal data class Action(
    val perform: String,
    val parameters: Map<String, String> = emptyMap()
)


public sealed class ServerDrivenEvent {
    data class ButtonClicked(val actionId: String, val parameters: Map<String, String>) : ServerDrivenEvent()
    data class ItemClicked(val itemId: String, val index: Int) : ServerDrivenEvent()
    data class ChipSelected(val chipId: String, val index: Int) : ServerDrivenEvent()
    data class NavigationRequested(val screen: String, val parameters: Map<String, String>) : ServerDrivenEvent()
    data class StateUpdateRequested(val key: String, val value: String) : ServerDrivenEvent()
}
