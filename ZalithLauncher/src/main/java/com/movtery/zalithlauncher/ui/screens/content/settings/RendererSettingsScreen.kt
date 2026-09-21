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

package com.movtery.zalithlauncher.ui.screens.content.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberScrollState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.text.font.FontWeight

import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.plugin.driver.Driver
import com.movtery.zalithlauncher.game.plugin.driver.DriverPluginManager
import com.movtery.zalithlauncher.game.plugin.renderer_v2.RendererV2Data
import com.movtery.zalithlauncher.game.renderer.RendererInterface
import com.movtery.zalithlauncher.game.renderer.Renderers
import com.movtery.zalithlauncher.game.renderer.renderers.KopperZinkRenderer
import com.movtery.zalithlauncher.game.version.installed.GraphicsApi
import com.movtery.zalithlauncher.path.URL_CLOUD_DRIVE_DRIVER_PLUGINS
import com.movtery.zalithlauncher.path.URL_CLOUD_RENDERER_PLUGINS
import com.movtery.zalithlauncher.path.URL_GITHUB_DRIVER_PLUGINS
import com.movtery.zalithlauncher.path.URL_GITHUB_RENDERER_PLUGINS
import com.movtery.zalithlauncher.utils.driver.TurnipDownloader
import com.movtery.zalithlauncher.bridge.ZLBridge
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.setting.unit.floatRange
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.components.AnimatedColumn
import com.movtery.zalithlauncher.ui.components.SimpleAlertDialog
import com.movtery.zalithlauncher.ui.components.verticalScrollWithBar
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.TitledNavKey
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.CardPosition
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.IntSliderSettingsCard
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.ListSettingsCard
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.SettingsCard
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.SettingsCardColumn
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.SwitchSettingsCard
import com.movtery.zalithlauncher.ui.screens.navigateTo
import com.movtery.zalithlauncher.utils.device.checkVulkanSupport
import com.movtery.zalithlauncher.utils.isAdrenoGPU
import com.movtery.zalithlauncher.utils.settings.MobileGluesConfig
import com.movtery.zalithlauncher.viewmodel.EventViewModel
import com.movtery.zalithlauncher.viewmodel.sendDLPlugin

/*
 * ============================================================
 * ALFAA NEON THEME
 * ============================================================
 */

private val AlfaaBackground = Color(0xFF05080D)
private val AlfaaSurface = Color(0xFF0A1118)
private val AlfaaSurface2 = Color(0xFF0E1821)

private val AlfaaCyan = Color(0xFF20E0B2)
private val AlfaaBlue = Color(0xFF43C7FF)
private val AlfaaPurple = Color(0xFFA78BFA)

private val AlfaaText = Color(0xFFE8FFF8)
private val AlfaaMuted = Color(0xFF829A98)

private val AlfaaBorder = Color(0xFF16483F)
private val AlfaaError = Color(0xFFFF5577)
private val AlfaaWarning = Color(0xFFFFB454)

private val NeonGradient = Brush.horizontalGradient(
    colors = listOf(
        AlfaaCyan,
        AlfaaBlue,
        AlfaaPurple
    )
)

/*
 * Renderer Settings
 */

