package com.sukui.authenticator.ui.component.lazygroup

import androidx.compose.runtime.compositionLocalOf

enum class GroupedItemType {
    First,
    Middle,
    Last,
    Only
}

val LocalGroupedItemType = compositionLocalOf { GroupedItemType.Only }