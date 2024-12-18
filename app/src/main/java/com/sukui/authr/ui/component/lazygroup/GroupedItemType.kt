package com.sukui.authr.ui.component.lazygroup

import androidx.compose.runtime.compositionLocalOf

enum class GroupedItemType {
    First,
    Middle,
    Last,
    Only
}

val LocalGroupedItemType = compositionLocalOf { GroupedItemType.Only }