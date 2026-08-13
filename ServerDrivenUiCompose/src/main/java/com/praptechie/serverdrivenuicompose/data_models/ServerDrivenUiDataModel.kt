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
    abstract val style: ComponentStyle?
    abstract val slot: String?
    abstract val visibleOn: List<String>?
}

@Serializable
@SerialName("text_input")
internal data class TextInputComponent(
    override val slot: String? = null,
    override val visibleOn: List<String>? = null,
    override val type: String = "text_input",
    val stateKey: String,
    val placeholder: String? = null,
    val maxLines: Int? = 1,
    val itemSize: ItemSize? = null,
    override val style: ComponentStyle? = null
) : UIComponent()

@Serializable
@SerialName("conditional")
internal data class ConditionalComponent(
    override val slot: String? = null,
    override val visibleOn: List<String>? = null,
    override val type: String = "conditional",
    val condition: String,
    @SerialName("then") val thenComponent: UIComponent,
    @SerialName("else") val elseComponent: UIComponent? = null,
    override val style: ComponentStyle? = null
) : UIComponent()

@Serializable
@SerialName("column")
internal data class ColumnComponent(
    override val slot: String? = null,
    override val visibleOn: List<String>? = null,
    override val type: String? = "column",
    override val style: ComponentStyle? = null,
    val itemSize: ItemSize?=null,
    val spacing: Int ?= 8,
    val children: List<UIComponent>? = emptyList(),
    val action: Action? = null,
) : UIComponent()

@Serializable
@SerialName("row")
internal data class RowComponent(
    override val slot: String? = null,
    override val visibleOn: List<String>? = null,
    override val type: String = "row",
    override val style: ComponentStyle? = null,
    val itemSize: ItemSize?=null,
    val children: List<UIComponent> = emptyList(),
) : UIComponent()

@Serializable
@SerialName("text")
internal data class TextComponent(
    override val slot: String? = null,
    override val visibleOn: List<String>? = null,
    override val type: String = "text",
    val itemSize: ItemSize?=null,
    val dataBinding: String? = null,
    val content: List<TextContent> = emptyList(),
    override val style: ComponentStyle? = null,
    val action: Action? = null,
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
    override val slot: String? = null,
    override val visibleOn: List<String>? = null,
    override val type: String = "image",
    val dataBinding: String? = null,
    val imageUrl: String? = null,
    override val style: ComponentStyle? = null,
    val itemSize: ItemSize? = null,  // ← ADD THIS from old
    val action: Action? = null
) : UIComponent()


@Serializable
@SerialName("lazy_vertical_staggered_grid")
internal data class LazyVerticalStaggeredGridComponent(
    override val slot: String? = null,
    override val visibleOn: List<String>? = null,
    override val type: String = "lazy_vertical_staggered_grid",
    val dataBinding: String? = null,
    val imageUrl: String? = null,
    val columns:Int?=2,
    val responsiveColumns: ResponsiveValue<Int>? = null,
    val verticalSpacing:Int?=0,
    val horizontalSpacing:Int?=0,
    override val style: ComponentStyle? = null,
    val itemTemplate: UIComponent,
    val itemSize: ItemSize? = null,
    val action: Action? = null
) : UIComponent()

@Serializable
@SerialName("button")
internal data class ButtonComponent(
    override val slot: String? = null,
    override val visibleOn: List<String>? = null,
    override val type: String = "button",
    val text: String,
    val dataBinding: String? = null,
    override val style: ComponentStyle? = null,
    val itemSize: ItemSize? = null,  // ← ADD THIS from old
    val action: Action?=null
) : UIComponent()

@Serializable
@SerialName("card")
internal data class CardComponent(
    override val slot: String? = null,
    override val visibleOn: List<String>? = null,
    override val type: String = "card",
    override val style: ComponentStyle? = null,
    val children: List<UIComponent> = emptyList(),
    val itemSize: ItemSize?=null,
    val action: Action? = null
) : UIComponent()

@Serializable
@SerialName("chip_group")
internal data class ChipGroupComponent(
    override val slot: String? = null,
    override val visibleOn: List<String>? = null,
    override val type: String = "chip_group",
    val dataBinding: String,
    val selectedStateKey: String = "selectedIndex",
    val chipTemplate: ChipTemplate,
    override val style: ComponentStyle? = null,  // ← REORDER
    val itemSize: ItemSize?=null,
    val scrollable: Boolean = true
) : UIComponent()