@Composable
fun RendererSettingsScreen(
    key: NestedNavKey.Settings,
    settingsScreenKey: TitledNavKey?,
    mainScreenKey: TitledNavKey?,
    eventViewModel: EventViewModel,
) {
    BaseScreen(
        Triple(key, mainScreenKey, false),
        Triple(
            NormalNavKey.Settings.Renderer,
            settingsScreenKey,
            false
        )
    ) { isVisible ->

        val context = LocalContext.current

        var showMobileGluesSettings by remember {
            mutableStateOf(false)
        }

        var showBenchmark by remember {
            mutableStateOf(false)
        }

        var driverToDelete by remember {
            mutableStateOf<Driver?>(null)
        }

        if (showMobileGluesSettings) {
            MobileGluesSettingsDialog(
                onDismissRequest = {
                    showMobileGluesSettings = false
                }
            )
        }

        if (showBenchmark) {
            Dialog(
                onDismissRequest = {
                    showBenchmark = false
                },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false
                )
            ) {
                RendererBenchmarkOverlay(
                    availableRenderers = Renderers.getRenderers(),
                    onDismiss = {
                        showBenchmark = false
                    }
                )
            }
        }

        driverToDelete?.let { driver ->

            SimpleAlertDialog(
                title = stringResource(
                    R.string.generic_delete
                ),
                text = stringResource(
                    R.string.turnip_driver_delete_confirm,
                    driver.name
                ),
                confirmText = stringResource(
                    R.string.generic_delete
                ),
                onConfirm = {

                    java.io.File(
                        driver.path
                    ).deleteRecursively()

                    DriverPluginManager
                        .scanExternalDrivers(context)

                    if (
                        AllSettings.vulkanDriver.getValue() ==
                        driver.id
                    ) {
                        AllSettings.vulkanDriver.save(
                            AllSettings.vulkanDriver.defaultValue
                        )
                    }

                    driverToDelete = null
                },
                onDismiss = {
                    driverToDelete = null
                }
            )
        }

        AnimatedColumn(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScrollWithBar(
                    state = rememberScrollState()
                )
                .padding(12.dp),
            isVisible = isVisible
        ) { scope ->

            /*
             * ====================================================
             * SECTION 01 — RENDERER CORE
             * ====================================================
             */

            AnimatedItem(scope) { yOffset ->

                SettingsCardColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset {
                            IntOffset(
                                x = 0,
                                y = yOffset.roundToPx()
                            )
                        }
                ) {

                    NeonSectionHeader(
                        title = "RENDER ENGINE",
                        subtitle = "GRAPHICS / DRIVER / PERFORMANCE"
                    )

                    RunBenchmarkPill(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 4.dp,
                                end = 4.dp,
                                bottom = 12.dp
                            ),
                        onClick = {
                            showBenchmark = true
                        }
                    )

                    val currentRendererId =
                        AllSettings.renderer.state

                    val v2PluginEnvUnits =
                        remember(currentRendererId) {

                            Renderers.getRenderers()
                                .filterIsInstance<RendererV2Data>()
                                .find {
                                    it.getUniqueIdentifier() ==
                                            currentRendererId
                                }
                                ?.env
                                ?.getConfigurableUnits()
                                ?.takeIf {
                                    it.isNotEmpty()
                                }
                        }

                    var showV2ConfigDialog by remember {
                        mutableStateOf(false)
                    }

                    /*
                     * RENDERER
                     */

                    ListSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Top,
                        unit = AllSettings.renderer,
                        items = Renderers.getRenderers(),

                        title = stringResource(
                            R.string.settings_renderer_global_renderer_title
                        ),

                        summary = stringResource(
                            R.string.settings_renderer_global_renderer_summary
                        ),

                        getItemText = {
                            it.getRendererName()
                        },

                        getItemId = {
                            it.getUniqueIdentifier()
                        },

                        getItemSummary = { renderer ->

                            Column {

                                RendererSummaryLayout(
                                    renderer
                                )

                                if (
                                    renderer.getRendererName() ==
                                    "MobileGlues"
                                ) {

                                    val hasConfig =
                                        remember {
                                            MobileGluesConfig
                                                .load() != null
                                        }

                                    if (hasConfig) {

                                        Text(
                                            text = "✓ CONFIGURED",
                                            fontSize = 10.sp,
                                            fontWeight =
                                                FontWeight.Bold,
                                            letterSpacing =
                                                1.3.sp,
                                            color = AlfaaCyan
                                        )
                                    }
                                }
                            }
                        },

                        getItemTrailing = { renderer ->

                            if (
                                renderer.getRendererName() ==
                                "MobileGlues"
                            ) {

                                NeonIconButton(
                                    icon = R.drawable.ic_settings_filled,
                                    contentDescription =
                                        stringResource(
                                            R.string.generic_setting
                                        ),
                                    onClick = {
                                        showMobileGluesSettings = true
                                    }
                                )
                            }
                        },

                        trailingIcon = {

                            if (
                                v2PluginEnvUnits != null
                            ) {

                                NeonIconButton(
                                    icon =
                                        R.drawable.ic_settings_filled,
                                    contentDescription =
                                        stringResource(
                                            R.string.settings_renderer_config_title
                                        ),
                                    onClick = {
                                        showV2ConfigDialog = true
                                    }
                                )
                            }

                            NeonIconButton(
                                icon =
                                    R.drawable.ic_download_2_filled,
                                contentDescription =
                                    stringResource(
                                        R.string.generic_download
                                    ),
                                onClick = {

                                    eventViewModel.sendDLPlugin(
                                        githubLink =
                                            URL_GITHUB_RENDERER_PLUGINS,

                                        cloudDrives =
                                            listOf(
                                                EventViewModel
                                                    .Event
                                                    .DownloadPlugins
                                                    .CloudDrive(
                                                        language = "zh",
                                                        link =
                                                            URL_CLOUD_RENDERER_PLUGINS
                                                    )
                                            )
                                    )
                                }
                            )
                        }
                    )

                    /*
                     * V2 CONFIG
                     */

                    if (
                        showV2ConfigDialog &&
                        v2PluginEnvUnits != null
                    ) {

                        RendererV2ConfigDialog(
                            units = v2PluginEnvUnits,
                            onDismissRequest = {
                                showV2ConfigDialog = false
                            }
                        )
                    }

                    /*
                     * VULKAN DRIVER
                     */

                    ListSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Middle,
                        unit = AllSettings.vulkanDriver,
                        items = DriverPluginManager.getDriverList(),

                        title = stringResource(
                            R.string.settings_renderer_global_vulkan_driver_title
                        ),

                        getItemText = {
                            it.name
                        },

                        getItemId = {
                            it.id
                        },

                        getItemSummary = {
                            DriverSummaryLayout(it)
                        },

                        getItemTrailing = { driver ->

                            if (driver.isExternal) {

                                NeonIconButton(
                                    icon =
                                        R.drawable.ic_delete_filled,
                                    contentDescription =
                                        stringResource(
                                            R.string.generic_delete
                                        ),
                                    tint = AlfaaError,
                                    onClick = {
                                        driverToDelete = driver
                                    }
                                )
                            }
                        },

                        trailingIcon = {

                            NeonIconButton(
                                icon =
                                    R.drawable.ic_download_2_filled,
                                contentDescription =
                                    stringResource(
                                        R.string.generic_download
                                    ),
                                onClick = {

                                    eventViewModel.sendDLPlugin(
                                        githubLink =
                                            URL_GITHUB_DRIVER_PLUGINS,

                                        cloudDrives =
                                            listOf(
                                                EventViewModel
                                                    .Event
                                                    .DownloadPlugins
                                                    .CloudDrive(
                                                        language = "zh",
                                                        link =
                                                            URL_CLOUD_DRIVE_DRIVER_PLUGINS
                                                    )
                                            )
                                    )
                                }
                            )
                        }
                    )

                    /*
                     * TURNIP
                     */

                    SettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Middle,

                        title = stringResource(
                            R.string.settings_renderer_download_turnip
                        ),

                        summary = stringResource(
                            R.string.settings_renderer_download_turnip_summary
                        ),

                        onClick = {
                            key.backStack.navigateTo(
                                NormalNavKey.Settings.TurnipDrivers
                            )
                        },

                        trailingIcon = {

                            Row {

                                NeonIconButton(
                                    icon = R.drawable.ic_link,
                                    contentDescription =
                                        stringResource(
                                            R.string.generic_open_link
                                        ),
                                    onClick = {
                                        eventViewModel.sendEvent(
                                            EventViewModel
                                                .Event
                                                .OpenWeb(
                                                    TurnipDownloader
                                                        .getRepoReleasesUrl()
                                                )
                                        )
                                    }
                                )

                                Icon(
                                    painter = painterResource(
                                        R.drawable.ic_download
                                    ),
                                    contentDescription = null,
                                    tint = AlfaaBlue,
                                    modifier = Modifier
                                        .align(
                                            Alignment.CenterVertically
                                        )
                                        .padding(
                                            end = 12.dp
                                        )
                                )
                            }
                        }
                    )

                    /*
                     * GRAPHICS API
                     */

                    ListSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Middle,
                        unit = AllSettings.graphicsApi,
                        items = GraphicsApi.entries,

                        title = stringResource(
                            R.string.settings_game_graphics_api_title
                        ),

                        summary = stringResource(
                            R.string.settings_game_graphics_api_summary
                        ),

                        getItemText = {

                            when (it) {

                                GraphicsApi.DEFAULT ->
                                    stringResource(
                                        R.string.settings_game_graphics_api_default
                                    )

                                GraphicsApi.DEFAULT_OPENGL ->
                                    stringResource(
                                        R.string.settings_game_graphics_api_default_opengl
                                    )

                                else ->
                                    it.displayName
                            }
                        }
                    )

                    /*
                     * RESOLUTION
                     */

                    IntSliderSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Middle,
                        unit = AllSettings.resolutionRatio,

                        title = stringResource(
                            R.string.settings_renderer_resolution_scale_title
                        ),

                        summary = stringResource(
                            R.string.settings_renderer_resolution_scale_summary
                        ),

                        valueRange =
                            AllSettings.resolutionRatio.floatRange,

                        suffix = "%",

                        fineTuningControl = true
                    )

                    /*
                     * FULLSCREEN
                     */

                    SwitchSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Bottom,
                        unit = AllSettings.gameFullScreen,

                        title = stringResource(
                            R.string.settings_renderer_full_screen_title
                        ),

                        summary = stringResource(
                            R.string.settings_renderer_full_screen_summary
                        )
                    )
                }
            }

            /*
             * ====================================================
             * SECTION 02 — PERFORMANCE
             * ====================================================
             */

            AnimatedItem(scope) { yOffset ->

                SettingsCardColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset {
                            IntOffset(
                                x = 0,
                                y = yOffset.roundToPx()
                            )
                        }
                ) {

                    NeonSectionHeader(
                        title = "PERFORMANCE",
                        subtitle = "DEVICE / FPS / GPU OPTIMIZATION"
                    )

                    /*
                     * SUSTAINED PERFORMANCE
                     */

                    SwitchSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Top,
                        unit = AllSettings.sustainedPerformance,

                        title = stringResource(
                            R.string.settings_renderer_sustained_performance_title
                        ),

                        summary = stringResource(
                            R.string.settings_renderer_sustained_performance_summary
                        )
                    )

                    /*
                     * SYSTEM DRIVER
                     */

                    var adrenoGPUAlert by remember {
                        mutableStateOf(false)
                    }

                    SwitchSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Middle,
                        unit = AllSettings.zinkPreferSystemDriver,

                        title = stringResource(
                            R.string.settings_renderer_vulkan_driver_system_title
                        ),

                        summary = stringResource(
                            R.string.settings_renderer_vulkan_driver_system_summary
                        ),

                        onCheckedChange = { checked ->

                            if (
                                checked &&
                                isAdrenoGPU()
                            ) {
                                adrenoGPUAlert = true
                            }
                        }
                    )

                    if (adrenoGPUAlert) {

                        SimpleAlertDialog(
                            title = stringResource(
                                R.string.generic_warning
                            ),

                            text = stringResource(
                                R.string.settings_renderer_zink_driver_adreno
                            ),

                            onConfirm = {

                                AllSettings
                                    .zinkPreferSystemDriver
                                    .save(true)

                                adrenoGPUAlert = false
                            },

                            onDismiss = {

                                AllSettings
                                    .zinkPreferSystemDriver
                                    .save(false)

                                adrenoGPUAlert = false
                            }
                        )
                    }

                    /*
                     * VSYNC
                     */

                    SwitchSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Middle,
                        unit = AllSettings.vsyncInZink,

                        title = stringResource(
                            R.string.settings_renderer_vsync_in_zink_title
                        ),

                        summary = stringResource(
                            R.string.settings_renderer_vsync_in_zink_summary
                        )
                    )

                    /*
                     * BIG CORE
                     */

                    SwitchSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Middle,
                        unit = AllSettings.bigCoreAffinity,

                        title = stringResource(
                            R.string.settings_renderer_force_big_core_title
                        ),

                        summary = stringResource(
                            R.string.settings_renderer_force_big_core_summary
                        )
                    )

                    /*
                     * FPS LIMIT
                     */

                    val display =
                        LocalContext.current.display

                    SwitchSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Middle,
                        unit = AllSettings.fpsLimitEnabled,

                        title = stringResource(
                            R.string.settings_renderer_fps_limit_title
                        ),

                        summary = stringResource(
                            R.string.settings_renderer_fps_limit_summary
                        ),

                        onCheckedChange = { checked ->

                            AllSettings
                                .fpsLimitEnabled
                                .save(checked)

                            if (checked) {

                                val hz =
                                    display
                                        ?.refreshRate
                                        ?.roundToInt()
                                        ?: 60

                                AllSettings
                                    .fpsLimit
                                    .save(hz)

                                ZLBridge.fpsLimitSet(hz)

                            } else {

                                ZLBridge.fpsLimitSet(0)
                            }
                        }
                    )

                    if (
                        AllSettings.fpsLimitEnabled.state
                    ) {

                        IntSliderSettingsCard(
                            modifier =
                                Modifier.fillMaxWidth(),

                            position =
                                CardPosition.Middle,

                            value =
                                AllSettings.fpsLimit.state,

                            onValueChange = {
                                AllSettings
                                    .fpsLimit
                                    .updateState(it)
                            },

                            onValueChangeFinished = {

                                val fps =
                                    AllSettings
                                        .fpsLimit
                                        .state

                                AllSettings
                                    .fpsLimit
                                    .save(fps)

                                ZLBridge.fpsLimitSet(
                                    fps
                                )
                            },

                            title = stringResource(
                                R.string.settings_renderer_fps_limit_title
                            ),

                            valueRange =
                                AllSettings
                                    .fpsLimit
                                    .floatRange,

                            suffix = " FPS",

                            fineTuningControl = false
                        )
                    }

                    /*
                     * SURFACE VIEW
                     */

                    val isKopperZinkSelected =
                        AllSettings.renderer.state ==
                                KopperZinkRenderer
                                    .getUniqueIdentifier()

                    var surfaceViewAutoDisabledAlert by remember {
                        mutableStateOf(false)
                    }

                    LaunchedEffect(
                        isKopperZinkSelected
                    ) {

                        if (
                            isKopperZinkSelected &&
                            AllSettings.useSurfaceView.state
                        ) {

                            AllSettings
                                .useSurfaceView
                                .save(false)

                            if (
                                !AllSettings
                                    .surfaceViewKopperWarningDontShow
                                    .state
                            ) {
                                surfaceViewAutoDisabledAlert =
                                    true
                            }
                        }
                    }

                    SwitchSettingsCard(
                        modifier =
                            Modifier.fillMaxWidth(),

                        position =
                            CardPosition.Middle,

                        checked =
                            AllSettings
                                .useSurfaceView
                                .state &&
                                !isKopperZinkSelected,

                        enabled =
                            !isKopperZinkSelected,

                        onCheckedChange = {
                            AllSettings
                                .useSurfaceView
                                .save(it)
                        },

                        title = stringResource(
                            R.string.settings_renderer_surface_title
                        ),

                        summary =
                            if (
                                isKopperZinkSelected
                            ) {

                                stringResource(
                                    R.string.settings_renderer_surface_summary_kopper_disabled
                                )

                            } else {

                                stringResource(
                                    R.string.settings_renderer_surface_summary
                                )
                            }
                    )

                    if (
                        surfaceViewAutoDisabledAlert
                    ) {

                        AlertDialog(
                            onDismissRequest = {
                                surfaceViewAutoDisabledAlert =
                                    false
                            },

                            title = {
                                Text(
                                    text =
                                        stringResource(
                                            R.string.generic_warning
                                        ),
                                    color = AlfaaCyan,
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            },

                            text = {

                                Text(
                                    text =
                                        stringResource(
                                            R.string.settings_renderer_surface_kopper_warning
                                        ),
                                    color = AlfaaText
                                )
                            },

                            confirmButton = {

                                Button(
                                    colors =
                                        ButtonDefaults
                                            .buttonColors(
                                                containerColor =
                                                    AlfaaCyan,
                                                contentColor =
                                                    Color.Black
                                            ),

                                    onClick = {

                                        AllSettings
                                            .surfaceViewKopperWarningDontShow
                                            .save(true)

                                        surfaceViewAutoDisabledAlert =
                                            false
                                    }
                                ) {

                                    Text(
                                        stringResource(
                                            R.string.settings_renderer_surface_kopper_warning_dont_show
                                        )
                                    )
                                }
                            },

                            dismissButton = {

                                OutlinedButton(
                                    colors =
                                        ButtonDefaults
                                            .outlinedButtonColors(
                                                contentColor =
                                                    AlfaaCyan
                                            ),

                                    onClick = {
                                        surfaceViewAutoDisabledAlert =
                                            false
                                    }
                                ) {

                                    Text(
                                        stringResource(
                                            R.string.generic_confirm
                                        )
                                    )
                                }
                            },

                            containerColor =
                                AlfaaSurface,

                            shape =
                                RoundedCornerShape(
                                    20.dp
                                )
                        )
                    }

                    /*
                     * SHADER DUMP
                     */

                    SwitchSettingsCard(
                        modifier =
                            Modifier.fillMaxWidth(),

                        position =
                            CardPosition.Bottom,

                        unit =
                            AllSettings.dumpShaders,

                        title =
                            stringResource(
                                R.string.settings_renderer_shader_dump_title
                            ),

                        summary =
                            stringResource(
                                R.string.settings_renderer_shader_dump_summary
                            )
                    )
                }
            }
        }
    }
}

