/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.movtery.zalithlauncher.ui.screens.splash

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.components.InstallableItem
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.content.NeonCard
import com.movtery.zalithlauncher.ui.screens.content.NeonColors
import com.movtery.zalithlauncher.ui.screens.content.NeonPrimaryButton
import com.movtery.zalithlauncher.ui.screens.content.neonBackgroundColor
import com.movtery.zalithlauncher.ui.screens.content.neonMutedColor
import com.movtery.zalithlauncher.ui.screens.content.neonTextColor
import com.movtery.zalithlauncher.utils.animation.getAnimateTween
import com.movtery.zalithlauncher.utils.animation.swapAnimateDpAsState
import com.movtery.zalithlauncher.viewmodel.SplashBackStackViewModel

@Composable
fun UnpackScreen(
    items: List<InstallableItem>,
    screenViewModel: SplashBackStackViewModel,
    onAgreeClick: () -> Unit = {}
) {
    BaseScreen(
        screenKey = NormalNavKey.UnpackDeps,
        currentKey = screenViewModel.splashScreen.currentKey
    ) { isVisible ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(neonBackgroundColor())
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                UnpackTaskList(
                    isVisible = isVisible,
                    items = items,
                    modifier = Modifier
                        .weight(6.5f)
                        .fillMaxHeight()
                )

                ActionMenu(
                    isVisible = isVisible,
                    modifier = Modifier
                        .weight(3.5f)
                        .fillMaxHeight(),
                    onAgreeClick = onAgreeClick
                )
            }
        }
    }
}

@Composable
private fun ActionMenu(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    onAgreeClick: () -> Unit = {}
) {
    var installing by remember {
        mutableStateOf(false)
    }

    val xOffset by swapAnimateDpAsState(
        targetValue = 40.dp,
        swapIn = isVisible,
        isHorizontal = true
    )

    Column(
        modifier = modifier.offset {
            IntOffset(
                x = xOffset.roundToPx(),
                y = 0
            )
        },
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        NeonCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            accent = NeonColors.CyanBright
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
    text = "ALFAA LAUNCHER",
    color = NeonColors.CyanBright,
    fontSize = 12.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = 2.5.sp
)

Text(
    text = "COMPONENT SETUP",
    color = neonTextColor(),
    fontSize = 25.sp,
    fontWeight = FontWeight.ExtraBold,
    letterSpacing = 1.2.sp
)

Text(
    text = if (installing) {
        stringResource(
            R.string.splash_screen_installing
        )
    } else {
        stringResource(
            R.string.splash_screen_unpack_desc
        )
    },
    color = neonMutedColor(),
    fontSize = 13.sp,
    fontWeight = FontWeight.Normal,
    letterSpacing = 0.3.sp,
    lineHeight = 20.sp
)

Text(
    text = if (installing) {
        "INSTALLING..."
    } else {
        "READY TO SET UP"
    },
    color = if (installing) {
        NeonColors.Orange
    } else {
        NeonColors.CyanBright
    },
    fontSize = 10.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = 1.8.sp
)
            }
        }

        NeonPrimaryButton(
            text = if (installing) {
                "INSTALLING..."
            } else {
                "PASANG KOMPONEN"
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !installing,
            onClick = {
                installing = true
                onAgreeClick()
            }
        )
    }
}

@Composable
private fun UnpackTaskList(
    isVisible: Boolean,
    items: List<InstallableItem>,
    modifier: Modifier = Modifier,
) {
    val yOffset by swapAnimateDpAsState(
        targetValue = (-40).dp,
        swapIn = isVisible
    )

    NeonCard(
        modifier = modifier.offset {
            IntOffset(
                x = 0,
                y = yOffset.roundToPx()
            )
        },
        accent = NeonColors.CyanBright
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        top = 18.dp,
                        end = 20.dp,
                        bottom = 10.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "COMPONENTS",
                        color = neonTextColor(),
                        fontSize = 17.sp
                    )

                    Text(
                        text = "${items.size} COMPONENTS READY",
                        color = neonMutedColor(),
                        fontSize = 10.sp,
                        letterSpacing = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .background(
                            color = NeonColors.CyanBright,
                            shape = CircleShape
                        )
                )
            }

            val scrollState = rememberLazyListState()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                state = scrollState
            ) {

                items(items) { item ->

                    TaskItem(
                        item = item
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskItem(
    item: InstallableItem,
    modifier: Modifier = Modifier
) {
    val state by item.state.collectAsStateWithLifecycle()

    val message by item.task.taskMessage
        .collectAsStateWithLifecycle()

    val accent = when (state) {

        InstallableItem.State.FINISHED ->
            NeonColors.CyanBright

        InstallableItem.State.RUNNING ->
            NeonColors.Blue

        InstallableItem.State.PENDING ->
            NeonColors.Orange

        else ->
            NeonColors.Purple
    }

    NeonCard(
        modifier = modifier.fillMaxWidth(),
        accent = accent,
        enabled = state != InstallableItem.State.NOT_STARTED
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .animateContentSize(
                        animationSpec = getAnimateTween()
                    )
            ) {

                Text(
                    text = item.name,
                    color = neonTextColor(),
                    fontSize = 13.sp
                )

                item.summary?.let {

                    Text(
                        text = it,
                        color = neonMutedColor(),
                        fontSize = 11.sp
                    )
                }

                if (
                    state ==
                    InstallableItem.State.RUNNING
                ) {

                    message?.let { taskMessage ->

                        Text(
                            text = taskMessage,
                            color = NeonColors.Blue,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            when (state) {

                InstallableItem.State.NOT_STARTED -> {

                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(
                            R.drawable.ic_folder_zip_outlined
                        ),
                        tint = accent,
                        contentDescription = null
                    )
                }

                InstallableItem.State.PENDING -> {

                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(
                            R.drawable.ic_update
                        ),
                        tint = accent,
                        contentDescription = null
                    )
                }

                InstallableItem.State.RUNNING -> {

                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = accent,
                        strokeWidth = 2.dp
                    )
                }

                InstallableItem.State.FINISHED -> {

                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(
                            R.drawable.ic_check
                        ),
                        tint = accent,
                        contentDescription = null
                    )
                }

                else -> Unit
            }
        }
    }
}
