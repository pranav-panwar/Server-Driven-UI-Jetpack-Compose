# Server-Driven-UI-Jetpack-Compose

[![](https://jitpack.io/v/pranav-panwar/Server-Driven-UI-Jetpack-Compose.svg)](https://jitpack.io/#pranav-panwar/Server-Driven-UI-Jetpack-Compose)

A flexible and robust SDK for building **dynamic, server-driven UIs in Jetpack Compose**, powered by JSON from the backend or Firebase Realtime Database.

---

## 📦 UI Designer

[SDUI Designer](https://sduidesigner.netlify.app/)

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
    implementation 'com.github.pranav-panwar:Server-Driven-UI-Jetpack-Compose:1.1.1'
}
```

> **Note:** Replace `1.0.0` with the latest version from [releases](https://github.com/pranav-panwar/Server-Driven-UI-Jetpack-Compose/releases).

---

## 🚀 Quickstart

### ⚡ Option 1: Automated Synchronization (Remote Config & FireUI)
The SDK provides a high-level `FireUI` wrapper composable that handles Firebase Remote Config fetching, caching, and state management automatically.

First, initialize the handler with default configurations:
```kotlin
val serverDrivenUiHandler = ServerDrivenUiHandler.Builder()
    .defaultUiJson("home_tab_config", fallbackJson) // Fallback offline JSON
    .defaultDataJson("home_tab_config", myRuntimeData) // Fallback data JSON
    .fetchIntervalSeconds(3600) // Fetch from Firebase Remote Config once per hour
    .build()
```

Then, wrap your composable:
```kotlin
@Composable
fun MyScreen(serverDrivenUiHandler: ServerDrivenUiHandler) {
    serverDrivenUiHandler.FireUI(
        screenKey = "home_tab_config",   // Remote Config key
        onEvent = { event ->
            // Handle events
        }
    ) {
        // User's own Composable content here
        MainScreen()
    }
}
```

### 🛠 Option 2: Imperative Loading (Manual Injection)
You can inject UI templates and dynamic data imperatively directly into the handler:

```kotlin
// Initialize the handler
val serverDrivenUiHandler = ServerDrivenUiHandler()

// Load UI and data imperatively at runtime
serverDrivenUiHandler.loadUI("home_tab_config", uiJsonString)
serverDrivenUiHandler.loadData("home_tab_config", myDataClassInstance)

@Composable
fun MyScreen(serverDrivenUiHandler: ServerDrivenUiHandler) {
    serverDrivenUiHandler.FireUI(
        screenKey = "home_tab_config",
        onEvent = { event ->
            // Handle events
        }
    ) {
        // User's own UI
        Text("Welcome to Server-Driven UI!")
    }
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
  "style": { /* ... */ },     // Optional: Styling properties
  "children": [ /* ... */ ],  // Optional: Child components (for containers)
  "action": { /* ... */ }     // Optional: Interactions/actions
}
```

---

## 🧩 Supported UI Elements

| Type | Description | Usage |
|------|-------------|-------|
| **column** | Vertical layout container | Contains `children`, supports spacing, alignment |
| **row** | Horizontal layout | Contains `children`, supports spacing, alignment |
| **card** | Card-style container | Supports border radius, shadow, background color |
| **text** | Display text | Use `content` array with text data |
| **image** | Async image loading | Supports clipping, sizing |
| **icon_button** | Interactive icon | Supports toggle states, colors |
| **button** | Clickable button | Supports text, styling, actions |
| **lazy_column** | Scrolling vertical list | Requires `dataBinding` and `itemTemplate` |
| **lazy_vertical_staggered_grid** | Masonry/staggered grid | For photo feeds, complex layouts |
| **grid** | Regular grid layout | Columns, spacing, items |
| **chip_group** | Group of selectable chips | For filters, tags |
| **box** | Layered container | Stacks children (Z-index support) |
| **spacer** | Fixed spacing | Adds gaps between components |
| **divider** | Visual separator | Horizontal line with custom thickness |
| **bottom_bar** | Navigation bar | Fixed bottom bar with icons/labels |

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

### Padding

```json

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

## 📐 Responsive Layouts & Sizing

The SDK uses a powerful sizing engine (`itemSize`) that supports both fixed and relative dimensions.

| Property | Type | Description |
| :--- | :--- | :--- |
| **`width` / `height`** | `Int` | Fixed size in DP. |
| **`widthPercent`** | `Float` | Occupy portion of parent width (`0.0` to `1.0`). |
| **`heightPercent`** | `Float` | Occupy portion of parent height (`0.0` to `1.0`). |
| **`weight`** | `Float` | Proportional distribution within `column` or `row`. |

**Example: Proportional Rows**
```json
{
  "type": "row",
  "children": [
    {
      "type": "text",
      "itemSize": { "weight": 1.0 },
      "content": [{ "text": "I take 1/3 space" }]
    },
    {
      "type": "text",
      "itemSize": { "weight": 2.0 },
      "content": [{ "text": "I take 2/3 space" }]
    }
  ]
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
      "style": {
        "modifier": { "padding": { "all": 12 } },
        "columnStyle": { "spaceBy": 8 }
      },
      "children": [
        {
          "type": "lazy_vertical_staggered_grid",
          "dataBinding": "@posts",
          "columns": 2,
          "verticalSpacing": 12,
          "horizontalSpacing": 12,
          "itemTemplate": {
            "type": "card",
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
                "style": {
                  "modifier": { "padding": { "all": 1 } },
                  "columnStyle": { "spaceBy": 6 }
                },
                "children": [
                  {
                    "type": "image",
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
fun PhotoFeedScreen(handler: ServerDrivenUiHandler, viewModel: PhotoViewModel) {
    handler.FireUI(
        screenKey = "photo_feed",
        onEvent = { event ->
            when (event) {
                is ServerDrivenEvent.ButtonClicked -> {
                    if (event.actionId == "like") {
                        val postId = event.parameters["postId"]
                        viewModel.toggleLike(postId)
                    }
                }
                else -> {}
            }
        }
    ) {
        // User content
    }
}
```

---

## 🔌 Handling Actions & Events

Register event handlers to respond to user interactions:

```kotlin
handler.FireUI(
    screenKey = "main_screen",
    onEvent = { event ->
        Log.d("ServerDrivenUI", "Event received: $event")
        when (event) {
            is ServerDrivenEvent.ButtonClicked -> {
                when (event.actionId) {
                    "navigate" -> navigateToScreen(event.parameters["screen"])
                    "like" -> likePost(event.parameters["postId"])
                    "share" -> shareContent(event.parameters["text"])
                }
            }
            else -> {}
        }
    }
) {
    // User content
}
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

## 💬 Dialogs & Bottom Sheets (Phase 3)

The SDK provides built-in action commands to show and hide customizable Material 3 Modal Bottom Sheets and Alert Dialogs. These UI elements can display standard text (title, message) or recursively render dynamic nested JSON layouts using the template engine.

### Bottom Sheets

#### Show Bottom Sheet Action (`show_bottom_sheet`)
Presents a standard Material 3 Modal Bottom Sheet overlay.

* **Parameters:**
  * `title`: (Optional) The text title displayed at the top of the sheet. Supports variable substitution (e.g. `{{state.userName}}`).
  * `content`: (Optional) A serialized raw JSON string representing a single nested `UIComponent` template (e.g. a `column` or `row` component wrapping a full form or layout).

* **JSON Configuration Example:**
```json
{
  "perform": "show_bottom_sheet",
  "parameters": {
    "title": "Select Options",
    "content": "{\"type\": \"column\", \"children\": [{\"type\": \"button\", \"text\": \"Close Sheet\", \"action\": {\"perform\": \"hide_bottom_sheet\"}}]}"
  }
}
```

#### Hide Bottom Sheet Action (`hide_bottom_sheet`)
Dismisses the currently visible Modal Bottom Sheet.
* **JSON Configuration Example:**
```json
{
  "perform": "hide_bottom_sheet"
}
```

---

### Alert Dialogs

#### Show Dialog Action (`show_dialog`)
Presents a standard Material 3 Alert Dialog overlay with a default "OK" confirm button.

* **Parameters:**
  * `title`: (Optional) The text title displayed at the top of the dialog. Supports variable substitution.
  * `message`: (Optional) The message text displayed in the body of the dialog. Supports variable substitution.
  * `content`: (Optional) A serialized raw JSON string of a nested `UIComponent` template to render inside the dialog body.

* **JSON Configuration Example:**
```json
{
  "perform": "show_dialog",
  "parameters": {
    "title": "Update Available",
    "message": "A new version of the app is ready to install.",
    "content": "{\"type\": \"text\", \"content\": [{\"text\": \"Click OK to close this popup.\"}]}"
  }
}
```

#### Hide Dialog Action (`hide_dialog`)
Dismisses the currently visible Alert Dialog.
* **JSON Configuration Example:**
```json
{
  "perform": "hide_dialog"
}
```

---

### Reactive State Mappings
When these actions run, the SDK updates specific reserved keys in `ServerDrivenState.stateMap` reactively. You can query these keys in your custom UI or let the SDK handle them automatically inside `ServerDrivenContent`:

| State Key | Expected Type | Description |
| :--- | :--- | :--- |
| **`sdui_bottom_sheet_visible`** | `String` (`"true"`/`"false"`) | Controls the visibility of the bottom sheet overlay. |
| **`sdui_bottom_sheet_title`** | `String` | Stores the title text of the bottom sheet. |
| **`sdui_bottom_sheet_content`** | `String` (JSON template) | Stores the raw JSON template for the sheet's content. |
| **`sdui_dialog_visible`** | `String` (`"true"`/`"false"`) | Controls the visibility of the dialog overlay. |
| **`sdui_dialog_title`** | `String` | Stores the title text of the dialog. |
| **`sdui_dialog_message`** | `String` | Stores the description/message text of the dialog. |
| **`sdui_dialog_content`** | `String` (JSON template) | Stores the raw JSON template for the dialog's custom body. |

---

## 🎭 Showing Dialogs & Bottom Sheets (Imperative API)

With the introduction of the `FireUI` wrapper and `FireUIScope`, you can imperatively control overlays from within your custom native Compose content using `LocalFireUIScope`.

### 1. Register your Slot Composable content

Before triggering bottom sheets or dialogs via slots, register your Composable content with the `ServerDrivenUiHandler` companion:

```kotlin
ServerDrivenUiHandler.registerSlotContent("my_premium_checkout_form") { slotContext ->
    // Your native Compose UI element or screen
    PremiumCheckoutForm()
}
```

### 2. Trigger overlays from custom content

Inside your screen wrapped in `FireUI`, call `LocalFireUIScope.current` to show or dismiss the overlays:

```kotlin
@Composable
fun MainScreen() {
    val uiScope = LocalFireUIScope.current

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Your Custom App Content")

        Button(onClick = {
            // Trigger bottom sheet using registered slot content key
            uiScope.showBottomSheet("my_premium_checkout_form")
        }) {
            Text("Upgrade to Premium")
        }

        Button(onClick = {
            // Trigger dialog
            uiScope.showDialog("my_premium_checkout_form")
        }) {
            Text("Show Preview Dialog")
        }
    }
}
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

---

## 🛣️ Platform Roadmap

### Phase 1: Foundation (Current) ✅
- [x] Comprehensive SDK Support (Jetpack Compose)
- [x] Automated Firebase Remote Config Synchronization
- [x] Professional Dark-Blue Orchestration Dashboard
- [x] Supabase Auth & Multi-Tenancy Integration
- [x] Secure Encrypted Credential Vault

### Phase 2: Orchestration Power 🚀
- [x] Dynamic List Binding & Data Providers (Room DB, Favorites, Collections)
- [x] Custom Action Interceptors (`registerCustomActionHandler`)
- [ ] Visual Drag-and-Drop Canvas (No-Code Editor)
- [ ] Versioning & Rollback Systems
- [ ] Draft vs Production Environment Management

### Phase 3: Intelligence & Growth 🧠
- [x] Built-in Alert Dialogs & Bottom Sheets Actions
- [ ] A/B Testing & User Targeting (Firebase Conditions)
- [ ] Integrated Impression & Click Analytics
- [ ] Multi-Platform Expansion (Future iOS SDK)

---

## 📖 Technology Stack

The SDK is built on modern Android standards:
- **Core:** Jetpack Compose (UI), Kotlin Coroutines (Async).
- **Networking/Media:** Coil (Image Loading).
- **Serialization:** Kotlinx Serialization (JSON Parsing).
- **Cloud:** Firebase Remote Config (Dynamic Synchronization).

---

## 🏛️ SDK Deep-Dive: File & Component Reference

This section provides an exhaustive description of every file, function, data structure, layout component, and utility class inside the SDUI SDK codebase.

---

### 📂 File: `ServerDrivenUiHandler.kt`
The main entry point class for rendering Server-Driven UI components. It integrates Compose rendering with view models, state managers, and remote sync configurations.

#### 🛠️ Key Functions & Composables

##### `FireUI`
Top-level wrapper and parent/host container composable that synchronizes layouts using Remote Config, manages overlays (dialogs, bottom sheets) on top of the user's Compose tree, and supports custom content passing as a child inside composition.
* **Signature:**
  ```kotlin
  @Composable
  fun ServerDrivenUiHandler.FireUI(
      screenKey: String,                          // Remote Config key / Screen identifier
      onEvent: (ServerDrivenEvent) -> Unit = {},  // Event callback
      modifier: Modifier = Modifier,              // Layout modifiers
      onError: ((String) -> Unit)? = null,        // Error handler callback
      content: @Composable FireUIScope.() -> Unit // Trailing lambda for user content
  )
  ```
* **Parameters:**
  * `screenKey` (String): The Firebase Remote Config identifier holding the UI schema JSON.
  * `onEvent` (Lambda): Callback wrapper receiving interactive user events.
  * `modifier` (Modifier): UI layout adjustments for the screen wrapper.
  * `onError` (Lambda): Optional callback triggered if JSON parsing or rendering fails.
  * `content` (Lambda): Composable slot providing a `FireUIScope` receiver, containing the user's own Compose screen layout.

##### `FireUIScope`
A control handle scope passed as the receiver of the `FireUI` content lambda, allowing imperative show/dismiss control over dialog and bottom sheet overlays.
* **Methods:**
  * `showBottomSheet(slotKey: String?)`: Displays the ModalBottomSheet rendering the registered Composable slot content.
  * `dismissBottomSheet()`: Hides the currently visible bottom sheet.
  * `showDialog(slotKey: String?)`: Displays the AlertDialog rendering the registered Composable slot content.
  * `dismissDialog()`: Hides the currently visible dialog.

##### `LocalFireUIScope`
A `CompositionLocal` providing access to the current `FireUIScope` instance. This allows deeply nested composables to trigger overlays without passing properties down.
* **Usage:** `val scope = LocalFireUIScope.current`

##### `ServerDrivenRemoteScreen` (Deprecated ⚠️)
> [!WARNING]
> **Deprecated:** Use `FireUI` instead. Backward-compatible mapping is provided automatically.

##### `ServerDrivenContainer` (Deprecated ⚠️)
> [!WARNING]
> **Deprecated:** Use `FireUI` instead. Backward-compatible mapping is provided automatically.

##### `MainScreen`
Internal state manager that binds incoming strings to `ServerDrivenUIViewModel` and listens to state updates.
* **Signature:**
  ```kotlin
  @Composable
  internal fun MainScreen(
      uiJsonString: String,
      dataJsonString: String,
      modifier: Modifier = Modifier,
      fallbackContent: @Composable (() -> Unit)? = null,
      viewModel: ServerDrivenUIViewModel = viewModel(),
      onEvent: (ServerDrivenEvent) -> Unit = {},
      onError: ((String) -> Unit)? = null
  )
  ```
* **Parameters:**
  * `viewModel` (ServerDrivenUIViewModel): View Model flow coordinator.

##### `ServerDrivenContent`
Internal root compositor wrapping top-level list elements into a default Compose `Box` layout. *Phase 3 Upgrade:* Also coordinates rendering the dynamic bottom sheet and dialog overlays reactively by reading visibility and content keys from the state map.
* **Signature:**
  ```kotlin
  @Composable
  internal fun ServerDrivenContent(
      uiDefinition: UIDefinition,
      dataJson: JsonObject,
      state: ServerDrivenState,
      modifier: Modifier = Modifier,
      onEvent: (ServerDrivenEvent) -> Unit
  )
  ```

#### ⚙️ Static Registries (Phase 2 Additions)

##### `registerDataProvider`
Registers dynamic data provider callbacks (e.g. for Room DB mappings) to resolve list bindings at runtime.
* **Signature:**
  ```kotlin
  @JvmStatic
  fun registerDataProvider(key: String, provider: () -> List<JsonObject>)
  ```
* **Usage Example:**
  ```kotlin
  ServerDrivenUiHandler.registerDataProvider("favourites") {
      favouriteDao.getAllFavoritesSync().map { it.toJsonObject() }
  }
  ```

##### `registerCustomActionHandler`
Registers custom action interceptors allowing client apps to handle operations (like DB CRUD or Refills) via custom code.
* **Signature:**
  ```kotlin
  @JvmStatic
  fun registerCustomActionHandler(handler: (Action, JsonObject, ServerDrivenState) -> Boolean)
  ```
* **Usage Example:**
  ```kotlin
  ServerDrivenUiHandler.registerCustomActionHandler { action, dataJson, state ->
      when (action.perform) {
          "database_insert" -> {
              val shayari = action.parameters["text"] ?: ""
              db.insert(Fav(poetryText = shayari))
              true // Handled
          }
          else -> false // Fallback to SDK standard actions
      }
  }
  ```

---

### 📂 File: `data_models/ServerDrivenUiDataModel.kt`
Contains the definition of all serializable data classes mapping the backend JSON schemas. It uses Kotlinx Serialization annotations (`@Serializable`).

#### 🗂️ Component Data Classes

##### `UIDefinition`
Root schema object representing the overall JSON payload wrapper.
* **Variables:**
  * `version` (String?): Compatible version marker (defaults to `"1.0.0"`).
  * `uiData` (List<UIComponent?>): Dynamic list of child layout components.

##### `UIComponent`
Sealed abstract class defining layout components dynamically parsed using polymorphic type discriminator `"type"`.
* **Variables:**
  * `type` (String?): Class type tag matching component types (e.g. `"column"`, `"text"`).
  * `style` (ComponentStyle?): Styling definitions container.

Here is the exact technical specification, default values, data processing, state handling, rendering requirements, and default behavior for each layout/UI component in the SDK:

###### 1. `ColumnComponent`
* **Default Values:** `spacing = 8` (dp), `enableScroll = false`, verticalArrangement = `"top"`, horizontalAlignment = `"start"`, size = `wrapContentWidth()`.
* **Properties Supported:** `children: List<UIComponent>`, `spacing: Int`, `action: Action`, custom alignment parameters (`horizontalAlignment`, `verticalArrangement`, `spaceBy`, `enableScroll` via `columnStyle`), `modifier` attributes.
* **Working Mechanism:** Instantiates a native Compose `Column` element, stacking children vertically. It applies a scroll state modifier conditionally if scrollable.
* **Data Processing:** Evaluates custom layouts. Dynamic expressions in children are evaluated recursively.
* **Data Handling:** Holds nested children. Click actions on the Column itself are dispatched to `handleAction`.
* **Rendering Requirements:** None. Can render empty or with children.
* **Default Behavior:** Wraps content width, takes minimum required height. Non-scrollable.

###### 2. `RowComponent`
* **Default Values:** horizontalArrangement = `"start"`, verticalAlignment = `"center"`, size = `fillMaxWidth()`.
* **Properties Supported:** `children: List<UIComponent>`, arrangement parameters (`horizontalArrangement`, `verticalAlignment`, `spaceBy` via `rowStyle`), `modifier` attributes.
* **Working Mechanism:** Instantiates a native Compose `Row` element, placing children side-by-side horizontally.
* **Data Processing:** Evaluates spacing between children.
* **State & Data Handling:** Propagates data references.
* **Rendering Requirements:** None.
* **Default Behavior:** Fills the maximum horizontal width, wraps vertical content height.

###### 3. `TextComponent`
* **Default Values:** `fontSize = 16` (sp), `textColor = "#000000"` (black), `fontWeight = "normal"`, size = `wrapContentWidth()`.
* **Properties Supported:** `content: List<TextContent>`, `dataBinding: String` (e.g. `"@posts"`), `action: Action`, custom style parameters (`fontSize`, `textColor`, `fontWeight` via `textStyle`), `modifier` attributes.
* **Working Mechanism:** Draws standard Compose `Text` element(s). If bound to a collection data source, it wraps them inside a vertical `Column`.
* **Data Processing:** If `dataBinding` resolves to a collection, it loops through each item and extracts the value matching `"text"` or `"title"` keys. If it's a simple path string, it substitutes template tokens (e.g., `{{item.name}}`).
* **State & Data Handling:** Reads values dynamically from local state maps and database JSONs.
* **Rendering Requirements:** Requires a non-empty `content` list or a valid resolved `dataBinding` text source.
* **Default Behavior:** Wraps content width and height. Non-interactive unless `action` or `modifier.onClick` is provided.

###### 4. `ImageComponent`
* **Default Values:** Scale = `ContentScale.Crop`, size = `fillMaxWidth()`.
* **Properties Supported:** `imageUrl: String`, `dataBinding: String` (dynamic URL source), `action: Action`, `modifier` attributes.
* **Working Mechanism:** Uses Coil library (`AsyncImage`) to load and cache remote graphics asynchronously.
* **Data Processing:** Resolves the URL source using token replacements (`TemplateProcessor.replaceVars`), prioritizing `dataBinding` over `imageUrl`.
* **State & Data Handling:** Re-triggers coil fetches if dynamic URL bounds change during recomposition.
* **Rendering Requirements:** A valid HTTP/HTTPS image URL string.
* **Default Behavior:** Fills layout width, crops image content to fill size constraints, invisible if URL string is blank.

###### 5. `ButtonComponent`
* **Default Values:** `buttonColor = "#ffffff"` (white), `buttonTextColor = "#ffffff"`, `buttonTextSize = 12` (sp), `buttonShape = "DEFAULT"` (RoundedCornerShape(0.dp)), size = `fillMaxWidth()`.
* **Properties Supported:** `text: String`, `dataBinding: String` (dynamic label), `action: Action`, custom styles (`buttonColor`, `buttonShape`, `buttonRounded` radius, `buttonTextColor`, `buttonTextSize` via `buttonStyle`), `modifier` attributes.
* **Working Mechanism:** Wraps a clickable Compose `Button` composable with a customized theme shape and color.
* **Data Processing:** Processes the label text dynamically using data context.
* **State & Data Handling:** Intercepts clicks and routes action parameters via `handleAction`.
* **Rendering Requirements:** Text label string or dynamic data binding source.
* **Default Behavior:** Rectangular flat button filling full width of parent.

###### 6. `CardComponent`
* **Default Values:** `cardShape = 8` (dp), `cardContainerColor = "#fff000"` (yellow-green), size = `fillMaxWidth()`.
* **Properties Supported:** `children: List<UIComponent>`, custom styles (`cardShape`, `cardContainerColor`, `cardPadding` via `cardStyle`), `modifier` attributes, click actions.
* **Working Mechanism:** Renders a standard Compose `Card` wrapping a vertical `Column` containing its children.
* **Data Processing:** Passes down environment data objects to nested items.
* **State & Data Handling:** Triggers actions if the card itself or its click modifier is clicked.
* **Rendering Requirements:** None.
* **Default Behavior:** Fills container width, wraps content height, applies 8dp rounded corner clip.

###### 7. `ChipGroupComponent`
* **Default Values:** `selectedStateKey = "selectedIndex"`, selected index = 0, scrollable = true, size = `fillMaxWidth()`.
* **Properties Supported:** `dataBinding: String` (array source), `selectedStateKey: String` (state map reference), `chipTemplate: ChipTemplate` (which contains `labelBinding`, `style`, `action`), `modifier` attributes.
* **Working Mechanism:** Instantiates a horizontally scrolling `LazyRow` of surface chips.
* **Data Processing:** Resolves data binding paths to lists of objects, setting a local context block for each chip (`item` and `index`).
* **State & Data Handling:** Reads and updates the selected index in `ServerDrivenState` using the `selectedStateKey`. Clicking a chip updates state, fires `chipTemplate.action` (appending selected index parameter), and posts `ServerDrivenEvent.ChipSelected`.
* **Rendering Requirements:** Non-empty array source in `dataBinding`.
* **Default Behavior:** Horizontally scrollable chip row with custom background and text color toggles.

###### 8. `IconButtonComponent`
* **Default Values:** size = `wrapContentSize()`, iconSize = 24 (dp), tint = `"#666666"` (gray), isToggled = false.
* **Properties Supported:** `stateKey: String`, custom styles (`iconName`, `toggledIconName`, `iconSize`, `tint`, `toggledTint`, `toggleStateFrom` via `iconButtonStyle`), `modifier` attributes, click actions.
* **Working Mechanism:** Displays an interactive toggle button. Automatically switches between active/inactive icons and color tints.
* **Data Processing:** Dynamically evaluates toggle state by querying local state map (`stateKey`) or evaluating data binding paths (`toggleStateFrom`).
* **State & Data Handling:** Clicking immediately flips the boolean state, writes it back to `ServerDrivenState` (under `stateKey`), executes the action (augmenting parameters with `isToggled` and `newState`), and posts `ServerDrivenEvent.StateUpdateRequested`.
* **Rendering Requirements:** Valid icon identifiers (`iconName`).
* **Default Behavior:** Stateless icon button unless `stateKey` or `toggleStateFrom` is defined.

###### 9. `GridComponent`
* **Default Values:** `columns = 2`, `spacing = 8` (dp), size = `fillMaxWidth()`.
* **Properties Supported:** `columns: Int`, `spacing: Int`, `dataBinding: String`, `itemTemplate: UIComponent`, `modifier` attributes.
* **Working Mechanism:** Instantiates a static `LazyVerticalGrid` utilizing fixed cell sizing rules.
* **Data Processing:** Loops through elements resolved from the data binding.
* **State & Data Handling:** Injects an `itemContext` containing `"item"` and `"index"` variables into each cell's template renderer.
* **Rendering Requirements:** Valid dynamic data binding array pointer and item template definition.
* **Default Behavior:** Draws a two-column grid layout.

###### 10. `LazyColumnComponent`
* **Default Values:** `spacing = 8` (dp), size = `fillMaxWidth()`.
* **Properties Supported:** `dataBinding: String`, `itemTemplate: UIComponent`, `spacing: Int`, custom styles via `columnStyle`, `modifier` attributes.
* **Working Mechanism:** Renders a vertically scrolling recycler-like `LazyColumn` container.
* **Data Processing:** Resolves the list of data items.
* **State & Data Handling:** Builds individual `itemContext` scopes mapping cells dynamically.
* **Rendering Requirements:** Valid dynamic data binding array pointer and item template definition.
* **Default Behavior:** Vertically scrollable infinite recycler list.

###### 11. `LazyRowComponent`
* **Default Values:** `spacing = 8` (dp), size = `fillMaxWidth()`.
* **Properties Supported:** `dataBinding: String`, `itemTemplate: UIComponent`, `spacing: Int`, custom styles, `modifier` attributes.
* **Working Mechanism:** Renders a horizontally scrolling `LazyRow` container.
* **Data Processing:** Resolves the list of data items.
* **State & Data Handling:** Spawns horizontal item elements matching individual data models.
* **Rendering Requirements:** Valid dynamic data binding array pointer and item template definition.
* **Default Behavior:** Horizontally scrollable recycler list.

###### 12. `LazyVerticalStaggeredGridComponent`
* **Default Values:** `columns = 2`, `verticalSpacing = 0` (dp), `horizontalSpacing = 0` (dp), size = `fillMaxWidth()`.
* **Properties Supported:** `columns: Int`, `verticalSpacing: Int`, `horizontalSpacing: Int`, `dataBinding: String`, `itemTemplate: UIComponent`, `modifier` attributes.
* **Working Mechanism:** Instantiates a native Compose `LazyVerticalStaggeredGrid` showing items in masonry columns.
* **Data Processing:** Resolves the list of dynamic cards/items.
* **State & Data Handling:** Binds individual cell properties inside item contexts.
* **Rendering Requirements:** Valid dynamic data binding array pointer and item template definition.
* **Default Behavior:** Masonry photo-feed style staggered grid list.

###### 13. `BoxComponent`
* **Default Values:** alignment = `"top_center"`, size = `fillMaxWidth() + fillMaxHeight()`.
* **Properties Supported:** `itemTemplate: UIComponent`, click actions, custom alignment (`boxContentAlignment` via `style`), `modifier` attributes.
* **Working Mechanism:** Instantiates a Compose `Box` container, stacking child templates on top of each other.
* **Data Processing:** Passes down dynamic variable bindings.
* **State & Data Handling:** Delegates click events to internal actions.
* **Rendering Requirements:** None.
* **Default Behavior:** Fills maximum available height and width, layering components center-aligned at the top.

###### 14. `BottomBarComponent`
* **Default Values:** `iconColor = "#ffffff"`, `textColor = "#ffffff"`, size = `fillMaxWidth()`, height = 60 (dp).
* **Properties Supported:** `bottomBarItems: List<BottomBarItems>`, custom styles (`iconColor`, `textColor` via `bottomBarStyle`), `modifier` attributes.
* **Working Mechanism:** Draws a fixed bottom row of navigation cells. Each navigation option contains a vertical stack of icon and label.
* **Data Processing:** Reads hardcoded configurations.
* **State & Data Handling:** Click triggers action definitions on item context.
* **Rendering Requirements:** Non-empty bottom navigation bar item list.
* **Default Behavior:** Horizontal bottom bar distributing tabs evenly with white icons and text.

###### 15. `SpacerComponent`
* **Default Values:** `size = 16` (dp).
* **Properties Supported:** `size: Int`, `style`.
* **Working Mechanism:** Instantiates a blank Compose `Spacer` of width and height dimensions set to `size.dp`.
* **Data Processing:** None.
* **State & Data Handling:** None.
* **Rendering Requirements:** None.
* **Default Behavior:** Gap placeholder taking up 16dp.

###### 16. `DividerComponent`
* **Default Values:** `thickness = 1` (dp), `color = "#EEEEEE"`.
* **Properties Supported:** `thickness: Int`, `color: String` (hex), `style`.
* **Working Mechanism:** Draws a thin Compose `HorizontalDivider` line.
* **Data Processing:** Parses hex color codes, defaulting to `Color.LightGray` if format is invalid.
* **State & Data Handling:** None.
* **Rendering Requirements:** None.
* **Default Behavior:** Horizontal divider line with LightGray fallback.

###### 17. `TextInputComponent` (Phase 1)
* **Default Values:** `maxLines = 1`, size = `wrapContentWidth()`.
* **Properties Supported:** `stateKey: String` (reactive key matching state), `placeholder: String?`, `maxLines: Int?`, `itemSize: ItemSize?`, `modifier` attributes via style.
* **Working Mechanism:** Instantiates an `OutlinedTextField` which reads its value dynamically from `state.get(stateKey)` and writes changes to `state.update(stateKey, newValue)`.
* **Data Processing:** Evaluates placeholder template variables.
* **State & Data Handling:** Mutates local UI state reactively inside the `ServerDrivenState` container.
* **Rendering Requirements:** Valid state key (`stateKey`).
* **Default Behavior:** Outlined single-line text input with standard border styling.

###### 18. `ConditionalComponent` (Phase 1)
* **Default Values:** None.
* **Properties Supported:** `condition: String` (expression statement, e.g. `{{state.isPro}} == true`), `then` (rendered if condition is true), `else` (rendered if condition is false), style configurations.
* **Working Mechanism:** Reads the condition expression, parses variables, checks comparison operators, and renders the corresponding component layout.
* **Data Processing:** Automatically substitutes dynamic state/data variables in the condition via `TemplateProcessor.evaluateCondition`.
* **State & Data Handling:** Recomposes immediately if variables involved in the condition are modified.
* **Rendering Requirements:** Valid `condition` string and a non-null `then` layout component.
* **Default Behavior:** Conditionally routes rendering output.

#### 🎨 Styling and Structural Classes
* **`ComponentStyle`**: Direct CSS-like attribute holder aggregating styles (modifier, texts, grids, cards, etc.).
* **`ModifierStyle`**: Maps standard Compose modifier rules (backgrounds, clip shapes, paddings, click actions).
* **`ModifierClipData`**: Specifies rounded or circle border shapes and radii.
* **`Padding`**: DP dimensions mapping (all, left, right, top, bottom).
* **`TextStyle`**: Fonts sizing, text coloring, and weights (bold, normal, medium, light).
* **`ChipStyle`**: Colors for selected/unselected chip shapes.
* **`ColumnStyle` / `RowStyle`**: Horizontal/vertical arrangement settings and scroll toggles.
* **`CardStyle`**: Card shapes and container coloring.
* **`IconButtonStyle`**: Toggle icon sets, tinting states, and data properties.
* **`ButtonStyle`**: Color properties, rounded parameters, text sizes, and font configurations.
* **`ItemSize`**: Layout dimensions controller supporting fixed DP values, percentage ratios (`widthPercent`, `heightPercent`), and weights.
* **`Action`**: Declares operations like `"navigate"`, `"update_state"`, or `"button_click"` with parameters.

#### 📡 Event Classes
* **`ServerDrivenEvent`** (Sealed): Emitted values passed back to client screens.
  * `ButtonClicked`: Triggered by clicks with action code parameters.
  * `ItemClicked`: List or row selection events with indices.
  * `ChipSelected`: Tag index adjustments.
  * `NavigationRequested`: Screen route redirect signals.
  * `StateUpdateRequested`: Key-value configuration changes.

---

### 📂 File: `handler_processors/DataConverter.kt`
Singleton utility object converting dynamic inputs (Maps, Lists, Strings, POJOs) into standard Kotlinx `JsonObject` instances for template rendering.

#### 🛠️ Functions
* **`toJsonObject(data: Any): JsonObject`**: Parses string representations or wraps Maps/Lists into Kotlinx JSON representation structures.
* **`mapToJsonObject(map: Map<*, *>)`**: Recursively maps key-value elements.
* **`listToJsonArray(list: List<*>)`**: Converts arrays to structured lists.

---

### 📂 File: `handler_processors/IconResolver.kt`
Maps text-based icon identifiers received from JSON config (e.g. `"heart"`, `"search"`, `"settings"`) to actual Material Design vector icons.

#### 🛠️ Functions
* **`getIcon(iconName: String?): ImageVector`**: Matches key strings and returns matching icons (defaults to `Icons.Default.Info`).

---

### 📂 File: `handler_processors/ServerDrivenState.kt`
Tracks dynamic, interactive states (like selection indices, toggles, form entries) inside the Compose lifecycle using an internal reactive state map.

#### 🛠️ Functions
* **`update(key: String, value: JsonElement)`**: Sets or updates states.
* **`update(key: String, value: String)`**: Sets state string values.
* **`update(key: String, value: Int)`**: Sets state integer values.
* **`get(key: String): JsonElement?`**: Reads specific state parameters.

---

### 📂 File: `handler_processors/TemplateProcessor.kt`
The central templating engine that handles data bindings and variable substitutions.

#### 🛠️ Functions
* **`replaceVars(text: String, data: JsonObject, state: Map<String, JsonElement>): String`**: Finds double-bracket references `{{value}}` and resolves them first from local state map, then falls back to JSON data properties.
* **`resolveValue(path: String, data: JsonObject): JsonElement`**: Walks down complex path keys (e.g. `user.profile.avatar`) to extract target leaf nodes.
* **`resolveDataSource(source: String, data: JsonObject, state: Map<String, JsonElement>): List<JsonObject>`**: Resolves data sources (like `@posts`) to collections of JSON objects. *Phase 2 Upgrade:* Checks if the target source points to a registered dynamic data provider (e.g. database query) and loads its data items directly.
* **`evaluateCondition(condition: String, data: JsonObject, state: Map<String, JsonElement>): Boolean`** *(Phase 1 Addition)*: Subtitutes variables inside text conditions and parses equality/comparison operators (`==`, `! =`, `>=`, `<=`, `>`, `<`) to evaluate boolean expressions.

---

### 📂 File: `remote_config/SduiRemoteConfig.kt`
Implements Firebase Remote Config integrations, handling cache intervals, online synchronization, version checking, and offline fallbacks.

#### 🛠️ Core Methods
* **`fetch()`**: Fetches updates. It immediately emits cached configurations, runs remote synchronization, activates fresh payloads, and emits changes if a new layout version is found.
* **`isNewer(oldJson: String, newJson: String): Boolean`**: Parses the `"version"` field in layouts to determine if updates exist.

#### 🛠️ Builder Pattern
Includes `SduiRemoteConfig.Builder` supporting initialization parameters:
* `screenKey(String)`
* `defaultJson(String)`
* `fetchIntervalSeconds(Long)`
* `onUpdate((String) -> Unit)`

---

### 📂 File: `view_model/ServerDrivenUIViewModel.kt`
Internal Android architecture component managing asynchronous parsing, loading states, and error handling.

#### 🛠️ Core Variables
* **`uiDefinition`** (StateFlow<UIDefinition?>): Current deserialized layout component list.
* **`dataJson`** (StateFlow<JsonObject?>): Active parsed runtime database state.
* **`isLoading`** (StateFlow<Boolean>): Controls overlay progress indicators.
* **`error`** (StateFlow<String?>): Holds active parsing/loading errors.

#### 🛠️ Core Functions
* **`loadUI(uiJsonString: String)`**: Starts parsing layout JSONs within the view model scope.
* **`loadData(dataJsonString: String)`**: Deserializes dynamic data payloads.

---

### 📂 File: `ui_elements_handler_styles/UiElementsAndStyles.kt`
Contains standard extension helper functions converting visual schemas into native Jetpack Compose constructs.

#### 🛠️ Functions
* **`handleAction(...)`**: Decodes click operations, runs state updates, and fires events. *Phase 2 Upgrade:* Queries the static `ServerDrivenUiHandler.executeCustomAction` interceptor before executing standard actions, returning immediately if handled by the client application.
* **`ModifierStyle?.toModifier()`**: Applies margins, paddings, backgrounds, and clips to a Compose `Modifier`.
* **`ItemSize?.toModifier()`**: Translates height, width, ratio sizes, and weights to a Compose `Modifier`.
* **`TextStyle.toTextStyle()`**: Maps typography parameters into Compose `TextStyle`.
* **Alignment Helpers**: `toVerticalArrangement()`, `toHorizontalAlignment()`, `toHorizontalArrangement()`, `toVerticalAlignment()`, `boxContentAlignment()`.
* **Color Resolvers**: `convertToIntColor()`, `convertToColor()`.

---

### 📂 Directory: `ui_elements/`
Contains composable renderers that map deserialized JSON components to Compose layouts.

#### 🛠️ Renderers
* **`RenderComponent.kt`**: Routes elements to target layout composables.
* **`BottomBarUi.kt`**: Renders custom tab bars.
* **`BoxUi.kt`**: Renders overlapping stack container boxes.
* **`ButtonUi.kt`**: Renders material buttons.
* **`CardUi.kt`**: Renders container cards.
* **`ChipUi.kt`**: Renders selectable horizontal chips/tags.
* **`ColumnUI.kt`**: Renders vertical containers.
* **`DividerUi.kt`**: Renders line dividers.
* **`GridUi.kt`**: Renders static grids.
* **`IconButtonUi.kt`**: Renders interactive icon buttons.
* **`ImageUi.kt`**: Renders Coil async images.
* **`LazyColumn.kt`**: Renders dynamic vertical scrolling lists.
* **`LazyRow.kt`**: Renders dynamic horizontal scrolling rows.
* **`LazyVerticalStaggeredGrid.kt`**: Renders staggered masonry lists.
* **`RowUI.kt`**: Renders simple row lists.
* **`SpacerUi.kt`**: Renders layout spacers.
* **`TextUI.kt`**: Renders dynamic formatted text views.
* **`TextInputUi.kt`**: Renders OutlinedTextField input controls and binds entries to state keys reactively.
* **`ConditionalUi.kt`**: Evaluates comparison expressions to toggle layouts dynamically.


## New Features & Architecture (v1.2.3 Update)

The FireUI SDK has been significantly updated with powerful new rendering and lifecycle capabilities.

### 1. General Slot System
FireUI now allows host applications to inject arbitrary native Jetpack Compose UI code into any server-driven component. This is done via the `slot` property.

**Architecture:**
- **Schema:** Any JSON component can define `"slot": "slot_name"`.
- **Registry:** The host app registers the slot using `ServerDrivenUiHandler.registerSlotContent("slot_name") { context -> ... }`.
- **Renderer:** When `RenderComponent` encounters a component with a `slot`, it renders the registered composable *instead* of the component's normal content, allowing deep integration of native and server-driven UI.

### 2. Dialog and Bottom Sheet Enhancements
Overlay controls are now fully customizable from the server.
- **Properties Supported:** `dismissOnOutsideClick`, `dismissOnBackPress`, `sheetSize`, `expandable`, `collapsible`, `initialState`.
- **Implementation:** Built on Compose Material 3 `ModalBottomSheet` and `AlertDialog`.
- **Slot Integration:** Combine `show_bottom_sheet` with a custom `slot` content to render entirely native bottom sheets managed by server logic!

### 3. Theme & Dark Mode Support
FireUI now understands themes natively!
- **Schema:** Use a `ColorValue` object instead of a string: `"backgroundColor": { "light": "#FFFFFF", "dark": "#000000" }`.
- **Renderer:** `ServerDrivenUiHandler.setTheme()` allows overriding the system theme, and `LocalFireUiTheme` ensures all colors automatically react to dark mode toggles without requiring a new fetch.

### 4. Responsive UI & Window Size Classes
- **Schema:** Add a `responsive` object to `ComponentStyle` and `ModifierStyle` to override properties based on the breakpoint: `{ "compact": { ... }, "medium": { ... }, "expanded": { ... } }`.
- **Grid Components:** `GridComponent` and `LazyVerticalStaggeredGridComponent` now support `responsiveColumns` to reflow content based on tablet/phone sizes.
- **Visibility:** Use `"visibleOn": ["compact"]` to conditionally hide components.

### 5. Custom Fonts
- Pre-register fonts on app launch: `ServerDrivenUiHandler.registerFont("MyFont", MyFontFamily)`.
- Use them safely in JSON via `"fontFamily": "MyFont"`. Falls back gracefully.

### 6. A/B Testing & Variant Resolution
- **cohortContext:** Pass dynamic user properties to the `ServerDrivenUiHandler` Builder.
- **variantResolverUrl:** The SDK makes a POST request to this URL with the screen key and cohort context. It evaluates A/B test experiments, assigns a variant, and returns it.
- **Fallback:** If variant resolution fails, it safely falls back to Firebase Remote Config.

### 7. Version History & Offline Fallback
- The SDK caches the "Last Known Good" configuration per screen. If a new fetch parses with errors or fails to render, it transparently rolls back to the cached version, ensuring 100% uptime.

### 8. Custom SaaS-Level Analytics
- All interactive actions (clicks, toggles) and screen views now emit a enriched `ServerDrivenEvent` including the `screenKey` and `variant`, which can be routed directly to Firebase Analytics or an external metrics pipeline.
