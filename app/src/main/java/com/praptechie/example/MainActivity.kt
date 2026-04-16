package com.praptechie.example

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.praptechie.example.ui.theme.ServerDrivenUIJetpackComposeTheme
import com.praptechie.serverdrivenuicompose.ServerDrivenUiHandler
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent
import com.praptechie.serverdrivenuicompose.remote_config.SduiRemoteConfig


class MainActivity : ComponentActivity() {

    private lateinit var serverDrivenUiHandler: ServerDrivenUiHandler
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
    val liveUiJson = mutableStateOf(uiJson)
    private val DATA_JSON = """
 {
  "posts": [
    {
      "title": "Wireless Headphone",
      "price": "$129.99",
      "image": "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80&w=400"
    },
    {
      "title": "Smart Watch Pro",
      "price": "$199.99",
      "image": "https://images.unsplash.com/photo-1523275335684-37898b6baf30?q=80&w=400"
    },
    {
      "title": "Bluetooth Speaker",
      "price": "$89.00",
      "image": "https://images.unsplash.com/photo-1608156639585-34054e815958?q=80&w=400"
    },
    {
      "title": "Noise Cancelling Earbuds",
      "price": "$159.99",
      "image": "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?q=80&w=400"
    }
  ],
  "grocery": [
    {
      "title": "Fresh Bananas",
      "weight": "5 pcs",
      "price": "$2.49",
      "image": "https://images.unsplash.com/photo-1603833665858-e61d17a86224?w=100"
    },
    {
      "title": "Organic Milk",
      "weight": "1L",
      "price": "$3.50",
      "image": "https://images.unsplash.com/photo-1550583724-125581fe2f8a?w=100"
    },
    {
      "title": "Whole Grain Bread",
      "weight": "400g",
      "price": "$4.20",
      "image": "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=100"
    },
    {
      "title": "Avocado",
      "weight": "1 pc",
      "price": "$1.99",
      "image": "https://images.unsplash.com/photo-1523049673857-eb18f1d7b578?w=100"
    }
  ],
  "chips": [
    {
      "title": "Electronics"
    },
    {
      "title": "Fashion"
    },
    {
      "title": "Home"
    },
    {
      "title": "Beauty"
    }
  ],
  "user": {
    "name": "Alex Rivera",
    "bio": "Product Designer & Coffee Enthusiast",
    "followers": "12.4k",
    "following": "842",
    "avatar": "https://i.pravatar.cc/150?u=alex"
  },
  "offer": {
    "title": "Flash Sale!",
    "description": "Get 20% cashback on your first grocery order today.",
    "code": "FIRST20"
  }
}
    """.trimIndent()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize Firebase Remote Config via SduiRemoteConfig.Builder screenKey MUST match what you created in the SDUI Pro Dashboard.
        SduiRemoteConfig.Builder(this)
            .screenKey("hell")
            .defaultJson(uiJson)          // shown instantly on first launch
            .fetchIntervalSeconds(0)               // 0 = always fetch (dev mode); use 3600 for prod or as per the use case
            .onUpdate { fetchedJson ->
                Log.d("SDUI", "[hell] Remote Config updated. JSON length: ${fetchedJson.length}")
                if (fetchedJson.isNotBlank()) {
                    liveUiJson.value = fetchedJson  // hot-swap UI without restart
                }
            }
            .build()
            .fetch()

        //  2. Initialize the core SDUI renderer handler
        serverDrivenUiHandler = ServerDrivenUiHandler()

        //  3. Launch Compose UI
        setContent {
            ServerDrivenUIJetpackComposeTheme {
                HelloWorldScreen()
            }
        }
    }

    /**
     * HelloWorldScreen - renders the SDUI layout for screenKey "hell".
     * The layout hot-swaps automatically when Remote Config delivers a new value.
     */
    @Composable
    fun HelloWorldScreen() {
        val uiJson by liveUiJson

        Box(modifier = Modifier.fillMaxSize().background(Color.LightGray.copy(.5f))) {
            serverDrivenUiHandler.ServerDrivenContainer(
                uiJsonString = uiJson,
                dataJsonString = DATA_JSON,
                onEvent = { event ->
                    when (event) {
                        is ServerDrivenEvent.ButtonClicked -> {
                            Log.d("SDUI", "[helloWorld] Button: id=${event.actionId} params=${event.parameters}")
                            val text = event.parameters["text"] ?: "Button tapped"
                            Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
                        }

                        is ServerDrivenEvent.NavigationRequested -> {
                            Log.d("SDUI", "[helloWorld] Navigate to: ${event.screen}, params=${event.parameters}")
                            // TODO: replace with your NavController.navigate(event.screen)
                        }

                        is ServerDrivenEvent.ChipSelected -> {
                            Log.d("SDUI", "[helloWorld] Chip selected: id=${event.chipId} index=${event.index}")
                        }

                        is ServerDrivenEvent.ItemClicked -> {
                            Log.d("SDUI", "[helloWorld] Item clicked: id=${event.itemId} index=${event.index}")
                        }

                        is ServerDrivenEvent.StateUpdateRequested -> {
                            Log.d("SDUI", "[helloWorld] State update: key=${event.key} value=${event.value}")
                        }

                        else -> {
                            Log.d("SDUI", "[helloWorld] Unhandled event: $event")
                        }
                    }
                },
                onError = { errorMsg ->
                    Log.e("SDUI", "[hell] Render error: $errorMsg")
                    Toast.makeText(this@MainActivity, "SDUI Error: $errorMsg", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}

