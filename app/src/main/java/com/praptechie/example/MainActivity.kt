package com.praptechie.example

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.praptechie.example.ui.theme.ServerDrivenUIJetpackComposeTheme
import com.praptechie.serverdrivenuicompose.ServerDrivenUiHandler
import com.praptechie.serverdrivenuicompose.data_models.ServerDrivenEvent

class MainActivity : ComponentActivity() {

    private lateinit var serverDrivenUiHandler: ServerDrivenUiHandler

    // Screen Key: MUST match the Remote Config parameter name in SDUI Pro Dashboard
    private val SCREEN_KEY = "hell"

    // Default (offline / fallback) UI JSON
    private val DEFAULT_UI_JSON = """
    {
  "version": "1.0.0",
  "uiData": [
    {
      "type": "column",
      "spacing": 8,
      "style": {
        "modifier": {
          "padding": {
            "all": 0
          },
          "backgroundColor": "#ffffff"
        },
        "columnStyle": {
          "verticalArrangement": "top",
          "horizontalAlignment": "start",
          "spaceBy": 0,
          "enableScroll": true
        }
      },
      "children": [
        {
          "type": "row",
          "style": {
            "modifier": {
              "padding": {
                "all": 16
              }
            },
            "rowStyle": {
              "horizontalArrangement": "start",
              "verticalAlignment": "center",
              "spaceBy": 0
            }
          },
          "children": [
            {
              "type": "text",
              "itemSize": {
                "weight": null
              },
              "content": [
                {
                  "text": "Shopify Pro"
                }
              ],
              "style": {
                "textStyle": {
                  "fontSize": 20,
                  "textColor": "#1e293b",
                  "fontWeight": "bold"
                }
              }
            },
            {
              "type": "icon_button",
              "style": {
                "iconButtonStyle": {
                  "iconName": "shopping_bag",
                  "toggledIconName": null,
                  "iconSize": 24,
                  "tint": "#3b82f6",
                  "toggledTint": null,
                  "toggleStateFrom": null
                }
              }
            }
          ]
        },
        {
          "type": "card",
          "style": {
            "modifier": {
              "padding": {
                "left": 16,
                "right": 16
              }
            },
            "cardStyle": {
              "cardContainerColor": "#f1f5f9",
              "cardShape": 30,
              "cardPadding": {
                "all": 12
              }
            }
          },
          "children": [
            {
              "type": "row",
              "style": {
                "rowStyle": {
                  "horizontalArrangement": "start",
                  "verticalAlignment": "center",
                  "spaceBy": 0
                }
              },
              "children": [
                {
                  "type": "icon_button",
                  "style": {
                    "iconButtonStyle": {
                      "iconName": "search",
                      "toggledIconName": null,
                      "iconSize": 24,
                      "tint": "#94a3b8",
                      "toggledTint": null,
                      "toggleStateFrom": null
                    }
                  }
                },
                {
                  "type": "text",
                  "itemSize": {
                    "weight": null
                  },
                  "content": [
                    {
                      "text": "Search products..."
                    }
                  ],
                  "style": {
                    "modifier": {
                      "padding": {
                        "left": 8
                      }
                    },
                    "textStyle": {
                      "fontSize": 14,
                      "textColor": "#94a3b8",
                      "fontWeight": "normal"
                    }
                  }
                }
              ]
            }
          ]
        },
        {
          "type": "column",
          "spacing": 8,
          "style": {
            "modifier": {
              "padding": {
                "top": 16
              }
            },
            "columnStyle": {
              "verticalArrangement": "top",
              "horizontalAlignment": "start",
              "spaceBy": 0,
              "enableScroll": false
            }
          },
          "children": [
            {
              "type": "card",
              "style": {
                "modifier": {
                  "padding": {
                    "left": 16,
                    "right": 16
                  }
                },
                "cardStyle": {
                  "cardContainerColor": "#1e293b",
                  "cardShape": 16,
                  "cardPadding": {
                    "all": 0
                  }
                }
              },
              "children": [
                {
                  "type": "row",
                  "style": {
                    "rowStyle": {
                      "horizontalArrangement": "start",
                      "verticalAlignment": "center",
                      "spaceBy": 0
                    }
                  },
                  "children": [
                    {
                      "type": "column",
                      "itemSize": {
                        "widthPercent": 0.6
                      },
                      "spacing": 8,
                      "style": {
                        "modifier": {
                          "padding": {
                            "all": 20
                          }
                        },
                        "columnStyle": {
                          "verticalArrangement": "top",
                          "horizontalAlignment": "start",
                          "spaceBy": 0,
                          "enableScroll": false
                        }
                      },
                      "children": [
                        {
                          "type": "text",
                          "itemSize": {
                            "weight": null
                          },
                          "content": [
                            {
                              "text": "NEW ARRIVALS"
                            }
                          ],
                          "style": {
                            "textStyle": {
                              "fontSize": 10,
                              "textColor": "#3b82f6",
                              "fontWeight": "bold"
                            }
                          }
                        },
                        {
                          "type": "text",
                          "itemSize": {
                            "weight": null
                          },
                          "content": [
                            {
                              "text": "Style Redefined\nfor 2026"
                            }
                          ],
                          "style": {
                            "modifier": {
                              "padding": {
                                "top": 8
                              }
                            },
                            "textStyle": {
                              "fontSize": 22,
                              "textColor": "#ffffff",
                              "fontWeight": "bold"
                            }
                          }
                        },
                        {
                          "type": "button",
                          "text": "Checkout Now",
                          "style": {
                            "modifier": {
                              "padding": {
                                "top": 16
                              }
                            },
                            "buttonStyle": {
                              "buttonColor": "#3b82f6",
                              "buttonShape": "ROUNDED",
                              "buttonRounded": 8,
                              "buttonTextColor": "#ffffff",
                              "buttonTextSize": 12
                            }
                          }
                        }
                      ]
                    },
                    {
                      "type": "image",
                      "itemSize": {
                        "height": 160,
                        "widthPercent": 0.4
                      },
                      "imageUrl": "https://images.unsplash.com/photo-1491553895911-0055eca6402d?w=200"
                    }
                  ]
                }
              ]
            }
          ]
        },
        {
          "type": "column",
          "spacing": 8,
          "style": {
            "modifier": {
              "padding": {
                "all": 16
              }
            },
            "columnStyle": {
              "verticalArrangement": "top",
              "horizontalAlignment": "start",
              "spaceBy": 0,
              "enableScroll": false
            }
          },
          "children": [
            {
              "type": "row",
              "style": {
                "rowStyle": {
                  "horizontalArrangement": "spacebetween",
                  "verticalAlignment": "center",
                  "spaceBy": 0
                }
              },
              "children": [
                {
                  "type": "text",
                  "itemSize": {
                    "weight": null
                  },
                  "content": [
                    {
                      "text": "Categories"
                    }
                  ],
                  "style": {
                    "textStyle": {
                      "fontSize": 16,
                      "textColor": "#000000",
                      "fontWeight": "bold"
                    }
                  }
                },
                {
                  "type": "text",
                  "itemSize": {
                    "weight": null
                  },
                  "content": [
                    {
                      "text": "View All"
                    }
                  ],
                  "style": {
                    "textStyle": {
                      "fontSize": 12,
                      "textColor": "#3b82f6",
                      "fontWeight": "normal"
                    }
                  }
                }
              ]
            },
            {
              "type": "chip_group",
              "dataBinding": "@chips",
              "selectedStateKey": "selectedIndex",
              "scrollable": true,
              "chipTemplate": {
                "labelBinding": "{{item.title}}",
                "style": {
                  "backgroundColor": "#E0E0E0",
                  "selectedBackgroundColor": "#6200EE",
                  "textColor": "#000000",
                  "selectedTextColor": "#FFFFFF",
                  "borderRadius": 10,
                  "paddingHorizontal": 16,
                  "paddingVertical": 8
                },
                "action": null
              },
              "style": {
                "modifier": {
                  "padding": {
                    "top": 12
                  }
                }
              }
            }
          ]
        },
        {
          "type": "column",
          "spacing": 8,
          "style": {
            "modifier": {
              "padding": {
                "all": 16
              }
            },
            "columnStyle": {
              "verticalArrangement": "top",
              "horizontalAlignment": "start",
              "spaceBy": 0,
              "enableScroll": false
            }
          },
          "children": [
            {
              "type": "text",
              "itemSize": {
                "weight": null
              },
              "content": [
                {
                  "text": "Trending Now"
                }
              ],
              "style": {
                "modifier": {
                  "padding": {
                    "bottom": 16
                  }
                },
                "textStyle": {
                  "fontSize": 16,
                  "textColor": "#000000",
                  "fontWeight": "bold"
                }
              }
            },
            {
              "type": "grid",
              "itemSize": {
                "height": 480
              },
              "columns": 2,
              "spacing": 16,
              "dataBinding": "@posts",
              "itemTemplate": {
                "type": "column",
                "spacing": 8,
                "style": {
                  "columnStyle": {
                    "verticalArrangement": "top",
                    "horizontalAlignment": "start",
                    "spaceBy": 0,
                    "enableScroll": false
                  }
                },
                "children": [
                  {
                    "type": "card",
                    "style": {
                      "cardStyle": {
                        "cardContainerColor": "#f8fafc",
                        "cardShape": 16,
                        "cardPadding": {
                          "all": 0
                        }
                      }
                    },
                    "children": [
                      {
                        "type": "image",
                        "itemSize": {
                          "height": 160
                        },
                        "imageUrl": "{{item.image}}"
                      }
                    ]
                  },
                  {
                    "type": "text",
                    "itemSize": {
                      "weight": null
                    },
                    "content": [
                      {
                        "text": "{{item.title}}"
                      }
                    ],
                    "style": {
                      "modifier": {
                        "padding": {
                          "top": 8
                        }
                      },
                      "textStyle": {
                        "fontSize": 14,
                        "textColor": "#000000",
                        "fontWeight": "bold"
                      }
                    }
                  },
                  {
                    "type": "text",
                    "itemSize": {
                      "weight": null
                    },
                    "content": [
                      {
                        "text": "{{item.price}}"
                      }
                    ],
                    "style": {
                      "textStyle": {
                        "fontSize": 13,
                        "textColor": "#3b82f6",
                        "fontWeight": "bold"
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
  ]
}
    """

