# Server-Driven-UI-Jetpack-Compose

[![](https://jitpack.io/v/pranav-panwar/Server-Driven-UI-Jetpack-Compose.svg)](https://jitpack.io/#pranav-panwar/Server-Driven-UI-Jetpack-Compose)

A flexible and robust SDK for building **dynamic, server-driven UIs in Jetpack Compose**, powered by JSON from the backend or Firebase Realtime Database.

---

## 📦 Installation

Add the following to your project using [JitPack](https://jitpack.io):

### Step 1: Add JitPack repository

In your root `settings.gradle` (or `settings.gradle.kts`):

```gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2: Add the dependency

In your app's `build.gradle` (or `build.gradle.kts`):

```gradle
dependencies {
    implementation 'com.github.pranav-panwar:Server-Driven-UI-Jetpack-Compose:1.0.0'
}
```

> **Note:** Replace `1.0.0` with the latest version from [releases](https://github.com/pranav-panwar/Server-Driven-UI-Jetpack-Compose/releases).

---

## 🚀 Quickstart

### Minimal Example

```kotlin
@Composable
fun MyScreen() {
    val uiJsonString = """
    {
      "version": "1.0.0",
      "uiData": [
        {
          "type": "column",
          "margin": null,
          "style": {
            "modifier": { "padding": { "all": 16 } },
            "columnStyle": { "spaceBy": 8 }
          },
          "children": [
            {
              "type": "text",
              "margin": null,
              "content": [{ "text": "Welcome to Server-Driven UI!" }],
              "style": {
                "textStyle": {
                  "fontSize": 24,
                  "textColor": "#000000",
                  "fontWeight": "bold"
                }
              }
            }
          ]
        }
      ]
    }
    """.trimIndent()

    ServerDrivenContainer(
        uiJsonString = uiJsonString,
        dataJsonString = "{}",
        onEvent = { event ->
            when (event) {
                // Handle events (navigation, actions, etc.)
                else -> {}
            }
        }
    )
}
```

---

## 📝 JSON Structure Guide

### Root Structure

```json
{
  "version": "1.0.0",
  "uiData": [ /* array of UI components */ ]
}
```

- **`version`**: SDK version (for compatibility checking)
- **`uiData`**: Root array containing UI components (must start with a layout like `column` or `row`)

### Component Structure

Every component has this general structure:

```json
{
  "type": "column",           // Required: Component type
  "margin": { /* ... */ },    // Optional: Margins around the component
  "style": { /* ... */ },     // Optional: Styling properties
  "children": [ /* ... */ ],  // Optional: Child components (for containers)
  "action": { /* ... */ }     // Optional: Interactions/actions
}
```

---

## 🧩 Supported UI Elements

| Type | Description | Usage |
|------|-------------|-------|
| **column** | Vertical layout container | Contains `children`, supports spacing |
| **row** | Horizontal layout | Contains `children`, supports spacing |
| **box** | Positioning container | Use for overlays, custom layouts |
| **card** | Card-style container | Supports border radius, shadow, background color |
| **text** | Display text | Use `content` array with text data |
| **image** | Async image loading | Supports clipping, sizing |
| **icon_button** | Interactive icon | Supports toggle states, colors |
| **button** | Clickable button | Supports text, styling, actions |
| **lazy_column** | Scrolling vertical list | Requires `dataBinding` and `itemTemplate` |
| **lazy_vertical_staggered_grid** | Masonry/staggered grid | For photo feeds, complex layouts |
| **grid** | Regular grid layout | Columns, spacing, items |
| **chip_group** | Group of selectable chips | For filters, tags |

---

## 🎨 Customization Guide

### Text Styling

```json
"style": {
  "textStyle": {
    "fontSize": 20,
    "textColor": "#3333EE",
    "fontWeight": "bold"
  }
}
```

**Options:**
- `fontSize`: Int (pixels)
- `textColor`: String (hex color, e.g., `"#000000"`)
- `fontWeight`: `"bold"`, `"normal"`, `"medium"`, etc.

### Padding & Margins

```json
{
  "margin": {
    "top": 8,
    "bottom": 8,
    "left": 8,
    "right": 8,
    "all": null  // Use "all" for uniform margin
  },
  "style": {
    "modifier": {
      "padding": {
        "all": 16
      }
    }
  }
}
```

### Shapes & Corners

```json
"style": {
  "modifier": {
    "clip": {
      "shape": "rounded",  // Options: "rounded"
      "radius": 12         // Corner radius in dp
    }
  }
}
```

### Background Color

```json
"style": {
  "modifier": {
    "backgroundColor": "#F0FFF0"
  }
}
```

### Card Styling

```json
"style": {
  "cardStyle": {
    "cardShape": 12,
    "cardContainerColor": "#FFFFFF",
    "cardPadding": { "all": 10 }
  }
}
```

### Icon Button with Toggle

```json
{
  "type": "icon_button",
  "size": 24,
  "style": {
    "iconButtonStyle": {
      "iconName": "heart_border",
      "toggledIconName": "heart",
      "iconSize": 24,
      "tint": "#999999",
      "toggledTint": "#FF3B30",
      "toggleStateFrom": "@item.isLiked"  // Bind to data
    }
  },
  "action": {
    "perform": "like",
    "parameters": { "itemId": "{{item.id}}" }
  }
}
```

### Column & Row Arrangements

```json
"style": {
  "columnStyle": {
    "verticalArrangement": "center",    // top, center, bottom, space_between, space_around
    "horizontalAlignment": "start",     // start, center, end
    "spaceBy": 8                        // Space between children
  }
}
```

```json
"style": {
  "rowStyle": {
    "horizontalArrangement": "center",  // start, center, end, space_between
    "verticalAlignment": "center",      // top, center, bottom
    "spaceBy": 8
  }
}
```

---

## 📊 Dynamic Data Binding

### Using Data in Lists/Grids

When using `lazy_vertical_staggered_grid`, `lazy_column`, or `grid`, provide data via `dataJsonString`:

```kotlin
val dataJsonString = """
{
  "posts": [
    {
      "id": "1",
      "displayName": "John Doe",
      "photoUrl": "https://example.com/photo1.jpg",
      "description": "Beautiful sunset",
      "isLiked": false,
      "likesCount": 42
    },
    {
      "id": "2",
      "displayName": "Jane Smith",
      "photoUrl": "https://example.com/photo2.jpg",
      "description": "Mountain view",
      "isLiked": true,
      "likesCount": 128
    }
  ]
}
""".trimIndent()
```

### Binding in UI JSON

Use `{{item.fieldName}}` to reference data:

```json
{
  "type": "lazy_vertical_staggered_grid",
  "dataBinding": "@posts",          // Bind to "posts" array
  "columns": 2,
  "verticalSpacing": 12,
  "horizontalSpacing": 12,
  "itemTemplate": {
    "type": "card",
    "children": [
      {
        "type": "image",
        "imageUrl": "{{item.photoUrl}}"  // Dynamic URL
      },
      {
        "type": "text",
        "content": [{ "text": "{{item.displayName}}" }]  // Dynamic text
      },
      {
        "type": "text",
        "content": [{ "text": "{{item.description}}" }]
      }
    ]
  }
}
```

---

## ✅ What TO Do

- ✅ Always start your JSON with a root layout (`"type": "column"` recommended)
- ✅ Provide `dataJsonString` when using `dataBinding` (even if empty: `"{}"`)
- ✅ Use `{{item.field}}` syntax inside templates for lists/grids
- ✅ Use `@dataKey` format for data binding (e.g., `"@posts"` binds to the `posts` array)
- ✅ Validate your JSON before sending from the server
- ✅ Use meaningful component names and organize hierarchically
- ✅ Test UI changes without app updates (main benefit!)
- ✅ Use hex colors for consistency (e.g., `"#FF5733"`)

## 🚫 What NOT to Do

- ❌ Don't omit the `"type"` field in components (it's required)
- ❌ Don't use data fields that don't exist in your `dataJsonString`
- ❌ Don't use raw image URLs without protocols (`http://` or `https://`)
- ❌ Don't send malformed or incomplete JSON
- ❌ Don't store sensitive data (passwords, tokens) in the UI JSON
- ❌ Don't nest components too deeply (may cause performance issues)
- ❌ Don't use invalid color hex codes
- ❌ Don't forget to include `margin: null` in your JSON (or other required nullable fields)

---

## 📋 Real-World Example: Photo Feed

Here's a complete example of a photo feed UI:

### UI JSON

```json
{
  "version": "1.0.0",
  "uiData": [
    {
      "type": "column",
      "margin": null,
      "style": {
        "modifier": { "padding": { "all": 12 } },
        "columnStyle": { "spaceBy": 8 }
      },
      "children": [
        {
          "type": "lazy_vertical_staggered_grid",
          "margin": null,
          "dataBinding": "@posts",
          "columns": 2,
          "verticalSpacing": 12,
          "horizontalSpacing": 12,
          "itemTemplate": {
            "type": "card",
            "margin": null,
            "style": {
              "cardStyle": {
                "cardShape": 12,
                "cardContainerColor": "#FFFFFF",
                "cardPadding": { "all": 10 }
              }
            },
            "children": [
              {
                "type": "column",
                "margin": null,
                "style": {
                  "modifier": { "padding": { "all": 1 } },
                  "columnStyle": { "spaceBy": 6 }
                },
                "children": [
                  {
                    "type": "image",
                    "margin": null,
                    "imageUrl": "{{item.photoUrl}}",
                    "itemSize": { "width": 160, "height": 140 },
                    "style": {
                      "modifier": {
                        "clip": { "shape": "rounded", "radius": 12 }
                      }
                    }
                  },
                  {
                    "type": "text",
                    "margin": null,
                    "content": [{ "text": "{{item.displayName}}" }],
                    "style": {
                      "textStyle": {
                        "fontSize": 14,
                        "textColor": "#000000",
                        "fontWeight": "bold"
                      }
                    }
                  },
                  {
                    "type": "text",
                    "margin": null,
                    "content": [{ "text": "{{item.description}}" }],
                    "style": {
                      "textStyle": {
                        "fontSize": 12,
                        "textColor": "#666666",
                        "fontWeight": "normal"
                      }
                    }
                  },
                  {
                    "type": "row",
                    "margin": null,
                    "itemSize": { "width": 100, "height": 40 },
                    "style": {
                      "rowStyle": {
                        "horizontalArrangement": "start",
                        "spaceBy": 4
                      }
                    },
                    "children": [
                      {
                        "type": "icon_button",
                        "margin": null,
                        "size": 20,
                        "style": {
                          "iconButtonStyle": {
                            "toggleStateFrom": "@item.isLiked",
                            "iconName": "heart_border",
                            "toggledIconName": "heart",
                            "iconSize": 20,
                            "tint": "#999999",
                            "toggledTint": "#FF3B30"
                          }
                        },
                        "action": {
                          "perform": "like",
                          "parameters": {
                            "postId": "{{item.id}}"
                          }
                        }
                      },
                      {
                        "type": "text",
                        "margin": null,
                        "content": [{ "text": "{{item.likesCount}}" }],
                        "style": {
                          "textStyle": {
                            "fontSize": 11,
                            "textColor": "#666666",
                            "fontWeight": "medium"
                          }
                        }
                      }
                    ]
                  }
                ]
              }
            ]
          }
        }
      ]
    }
  ]
}
```

### Data JSON

```json
{
  "posts": [
    {
      "id": "post1",
      "displayName": "Pranav Panwar",
      "photoUrl": "https://firebasestorage.googleapis.com/v0/b/shayari-ai.appspot.com/o/users_data%2Fuser1.jpg",
      "description": "Beautiful sunset at the beach",
      "isLiked": true,
      "likesCount": 245
    },
    {
      "id": "post2",
      "displayName": "Jane Smith",
      "photoUrl": "https://firebasestorage.googleapis.com/v0/b/shayari-ai.appspot.com/o/users_data%2Fuser2.jpg",
      "description": "Mountain adventures",
      "isLiked": false,
      "likesCount": 128
    }
  ]
}
```

### Kotlin Code

```kotlin
@Composable
fun PhotoFeedScreen(viewModel: PhotoViewModel) {
    val uiJson = /* load from Firebase or API */
    val dataJson = /* load from Firebase or API */

    ServerDrivenContainer(
        uiJsonString = uiJson,
        dataJsonString = dataJson,
        onEvent = { event ->
            when (event) {
                is ServerDrivenEvent.ActionPerformed -> {
                    when (event.action.perform) {
                        "like" -> {
                            val postId = event.action.parameters["postId"]
                            viewModel.toggleLike(postId)
                        }
                    }
                }
            }
        }
    )
}
```

---

## 🔌 Handling Actions & Events

Register event handlers to respond to user interactions:

```kotlin
ServerDrivenContainer(
    uiJsonString = uiJsonString,
    dataJsonString = dataJsonString,
    onEvent = { event ->
        Log.d("ServerDrivenUI", "Event received: $event")
        when (event) {
            is ServerDrivenEvent.ActionPerformed -> {
                val action = event.action.perform
                when (action) {
                    "navigate" -> navigateToScreen(event.action.parameters["screen"])
                    "like" -> likePost(event.action.parameters["postId"])
                    "share" -> shareContent(event.action.parameters["text"])
                }
            }
            else -> {}
        }
    }
)
```

---

## 🔄 Firebase Integration

Fetch JSON from Firebase Realtime Database:

```kotlin
val databaseRef = FirebaseDatabase.getInstance().getReference("ui_config")

databaseRef.addValueEventListener(object : ValueEventListener {
    override fun onDataChange(snapshot: DataSnapshot) {
        val uiJsonString = snapshot.value as? String
        if (uiJsonString != null) {
            // Update your state/viewmodel
            viewModel.updateUI(uiJsonString)
        }
    }

    override fun onCancelled(error: DatabaseError) {
        Log.e("Firebase", "Error reading UI config", error.toException())
    }
})
```

---

## 📚 Advanced Features

### Custom Modifiers

Use `modifier` properties for fine-grained control:

```json
"style": {
  "modifier": {
    "backgroundColor": "#F5F5F5",
    "padding": { "all": 16 },
    "clip": { "shape": "rounded", "radius": 8 },
    "onClick": {
      "action": { "perform": "navigate", "parameters": { "screen": "details" } }
    }
  }
}
```

### Conditional Rendering

Use data binding to show/hide elements based on data:

```json
"content": [{ "text": "{{item.displayName}}" }]
```

---

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| JSON parsing error | Validate JSON syntax, ensure all required fields present, check for escaped quotes |
| Data not showing | Verify `dataJsonString` is valid JSON, ensure field names match `{{item.field}}` |
| Images not loading | Check image URLs have `http://` or `https://`, verify image accessibility |
| UI not updating | Ensure `dataJsonString` changes trigger recomposition (use `remember` with keys) |
| Events not firing | Verify `action` object is properly formatted with `perform` and `parameters` |

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request or open an issue.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 💬 Support

For questions, issues, or suggestions:

- Open an [issue](https://github.com/pranav-panwar/Server-Driven-UI-Jetpack-Compose/issues)

---

**Happy dynamic UI building!** 🚀
