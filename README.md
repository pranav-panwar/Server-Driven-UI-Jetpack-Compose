# Server-Driven UI with Firebase

[![](https://jitpack.io/v/ppthegamer/Server-Driven-UI-Jetpack-Compose.svg)](https://jitpack.io/#ppthegamer/Server-Driven-UI-Jetpack-Compose)

## Overview
The **Server-Driven UI with Firebase** project dynamically renders user interfaces based on JSON configurations fetched from a Firebase Realtime Database. This approach provides a flexible way to update the app's UI without requiring app updates.

## Project Structure
### Major Components
1. **Firebase Integration**: Fetches JSON configurations stored in Firebase Realtime Database.
2. **JSON Parser**: Maps the JSON data into Kotlin data classes.
3. **UI Renderer**: Dynamically builds the UI based on the JSON structure.
4. **Layouts**: Supports hierarchical layouts like Box, Column, and Row.
5. **Text Components**: Displays styled text elements with options for font size, color, and weight.

## JSON Structure
The server provides a JSON response that defines the UI. Below is the structure of the JSON:

```json
{
  "uiData": [
    {
      "type": "column",
      "margin": {
        "top": 16,
        "left": 16,
        "bottom": 16,
        "right": 16,
        "all": null
      },
      "style": {
        "modifier": {
          "clip": {
            "shape": "rounded",
            "radius": 8
          },
          "backgroundColor": "#FFFFFF",
          "padding": {
            "top": 8,
            "left": 8,
            "bottom": 8,
            "right": 8,
            "all": null
          },
          "onClick": {
            "action": {
              "perform": "navigate",
              "parameters": {
                "text": null,
                "screen": "details_screen"
              }
            }
          }
        },
        "columnStyle": {
          "verticalArrangement": "center",
          "horizontalAlignment": "start",
          "spaceBy": 16
        },
        "boxContentAlignment": null
      },
      "children": [
        {
          "type": "text",
          "content": [
            {
              "text": "Welcome to the Server-Driven UI!",
              "imageUrl": null,
              "url": null,
              "modifier": null
            }
          ],
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
```

### JSON Key Definitions
- **uiData**: Root array containing UI components.
- **type**: Component type (`COLUMN`, `TEXT`, `ROW`, `BOX`).
- **margin**: Defines margins for the component.
- **style**: Styling options, including modifiers and layout-specific styles.
- **content**: Data for text, image, or URL content.
- **action**: Defines actions like navigation or API calls.
- **itemSize**: Specifies height and width for components.
- **children**: Nested components.

## Layouts
### Box
- **Purpose**: Containers for holding items.
- **Style**: Includes content alignment and box-specific modifiers.

### Column
- **Purpose**: Vertical arrangement of child components.
- **Style**:
  - **verticalArrangement**: Specifies how children are arranged vertically (e.g., `CENTER`, `SPACE_BETWEEN`).
  - **horizontalAlignment**: Aligns children horizontally within the column (e.g., `START`, `END`).
  - **spaceBy**: Defines spacing between items.

### Row
- **Purpose**: Horizontal arrangement of child components.
- **Style**:
  - **horizontalArrangement**: Specifies how children are arranged horizontally (e.g., `SPACE_BETWEEN`, `CENTER`).
  - **verticalAlignment**: Aligns children vertically within the row (e.g., `TOP`, `CENTER`).
  - **spaceBy**: Defines spacing between items.

## Text Support
- **Text Style**: Font size, color, and weight can be customized for text components.

## Modifiers
- **Clip**: Define shapes (e.g., rounded corners) and radii.
- **BackgroundColor**: Set background color for components.
- **Padding**: Add spacing inside components.
- **OnClick**: Specify actions triggered by user interactions.

# Server-Driven-UI-Jetpack-Compose
