package com.praptechie.example

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.praptechie.example.ui.theme.ServerDrivenUIJetpackComposeTheme
import com.praptechie.serverdrivenuicompose.ServerDrivenUiHandler
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.FireUI

class MainActivity : ComponentActivity() {

    private lateinit var serverDrivenUiHandler: ServerDrivenUiHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize the core SDUI renderer handler with our test screens
        serverDrivenUiHandler = ServerDrivenUiHandler.Builder()
            .defaultUiJson("splash", SPLASH_UI_JSON)
            .defaultUiJson("onboarding", ONBOARDING_UI_JSON)
            .defaultUiJson("main", MAIN_UI_JSON)
            .defaultDataJson("splash", "{}")
            .defaultDataJson("onboarding", "{}")
            .defaultDataJson("main", "{}")
            .fetchIntervalSeconds(0) // disable Remote Config caching for instant dev/test reload
            .build()

        // 2. Launch Compose UI with navigation flow
        setContent {
            ServerDrivenUIJetpackComposeTheme {
                var currentScreenKey by remember { mutableStateOf("splash") }


                    serverDrivenUiHandler.FireUI(
                        screenKey = currentScreenKey,
                        onEvent = { event ->
                            when (event) {
                                is ServerDrivenEvent.NavigationRequested -> {
                                    Log.d("SDUI", "Navigation requested to: ${event.screen}")
                                    if (event.screen == "back") {
                                        // Simple reverse routing
                                        currentScreenKey = when (currentScreenKey) {
                                            "main" -> "onboarding"
                                            "onboarding" -> "splash"
                                            else -> "splash"
                                        }
                                    } else {
                                        currentScreenKey = event.screen
                                    }
                                }
                                is ServerDrivenEvent.ButtonClicked -> {
                                    Log.d("SDUI", "Button clicked: ${event.actionId}")
                                    val text = event.parameters["text"] ?: "Button clicked!"
                                    Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
                                }
                                is ServerDrivenEvent.ChipSelected -> {
                                    Log.d("SDUI", "Chip selected: ${event.index}")
                                }
                                else -> Log.d("SDUI", "Event received: $event")
                            }
                        },
                        onError = { errorMsg ->
                            Log.e("SDUI", "Render error: $errorMsg")
                            Toast.makeText(this@MainActivity, "SDUI Error: $errorMsg", Toast.LENGTH_LONG).show()
                        }
                    ) {
                        // Trailing lambda container content
                    }

            }
        }
    }


    companion object {
        private const val SPLASH_UI_JSON = """
        {
  "version": "1.0.0",
  "uiData": [
    {
      "type": "splash_screen",
      "slot": "splash",
      "duration": 2500,
      "background": {
        "type": "color",
        "color": "#121212"
      },
      "tagline": null
    }
  ]
}
        """

        private const val ONBOARDING_UI_JSON = """
        {
          "version": "1.0.0",
          "uiData": [
            {
              "type": "onboarding_screen",
              "cacheKey": "onboard_test_key",
              "showEveryTime": true,
              "style": {
                "background": {
                  "type": "color",
                  "color": { "light": "#F8FAFC", "dark": "#0F172A" }
                },
                "nextButtonLabel": "Next Slide",
                "finishButtonLabel": "Start App",
                "skipButtonLabel": "Skip Intro",
                "showPageIndicator": true,
                "pageIndicatorStyle": "lines",
                "pageIndicatorActiveColor": { "light": "#4F46E5", "dark": "#818CF8" },
                "pageIndicatorInactiveColor": { "light": "#CBD5E1", "dark": "#475569" },
                "transitionAnimation": "scale",
                "nextAction": { "perform": "navigate", "parameters": { "screen": "main" } },
                "skipAction": { "perform": "navigate", "parameters": { "screen": "main" } }
              },
              "pages": [
                {
                  "title": "Welcome to FireUI",
                  "titleStyle": { "fontSize": 24, "textColor": { "light": "#0F172A", "dark": "#FFFFFF" }, "fontWeight": "bold" },
                  "subtitle": "Build and test dynamic server-driven UI elements seamlessly.",
                  "subtitleStyle": { "fontSize": 16, "textColor": { "light": "#475569", "dark": "#94A3B8" } },
                  "media": {
                    "type": "image",
                    "url": "https://images.unsplash.com/photo-1551288049-bebda4e38f71?auto=format&fit=crop&w=400&q=80"
                  }
                },
                {
                  "title": "Interactive Controls",
                  "titleStyle": { "fontSize": 24, "textColor": { "light": "#0F172A", "dark": "#FFFFFF" }, "fontWeight": "bold" },
                  "subtitle": "Support actions, bottom sheets, dialogues, and custom event handlers.",
                  "subtitleStyle": { "fontSize": 16, "textColor": { "light": "#475569", "dark": "#94A3B8" } },
                  "media": {
                    "type": "image",
                    "url": "https://images.unsplash.com/photo-1460925895917-afdab827c52f?auto=format&fit=crop&w=400&q=80"
                  }
                }
              ]
            }
          ]
        }
        """

        private const val MAIN_UI_JSON = """
        {
          "version": "1.0.0",
          "uiData": [
            {
              "type": "top_app_bar",
              "title": "FireUI Home",
              "subtitle": "Version 1.2.3",
              "style": {
                "topAppBarStyle": {
                  "backgroundColor": { "light": "#F1F5F9", "dark": "#1E293B" },
                  "elevation": 4.0,
                  "cornerRadius": 16.0,
                  "scrollBehavior": "pinned",
                  "overlayContent": false
                }
              },
              "navigationIcon": {
                "type": "back",
                "tintColor": { "light": "#4F46E5", "dark": "#818CF8" }
              },
              "actions": [
                {
                  "type": "icon",
                  "iconName": "search",
                  "action": { "perform": "button_click", "parameters": { "text": "Search tap!" } }
                },
                {
                  "type": "dropdown_trigger",
                  "iconName": "more_vert",
                  "dropdownMenu": {
                    "items": [
                      { "label": "About SDK", "iconName": "info", "action": { "perform": "button_click", "parameters": { "text": "FireUI Compose SDK v1.2.3" } } },
                      { "label": "Reset Onboarding", "iconName": "settings", "action": { "perform": "navigate", "parameters": { "screen": "onboarding" } } }
                    ]
                  }
                }
              ]
            },
            {
              "type": "column",
              "spacing": 16,
              "style": {
                "modifier": {
                  "padding": { "all": 24 }
                }
              },
              "children": [
                {
                  "type": "text",
                  "content": [{ "text": "Explore New Components" }],
                  "style": { "textStyle": { "fontSize": 22, "fontWeight": "bold", "textColor": "#4F46E5" } }
                },
                {
                  "type": "card",
                  "style": {
                    "cardStyle": { "cardShape": 12, "cardContainerColor": { "light": "#F8FAFC", "dark": "#0F172A" } }
                  },
                  "children": [
                    {
                      "type": "text",
                      "content": [{ "text": "This screen validates the newly integrated TopAppBar, upgraded BottomBar with badges/images, Onboarding pages and preference caching, and animated Splash logo transitions." }],
                      "style": { "textStyle": { "fontSize": 14 } }
                    }
                  ]
                }
              ]
            },
            {
              "type": "bottom_bar",
              "selectedStateKey": "selected_tab",
              "style": {
                "bottomBarStyle": {
                  "type": "floating",
                  "backgroundColor": { "light": "#FFFFFF", "dark": "#1E293B" },
                  "selectedColor": { "light": "#4F46E5", "dark": "#818CF8" },
                  "unselectedColor": { "light": "#94A3B8", "dark": "#64748B" },
                  "indicatorColor": { "light": "#E0E7FF", "dark": "#312E81" },
                  "elevation": 8.0,
                  "cornerRadius": 20.0,
                  "showLabels": true,
                  "showIndicator": true,
                  "borderColor": { "light": "#E2E8F0", "dark": "#334155" },
                  "height": 64.0
                }
              },
              "bottomBarItems": [
                {
                  "iconName": "home",
                  "text": "Home",
                  "badge": "NEW",
                  "badgeColor": { "light": "#EF4444", "dark": "#F87171" },
                  "action": { "perform": "button_click", "parameters": { "text": "Home tab clicked!" } }
                },
                {
                  "iconName": "settings",
                  "text": "Settings",
                  "action": { "perform": "button_click", "parameters": { "text": "Settings tab clicked!" } }
                }
              ]
            }
          ]
        }
        """
    }
}
