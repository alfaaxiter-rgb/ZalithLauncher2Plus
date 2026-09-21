/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package com.movtery.zalithlauncher.ui.screens.content.settings.layouts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.ui.components.TitleAndSummary

/**
 * ALFAA NEON SETTINGS
 *
 * Semua SettingsCard memakai style neon yang sama.
 * Logic asli tetap dipertahankan.
 */

private val AlfaaBackground = Color(0xFF05080D)
private val AlfaaSurface = Color(0xFF0A1118)
private val AlfaaSurface2 = Color(0xFF0E1821)
private val AlfaaCyan = Color(0xFF20E0B2)
private val AlfaaBlue = Color(0xFF43C7FF)
private val AlfaaText = Color(0xFFE8FFF8)
private val AlfaaMuted = Color(0xFF829A98)
private val AlfaaBorder = Color(0xFF16483F)
private val AlfaaDisabled = Color(0xFF263238)

/**
 * 根据卡片在 UI 组中的位置，选择不同的圆角形状
 */
enum class CardPosition {
    Top,
    TopStart,
    TopEnd,
    Middle,
    Bottom,
    BottomStart,
    BottomEnd,
    Single
}

/**
 * 根据 UI 组件在组中的位置决定形状
 */
@Composable
fun rememberSettingsCardShape(
    position: CardPosition,
    outerShape: Dp = 28.dp,
    innerShape: Dp = 4.dp
): Shape {
    return remember(position, outerShape, innerShape) {
        when (position) {
            CardPosition.Top -> RoundedCornerShape(
                topStart = outerShape,
                topEnd = outerShape,
                bottomStart = innerShape,
                bottomEnd = innerShape
            )

            CardPosition.TopStart -> RoundedCornerShape(
                topStart = outerShape,
                topEnd = innerShape,
                bottomStart = innerShape,
                bottomEnd = innerShape
            )

            CardPosition.TopEnd -> RoundedCornerShape(
                topStart = innerShape,
                topEnd = outerShape,
                bottomStart = innerShape,
                bottomEnd = innerShape
            )

            CardPosition.Middle -> RoundedCornerShape(innerShape)

            CardPosition.Bottom -> RoundedCornerShape(
                topStart = innerShape,
                topEnd = innerShape,
                bottomStart = outerShape,
                bottomEnd = outerShape
            )

            CardPosition.BottomStart -> RoundedCornerShape(
                topStart = innerShape,
                topEnd = innerShape,
                bottomStart = outerShape,
                bottomEnd = innerShape
            )

            CardPosition.BottomEnd -> RoundedCornerShape(
                topStart = innerShape,
                topEnd = innerShape,
                bottomStart = innerShape,
                bottomEnd = outerShape
            )

            CardPosition.Single -> RoundedCornerShape(outerShape)
        }
    }
}

/**
 * Base neon card.
 */
@Composable
private fun NeonSettingsSurface(
    modifier: Modifier,
    shape: Shape,
    enabled: Boolean,
    onClick: (() -> Unit)?,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val backgroundColor = when {
        !enabled -> AlfaaDisabled.copy(alpha = 0.55f)
        onClick != null -> AlfaaSurface2
        else -> AlfaaSurface
    }

    val borderColor = when {
        !enabled -> AlfaaBorder.copy(alpha = 0.35f)
        onClick != null -> AlfaaCyan.copy(alpha = 0.45f)
        else -> AlfaaBorder.copy(alpha = 0.75f)
    }

    Surface(
        modifier = modifier.then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = onClick
                )
            } else {
                Modifier
            }
        ),
        shape = shape,
        color = backgroundColor,
        border = BorderStroke(
            width = 1.dp,
            color = borderColor
        ),
        content = content
    )
}

/**
 * SettingsCard tanpa click.
 */
@Composable
fun SettingsCard(
    modifier: Modifier = Modifier,
    position: CardPosition,
    outerShape: Dp = 28.dp,
    innerShape: Dp = 4.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = rememberSettingsCardShape(
        position = position,
        outerShape = outerShape,
        innerShape = innerShape
    )

    NeonSettingsSurface(
        modifier = modifier,
        shape = shape,
        enabled = true,
        onClick = null,
        content = content
    )
}

/**
 * SettingsCard dengan click.
 */
@Composable
fun SettingsCard(
    modifier: Modifier = Modifier,
    position: CardPosition,
    outerShape: Dp = 28.dp,
    innerShape: Dp = 4.dp,
    onClick: () -> Unit,
    enabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = rememberSettingsCardShape(
        position = position,
        outerShape = outerShape,
        innerShape = innerShape
    )

    NeonSettingsSurface(
        modifier = modifier,
        shape = shape,
        enabled = enabled,
        onClick = onClick,
        content = content
    )
}

/**
 * SettingsCard dengan title + summary + trailing component.
 */
@Composable
fun SettingsCard(
    position: CardPosition,
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    titleStyle: TextStyle = MaterialTheme.typography.titleSmall.copy(
        color = AlfaaText
    ),
    summaryStyle: TextStyle = MaterialTheme.typography.labelSmall.copy(
        color = AlfaaMuted
    ),
    outerShape: Dp = 28.dp,
    innerShape: Dp = 4.dp,
    innerPadding: PaddingValues = PaddingValues(all = 16.dp),
    onClick: () -> Unit,
    trailingIcon: (@Composable RowScope.() -> Unit)? = null,
    enabled: Boolean = true
) {
    val shape = rememberSettingsCardShape(
        position = position,
        outerShape = outerShape,
        innerShape = innerShape
    )

    NeonSettingsSurface(
        modifier = modifier,
        shape = shape,
        enabled = enabled,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TitleAndSummary(
                modifier = Modifier.weight(1f),
                title = title,
                summary = summary,
                titleStyle = titleStyle,
                summaryStyle = summaryStyle
            )

            trailingIcon?.let { trailing ->
                Row(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    content = trailing
                )
            }
        }
    }
}

/**
 * Container untuk kumpulan SettingsCard.
 */
@Composable
fun SettingsCardColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        content = content
    )
}
