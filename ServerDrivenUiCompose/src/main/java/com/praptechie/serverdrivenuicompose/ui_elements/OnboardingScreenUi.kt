package com.praptechie.serverdrivenuicompose.ui_elements

import android.content.Context
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.praptechie.serverdrivenuicompose.data_models.ButtonStyle
import com.praptechie.serverdrivenuicompose.data_models.OnboardingScreenComponent
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.handler_processors.ServerDrivenState
import com.praptechie.serverdrivenuicompose.ui_elements.shared.RenderBackground
import com.praptechie.serverdrivenuicompose.ui_elements.shared.RenderOnboardingMedia
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.convertToColor
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.handleAction
import com.praptechie.serverdrivenuicompose.ui_elements_handler_styles.toTextStyle
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject

private val Context.onboardingDataStore: DataStore<Preferences> by preferencesDataStore(name = "sdui_onboarding_prefs")

@Composable
internal fun OnboardingScreenUi(
    component: OnboardingScreenComponent,
    dataJson: JsonObject,
    state: ServerDrivenState,
    onEvent: (ServerDrivenEvent) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val pages = component.pages
    val pageCount = pages.size
    if (pageCount == 0) return

    val onboardingStyle = component.onboardingStyle
    val showSkipButton = onboardingStyle?.showSkipButton ?: true
    val showNextButton = onboardingStyle?.showNextButton ?: true
    val showPageIndicator = onboardingStyle?.showPageIndicator ?: true

    // Check datastore cache state
    val onboardingCompleted by androidx.compose.runtime.remember(component.cacheKey) {
        if (component.cacheKey != null && component.showEveryTime != true) {
            val key = booleanPreferencesKey(component.cacheKey)
            context.onboardingDataStore.data.map { preferences ->
                preferences[key] == true
            }
        } else {
            flowOf(false)
        }
    }.collectAsState(initial = null)

    // Trigger skipAction once if already completed
    LaunchedEffect(onboardingCompleted) {
        if (onboardingCompleted == true) {
            onboardingStyle?.skipAction?.let { action ->
                handleAction(action, onEvent, dataJson, state)
            }
        }
    }

    // Skip rendering if completed or still loading completion state
    if (component.cacheKey != null && component.showEveryTime != true && onboardingCompleted != false) {
        return
    }

    val pagerState = rememberPagerState(pageCount = { pageCount })
    val currentPage = pagerState.currentPage

    Box(modifier = Modifier.fillMaxSize()) {
        // Horizontal Carousel
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val page = pages[pageIndex]
            val pageOffset = (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
            val absOffset = kotlin.math.abs(pageOffset)

            val pageModifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    when (onboardingStyle?.transitionAnimation?.lowercase()?.trim()) {
                        "fade" -> {
                            alpha = 1f - absOffset.coerceIn(0f, 1f)
                        }
                        "scale" -> {
                            val scale = 0.85f + 0.15f * (1f - absOffset.coerceIn(0f, 1f))
                            scaleX = scale
                            scaleY = scale
                            alpha = 1f - absOffset.coerceIn(0f, 1f)
                        }
                        "slide" -> {
                            // Default HorizontalPager sliding transitions
                        }
                    }
                }

            Box(modifier = pageModifier) {
                // Page Background config (Page config overrides screen level config)
                val bgConfig = page.background ?: onboardingStyle?.background
                bgConfig?.let { RenderBackground(it) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Media
                    page.media?.let { media ->
                        RenderOnboardingMedia(media = media, modifier = Modifier.padding(bottom = 24.dp))
                    }

                    // Title
                    page.title?.let { titleText ->
                        Text(
                            text = titleText,
                            style = page.titleStyle?.toTextStyle() ?: MaterialTheme.typography.headlineMedium.copy(
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(bottom = 12.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Subtitle
                    page.subtitle?.let { subtitleText ->
                        Text(
                            text = subtitleText,
                            style = page.subtitleStyle?.toTextStyle() ?: MaterialTheme.typography.bodyLarge.copy(
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(bottom = 24.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Custom SDUI children
                    page.customComponents?.forEach { child ->
                        RenderComponent(
                            component = child,
                            dataJson = dataJson,
                            state = state,
                            onEvent = onEvent
                        )
                    }
                }
            }
        }

        // Float Skip Button at Top-End
        if (showSkipButton && currentPage < pageCount - 1) {
            val skipLabel = onboardingStyle?.skipButtonLabel ?: "Skip"
            TextButton(
                onClick = {
                    scope.launch {
                        component.cacheKey?.let { key ->
                            context.onboardingDataStore.edit { prefs ->
                                prefs[booleanPreferencesKey(key)] = true
                            }
                        }
                        onboardingStyle?.skipAction?.let { action ->
                            handleAction(action, onEvent, dataJson, state)
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 36.dp, end = 16.dp)
            ) {
                val textColor = onboardingStyle?.skipButtonStyle?.buttonTextColor?.convertToColor()
                    ?: MaterialTheme.colorScheme.onSurface
                Text(text = skipLabel, color = textColor)
            }
        }

        // Bottom Navigation Bar (Page Indicator + Next/Finish button)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Page Indicator
            if (showPageIndicator) {
                val activeColor = onboardingStyle?.pageIndicatorActiveColor?.convertToColor()
                    ?: MaterialTheme.colorScheme.primary
                val inactiveColor = onboardingStyle?.pageIndicatorInactiveColor?.convertToColor()
                    ?: MaterialTheme.colorScheme.outlineVariant

                when (onboardingStyle?.pageIndicatorStyle?.lowercase()?.trim()) {
                    "lines" -> LineIndicator(pageCount, currentPage, activeColor, inactiveColor)
                    "numbers" -> Text(
                        text = "${currentPage + 1} / $pageCount",
                        color = activeColor,
                        fontSize = 14.sp
                    )
                    else -> DotIndicator(pageCount, currentPage, activeColor, inactiveColor)
                }
            }

            // Next / Finish Action button
            if (showNextButton) {
                val isLastPage = currentPage == pageCount - 1
                val buttonLabel = if (isLastPage) {
                    onboardingStyle?.finishButtonLabel ?: "Get Started"
                } else {
                    onboardingStyle?.nextButtonLabel ?: "Next"
                }

                val buttonStyle = onboardingStyle?.nextButtonStyle
                val isFullWidth = buttonStyle?.buttonShape?.uppercase()?.trim() == "DEFAULT" || buttonStyle == null
                val btnModifier = if (isFullWidth) Modifier.fillMaxWidth() else Modifier.align(Alignment.End)

                StyledButton(
                    text = buttonLabel,
                    buttonStyle = buttonStyle,
                    modifier = btnModifier,
                    onClick = {
                        if (isLastPage) {
                            scope.launch {
                                component.cacheKey?.let { key ->
                                    context.onboardingDataStore.edit { prefs ->
                                        prefs[booleanPreferencesKey(key)] = true
                                    }
                                }
                                onboardingStyle?.nextAction?.let { action ->
                                    handleAction(action, onEvent, dataJson, state)
                                }
                            }
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(currentPage + 1)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun DotIndicator(pageCount: Int, currentPage: Int, activeColor: Color, inactiveColor: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until pageCount) {
            val isSelected = i == currentPage
            val scale by animateFloatAsState(targetValue = if (isSelected) 1.25f else 1.0f)
            val size = if (isSelected) 9.dp else 7.dp
            Box(
                modifier = Modifier
                    .size(size * scale)
                    .clip(CircleShape)
                    .background(if (isSelected) activeColor else inactiveColor)
            )
        }
    }
}

@Composable
private fun LineIndicator(pageCount: Int, currentPage: Int, activeColor: Color, inactiveColor: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until pageCount) {
            val isSelected = i == currentPage
            val width by animateDpAsState(targetValue = if (isSelected) 24.dp else 12.dp)
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .width(width)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isSelected) activeColor else inactiveColor)
            )
        }
    }
}

@Composable
private fun StyledButton(
    text: String,
    buttonStyle: ButtonStyle?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val buttonShape = when (buttonStyle?.buttonShape?.uppercase()?.trim()) {
        "ROUNDED" -> RoundedCornerShape((buttonStyle.buttonRounded ?: 8).dp)
        "CIRCLE" -> CircleShape
        else -> RoundedCornerShape(8.dp)
    }
    val containerColor = buttonStyle?.buttonColor?.convertToColor() ?: MaterialTheme.colorScheme.primary
    val textColor = buttonStyle?.buttonTextColor?.convertToColor() ?: MaterialTheme.colorScheme.onPrimary
    val textSize = buttonStyle?.buttonTextSize?.sp ?: 14.sp

    Button(
        onClick = onClick,
        shape = buttonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = textColor
        ),
        modifier = modifier
    ) {
        Text(text = text, fontSize = textSize, color = textColor)
    }
}
