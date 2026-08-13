import re

file_path = "ServerDrivenUiCompose/src/main/java/com/praptechie/serverdrivenuicompose/ui_elements_handler_styles/UiElementsAndStyles.kt"

with open(file_path, "r") as f:
    content = f.read()

# Add imports
imports = """import androidx.compose.runtime.Composable
import com.praptechie.serverdrivenuicompose.LocalFireUiTheme
import com.praptechie.serverdrivenuicompose.LocalFireUiWindowSize
import com.praptechie.serverdrivenuicompose.FireUiWindowSizeClass
import com.praptechie.serverdrivenuicompose.data_models.FireUiTheme
import com.praptechie.serverdrivenuicompose.data_models.ColorValue
import com.praptechie.serverdrivenuicompose.ServerDrivenUiHandler
"""
content = re.sub(r'(import [^\n]+)', imports + r'\1', content, count=1)

# Modify toModifier
modifier_orig = """ internal fun ModifierStyle?.toModifier(): Modifier {
    if (this == null) return Modifier

    var modifier : Modifier= Modifier

    // Apply padding
    this.padding?.let {
        modifier = modifier.padding(
            top = (it.top ?: it.all ?: 0).dp,
            bottom = (it.bottom ?: it.all ?: 0).dp,
            start = (it.left ?: it.all ?: 0).dp,
            end = (it.right ?: it.all ?: 0).dp
        )
    }

    // Apply background color
    this.backgroundColor?.let {
        try {
            modifier = modifier.background(Color(android.graphics.Color.parseColor(it)))
        } catch (e: Exception) {
            Log.e("ServerDrivenUILogTag", "ModifierStyle Exception - Invalid color: $it")
        }
    }

    // Apply clip shape
    this.clip?.let {
        if (it.shape == "rounded") {
            modifier = modifier.clip(RoundedCornerShape(it.radius.dp))
        }
        else if(it.shape=="circle")
            modifier = modifier.clip(CircleShape)
    }

    return modifier
}"""

modifier_new = """@Composable
internal fun ModifierStyle?.toModifier(): Modifier {
    if (this == null) return Modifier

    val isDark = LocalFireUiTheme.current == FireUiTheme.DARK
    val windowSize = LocalFireUiWindowSize.current
    
    var effectiveStyle = this
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
}"""
content = content.replace(modifier_orig, modifier_new)

# Modify TextStyle.toTextStyle()
textstyle_orig = """internal fun TextStyle.toTextStyle(): androidx.compose.ui.text.TextStyle {
    return androidx.compose.ui.text.TextStyle(
        fontSize = (fontSize?:16).sp,
        color = Color(android.graphics.Color.parseColor(textColor)),
        fontWeight = when (fontWeight?.lowercase()) {
            "bold" -> FontWeight.Bold
            "medium" -> FontWeight.Medium
            "light" -> FontWeight.Light
            else -> FontWeight.Normal
        }
    )
}"""
textstyle_new = """@Composable
internal fun TextStyle.toTextStyle(): androidx.compose.ui.text.TextStyle {
    val isDark = LocalFireUiTheme.current == FireUiTheme.DARK
    // Wait, ServerDrivenUiDataModel's TextStyle needs fontFamily added
    // We'll assume fontFamily is added or we just look it up.
    // Actually, ServerDrivenUiDataModel needs fontFamily added to TextStyle! We'll add it to the data model first.
    // Assuming it's added.
    val colorStr = textColor?.resolve(isDark) ?: "#000000"
    return androidx.compose.ui.text.TextStyle(
        fontSize = (fontSize?:16).sp,
        color = try { Color(android.graphics.Color.parseColor(colorStr)) } catch(e:Exception){ Color.Black },
        fontWeight = when (fontWeight?.lowercase()) {
            "bold" -> FontWeight.Bold
            "medium" -> FontWeight.Medium
            "light" -> FontWeight.Light
            else -> FontWeight.Normal
        }
    )
}"""
content = content.replace(textstyle_orig, textstyle_new)

# Modify convertToIntColor and convertToColor
content = content.replace("""internal fun String?.convertToIntColor(): Int {
    return android.graphics.Color.parseColor(this)
}""", """@Composable
internal fun ColorValue?.convertToIntColor(): Int {
    if (this == null) return android.graphics.Color.BLACK
    val isDark = LocalFireUiTheme.current == FireUiTheme.DARK
    return try { android.graphics.Color.parseColor(this.resolve(isDark)) } catch(e:Exception){ android.graphics.Color.BLACK }
}""")

content = content.replace("""internal fun Int.convertToColor(): Color {
    return Color(this)
}""", """internal fun Int.convertToColor(): Color {
    return Color(this)
}

@Composable
internal fun ColorValue?.convertToColor(): Color {
    if (this == null) return Color.Transparent
    val isDark = LocalFireUiTheme.current == FireUiTheme.DARK
    return try { Color(android.graphics.Color.parseColor(this.resolve(isDark))) } catch(e:Exception){ Color.Transparent }
}""")

with open(file_path, "w") as f:
    f.write(content)
print("Updated UiElementsAndStyles.kt")
