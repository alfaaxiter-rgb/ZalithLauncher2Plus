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
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.ui.screens.content

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.download.assets.platform.PlatformClasses
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.TitledNavKey
import com.movtery.zalithlauncher.ui.screens.content.download.DownloadGameScreen
import com.movtery.zalithlauncher.ui.screens.content.download.DownloadModPackScreen
import com.movtery.zalithlauncher.ui.screens.content.download.DownloadModScreen
import com.movtery.zalithlauncher.ui.screens.content.download.DownloadResourcePackScreen
import com.movtery.zalithlauncher.ui.screens.content.download.DownloadSavesScreen
import com.movtery.zalithlauncher.ui.screens.content.download.DownloadShadersScreen
import com.movtery.zalithlauncher.ui.screens.content.download.assets.search.SearchIdScreen
import com.movtery.zalithlauncher.ui.screens.content.elements.CategoryIcon
import com.movtery.zalithlauncher.ui.screens.content.elements.CategoryItem
import com.movtery.zalithlauncher.ui.screens.navigateOnce
import com.movtery.zalithlauncher.ui.screens.onBack
import com.movtery.zalithlauncher.ui.screens.rememberTransitionSpec
import com.movtery.zalithlauncher.viewmodel.ErrorViewModel
import com.movtery.zalithlauncher.viewmodel.EventViewModel
import com.movtery.zalithlauncher.viewmodel.ModpackImportViewModel
import com.movtery.zalithlauncher.viewmodel.ScreenBackStackViewModel

/**
 * Navigasi ke DownloadScreen.
 */
fun ScreenBackStackViewModel.navigateToDownload(
    targetScreen: TitledNavKey? = null
) {
    downloadScreen.clearWith(
        targetScreen ?: downloadGameScreen
    )

    mainScreen.removeAndNavigateTo(
        removes = clearBeforeNavKeys,
        screenKey = downloadScreen,
        useClassEquality = true
    )
}

