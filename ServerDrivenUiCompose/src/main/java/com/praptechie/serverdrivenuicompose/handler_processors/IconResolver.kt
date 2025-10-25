package com.praptechie.serverdrivenuicompose.handler_processors

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Groups2
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Share
import androidx.compose.ui.graphics.vector.ImageVector

internal object IconResolver {
    /**
     * Get icon from string name like "heart", "favorite", "share", etc.
     */
    fun getIcon(iconName: String?): ImageVector {
        return when (iconName?.lowercase()?.trim()) {
            // Heart/Like icons
            "heart", "favorite" -> Icons.Default.Favorite
            "heart_border", "favorite_border" -> Icons.Default.FavoriteBorder

            // Share icons
            "share" -> Icons.Default.Share
            "sharealt", "share_alt" -> Icons.Outlined.Share

            // Copy icons
            "copy", "copyall" -> Icons.Default.ContentCopy
            "copy_all" -> Icons.Default.ContentCopy

            // Navigation
            "back", "arrow_back" -> Icons.Default.ArrowBack
            "forward", "arrow_forward" -> Icons.Default.ArrowForward
            "close", "cancel" -> Icons.Default.Close
            "menu" -> Icons.Default.Menu

            // Common icons
            "search" -> Icons.Default.Search
            "add", "add_circle" -> Icons.Default.Add
            "delete", "delete_outline" -> Icons.Default.Delete
            "edit" -> Icons.Default.Edit
            "more", "more_vert" -> Icons.Default.MoreVert
            "info", "info_circle" -> Icons.Default.Info
            "warning" -> Icons.Default.Warning
            "error" -> Icons.Default.Error
            "check", "check_circle" -> Icons.Default.Check

            // User icons
            "person", "account", "profile" -> Icons.Default.Person2
            "people", "group" -> Icons.Default.Groups2

            // Settings
            "settings", "gear" -> Icons.Default.Settings
            "logout", "exit" -> Icons.Default.Logout

            // Other
            "home" -> Icons.Default.Home
            "star", "favorite_star" -> Icons.Default.Star
            "calendar" -> Icons.Default.CalendarMonth
            "phone", "call" -> Icons.Default.Call
            "email", "mail" -> Icons.Default.Email
            "location", "place" -> Icons.Default.LocationOn
            "link" -> Icons.Default.Link

            else -> Icons.Default.Info // Default fallback
        }
    }
}