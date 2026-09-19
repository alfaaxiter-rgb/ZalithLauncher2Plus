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
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 */

package com.movtery.zalithlauncher.ui.screens.content

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.components.WarningCard
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.TitledNavKey
import com.movtery.zalithlauncher.ui.screens.content.settings.AboutInfoScreen
import com.movtery.zalithlauncher.ui.screens.content.settings.ControlManageScreen
import com.movtery.zalithlauncher.ui.screens.content.settings.ControlSettingsScreen
import com.movtery.zalithlauncher.ui.screens.content.settings.GameSettingsScreen
import com.movtery.zalithlauncher.ui.screens.content.settings.GamepadSettingsScreen
import com.movtery.zalithlauncher.ui.screens.content.settings.JavaManageScreen
import com.movtery.zalithlauncher.ui.screens.content.settings.LauncherSettingsScreen
import com.movtery.zalithlauncher.ui.screens.content.settings.RendererSettingsScreen
import com.movtery.zalithlauncher.ui.screens.content.settings.TurnipDriversScreen
import com.movtery.zalithlauncher.ui.screens.navigateOnce
import com.movtery.zalithlauncher.ui.screens.onBack
import com.movtery.zalithlauncher.ui.screens.rememberTransitionSpec
import com.movtery.zalithlauncher.ui.screens.content.elements.CategoryIcon
import com.movtery.zalithlauncher.viewmodel.ErrorViewModel
import com.movtery.zalithlauncher.viewmodel.EventViewModel
import com.movtery.zalithlauncher.viewmodel.ScreenBackStackViewModel


