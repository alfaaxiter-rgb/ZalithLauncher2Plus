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

package com.movtery.zalithlauncher.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.coroutine.Task
import com.movtery.zalithlauncher.coroutine.TaskSystem
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.ui.AndroidStringText
import com.movtery.zalithlauncher.ui.androidText
import com.movtery.zalithlauncher.ui.components.BackgroundCard
import com.movtery.zalithlauncher.ui.components.CardTitleLayout
import com.movtery.zalithlauncher.ui.components.SimpleAlertDialog
import com.movtery.zalithlauncher.ui.screens.BackStackNavKey
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.content.AccountManageScreen
import com.movtery.zalithlauncher.ui.screens.content.BuiltInFileManagerScreen
import com.movtery.zalithlauncher.ui.screens.content.DownloadScreen
import com.movtery.zalithlauncher.ui.screens.content.FileEditorScreen
import com.movtery.zalithlauncher.ui.screens.content.FileSelectorScreen
import com.movtery.zalithlauncher.ui.screens.content.HomePageEditorScreen
import com.movtery.zalithlauncher.ui.screens.content.LauncherScreen
import com.movtery.zalithlauncher.ui.screens.content.LicenseScreen
import com.movtery.zalithlauncher.ui.screens.content.GameStatsScreen
import com.movtery.zalithlauncher.ui.screens.content.CapeGalleryScreen
import com.movtery.zalithlauncher.ui.screens.content.PlayTimeStatsScreen
import com.movtery.zalithlauncher.ui.screens.content.RecordingsScreen
import com.movtery.zalithlauncher.ui.screens.content.LogViewScreen
import com.movtery.zalithlauncher.ui.screens.content.MultiplayerScreen
import com.movtery.zalithlauncher.ui.screens.content.SettingsScreen
import com.movtery.zalithlauncher.ui.screens.content.VersionExportScreen
import com.movtery.zalithlauncher.ui.screens.content.VersionSettingsScreen
import com.movtery.zalithlauncher.ui.screens.content.VersionsManageScreen
import com.movtery.zalithlauncher.ui.screens.content.WebViewScreen
import com.movtery.zalithlauncher.ui.screens.content.assetinfo.AssetInfoScreen
import com.movtery.zalithlauncher.ui.screens.navigateTo
import com.movtery.zalithlauncher.ui.screens.onBack
import com.movtery.zalithlauncher.ui.screens.rememberTransitionSpec
import com.movtery.zalithlauncher.ui.theme.backgroundColor
import com.movtery.zalithlauncher.ui.theme.cardColor
import com.movtery.zalithlauncher.ui.theme.onBackgroundColor
import com.movtery.zalithlauncher.ui.theme.onCardColor
import com.movtery.zalithlauncher.utils.animation.getAnimateTween
import com.movtery.zalithlauncher.utils.file.formatFileSize
import com.movtery.zalithlauncher.viewmodel.ErrorViewModel
import com.movtery.zalithlauncher.viewmodel.EventViewModel
import com.movtery.zalithlauncher.viewmodel.LocalBackgroundViewModel
import com.movtery.zalithlauncher.viewmodel.ModpackImportViewModel
import com.movtery.zalithlauncher.viewmodel.ScreenBackStackViewModel
import com.movtery.zalithlauncher.viewmodel.sendKeepScreen
import com.movtery.zalithlauncher.viewmodel.sendToast