@Composable
fun DownloadScreen(
    key: NestedNavKey.Download,
    backScreenViewModel: ScreenBackStackViewModel,
    modpackImportViewModel: ModpackImportViewModel,
    eventViewModel: EventViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    BaseScreen(
        screenKey = key,
        currentKey = backScreenViewModel.mainScreen.currentKey,
        useClassEquality = true
    ) { isVisible ->

        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF050608))
        ) {

            DownloadSidebar(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(190.dp),
                isVisible = isVisible,
                backStack = key.backStack,
                backScreenViewModel = backScreenViewModel
            )

            NavigationUI(
                key = key,
                backScreenViewModel = backScreenViewModel,
                eventViewModel = eventViewModel,
                modpackImportViewModel = modpackImportViewModel,
                submitError = submitError,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
private fun DownloadSidebar(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    backStack: NavBackStack<TitledNavKey>,
    backScreenViewModel: ScreenBackStackViewModel
) {
    val downloadsList = listOf(
        CategoryItem(
            backScreenViewModel.downloadGameScreen,
            {
                CategoryIcon(
                    R.drawable.ic_sports_esports_outlined,
                    R.string.download_category_game
                )
            },
            R.string.download_category_game
        ),

        CategoryItem(
            backScreenViewModel.downloadModPackScreen,
            {
                CategoryIcon(
                    R.drawable.ic_package_2_outlined,
                    R.string.download_category_modpack
                )
            },
            R.string.download_category_modpack
        ),

        CategoryItem(
            backScreenViewModel.downloadModScreen,
            {
                CategoryIcon(
                    R.drawable.ic_extension_outlined,
                    R.string.download_category_mod
                )
            },
            R.string.download_category_mod
        ),

        CategoryItem(
            backScreenViewModel.downloadResourcePackScreen,
            {
                CategoryIcon(
                    R.drawable.ic_format_paint_outlined,
                    R.string.download_category_resource_pack
                )
            },
            R.string.download_category_resource_pack
        ),

        CategoryItem(
            backScreenViewModel.downloadSavesScreen,
            {
                CategoryIcon(
                    R.drawable.ic_public,
                    R.string.download_category_saves
                )
            },
            R.string.download_category_saves
        ),

        CategoryItem(
            backScreenViewModel.downloadShadersScreen,
            {
                CategoryIcon(
                    R.drawable.ic_lightbulb,
                    R.string.download_category_shaders
                )
            },
            R.string.download_category_shaders
        ),

        CategoryItem(
            NormalNavKey.SearchId,
            {
                CategoryIcon(
                    R.drawable.ic_card,
                    R.string.download_category_by_id
                )
            },
            R.string.download_category_by_id
        )
    )

    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier.padding(
            start = 12.dp,
            top = 12.dp,
            bottom = 12.dp,
            end = 8.dp
        ),
        shape = RoundedCornerShape(26.dp),
        color = Color(0xFF080A0D),
        tonalElevation = 0.dp
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            /*
             * Header
             */
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF0D1116)
            ) {

                Column(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 18.dp
                    )
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Surface(
                            modifier = Modifier.size(42.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF0D2928)
                        ) {

                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = null,
                                    tint = NeonColors.CyanBright,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "DOWNLOAD",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Explore Minecraft content",
                                color = Color(0xFF7E8794),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "CONTENT",
                color = NeonColors.CyanBright,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 4.dp
                )
            )

            downloadsList.forEach { item ->

                val selected =
                    backScreenViewModel.downloadScreen.currentKey == item.key

                DownloadSidebarItem(
                    item = item,
                    selected = selected,
                    onClick = {
                        backStack.navigateOnce(item.key)
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0A0D11)
            ) {

                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF697381),
                        modifier = Modifier.size(17.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = "Search by ID",
                        color = Color(0xFF697381),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun DownloadSidebarItem(
    item: CategoryItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background =
        if (selected) {
            Color(0xFF102B2B)
        } else {
            Color.Transparent
        }

    val iconBackground =
        if (selected) {
            Color(0xFF123E3B)
        } else {
            Color(0xFF101419)
        }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(17.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(17.dp),
        color = background,
        tonalElevation = 0.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 10.dp,
                    vertical = 9.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(13.dp),
                color = iconBackground
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {
                    item.icon()
                }
            }

            Spacer(
                modifier = Modifier.width(11.dp)
            )

            Text(
                modifier = Modifier
                    .weight(1f)
                    .basicMarquee(
                        iterations = Int.MAX_VALUE
                    ),
                text = stringResource(item.textRes),
                color = if (selected) {
                    Color.White
                } else {
                    Color(0xFF9AA2AE)
                },
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Medium
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (selected) {
                Surface(
                    modifier = Modifier
                        .width(3.dp)
                        .height(24.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = NeonColors.CyanBright
                ) {}
            }
        }
    }
}

@Composable
private fun NavigationUI(
    key: NestedNavKey.Download,
    backScreenViewModel: ScreenBackStackViewModel,
    eventViewModel: EventViewModel,
    modpackImportViewModel: ModpackImportViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit,
    modifier: Modifier = Modifier
) {
    val backStack = key.backStack
    val stackTopKey = backStack.lastOrNull()

    LaunchedEffect(stackTopKey) {
        backScreenViewModel.downloadScreen.currentKey = stackTopKey
    }

    if (backStack.isNotEmpty()) {

        NavDisplay(
            backStack = backStack,
            modifier = modifier,
            onBack = {
                onBack(backStack)
            },
            transitionSpec = rememberTransitionSpec(),
            popTransitionSpec = rememberTransitionSpec(),
            entryProvider = entryProvider {

                entry<NestedNavKey.DownloadGame> { key ->

                    DownloadGameScreen(
                        key = key,
                        mainScreenKey =
                            backScreenViewModel.mainScreen.currentKey,
                        downloadScreenKey =
                            backScreenViewModel.downloadScreen.currentKey,
                        downloadGameScreenKey =
                            backScreenViewModel.downloadGameScreen.currentKey,
                        onCurrentKeyChange = { newKey ->
                            backScreenViewModel
                                .downloadGameScreen
                                .currentKey = newKey
                        },
                        eventViewModel = eventViewModel
                    )
                }

                entry<NestedNavKey.DownloadModPack> { key ->

                    DownloadModPackScreen(
                        key = key,
                        mainScreenKey =
                            backScreenViewModel.mainScreen.currentKey,
                        downloadScreenKey =
                            backScreenViewModel.downloadScreen.currentKey,
                        downloadModPackScreenKey =
                            backScreenViewModel
                                .downloadModPackScreen
                                .currentKey,
                        onCurrentKeyChange = { newKey ->
                            backScreenViewModel
                                .downloadModPackScreen
                                .currentKey = newKey
                        },
                        eventViewModel = eventViewModel,
                        importerViewModel = modpackImportViewModel
                    )
                }

                entry<NestedNavKey.DownloadMod> { key ->

                    DownloadModScreen(
                        key = key,
                        mainScreenKey =
                            backScreenViewModel.mainScreen.currentKey,
                        downloadScreenKey =
                            backScreenViewModel.downloadScreen.currentKey,
                        downloadModScreenKey =
                            backScreenViewModel.downloadModScreen.currentKey,
                        onCurrentKeyChange = { newKey ->
                            backScreenViewModel
                                .downloadModScreen
                                .currentKey = newKey
                        },
                        submitError = submitError,
                        eventViewModel = eventViewModel
                    )
                }

                entry<NestedNavKey.DownloadResourcePack> { key ->

                    DownloadResourcePackScreen(
                        key = key,
                        mainScreenKey =
                            backScreenViewModel.mainScreen.currentKey,
                        downloadScreenKey =
                            backScreenViewModel.downloadScreen.currentKey,
                        downloadResourcePackScreenKey =
                            backScreenViewModel
                                .downloadResourcePackScreen
                                .currentKey,
                        onCurrentKeyChange = { newKey ->
                            backScreenViewModel
                                .downloadResourcePackScreen
                                .currentKey = newKey
                        },
                        submitError = submitError,
                        eventViewModel = eventViewModel
                    )
                }

                entry<NestedNavKey.DownloadSaves> { key ->

                    DownloadSavesScreen(
                        key = key,
                        mainScreenKey =
                            backScreenViewModel.mainScreen.currentKey,
                        downloadScreenKey =
                            backScreenViewModel.downloadScreen.currentKey,
                        downloadSavesScreenKey =
                            backScreenViewModel
                                .downloadSavesScreen
                                .currentKey,
                        onCurrentKeyChange = { newKey ->
                            backScreenViewModel
                                .downloadSavesScreen
                                .currentKey = newKey
                        },
                        submitError = submitError,
                        eventViewModel = eventViewModel
                    )
                }

                entry<NestedNavKey.DownloadShaders> { key ->

                    DownloadShadersScreen(
                        key = key,
                        mainScreenKey =
                            backScreenViewModel.mainScreen.currentKey,
                        downloadScreenKey =
                            backScreenViewModel.downloadScreen.currentKey,
                        downloadShadersScreenKey =
                            backScreenViewModel
                                .downloadShadersScreen
                                .currentKey,
                        onCurrentKeyChange = { newKey ->
                            backScreenViewModel
                                .downloadShadersScreen
                                .currentKey = newKey
                        },
                        submitError = submitError,
                        eventViewModel = eventViewModel
                    )
                }

                entry<NormalNavKey.SearchId> {

                    SearchIdScreen(
                        mainScreenKey =
                            backScreenViewModel.mainScreen.currentKey,
                        downloadScreenKey =
                            backScreenViewModel.downloadScreen.currentKey,

                        swapToDownload = {
                                platform,
                                classes,
                                projectId,
                                iconUrl ->

                            val backStackTarget =
                                when (classes) {

                                    PlatformClasses.MOD ->
                                        backScreenViewModel.downloadModScreen

                                    PlatformClasses.MOD_PACK ->
                                        backScreenViewModel.downloadModPackScreen

                                    PlatformClasses.RESOURCE_PACK ->
                                        backScreenViewModel.downloadResourcePackScreen

                                    PlatformClasses.SAVES ->
                                        backScreenViewModel.downloadSavesScreen

                                    PlatformClasses.SHADERS ->
                                        backScreenViewModel.downloadShadersScreen
                                }

                            backScreenViewModel.navigateToDownload(
                                targetScreen =
                                    backStackTarget.apply {

                                        navigateTo(
                                            NormalNavKey.DownloadAssets(
                                                platform = platform,
                                                projectId = projectId,
                                                classes = PlatformClasses.MOD,
                                                iconUrl = iconUrl
                                            )
                                        )
                                    }
                            )
                        },

                        openLink = { link ->

                            eventViewModel.sendEvent(
                                EventViewModel.Event.OpenLink(link)
                            )
                        }
                    )
                }
            }
        )

    } else {

        Box(
            modifier = modifier
        )
    }
}