@Serializable
@SerialName("icon_button")
internal data class IconButtonComponent(
    override val slot: String? = null,
    override val visibleOn: List<String>? = null,
    override val type: String = "icon_button",
    override val style: ComponentStyle? = null,
    val itemSize: ItemSize?=null,
    val stateKey: String? = null,             // Key to check toggle state (e.g., "isLiked")
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
    val itemSize: ItemSize?=null,
    val columns: Int?= 2,
    val responsiveColumns: ResponsiveValue<Int>? = null,
    val spacing: Int?= 8,
    val dataBinding: String,
    override val style: ComponentStyle? = null,
    override val slot: String? = null,
    override val visibleOn: List<String>? = null,
    val itemTemplate: UIComponent
) : UIComponent()

@Serializable
@SerialName("lazy_column")
internal data class LazyColumnComponent(
    override val slot: String? = null,
    override val visibleOn: List<String>? = null,
    override val type: String = "lazy_column",
    val spacing: Int ?= 8,
    val dataBinding: String,
    override val style: ComponentStyle? = null,  // ← REORDER
    val itemSize: ItemSize?=null,
    val itemTemplate: UIComponent
) : UIComponent()

@Serializable
@SerialName("box")
internal data class BoxComponent(
    override val slot: String? = null,
    override val visibleOn: List<String>? = null,
    override val type: String = "box",
    override val style: ComponentStyle? = null,  // ← REORDER
    val itemSize: ItemSize?=null,
    val itemTemplate: UIComponent?=null,
    val action: Action? = null,
) : UIComponent()

@Serializable
@SerialName("lazy_row")
internal data class LazyRowComponent(
    override val slot: String? = null,
    override val visibleOn: List<String>? = null,
    override val type: String = "lazy_row",
    val spacing: Int ?= 8,
    val dataBinding: String?=null,
    override val style: ComponentStyle? = null,  // ← REORDER
    val itemSize: ItemSize?=null,
    val itemTemplate: UIComponent?=null,
) : UIComponent()

@Serializable
@SerialName("bottom_bar")
internal data class BottomBarComponent(
    override val slot: String? = null,
    override val visibleOn: List<String>? = null,
    override val type: String?="bottom_bar",
    override val style: ComponentStyle?,
    val itemSize: ItemSize?=null,
    val bottomBarItems:List<BottomBarItems>?=null,
    val selectedStateKey: String? = null
): UIComponent()

@Serializable
@SerialName("spacer")
internal data class SpacerComponent(
    override val slot: String? = null,
    override val visibleOn: List<String>? = null,
    override val type: String = "spacer",
    val size: Int = 16,
    override val style: ComponentStyle? = null
) : UIComponent()

@Serializable
@SerialName("divider")
internal data class DividerComponent(
    override val slot: String? = null,
    override val visibleOn: List<String>? = null,
    override val type: String = "divider",
    val thickness: Int = 1,
    val color: ColorValue? = null,
    override val style: ComponentStyle? = null
) : UIComponent()

@Serializable
internal data class BottomBarItems(
    val iconName: String? = null,
    val text: String? = null,
    val action: Action? = null,
    val badge: String? = null,
    val badgeColor: ColorValue? = null,
    val imageUrl: String? = null,
    val svgUrl: String? = null
)

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
    val bottomBarStyle: BottomBarStyle? = null,
    val buttonStyle: ButtonStyle? = null,
    val boxContentAlignment: String? = null,
    val topAppBarStyle: TopAppBarStyle? = null,
    val responsive: ResponsiveValue<ComponentStyle>? = null
)

@Serializable
internal data class ModifierStyle(
    val clip: ModifierClipData? = null,  // ← REORDER
    val backgroundColor: ColorValue? = null,
    val padding: Padding? = null,
    val onClick: OnClickData? = null,
    val responsive: ResponsiveValue<ModifierStyle>? = null  // ← ADD THIS from old
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
    val fontSize: Int?= 16,
    val textColor: ColorValue? = null,
    val fontWeight: String? = "normal",
    val fontFamily: String? = null
)

@Serializable
internal data class ChipStyle(
    val backgroundColor: ColorValue? = null,
    val selectedBackgroundColor: ColorValue? = null,
    val textColor: ColorValue? = null,
    val selectedTextColor: ColorValue? = null,
    val borderRadius: Int = 20,
    val paddingHorizontal: Int = 16,
    val paddingVertical: Int = 8
)

@Serializable
internal data class ColumnStyle(
    val verticalArrangement: String? = "top",
    val horizontalAlignment: String? = "start",
    val spaceBy: Int? = 0,
    val enableScroll: Boolean? = false
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
    val cardContainerColor: ColorValue? = null,
    val cardPadding:Padding?=null
)


@Serializable
internal data class IconButtonStyle(
    val iconName: String,
    val toggledIconName: String? = null,
    val iconSize: Int = 24,
    val tint: ColorValue? = null,
    val toggledTint: ColorValue? = null,
    val toggleStateFrom: String? = null
)

