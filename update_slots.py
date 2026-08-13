import re

file_path = "ServerDrivenUiCompose/src/main/java/com/praptechie/serverdrivenuicompose/data_models/ServerDrivenUiDataModel.kt"

with open(file_path, "r") as f:
    content = f.read()

# Add abstract val slot: String? to UIComponent
content = re.sub(
    r'(abstract val style: ComponentStyle\?)',
    r'\1\n    abstract val slot: String?',
    content
)

# Find all internal data class declarations that inherit from UIComponent()
# and add override val slot: String? = null
# We'll use regex to find each subclass
def add_slot(match):
    declaration = match.group(0)
    if "override val slot" not in declaration:
        # insert it right after the class name and opening paren
        return re.sub(r'(internal data class \w+\()', r'\1\n    override val slot: String? = null,', declaration)
    return declaration

content = re.sub(
    r'internal data class \w+\([\s\S]*?\) : UIComponent\(\)',
    add_slot,
    content
)

with open(file_path, "w") as f:
    f.write(content)

print("Updated ServerDrivenUiDataModel.kt")
