import re

file_path = "ServerDrivenUiCompose/src/main/java/com/praptechie/serverdrivenuicompose/data_models/ServerDrivenUiDataModel.kt"

with open(file_path, "r") as f:
    content = f.read()

# Add visibleOn to UIComponent
content = re.sub(
    r'(abstract val slot: String\?)',
    r'\1\n    abstract val visibleOn: List<String>?',
    content
)

# Add visibleOn to all classes implementing UIComponent
def add_visible_on(match):
    declaration = match.group(0)
    if "override val visibleOn" not in declaration:
        return re.sub(r'(override val slot: String\? = null,)', r'\1\n    override val visibleOn: List<String>? = null,', declaration)
    return declaration

content = re.sub(
    r'internal data class \w+\([\s\S]*?\) : UIComponent\(\)',
    add_visible_on,
    content
)

# Change Color fields to ColorValue?
color_fields = [
    "backgroundColor",
    "textColor",
    "selectedBackgroundColor",
    "selectedTextColor",
    "cardContainerColor",
    "tint",
    "toggledTint",
    "buttonColor",
    "buttonTextColor",
    "iconColor",
    "color"
]

for field in color_fields:
    # Match `val fieldName: String? = "#hex"` or `val fieldName: String = "#hex"`
    # Replace with `val fieldName: ColorValue? = null`
    content = re.sub(
        rf'(val\s+{field}\s*:\s*String\??\s*(=\s*"[^"]*")?)',
        rf'val {field}: ColorValue? = null',
        content
    )

# Add responsive to ComponentStyle and ModifierStyle
content = re.sub(
    r'(val boxContentAlignment: String\? = null)',
    r'\1,\n    val responsive: ResponsiveValue<ComponentStyle>? = null',
    content
)

content = re.sub(
    r'(val onClick: OnClickData\? = null)',
    r'\1,\n    val responsive: ResponsiveValue<ModifierStyle>? = null',
    content
)

with open(file_path, "w") as f:
    f.write(content)

print("Updated ServerDrivenUiDataModel.kt")