    // Runtime Data: values injected into the UI template
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
    """

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize the core SDUI renderer handler
        serverDrivenUiHandler = ServerDrivenUiHandler()


        // 2. Launch Compose UI
        setContent {
            ServerDrivenUIJetpackComposeTheme {
                Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
                    // 3. Render the Remote Screen with just the Screen Key and Data JSON
                    // The SDK handles Remote Config fetching and hot-swapping automatically!
                    serverDrivenUiHandler.ServerDrivenRemoteScreen(
                        screenKey = SCREEN_KEY,
                        dataJsonString = DATA_JSON,
                        defaultUiJson = DEFAULT_UI_JSON,
                        fetchIntervalSeconds = 0, // 0 = always fetch for dev; use 3600 for prod
                        onEvent = { event -> handleSduiEvent(event) },
                        onError = { errorMsg ->
                            Log.e("SDUI", "Render error: $errorMsg")
                            Toast.makeText(this@MainActivity, "SDUI Error: $errorMsg", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }
        }
    }

    private fun handleSduiEvent(event: ServerDrivenEvent) {
        when (event) {
            is ServerDrivenEvent.ButtonClicked -> {
                Log.d("SDUI", "Button tapped: ${event.actionId}")
                val text = event.parameters["text"] ?: "Button tapped"
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            }
            is ServerDrivenEvent.NavigationRequested -> {
                Log.d("SDUI", "Navigate to: ${event.screen}")
            }
            is ServerDrivenEvent.ChipSelected -> {
                Log.d("SDUI", "Chip selected index: ${event.index}")
            }
            else -> Log.d("SDUI", "Other event: $event")
        }
    }
}