@Composable
fun SettingsScreen(
    key: NestedNavKey.Settings,
    backStackViewModel: ScreenBackStackViewModel,
    openLicenseScreen: (raw: Int) -> Unit,
    eventViewModel: EventViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    BaseScreen(
        screenKey = key,
        currentKey = backStackViewModel.mainScreen.currentKey
    ) { isVisible ->

        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (com.movtery.zalithlauncher.setting.enums.isLauncherInDarkTheme()) {
                        NeonColors.DarkBackground
                    } else {
                        NeonColors.LightBackground
                    }
                )
        ) {

            NeonSettingsSidebar(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(205.dp),
                isVisible = isVisible,
                settingsScreenKey = backStackViewModel.settingsScreen.currentKey,
                navigateTo = { settingKey ->
                    key.backStack.navigateOnce(settingKey)
                }
            )

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {

                NavigationUI(
                    key = key,
                    mainScreenKey = backStackViewModel.mainScreen.currentKey,
                    settingsScreenKey = backStackViewModel.settingsScreen.currentKey,
                    onCurrentKeyChange = { newKey ->
                        backStackViewModel.settingsScreen.currentKey = newKey
                    },
                    openLicenseScreen = openLicenseScreen,
                    toHomePageEditor = {
                        backStackViewModel.mainScreen.navigateTo(
                            NormalNavKey.HomePageEditor
                        )
                    },
                    eventViewModel = eventViewModel,
                    submitError = submitError,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/*
 * ============================================================
 * SETTINGS CATEGORIES
 * ============================================================
 */

private data class NeonSettingItem(
    val key: TitledNavKey,
    val icon: Int,
    val title: Int,
    val accent: Color
)

private val settingItems = listOf(

    NeonSettingItem(
        key = NormalNavKey.Settings.Renderer,
        icon = R.drawable.ic_video_settings,
        title = R.string.settings_tab_renderer,
        accent = NeonColors.Cyan
    ),

    NeonSettingItem(
        key = NormalNavKey.Settings.Game,
        icon = R.drawable.ic_rocket_launch_filled,
        title = R.string.settings_tab_game,
        accent = NeonColors.Blue
    ),

    NeonSettingItem(
        key = NormalNavKey.Settings.Control,
        icon = R.drawable.ic_videogame_asset_outlined,
        title = R.string.settings_tab_control,
        accent = NeonColors.Purple
    ),

    NeonSettingItem(
        key = NormalNavKey.Settings.Gamepad,
        icon = R.drawable.ic_sports_esports_outlined,
        title = R.string.settings_tab_gamepad,
        accent = NeonColors.Orange
    ),

    NeonSettingItem(
        key = NormalNavKey.Settings.Launcher,
        icon = R.drawable.ic_setting_launcher,
        title = R.string.settings_tab_launcher,
        accent = NeonColors.CyanBright
    ),

    NeonSettingItem(
        key = NormalNavKey.Settings.ControlManager,
        icon = R.drawable.ic_videogame_asset_outlined,
        title = R.string.settings_tab_control_manage,
        accent = NeonColors.Purple
    ),

    NeonSettingItem(
        key = NormalNavKey.Settings.AboutInfo,
        icon = R.drawable.ic_info_outlined,
        title = R.string.settings_tab_info_about,
        accent = NeonColors.Blue
    )
)

/*
 * ============================================================
 * NEON SIDEBAR
 * ============================================================
 */

@Composable
private fun NeonSettingsSidebar(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    settingsScreenKey: TitledNavKey?,
    navigateTo: (TitledNavKey) -> Unit
) {

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .padding(
                start = 12.dp,
                top = 12.dp,
                bottom = 12.dp
            )
    ) {

        NeonCard(
            modifier = Modifier.fillMaxWidth(),
            accent = NeonColors.Cyan
        ) {

            Column(
                modifier = Modifier.padding(
                    horizontal = 15.dp,
                    vertical = 14.dp
                )
            ) {

                Text(
                    text = "ALFAA",
                    color = NeonColors.Cyan,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "SETTINGS",
                    color = neonTextColor(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "LAUNCHER CONFIGURATION",
                    color = neonMutedColor(),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 7.sp,
                    letterSpacing = 0.8.sp
                )
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {

            settingItems.forEach { item ->

                NeonSettingsItem(
                    item = item,
                    selected = settingsScreenKey == item.key,
                    onClick = {
                        navigateTo(item.key)
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        NeonDivider(
            accent = NeonColors.Cyan.copy(alpha = 0.45f)
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        NeonThemeToggle(
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "TikTok: @alfathgpp",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            color = neonMutedColor(),
            fontFamily = FontFamily.Monospace,
            fontSize = 7.sp
        )
    }
}

/*
 * ============================================================
 * SIDEBAR ITEM
 * ============================================================
 */

@Composable
private fun NeonSettingsItem(
    item: NeonSettingItem,
    selected: Boolean,
    onClick: () -> Unit
) {

    NeonCard(
        modifier = Modifier.fillMaxWidth(),
        accent = item.accent,
        onClick = onClick
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 11.dp,
                    vertical = 11.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier.width(30.dp),
                contentAlignment = Alignment.Center
            ) {

                CategoryIcon(
    icon = item.icon,
    textRes = item.title
)
            }

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = stringResource(item.title),
                    color = if (selected) {
                        item.accent
                    } else {
                        neonTextColor()
                    },
                    fontWeight = if (selected) {
                        FontWeight.ExtraBold
                    } else {
                        FontWeight.Bold
                    },
                    fontSize = 10.sp
                )

                Text(
                    text = if (selected) {
                        "ACTIVE"
                    } else {
                        "CONFIGURE"
                    },
                    color = if (selected) {
                        item.accent.copy(alpha = 0.72f)
                    } else {
                        neonMutedColor()
                    },
                    fontFamily = FontFamily.Monospace,
                    fontSize = 6.sp,
                    letterSpacing = 0.6.sp
                )
            }

            if (selected) {

                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(25.dp)
                        .background(item.accent)
                )
            }
        }
    }
}

/*
 * ============================================================
 * NAVIGATION CONTENT
 * ============================================================
 */

@Composable
private fun NavigationUI(
    key: NestedNavKey.Settings,
    mainScreenKey: TitledNavKey?,
    settingsScreenKey: TitledNavKey?,
    onCurrentKeyChange: (TitledNavKey?) -> Unit,
    openLicenseScreen: (raw: Int) -> Unit,
    toHomePageEditor: () -> Unit,
    eventViewModel: EventViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit,
    modifier: Modifier = Modifier
) {

    val backStack = key.backStack
    val currentKey = backStack.lastOrNull()

    LaunchedEffect(currentKey) {
        onCurrentKeyChange(currentKey)
    }

    Column(
        modifier = modifier
            .padding(
                start = 10.dp,
                end = 12.dp,
                top = 12.dp,
                bottom = 12.dp
            )
    ) {

        /*
         * ====================================================
         * HEADER
         * ====================================================
         */

        NeonSettingsHeader(
            currentKey = currentKey,
            onBack = {
                onBack(backStack)
            }
        )

        /*
         * ====================================================
         * IMPORT / EXPORT WARNING
         * ====================================================
         */

        if (AllSettings.showSettingsTip.state) {

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            WarningCard(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.generic_info),
                text = {
                    Text(
                        text = stringResource(
                            R.string.settings_tip_import_export
                        )
                    )
                },
                onDismiss = {
                    AllSettings.showSettingsTip.save(false)
                }
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        /*
         * ====================================================
         * ACTUAL SETTINGS SCREEN
         *
         * Semua screen lama tetap dipanggil.
         * Hanya shell luarnya yang kita ubah.
         * ====================================================
         */

        if (backStack.isNotEmpty()) {

            NeonCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                accent = NeonColors.Cyan.copy(alpha = 0.45f)
            ) {

                NavDisplay(
                    backStack = backStack,
                    modifier = Modifier.fillMaxSize(),
                    onBack = {
                        onBack(backStack)
                    },
                    transitionSpec = rememberTransitionSpec(),
                    popTransitionSpec = rememberTransitionSpec(),
                    entryProvider = entryProvider {

                        /*
                         * ================================
                         * RENDERER
                         * ================================
                         */

                        entry<NormalNavKey.Settings.Renderer> {

                            RendererSettingsScreen(
                                key,
                                settingsScreenKey,
                                mainScreenKey,
                                eventViewModel
                            )
                        }

                        /*
                         * ================================
                         * TURNIP DRIVERS
                         * ================================
                         */

                        entry<NormalNavKey.Settings.TurnipDrivers> {

                            TurnipDriversScreen(
                                key,
                                settingsScreenKey,
                                mainScreenKey
                            )
                        }

                        /*
                         * ================================
                         * GAME
                         * ================================
                         */

                        entry<NormalNavKey.Settings.Game> {

                            GameSettingsScreen(
                                key,
                                settingsScreenKey,
                                mainScreenKey,
                                eventViewModel
                            )
                        }

                        /*
                         * ================================
                         * CONTROL
                         * ================================
                         */

                        entry<NormalNavKey.Settings.Control> {

                            ControlSettingsScreen(
                                key,
                                settingsScreenKey,
                                mainScreenKey,
                                eventViewModel,
                                submitError
                            )
                        }

                        /*
                         * ================================
                         * GAMEPAD
                         * ================================
                         */

                        entry<NormalNavKey.Settings.Gamepad> {

                            GamepadSettingsScreen(
                                key,
                                settingsScreenKey,
                                mainScreenKey,
                                eventViewModel
                            )
                        }

                        /*
                         * ================================
                         * LAUNCHER
                         * ================================
                         */

                        entry<NormalNavKey.Settings.Launcher> {

                            LauncherSettingsScreen(
                                key = key,
                                settingsScreenKey = settingsScreenKey,
                                mainScreenKey = mainScreenKey,
                                eventViewModel = eventViewModel,
                                toHomePageEditor = toHomePageEditor,
                                submitError = submitError
                            )
                        }

                        /*
                         * ================================
                         * JAVA
                         * ================================
                         */

                        entry<NormalNavKey.Settings.JavaManager> {

                            JavaManageScreen(
                                key,
                                settingsScreenKey,
                                mainScreenKey,
                                eventViewModel,
                                submitError
                            )
                        }

                        /*
                         * ================================
                         * CONTROL MANAGER
                         * ================================
                         */

                        entry<NormalNavKey.Settings.ControlManager> {

                            ControlManageScreen(
                                key,
                                settingsScreenKey,
                                mainScreenKey,
                                eventViewModel,
                                submitError
                            )
                        }

                        /*
                         * ================================
                         * ABOUT
                         * ================================
                         */

                        entry<NormalNavKey.Settings.AboutInfo> {

                            AboutInfoScreen(
                                key = key,
                                settingsScreenKey = settingsScreenKey,
                                mainScreenKey = mainScreenKey,

                                checkUpdate = {
                                    eventViewModel.sendEvent(
                                        EventViewModel.Event.CheckUpdate
                                    )
                                },

                                openLicense = openLicenseScreen,

                                openLink = { url ->

                                    eventViewModel.sendEvent(
                                        EventViewModel.Event.OpenLink(url)
                                    )
                                }
                            )
                        }
                    }
                )
            }

        } else {

            /*
             * Empty state.
             *
             * Tidak mengubah navigation logic.
             */

            NeonCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                accent = NeonColors.Cyan
            ) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(7.dp)
                    ) {

                        Text(
                            text = "SETTINGS",
                            color = NeonColors.Cyan,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            letterSpacing = 2.sp
                        )

                        Text(
                            text = "SELECT A CATEGORY",
                            color = neonMutedColor(),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp
                        )
                    }
                }
            }
        }
    }
}

/*
 * ============================================================
 * SETTINGS HEADER
 * ============================================================
 */

@Composable
private fun NeonSettingsHeader(
    currentKey: TitledNavKey?,
    onBack: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "CONTROL CENTER",
                color = NeonColors.Cyan,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 8.sp,
                letterSpacing = 1.6.sp
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = when (currentKey) {

                    NormalNavKey.Settings.Renderer ->
                        stringResource(R.string.settings_tab_renderer)

                    NormalNavKey.Settings.Game ->
                        stringResource(R.string.settings_tab_game)

                    NormalNavKey.Settings.Control ->
                        stringResource(R.string.settings_tab_control)

                    NormalNavKey.Settings.Gamepad ->
                        stringResource(R.string.settings_tab_gamepad)

                    NormalNavKey.Settings.Launcher ->
                        stringResource(R.string.settings_tab_launcher)

                    NormalNavKey.Settings.JavaManager ->
                        stringResource(R.string.settings_tab_java_manage)

                    NormalNavKey.Settings.ControlManager ->
                        stringResource(R.string.settings_tab_control_manage)

                    NormalNavKey.Settings.AboutInfo ->
                        stringResource(R.string.settings_tab_info_about)

                    else ->
                        "SETTINGS"
                },
                color = neonTextColor(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 23.sp
            )
        }

        Text(
            text = "ZL // CUSTOM UI",
            color = neonMutedColor(),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 7.sp
        )
    }
}
