package com.praptechie.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.praptechie.example.ui.theme.ServerDrivenUIJetpackComposeTheme
import com.praptechie.serverdrivenuicompose.ServerDrivenUiHandler
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent

class MainActivity : ComponentActivity() {
    private val serverDrivenUiHandler = ServerDrivenUiHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ServerDrivenUIJetpackComposeTheme {
                CatalogueScreen()

            }
        }
    }

    @Composable
    fun CatalogueScreen() {
        val uiJson = """
    {
      "version": "1.0.0",
      "uiData": [
        {
          "type": "column",
          "style": {
            "modifier": {"padding": {"all": 16}},
            "columnStyle": {"spaceBy": 16}
          },
          "children": [
            {
              "type": "text",
              "content": [{"text": "{{title}}"}],
              "style": {
                "textStyle": {"fontSize": 24, "fontWeight": "bold"}
              }
            },
            {
              "type": "chip_group",
              "dataBinding": "@categories",
              "selectedStateKey": "selectedCategory",
              "chipTemplate": {
                "labelBinding": "{{item.name}}",
                "style": {
                  "backgroundColor": "#E0E0E0",
                  "selectedBackgroundColor": "#6200EE",
                  "textColor": "#000000",
                  "selectedTextColor": "#FFFFFF",
                  "borderRadius": 20,
                  "paddingHorizontal": 16,
                  "paddingVertical": 8
                }
              }
            },
            {
              "type": "grid",
              "columns": 3,
              "spacing": 12,
              "dataBinding": "@images",
              "itemTemplate": {
                "type": "card",
                "style": {
                  "modifier": {
                    "clip": {"shape": "rounded", "radius": 12}
                  }
                },
                "children": [
                  {
                    "type": "image",
                    "imageUrl": "{{item.url}}",
                    "action": {
                      "perform": "navigate",
                      "parameters": {
                        "screen": "detail",
                        "imageId": "{{item.id}}"
                      }
                    }
                  }
                ]
              }
            }
          ]
        }
      ]
    }
    """

        val dataJson = """
    {
      "title": "Food Gallery",
      "categories": [
        {"id": "1", "name": "Pizza"},
        {"id": "2", "name": "Burgers"},
        {"id": "3", "name": "Vegetables"}
      ],
      "images": [
        {"id": "p1", "url": "https://i.ibb.co/0RBsDw4j/1338709.png"},
        {"id": "p2", "url": "https://i.ibb.co/0RBsDw4j/1338709.png"},
        {"id": "p3", "url": "https://i.ibb.co/0RBsDw4j/1338709.png"}
      ]
    }
    """

        serverDrivenUiHandler.ServerDrivenContainer(
            uiJsonString = uiJson,
            dataJsonString = dataJson,
            onEvent = { event ->
                when (event) {
                    is ServerDrivenEvent.NavigationRequested -> {
                        // Navigate to detail screen
                        Log.d("Navigation", "Screen: ${event.screen}, Params: ${event.parameters}")
                    }
                    is ServerDrivenEvent.ChipSelected -> {
                        // Filter images by category
                        Log.d("Chip", "Selected: ${event.chipId} at ${event.index}")
                    }
                    is ServerDrivenEvent.ButtonClicked -> {
                        // Handle button action
                    }
                    else -> {}
                }
            }
        )
    }
}

