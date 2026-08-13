package com.praptechie.serverdrivenuicompose.ui_elements.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.praptechie.serverdrivenuicompose.data_models.BackgroundConfig
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.convertToColor

@Composable
internal fun RenderBackground(config: BackgroundConfig, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        when (config.type.lowercase().trim()) {
            "color" -> {
                val color = config.color.convertToColor()
                Box(Modifier.fillMaxSize().background(color))
            }
            "gradient" -> {
                val colors = config.gradient?.colors?.map {
                    try { Color(android.graphics.Color.parseColor(it)) } catch (e: Exception) { Color.Transparent }
                } ?: emptyList()
                val angle = config.gradient?.angle ?: 0f
                Box(
                    Modifier
                        .fillMaxSize()
                        .drawBehind {
                            if (colors.isNotEmpty()) {
                                // Map angle (0 = top-to-bottom, 90 = left-to-right) to offsets
                                val angleRad = (90f - angle) * (Math.PI / 180.0)
                                val start = Offset(
                                    x = (0.5f - 0.5f * kotlin.math.cos(angleRad).toFloat()) * size.width,
                                    y = (0.5f - 0.5f * kotlin.math.sin(angleRad).toFloat()) * size.height
                                )
                                val end = Offset(
                                    x = (0.5f + 0.5f * kotlin.math.cos(angleRad).toFloat()) * size.width,
                                    y = (0.5f + 0.5f * kotlin.math.sin(angleRad).toFloat()) * size.height
                                )
                                drawRect(
                                    brush = Brush.linearGradient(
                                        colors = colors,
                                        start = start,
                                        end = end
                                    ),
                                    size = size
                                )
                            }
                        }
                )
            }
            "image" -> {
                val imageUrl = config.imageUrl
                if (!imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                config.imageOverlayAlpha?.let { alpha ->
                    if (alpha > 0f) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = alpha.coerceIn(0f, 1f)))
                        )
                    }
                }
            }
        }
    }
}
