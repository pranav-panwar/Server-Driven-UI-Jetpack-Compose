package com.praptechie.serverdrivenuicompose.ui_elements

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.data_models.SplashScreenComponent
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import com.praptechie.serverdrivenuicompose.ui_elements.shared.RenderBackground
import com.praptechie.serverdrivenuicompose.ui_elements.shared.RenderOnboardingMedia
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.convertToColor
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.handleAction
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toTextStyle
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toModifier
import kotlinx.coroutines.delay
import kotlinx.serialization.json.JsonObject

@Composable
internal fun SplashScreenUi(
    component: SplashScreenComponent,
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    val duration = component.duration ?: 2500L

    // Splash timer complete handler
    LaunchedEffect(Unit) {
        delay(duration)
        component.onComplete?.let { action ->
            handleAction(action, onEvent, dataJson, state)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background config
        RenderBackground(config = component.background)

        // Center Content Box
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val lottieMedia = component.lottie
            if (lottieMedia != null) {
                // Centered Lottie animation (takes priority)
                RenderOnboardingMedia(media = lottieMedia)
            } else {
                // Logo & Tagline column
                val logo = component.logo
                val tagline = component.tagline
                val animatable = remember { Animatable(0f) }
                val density = LocalDensity.current

                val animConfig = logo?.animation
                val animType = animConfig?.type?.lowercase()?.trim() ?: "none"
                val animDuration = animConfig?.durationMs ?: 800L
                val animDelay = animConfig?.delayMs ?: 0L
                val fromScale = animConfig?.fromScale ?: 0.5f
                val toScale = animConfig?.toScale ?: 1.0f
                val fromRotation = animConfig?.fromRotation ?: 0f
                val toRotation = animConfig?.toRotation ?: 360f

                LaunchedEffect(logo) {
                    if (logo != null && animType != "none") {
                        if (animDelay > 0L) {
                            delay(animDelay)
                        }
                        val easingSpec = when (animConfig?.easing?.lowercase()?.trim()) {
                            "ease_in" -> FastOutLinearInEasing
                            "ease_out" -> LinearOutSlowInEasing
                            "ease_in_out" -> FastOutSlowInEasing
                            "linear" -> LinearEasing
                            else -> FastOutSlowInEasing
                        }
                        val animationSpec = if (animType == "bounce" || animConfig?.easing?.lowercase()?.trim() == "bounce") {
                            spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy)
                        } else {
                            tween<Float>(
                                durationMillis = animDuration.toInt(),
                                easing = easingSpec
                            )
                        }
                        animatable.animateTo(1f, animationSpec = animationSpec)
                    } else {
                        // Immediately set progress to 1 if no animation specified
                        animatable.snapTo(1f)
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    if (logo != null) {
                        val sizeModifier = logo.size?.toModifier() ?: Modifier
                        val translationYPx = with(density) { 150.dp.toPx() }

                        val animatedModifier = Modifier.graphicsLayer {
                            when (animType) {
                                "scale" -> {
                                    val scale = fromScale + (toScale - fromScale) * animatable.value
                                    scaleX = scale
                                    scaleY = scale
                                }
                                "bounce" -> {
                                    val scale = fromScale + (toScale - fromScale) * animatable.value
                                    scaleX = scale
                                    scaleY = scale
                                }
                                "fade" -> {
                                    alpha = animatable.value
                                }
                                "rotate" -> {
                                    rotationZ = fromRotation + (toRotation - fromRotation) * animatable.value
                                }
                                "slide_up" -> {
                                    translationY = (1f - animatable.value) * translationYPx
                                }
                                "slide_down" -> {
                                    translationY = (animatable.value - 1f) * translationYPx
                                }
                            }
                        }

                        Box(modifier = animatedModifier) {
                            when (logo.type.lowercase().trim()) {
                                "image", "svg" -> {
                                    logo.url?.let { url ->
                                        AsyncImage(
                                            model = url,
                                            contentDescription = "Splash Logo",
                                            modifier = sizeModifier
                                        )
                                    }
                                }
                                "text" -> {
                                    logo.text?.let { textVal ->
                                        Text(
                                            text = textVal,
                                            style = logo.textStyle?.toTextStyle() ?: MaterialTheme.typography.headlineLarge
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (!tagline.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = tagline,
                            style = component.taglineStyle?.toTextStyle() ?: MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