@Serializable
internal data class ButtonStyle(
    val buttonColor: ColorValue? = null,
    val buttonShape:String?="DEFAULT",
    val buttonRounded:Int?=0,
    val buttonTextColor: ColorValue? = null,
    val buttonTextSize:Int?=12,
)

@Serializable
internal data class BottomBarStyle(
    val iconColor: ColorValue? = null,
    val textColor: ColorValue? = null,
    val type: String? = null,               // "standard" | "floating" | "labeled" | "icon_only" | "pill"
    val backgroundColor: ColorValue? = null,
    val selectedColor: ColorValue? = null,
    val unselectedColor: ColorValue? = null,
    val indicatorColor: ColorValue? = null, // pill/active indicator color
    val elevation: Float? = null,
    val cornerRadius: Float? = null,        // for "floating" type — rounds all corners
    val showLabels: Boolean? = null,
    val showIndicator: Boolean? = null,     // shows active pill/indicator behind selected item
    val borderColor: ColorValue? = null,    // optional top/outer border
    val height: Float? = null,              // custom height in dp
    val itemSpacing: Float? = null
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
    val width: Int? = null,
    val widthPercent:Float?=null,
    val heightPercent:Float?=null,
    val weight:Float?=null
)

// ============ Actions ============

@Serializable
public data class Action(
    val perform: String,
    val parameters: Map<String, String> = emptyMap()
)


public sealed class ServerDrivenEvent {
    abstract val screenKey: String?
    abstract val variant: String?

    data class ScreenRendered(
        override val screenKey: String,
        override val variant: String?,
        val componentCount: Int
    ) : ServerDrivenEvent()

    data class ButtonClicked(
        val actionId: String,
        val parameters: Map<String, String>,
        override val screenKey: String? = null,
        override val variant: String? = null
    ) : ServerDrivenEvent()

    data class ItemClicked(
        val itemId: String,
        val index: Int,
        override val screenKey: String? = null,
        override val variant: String? = null
    ) : ServerDrivenEvent()

    data class ChipSelected(
        val chipId: String,
        val index: Int,
        override val screenKey: String? = null,
        override val variant: String? = null
    ) : ServerDrivenEvent()

    data class NavigationRequested(
        val screen: String,
        val parameters: Map<String, String>,
        override val screenKey: String? = null,
        override val variant: String? = null
    ) : ServerDrivenEvent()

    data class StateUpdateRequested(
        val key: String,
        val value: String,
        override val screenKey: String? = null,
        override val variant: String? = null
    ) : ServerDrivenEvent()
}

// ============ New SDUI Components ============

@Serializable
@SerialName("top_app_bar")
internal data class TopAppBarComponent(
    override val slot: String? = null,
    override val visibleOn: List<String>? = null,
    override val type: String = "top_app_bar",
    override val style: ComponentStyle? = null,
    val navigationIcon: TopAppBarNavigationIcon? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val actions: List<TopAppBarActionItem>? = null
) : UIComponent()

@Serializable
internal data class TopAppBarStyle(
    val backgroundColor: ColorValue? = null,
    val titleTextStyle: TextStyle? = null,
    val subtitleTextStyle: TextStyle? = null,
    val elevation: Float? = null,
    val cornerRadius: Float? = null,          // rounds bottom corners of the bar
    val scrollBehavior: String? = null,        // "pinned" | "enterAlways" | "exitUntilCollapsed"
    val overlayContent: Boolean? = null        // if true, bar floats over content (no top padding pushed)
)

@Serializable
internal data class TopAppBarNavigationIcon(
    val type: String,                          // "back" | "close" | "menu" | "image" | "svg" | "url"
    val iconName: String? = null,              // material icon name if type = "back"/"close"/"menu"
    val imageUrl: String? = null,              // remote image URL if type = "image" or "url"
    val svgUrl: String? = null,                // remote SVG URL if type = "svg"
    val contentDescription: String? = null,
    val tintColor: ColorValue? = null,
    val action: Action? = null                 // fires event or navigate; "back" type auto-fires NavigationRequested("back")
)

@Serializable
internal data class TopAppBarActionItem(
    val type: String,                          // "icon" | "image" | "svg" | "text" | "dropdown_trigger"
    val iconName: String? = null,
    val imageUrl: String? = null,
    val svgUrl: String? = null,
    val label: String? = null,
    val contentDescription: String? = null,
    val tintColor: ColorValue? = null,
    val action: Action? = null,
    val dropdownMenu: TopAppBarDropdownMenu? = null   // only when type = "dropdown_trigger"
)

@Serializable
internal data class TopAppBarDropdownMenu(
    val items: List<TopAppBarDropdownItem>
)