@Composable
fun MainScreen(
    screenBackStackModel: ScreenBackStackViewModel,
    eventViewModel: EventViewModel,
    modpackImportViewModel: ModpackImportViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    val tasks by TaskSystem.tasksFlow.collectAsStateWithLifecycle()

    // Monitor running tasks.
    LaunchedEffect(tasks) {
        if (tasks.isEmpty()) {
            eventViewModel.sendKeepScreen(false)
        } else {
            eventViewModel.sendKeepScreen(true)
        }
    }

    val isTaskMenuExpanded =
        AllSettings.launcherTaskMenuExpanded.state

    val showDisclaimer =
        AllSettings.disclaimerAccepted.state

    val context = LocalContext.current

    if (!showDisclaimer) {
        SimpleAlertDialog(
            title = "ZalithLauncher 2 Plus",
            text = "Launcher ini merupakan fork yang dikustomisasi secara independen.\n\n" +
                    "UI launcher telah dimodifikasi dan dikembangkan kembali untuk " +
                    "memberikan tampilan yang berbeda dari launcher bawaan.",
            confirmText = stringResource(
                R.string.generic_got_it
            ),
            dismissText = "TikTok",
            onConfirm = {
                AllSettings.disclaimerAccepted.save(true)
            },
            onDismiss = {
                val intent = android.content.Intent(
                    android.content.Intent.ACTION_VIEW,
                    android.net.Uri.parse(
                        "https://www.tiktok.com/@alfathgpp"
                    )
                )
                context.startActivity(intent)
            }
        )
    }

    fun changeTasksExpandedState() {
        AllSettings.launcherTaskMenuExpanded.save(
            !isTaskMenuExpanded
        )
    }

    /**
     * Return to launcher main screen.
     */
    val toMainScreen: () -> Unit = {
        screenBackStackModel.mainScreen.clearWith(
            NormalNavKey.LauncherMain
        )
    }

    val mainScreenKey =
        screenBackStackModel.mainScreen.currentKey

    val isBackgroundValid =
        LocalBackgroundViewModel.current?.isValid == true

    val launcherBackgroundOpacity =
        AllSettings.launcherBackgroundOpacity.state.toFloat() / 100f

    val backgroundColor = if (isBackgroundValid) {
        backgroundColor().copy(
            alpha = launcherBackgroundOpacity
        )
    } else {
        backgroundColor()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor,
        contentColor = onBackgroundColor()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                NavigationUI(
                    modifier = Modifier.fillMaxSize(),
                    screenBackStackModel = screenBackStackModel,
                    toMainScreen = toMainScreen,
                    eventViewModel = eventViewModel,
                    modpackImportViewModel = modpackImportViewModel,
                    submitError = submitError
                )

                TaskMenu(
                    tasks = tasks,
                    isExpanded = isTaskMenuExpanded,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.3f)
                        .align(Alignment.CenterStart)
                        .padding(6.dp)
                ) {
                    changeTasksExpandedState()
                }
            }
        }
    }
}

/*
 * Navigation content.
 *
 * TopBar has intentionally been removed.
 * All launcher navigation is handled by the custom
 * persistent left sidebar.
 */
