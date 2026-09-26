/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.movtery.zalithlauncher.ui.screens.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.version.installed.PlayTimeRepository
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionIconImage
import com.movtery.zalithlauncher.utils.PlayTimeUtils
import com.movtery.zalithlauncher.viewmodel.ScreenBackStackViewModel

private val AlfaaBlack = Color(0xFF050608)
private val AlfaaPanel = Color(0xFF0A0D11)
private val AlfaaPanel2 = Color(0xFF10141A)

private val AlfaaCyan = Color(0xFF00E5FF)
private val AlfaaGreen = Color(0xFF00FF9D)

private val AlfaaText = Color(0xFFF1F5F9)
private val AlfaaMuted = Color(0xFF7D8996)
private val AlfaaBorder = Color(0xFF1B3035)

@Composable
fun GameStatsScreen(
    backStackViewModel: ScreenBackStackViewModel
) {
    BaseScreen(
        screenKey = NormalNavKey.GameStats,
        currentKey = backStackViewModel.mainScreen.currentKey
    ) {
        val context = LocalContext.current
        val versions = remember {
            VersionsManager.versions.value
        }

        data class VersionStat(
            val name: String,
            val version: com.movtery.zalithlauncher.game.version.installed.Version,
            val totalMs: Long
        )

        val stats = remember(versions) {
            versions
                .map {
                    VersionStat(
                        it.getVersionName(),
                        it,
                        PlayTimeRepository.getTotalPlayTime(
                            it.getVersionName()
                        )
                    )
                }
                .sortedByDescending {
                    it.totalMs
                }
        }

        val maxMs = stats
            .firstOrNull()
            ?.totalMs
            ?.takeIf { it > 0 }
            ?: 1L

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AlfaaBlack)
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                AlfaaPanel2,
                                AlfaaPanel,
                                AlfaaBlack
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = AlfaaBorder,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(16.dp)
            ) {

                // ─────────────────────────────
                // HEADER
                // ─────────────────────────────

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 4.dp,
                            vertical = 4.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(42.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        AlfaaCyan,
                                        AlfaaGreen
                                    )
                                )
                            )
                    )

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        AlfaaLabel(
                            text = stringResource(
                                R.string.stats_game_stats
                            ),
                            color = AlfaaText,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.6.sp
                        )

                        Spacer(
                            modifier = Modifier.height(2.dp)
                        )

                        AlfaaLabel(
                            text = "PLAYTIME / INSTANCE",
                            color = AlfaaCyan,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(50)
                            )
                            .background(
                                AlfaaGreen.copy(alpha = 0.08f)
                            )
                            .border(
                                1.dp,
                                AlfaaGreen.copy(alpha = 0.35f),
                                RoundedCornerShape(50)
                            )
                            .padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            )
                    ) {
                        AlfaaLabel(
                            text = "${stats.size} INSTANCES",
                            color = AlfaaGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                // ─────────────────────────────
                // CONTENT
                // ─────────────────────────────

                if (stats.all { it.totalMs == 0L }) {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(
                                        RoundedCornerShape(18.dp)
                                    )
                                    .background(
                                        AlfaaCyan.copy(
                                            alpha = 0.07f
                                        )
                                    )
                                    .border(
                                        1.dp,
                                        AlfaaCyan.copy(
                                            alpha = 0.25f
                                        ),
                                        RoundedCornerShape(18.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                AlfaaLabel(
                                    text = "0",
                                    color = AlfaaCyan,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            AlfaaLabel(
                                text = stringResource(
                                    R.string.stats_no_data
                                ),
                                color = AlfaaText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            AlfaaLabel(
                                text = "PLAY A VERSION TO CREATE STATISTICS",
                                color = AlfaaMuted,
                                fontSize = 9.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                } else {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            vertical = 4.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(
                            10.dp
                        )
                    ) {
                        itemsIndexed(
                            stats,
                            key = { _, stat -> stat.name }
                        ) { index, stat ->

                            val progress = (
                                stat.totalMs.toFloat() /
                                    maxMs.toFloat()
                                ).coerceIn(0f, 1f)

                            // ─────────────────────
                            // INSTANCE CARD
                            // ─────────────────────

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(
                                        RoundedCornerShape(18.dp)
                                    )
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                AlfaaPanel2,
                                                AlfaaPanel
                                            )
                                        )
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (index == 0) {
                                            AlfaaCyan.copy(
                                                alpha = 0.42f
                                            )
                                        } else {
                                            AlfaaBorder
                                        },
                                        shape = RoundedCornerShape(
                                            18.dp
                                        )
                                    )
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                // VERSION ICON

                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(
                                            RoundedCornerShape(
                                                14.dp
                                            )
                                        )
                                        .background(
                                            AlfaaBlack
                                        )
                                        .border(
                                            1.dp,
                                            if (index == 0) {
                                                AlfaaCyan.copy(
                                                    alpha = 0.45f
                                                )
                                            } else {
                                                AlfaaBorder
                                            },
                                            RoundedCornerShape(
                                                14.dp
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    VersionIconImage(
                                        version = stat.version,
                                        modifier = Modifier.size(
                                            34.dp
                                        )
                                    )
                                }

                                Spacer(
                                    modifier = Modifier.width(12.dp)
                                )

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {

                                    // NAME + TIME

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AlfaaLabel(
                                            text = stat.name,
                                            color = AlfaaText,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Spacer(
                                            modifier = Modifier.width(8.dp)
                                        )

                                        AlfaaLabel(
                                            text = PlayTimeUtils.formatPlayTime(
                                                context,
                                                stat.totalMs
                                            ),
                                            color = if (index == 0) {
                                                AlfaaGreen
                                            } else {
                                                AlfaaCyan
                                            },
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(
                                        modifier = Modifier.height(8.dp)
                                    )

                                    // NEON PROGRESS BAR

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(7.dp)
                                            .clip(
                                                RoundedCornerShape(50)
                                            )
                                            .background(
                                                AlfaaBlack
                                            )
                                            .border(
                                                1.dp,
                                                AlfaaBorder.copy(
                                                    alpha = 0.7f
                                                ),
                                                RoundedCornerShape(50)
                                            )
                                    ) {
                                        if (progress > 0f) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth(
                                                        progress
                                                    )
                                                    .height(7.dp)
                                                    .clip(
                                                        RoundedCornerShape(
                                                            50
                                                        )
                                                    )
                                                    .background(
                                                        Brush.horizontalGradient(
                                                            listOf(
                                                                AlfaaCyan,
                                                                AlfaaGreen
                                                            )
                                                        )
                                                    )
                                            )
                                        }
                                    }

                                    Spacer(
                                        modifier = Modifier.height(5.dp)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        AlfaaLabel(
                                            text = "PLAYTIME",
                                            color = AlfaaMuted,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        )

                                        AlfaaLabel(
                                            text = "${(progress * 100).toInt()}%",
                                            color = AlfaaMuted,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlfaaLabel(
    text: String,
    color: Color,
    fontSize: androidx.compose.ui.unit.TextUnit = 14.sp,
    fontWeight: FontWeight? = null,
    letterSpacing: androidx.compose.ui.unit.TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier
) {
    BasicText(
        text = text,
        modifier = modifier,
        maxLines = maxLines,
        style = TextStyle(
            color = color,
            fontSize = fontSize,
            fontWeight = fontWeight,
            letterSpacing = letterSpacing
        )
    )
}
