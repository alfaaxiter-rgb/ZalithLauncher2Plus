/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.movtery.zalithlauncher.ui.screens.content.settings.layouts

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.setting.unit.EnumSettingUnit
import com.movtery.zalithlauncher.ui.components.TitleAndSummary
import com.movtery.zalithlauncher.ui.screens.content.elements.DisabledAlpha
import kotlin.enums.EnumEntries

private val AlfaaBackground = Color(0xFF05080D)
private val AlfaaSurface = Color(0xFF0B1118)
private val AlfaaSurface2 = Color(0xFF101923)
private val AlfaaCyan = Color(0xFF43C7FF)
private val AlfaaGreen = Color(0xFF20E0B2)
private val AlfaaText = Color(0xFFE8FFF8)
private val AlfaaMuted = Color(0xFF829A98)
private val AlfaaBorder = Color(0xFF16483F)

@Composable
fun <E : Enum<E>> EnumSettingsCard(
    value: E,
    entries: EnumEntries<E>,
    title: String,
    position: CardPosition,
    modifier: Modifier = Modifier,
    outerShape: Dp = 28.dp,
    innerShape: Dp = 4.dp,
    summary: String? = null,
    getRadioText: @Composable (E) -> String,
    getRadioEnable: (E) -> Boolean,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
    titleStyle: TextStyle = MaterialTheme.typography.titleSmall,
    summaryStyle: TextStyle = MaterialTheme.typography.labelSmall,
    onRadioClick: (E) -> Unit = {},
) {
    SettingsCard(
        modifier = modifier,
        position = position,
        outerShape = outerShape,
        innerShape = innerShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TitleAndSummary(
                title = title,
                summary = summary,
                titleStyle = titleStyle,
                summaryStyle = summaryStyle
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(
                        animationSpec = tween(durationMillis = 180)
                    ),
                horizontalArrangement = Arrangement.spacedBy(
                    6.dp,
                    Alignment.CenterHorizontally
                ),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                maxItemsInEachRow = maxItemsInEachRow
            ) {
                entries.forEach { enum ->
                    val enabled = getRadioEnable(enum)
                    val selected = value == enum
                    val radioText = getRadioText(enum)

                    val interactionModifier = remember(enabled) {
                        Modifier.selectable(
                            selected = selected,
                            enabled = enabled,
                            role = Role.RadioButton,
                            onClick = {
                                if (enabled) {
                                    onRadioClick(enum)
                                }
                            }
                        )
                    }

                    Surface(
                        modifier = interactionModifier
                            .padding(2.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = if (selected) {
                            AlfaaCyan.copy(alpha = 0.12f)
                        } else {
                            AlfaaSurface2.copy(alpha = 0.72f)
                        },
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (selected) {
                                AlfaaCyan.copy(alpha = 0.65f)
                            } else {
                                AlfaaBorder.copy(alpha = 0.65f)
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                enabled = enabled,
                                selected = selected,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = AlfaaGreen,
                                    unselectedColor = AlfaaMuted,
                                    disabledSelectedColor = AlfaaGreen.copy(
                                        alpha = 0.35f
                                    ),
                                    disabledUnselectedColor = AlfaaMuted.copy(
                                        alpha = 0.25f
                                    )
                                )
                            )

                            Text(
                                text = radioText,
                                color = when {
                                    !enabled -> AlfaaMuted
                                    selected -> AlfaaText
                                    else -> AlfaaText.copy(alpha = 0.88f)
                                },
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .alpha(
                                        if (enabled) {
                                            1f
                                        } else {
                                            DisabledAlpha
                                        }
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

@NonRestartableComposable
@Composable
fun <E : Enum<E>> EnumSettingsCard(
    unit: EnumSettingUnit<E>,
    entries: EnumEntries<E>,
    title: String,
    position: CardPosition,
    modifier: Modifier = Modifier,
    outerShape: Dp = 28.dp,
    innerShape: Dp = 4.dp,
    summary: String? = null,
    getRadioText: @Composable (E) -> String,
    getRadioEnable: (E) -> Boolean,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
    titleStyle: TextStyle = MaterialTheme.typography.titleSmall,
    summaryStyle: TextStyle = MaterialTheme.typography.labelSmall,
    onRadioClick: (E) -> Unit = {},
    onValueChange: (E) -> Unit = {},
) = EnumSettingsCard(
    value = unit.state,
    entries = entries,
    title = title,
    position = position,
    modifier = modifier,
    outerShape = outerShape,
    innerShape = innerShape,
    summary = summary,
    getRadioText = getRadioText,
    getRadioEnable = getRadioEnable,
    maxItemsInEachRow = maxItemsInEachRow,
    titleStyle = titleStyle,
    summaryStyle = summaryStyle,
    onRadioClick = { enum ->
        onRadioClick(enum)

        if (unit.state == enum) return@EnumSettingsCard

        unit.save(enum)
        onValueChange(enum)
    },
)