@Composable
private fun NavigationUI(
    modifier: Modifier = Modifier,
    screenBackStackModel: ScreenBackStackViewModel,
    toMainScreen: () -> Unit,
    eventViewModel: EventViewModel,
    modpackImportViewModel: ModpackImportViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    val backStack =
        screenBackStackModel.mainScreen.backStack

    val currentKey =
        backStack.lastOrNull()

    LaunchedEffect(currentKey) {
        screenBackStackModel.mainScreen.currentKey =
            currentKey
    }

    if (backStack.isNotEmpty()) {

        /**
         * Navigate to version settings.
         */
        val navigateToVersions: (Version) -> Unit =
            remember(screenBackStackModel) {
                { version ->
                    screenBackStackModel.mainScreen.navigateTo(
                        screenKey = NestedNavKey.VersionSettings(
                            version
                        ),
                        useClassEquality = true
                    )
                }
            }

        /**
         * Navigate to modpack export.
         */
        val navigateToExport: (Version) -> Unit =
            remember(screenBackStackModel) {
                { version ->
                    screenBackStackModel.mainScreen.removeAndNavigateTo(
                        remove = NestedNavKey.VersionSettings::class,
                        screenKey = NestedNavKey.VersionExport(
                            version
                        ),
                        useClassEquality = true
                    )
                }
            }

        val provider = remember(
            screenBackStackModel,
            toMainScreen,
            eventViewModel,
            modpackImportViewModel,
            submitError,
            navigateToVersions,
            navigateToExport
        ) {
            entryProvider {

                entry<NormalNavKey.LauncherMain> {
                    LauncherScreen(
                        backStackViewModel =
                            screenBackStackModel,

                        navigateToVersions =
                            navigateToVersions,

                        onLaunchGame = { version ->
                            eventViewModel.sendEvent(
                                EventViewModel.Event.Launch.Game(
                                    version
                                )
                            )
                        },

                        onOpenLink = {
                            eventViewModel.sendEvent(
                                EventViewModel.Event.OpenLink(it)
                            )
                        },

                        onHomePageEvent = { event ->
                            eventViewModel.sendEvent(
                                EventViewModel.Event.HomePage.Event(
                                    event
                                )
                            )
                        },

                        onNavigateToStats = {
                            backStack.navigateTo(
                                NormalNavKey.GameStats
                            )
                        },

                        onNavigateToPlayTimeStats = {
                            backStack.navigateTo(
                                NormalNavKey.PlayTimeStats
                            )
                        },

                        onNavigateToLog = { logPath ->
                            backStack.navigateTo(
                                NormalNavKey.LogView(logPath)
                            )
                        }
                    )
                }

                entry<NestedNavKey.Settings> { key ->
                    SettingsScreen(
                        key = key,
                        backStackViewModel =
                            screenBackStackModel,
                        openLicenseScreen = { raw ->
                            backStack.navigateTo(
                                NormalNavKey.License(raw)
                            )
                        },
                        eventViewModel = eventViewModel,
                        submitError = submitError
                    )
                }

                entry<NormalNavKey.License> { key ->
                    LicenseScreen(
                        key = key,
                        backStackViewModel =
                            screenBackStackModel
                    )
                }

                entry<NormalNavKey.AccountManager> { key ->
                    AccountManageScreen(
                        key = key,
                        backStackViewModel =
                            screenBackStackModel,
                        backToMainScreen =
                            toMainScreen,
                        openLink = { url ->
                            eventViewModel.sendEvent(
                                EventViewModel.Event.OpenLink(
                                    url
                                )
                            )
                        },
                        showToast = { text, duration ->
                            eventViewModel.sendToast(
                                text,
                                duration
                            )
                        },
                        submitError = submitError
                    )
                }

                entry<NormalNavKey.WebScreen> { key ->
                    WebViewScreen(
                        key = key,
                        backStackViewModel =
                            screenBackStackModel,
                        eventViewModel = eventViewModel
                    )
                }

                entry<NormalNavKey.VersionsManager> {
                    VersionsManageScreen(
                        backScreenViewModel =
                            screenBackStackModel,
                        navigateToVersions =
                            navigateToVersions,
                        navigateToExport =
                            navigateToExport,
                        eventViewModel =
                            eventViewModel,
                        submitError =
                            submitError
                    )
                }

                entry<NormalNavKey.FileSelector> { key ->
                    FileSelectorScreen(
                        key = key,
                        backScreenViewModel =
                            screenBackStackModel
                    ) {
                        backStack.removeLastOrNull()
                    }
                }

                entry<NestedNavKey.VersionSettings> { key ->
                    VersionSettingsScreen(
                        key = key,
                        backScreenViewModel =
                            screenBackStackModel,
                        backToMainScreen =
                            toMainScreen,
                        onExportModpack = {
                            navigateToExport(
                                key.version
                            )
                        },
                        eventViewModel =
                            eventViewModel,
                        submitError =
                            submitError
                    )
                }

                entry<NestedNavKey.VersionExport> { key ->
                    VersionExportScreen(
                        key = key,
                        backScreenViewModel =
                            screenBackStackModel,
                        eventViewModel =
                            eventViewModel,
                        backToMainScreen =
                            toMainScreen
                    )
                }

                entry<NestedNavKey.Download> { key ->
                    DownloadScreen(
                        key = key,
                        backScreenViewModel =
                            screenBackStackModel,
                        eventViewModel =
                            eventViewModel,
                        modpackImportViewModel =
                            modpackImportViewModel,
                        submitError =
                            submitError
                    )
                }

                entry<NestedNavKey.AssetInfo> { key ->
                    AssetInfoScreen(
                        key = key,
                        mainScreenKey =
                            screenBackStackModel.mainScreen.currentKey,
                        assetInfoScreenKey =
                            key.currentKey,
                        eventViewModel =
                            eventViewModel,
                        submitError =
                            submitError
                    )
                }

                entry<NormalNavKey.Multiplayer> {
                    MultiplayerScreen(
                        backScreenViewModel =
                            screenBackStackModel,
                        eventViewModel =
                            eventViewModel
                    )
                }

                entry<NormalNavKey.BuiltInFileManager> { key ->
                    BuiltInFileManagerScreen(
                        key = key,
                        backStackViewModel =
                            screenBackStackModel,
                        submitError =
                            submitError,
                        navigateToEditor = { path ->
                            backStack.navigateTo(
                                NormalNavKey.FileEditor(
                                    filePath = path
                                )
                            )
                        }
                    )
                }

                entry<NormalNavKey.FileEditor> { key ->
                    FileEditorScreen(
                        key = key,
                        backStackViewModel =
                            screenBackStackModel,
                        submitError =
                            submitError
                    )
                }

                entry<NormalNavKey.HomePageEditor> {
                    HomePageEditorScreen(
                        backStackViewModel =
                            screenBackStackModel
                    )
                }

                entry<NormalNavKey.LogView> { key ->
                    LogViewScreen(
                        key = key,
                        backStackViewModel =
                            screenBackStackModel
                    )
                }

                entry<NormalNavKey.GameStats> {
                    GameStatsScreen(
                        backStackViewModel =
                            screenBackStackModel
                    )
                }

                entry<NormalNavKey.PlayTimeStats> {
                    PlayTimeStatsScreen(
                        backStackViewModel =
                            screenBackStackModel
                    )
                }

                entry<NormalNavKey.CapeGallery> { key ->
                    CapeGalleryScreen(
                        key = key,
                        backStackViewModel =
                            screenBackStackModel
                    )
                }

                entry<NormalNavKey.Recordings> {
                    RecordingsScreen(
                        backStackViewModel =
                            screenBackStackModel
                    )
                }
            }
        }

        NavDisplay(
            backStack = backStack,
            modifier = modifier,
            onBack = {
                onBack(backStack)
            },
            transitionSpec = rememberTransitionSpec(),
            popTransitionSpec = rememberTransitionSpec(),
            entryProvider = provider
        )
    } else {
        Box(
            modifier = modifier
        )
    }
}