/*
 * ============================================================
 * RENDERER SUMMARY
 * ============================================================
 */

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun RendererSummaryLayout(
    renderer: RendererInterface
) {

    FlowRow(
        modifier = Modifier.alpha(0.9f),

        horizontalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {

        with(renderer) {

            getRendererSummary()?.let { summary ->

                Text(
                    text = summary,
                    color = AlfaaMuted,
                    fontSize = 10.sp,
                    letterSpacing = 0.4.sp
                )
            }

            val minVer =
                getMinMCVersion()

            val maxVer =
                getMaxMCVersion()

            if (
                minVer != null ||
                maxVer != null
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically,

                    horizontalArrangement =
                        Arrangement.spacedBy(4.dp)
                ) {

                    Text(
                        text =
                            stringResource(
                                R.string.renderer_version_support
                            ),

                        color = AlfaaBlue,
                        fontSize = 10.sp,
                        fontWeight =
                            FontWeight.Bold
                    )

                    minVer?.let {

                        Text(
                            text = ">= $it",
                            color = AlfaaMuted,
                            fontSize = 10.sp
                        )
                    }

                    maxVer?.let {

                        Text(
                            text = "<= $it",
                            color = AlfaaMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

/*
 * ============================================================
 * DRIVER SUMMARY
 * ============================================================
 */

@Composable
fun DriverSummaryLayout(
    driver: Driver
) {

    with(driver) {

        summary?.let { text ->

            Text(
                modifier =
                    Modifier.alpha(0.9f),

                text = text,

                color = AlfaaMuted,

                fontSize = 10.sp,

                letterSpacing = 0.3.sp
            )
        }
    }
}

/*
 * ============================================================
 * BENCHMARK BUTTON
 * ============================================================
 */

@Composable
private fun RunBenchmarkPill(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Surface(
        modifier = modifier,

        shape =
            RoundedCornerShape(
                16.dp
            ),

        color =
            AlfaaSurface,

        border =
            BorderStroke(
                1.dp,
                AlfaaCyan.copy(
                    alpha = 0.55f
                )
            ),

        shadowElevation = 6.dp,

        onClick = onClick
    ) {

        Row(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            AlfaaCyan.copy(
                                alpha = 0.22f
                            ),
                            AlfaaBlue.copy(
                                alpha = 0.15f
                            )
                        )
                    )
                )
                .padding(
                    horizontal = 20.dp,
                    vertical = 14.dp
                ),

            horizontalArrangement =
                Arrangement.Center,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                painter =
                    painterResource(
                        R.drawable.ic_rocket_launch_filled
                    ),

                contentDescription = null,

                tint = AlfaaCyan,

                modifier =
                    Modifier.size(20.dp)
            )

            Text(
                modifier =
                    Modifier.padding(
                        start = 10.dp
                    ),

                text =
                    stringResource(
                        R.string.benchmark_run
                    ),

                color = AlfaaText,

                fontSize = 13.sp,

                fontWeight =
                    FontWeight.Bold,

                letterSpacing = 1.1.sp
            )
        }
    }
}

/*
 * ============================================================
 * NEON ICON BUTTON
 * ============================================================
 */

@Composable
private fun NeonIconButton(
    icon: Int,
    contentDescription: String?,
    onClick: () -> Unit,
    tint: Color = AlfaaCyan
) {

    Surface(
        modifier = Modifier.size(38.dp),

        shape =
            RoundedCornerShape(
                11.dp
            ),

        color =
            tint.copy(
                alpha = 0.08f
            ),

        border =
            BorderStroke(
                1.dp,
                tint.copy(
                    alpha = 0.28f
                )
            ),

        onClick = onClick
    ) {

        Icon(
            painter =
                painterResource(icon),

            contentDescription =
                contentDescription,

            tint = tint,

            modifier =
                Modifier
                    .padding(9.dp)
                    .size(20.dp)
        )
    }
}
