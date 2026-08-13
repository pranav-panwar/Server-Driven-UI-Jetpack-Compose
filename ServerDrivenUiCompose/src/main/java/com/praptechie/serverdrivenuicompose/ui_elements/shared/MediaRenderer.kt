package com.praptechie.serverdrivenuicompose.ui_elements.shared

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.praptechie.serverdrivenuicompose.data_models.OnboardingMedia
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toModifier

private val isLottieAvailable: Boolean by lazy {
    try {
        Class.forName("com.airbnb.lottie.compose.LottieAnimationKt")
        true
    } catch (e: ClassNotFoundException) {
        false
    }
}

@Composable
internal fun RenderOnboardingMedia(media: OnboardingMedia, modifier: Modifier = Modifier) {
    val sizeModifier = media.size?.toModifier() ?: Modifier
    when (media.type.lowercase().trim()) {
        "image" -> {
            media.url?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = modifier.then(sizeModifier),
                    contentScale = ContentScale.Fit
                )
            }
        }
        "svg" -> {
            media.url?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = modifier.then(sizeModifier),
                    contentScale = ContentScale.Fit
                )
            }
        }
        "lottie" -> {
            if (isLottieAvailable) {
                LottieRendererHelper.RenderLottie(media, modifier.then(sizeModifier))
            } else {
                Log.w("ServerDrivenUiSDK", "Lottie is requested but com.airbnb.android:lottie-compose is not added to the host app dependencies.")
            }
        }
    }
}

private object LottieRendererHelper {
    @Composable
    fun RenderLottie(media: OnboardingMedia, modifier: Modifier) {
        val url = media.url ?: return
        val loop = media.loop ?: true
        val autoPlay = media.autoPlay ?: true

        val compositionResult = com.airbnb.lottie.compose.rememberLottieComposition(
            spec = com.airbnb.lottie.compose.LottieCompositionSpec.Url(url)
        )
        val progress by com.airbnb.lottie.compose.animateLottieCompositionAsState(
            composition = compositionResult.value,
            iterations = if (loop) com.airbnb.lottie.compose.LottieConstants.IterateForever else 1,
            isPlaying = autoPlay
        )
        com.airbnb.lottie.compose.LottieAnimation(
            composition = compositionResult.value,
            progress = { progress },
            modifier = modifier
        )
    }
}