@Serializable
internal data class TopAppBarDropdownItem(
    val label: String,
    val iconName: String? = null,
    val action: Action? = null
)

@Serializable
@SerialName("onboarding_screen")
internal data class OnboardingScreenComponent(
    override val slot: String? = null,
    override val visibleOn: List<String>? = null,
    override val type: String = "onboarding_screen",
    @SerialName("style") val onboardingStyle: OnboardingScreenStyle? = null,
    val pages: List<OnboardingPage>,
    val cacheKey: String? = null,             // if set, show once and cache completion in DataStore
    val showEveryTime: Boolean? = null        // if true, ignore cache and always show
) : UIComponent() {
    override val style: ComponentStyle? get() = null
}

@Serializable
internal data class OnboardingPage(
    val background: BackgroundConfig? = null, // overrides screen-level background for this page
    val media: OnboardingMedia? = null,
    val title: String? = null,
    val titleStyle: TextStyle? = null,
    val subtitle: String? = null,
    val subtitleStyle: TextStyle? = null,
    val customComponents: List<UIComponent>? = null  // any SDUI components rendered below subtitle
)

@Serializable
internal data class OnboardingMedia(
    val type: String,                // "image" | "lottie" | "svg" | "none"
    val url: String? = null,         // remote URL for image/lottie/svg
    val loop: Boolean? = null,       // lottie only
    val autoPlay: Boolean? = null,   // lottie only
    val size: ItemSize? = null
)

@Serializable
internal data class BackgroundConfig(
    val type: String,               // "color" | "gradient" | "image"
    val color: ColorValue? = null,
    val gradient: GradientConfig? = null,
    val imageUrl: String? = null,   // full-screen background image (ContentScale.Crop)
    val imageOverlayAlpha: Float? = null  // 0.0–1.0 dark overlay on top of bg image
)

@Serializable
internal data class GradientConfig(
    val colors: List<String>,        // list of hex color strings
    val angle: Float? = null         // 0 = vertical top-to-bottom, 90 = left-to-right
)

@Serializable
internal data class OnboardingScreenStyle(
    val background: BackgroundConfig? = null,       // default background for all pages
    val nextButtonStyle: ButtonStyle? = null,
    val skipButtonStyle: ButtonStyle? = null,
    val nextButtonLabel: String? = null,             // default "Next"
    val finishButtonLabel: String? = null,           // label on last page, default "Get Started"
    val skipButtonLabel: String? = null,             // default "Skip"
    val showSkipButton: Boolean? = null,
    val showNextButton: Boolean? = null,
    val showPageIndicator: Boolean? = null,
    val pageIndicatorActiveColor: ColorValue? = null,
    val pageIndicatorInactiveColor: ColorValue? = null,
    val pageIndicatorStyle: String? = null,          // "dots" | "lines" | "numbers"
    val transitionAnimation: String? = null,         // "slide" | "fade" | "scale"
    val nextAction: Action? = null,                  // fires on last page "finish" tap
    val skipAction: Action? = null                   // fires on skip tap
)

@Serializable
@SerialName("splash_screen")
internal data class SplashScreenComponent(
    override val slot: String? = null,
    override val visibleOn: List<String>? = null,
    override val type: String = "splash_screen",
    val background: BackgroundConfig,         // reuse BackgroundConfig from onboarding
    val logo: SplashLogo? = null,
    val tagline: String? = null,
    val taglineStyle: TextStyle? = null,
    val lottie: OnboardingMedia? = null,      // reuse OnboardingMedia, type must be "lottie"
    val duration: Long? = null,               // total splash duration in ms, default 2500
    val onComplete: Action? = null            // action fired when splash duration ends
) : UIComponent() {
    override val style: ComponentStyle? get() = null
}

@Serializable
internal data class SplashLogo(
    val type: String,                         // "image" | "svg" | "text"
    val url: String? = null,                  // for image/svg
    val text: String? = null,                 // for text logo
    val textStyle: TextStyle? = null,
    val size: ItemSize? = null,
    val animation: SplashLogoAnimation? = null
)

@Serializable
internal data class SplashLogoAnimation(
    val type: String,                         // "scale" | "fade" | "rotate" | "slide_up" | "slide_down" | "bounce" | "none"
    val durationMs: Long? = null,             // animation duration, default 800ms
    val delayMs: Long? = null,                // delay before animation starts
    val fromScale: Float? = null,             // for "scale" — initial scale, default 0.5
    val toScale: Float? = null,               // for "scale" — target scale, default 1.0
    val fromRotation: Float? = null,          // for "rotate"
    val toRotation: Float? = null,
    val easing: String? = null               // "linear" | "ease_in" | "ease_out" | "ease_in_out" | "bounce"
)