@Composable
private fun TaskMenu(
    tasks: List<Task>,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    changeExpandedState: () -> Unit = {}
) {
    val show =
        isExpanded && tasks.isNotEmpty()

    val isRtl =
        LocalLayoutDirection.current == LayoutDirection.Rtl

    AnimatedVisibility(
        modifier = modifier,
        enter = slideInHorizontally(
            initialOffsetX = {
                if (isRtl) it else -it
            },
            animationSpec = getAnimateTween()
        ) + fadeIn(),
        exit = slideOutHorizontally(
            targetOffsetX = {
                if (isRtl) it else -it
            },
            animationSpec = getAnimateTween()
        ) + fadeOut(),
        visible = show
    ) {
        BackgroundCard(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            influencedByBackground = false,
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor(),
                contentColor = onBackgroundColor()
            ),
            elevation =
                CardDefaults.elevatedCardElevation(
                    defaultElevation = 6.dp
                )
        ) {
            Column {

                CardTitleLayout(blur = 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(
                                top = 8.dp,
                                bottom = 4.dp
                            )
                    ) {
                        IconButton(
                            modifier = Modifier
                                .size(28.dp)
                                .align(
                                    Alignment.CenterStart
                                ),
                            onClick =
                                changeExpandedState
                        ) {
                            Icon(
                                modifier =
                                    Modifier.size(28.dp),
                                painter =
                                    painterResource(
                                        R.drawable.ic_arrow_left_rounded
                                    ),
                                contentDescription =
                                    stringResource(
                                        R.string.generic_collapse
                                    )
                            )
                        }

                        androidx.compose.material3.Text(
                            modifier =
                                Modifier.align(
                                    Alignment.Center
                                ),
                            text =
                                stringResource(
                                    R.string.main_task_menu
                                )
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    contentPadding =
                        PaddingValues(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        )
                ) {
                    items(
                        items = tasks,
                        key = { it.id },
                        contentType = { "task" }
                    ) { task ->

                        val taskProgress by
                            task.progress
                                .collectAsStateWithLifecycle()

                        val taskMessage by
                            task.message
                                .collectAsStateWithLifecycle()

                        val rateBytesPerSec by
                            task.rateBytesPerSec
                                .collectAsStateWithLifecycle()

                        TaskItem(
                            taskProgress =
                                taskProgress,
                            taskMessage =
                                taskMessage,
                            rateBytesPerSec =
                                rateBytesPerSec,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 6.dp
                                )
                        ) {
                            TaskSystem.cancelTask(
                                task.id
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskItem(
    taskProgress: Float,
    taskMessage: AndroidStringText?,
    rateBytesPerSec: Long?,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    color: Color = cardColor(false),
    contentColor: Color = onCardColor(),
    onCancelClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = color,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            IconButton(
                modifier = Modifier
                    .size(24.dp)
                    .align(
                        Alignment.CenterVertically
                    ),
                onClick = onCancelClick
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter =
                        painterResource(
                            R.drawable.ic_close
                        ),
                    contentDescription =
                        stringResource(
                            R.string.generic_cancel
                        )
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(
                        Alignment.CenterVertically
                    )
            ) {

                taskMessage?.let { message ->
                    AndroidStringText(
                        text = message,
                        style =
                            MaterialTheme.typography.labelMedium
                    )
                }

                if (taskProgress < 0) {
                    LinearProgressIndicator(
                        modifier =
                            Modifier.fillMaxWidth()
                    )
                } else {
                    LinearProgressIndicator(
                        progress = {
                            taskProgress
                        },
                        modifier =
                            Modifier.fillMaxWidth()
                    )
                }

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    taskProgress
                        .takeIf {
                            it >= 0f
                        }
                        ?.let { progress ->

                            androidx.compose.material3.Text(
                                text =
                                    "${(progress * 100).toInt()}%",
                                style =
                                    MaterialTheme.typography.labelMedium
                            )
                        }

                    rateBytesPerSec?.let { bytes ->

                        val text =
                            remember(bytes) {
                                "${formatFileSize(bytes)}/s"
                            }

                        androidx.compose.material3.Text(
                            text = text,
                            style =
                                MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}
