/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.movtery.zalithlauncher.ui.screens.content.download

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Dialog
import androidx.compose.material3.DialogProperties
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.google.gson.JsonSyntaxException
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.coroutine.InstallerRestoreRegistry
import com.movtery.zalithlauncher.coroutine.TaskSystem
import com.movtery.zalithlauncher.game.download.game.GameDownloadInfo
import com.movtery.zalithlauncher.game.download.game.GameInstaller
import com.movtery.zalithlauncher.game.download.game.optifine.CantFetchingOptiFineUrlException
import com.movtery.zalithlauncher.game.download.jvm_server.JvmCrashException
import com.movtery.zalithlauncher.game.version.download.DownloadFailedException
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.notification.NotificationManager
import com.movtery.zalithlauncher.ui.components.fadeEdge
import com.movtery.zalithlauncher.ui.components.verticalScrollWithBar
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.TitledNavKey
import com.movtery.zalithlauncher.ui.screens.content.download.game.DownloadGameWithAddonScreen
import com.movtery.zalithlauncher.ui.screens.content.download.game.SelectGameVersionScreen
import com.movtery.zalithlauncher.ui.screens.content.elements.TitleTaskFlowDialog
import com.movtery.zalithlauncher.ui.screens.navigateTo
import com.movtery.zalithlauncher.ui.screens.onBack
import com.movtery.zalithlauncher.ui.screens.rememberTransitionSpec
import com.movtery.zalithlauncher.utils.logging.Logger
import com.movtery.zalithlauncher.utils.network.isUsingMobileData
import com.movtery.zalithlauncher.viewmodel.EventViewModel
import com.movtery.zalithlauncher.viewmodel.sendKeepScreen
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.serialization.SerializationException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException
import java.util.concurrent.TimeoutException

/* ============================================================
 * ALFAA NEON PALETTE
 * ============================================================ */

private val AlfaaBlack = Color(0xFF05080D)
private val AlfaaSurface = Color(0xFF0B1118)
private val AlfaaSurface2 = Color(0xFF101923)
private val AlfaaCyan = Color(0xFF43C7FF)
private val AlfaaGreen = Color(0xFF20E0B2)
private val AlfaaPurple = Color(0xFFA78BFA)
private val AlfaaWarning = Color(0xFFFFB454)
private val AlfaaError = Color(0xFFFF5577)
private val AlfaaText = Color(0xFFE8FFF8)
private val AlfaaMuted = Color(0xFF829A98)
private val AlfaaBorder = Color(0xFF16483F)

/* ============================================================
 * INSTALL OPERATION
 * ============================================================ */

private sealed interface GameInstallOperation {
    data object None : GameInstallOperation

    data object Install : GameInstallOperation

    data class WarningForNotification(
        val info: GameDownloadInfo
    ) : GameInstallOperation

    data class WarningForMobileData(
        val info: GameDownloadInfo
    ) : GameInstallOperation

    data class Error(
        val th: Throwable
    ) : GameInstallOperation

    data object Success : GameInstallOperation
}

/* ============================================================
 * VIEW MODEL
 * ============================================================ */

private class GameDownloadViewModel : ViewModel() {

    var versionNameErrorCheck by mutableStateOf(false)

    var installOperation by mutableStateOf<GameInstallOperation>(
        GameInstallOperation.None
    )

    var installer by mutableStateOf<GameInstaller?>(null)

    private fun refreshVersionNameCheck() {
        versionNameErrorCheck = !versionNameErrorCheck
    }

    fun install(
        context: Context,
        info: GameDownloadInfo,
        onStart: () -> Unit = {},
        onStop: () -> Unit = {},
    ) {
        installOperation = GameInstallOperation.Install

        installer = GameInstaller(
            context,
            info,
            viewModelScope
        ).also {
            it.installGame(
                onInstalled = { version ->
                    installer = null

                    VersionsManager.refresh(
                        "[DownloadGame] GameInstaller.onInstalled",
                        version
                    )

                    installOperation =
                        GameInstallOperation.Success

                    refreshVersionNameCheck()
                    onStop()
                },

                onError = { th ->
                    installer = null

                    installOperation =
                        GameInstallOperation.Error(th)

                    refreshVersionNameCheck()
                    onStop()
                },

                onGameAlreadyInstalled = {
                    installOperation =
                        GameInstallOperation.None

                    refreshVersionNameCheck()
                    onStop()
                }
            )
        }

        onStart()
    }

    fun cancel() {
        installer?.cancelInstall()
        installer = null
        installOperation = GameInstallOperation.None
        refreshVersionNameCheck()
    }

    override fun onCleared() {
        cancel()
    }
}

@Composable
private fun rememberGameDownloadViewModel(
    key: NestedNavKey.DownloadGame
): GameDownloadViewModel {
    return viewModel(
        key = key.toString()
    ) {
        GameDownloadViewModel()
    }
}

/* ============================================================
 * MAIN DOWNLOAD SCREEN
 * ============================================================ */

@Composable
fun DownloadGameScreen(
    key: NestedNavKey.DownloadGame,
    mainScreenKey: TitledNavKey?,
    downloadScreenKey: TitledNavKey?,
    downloadGameScreenKey: TitledNavKey?,
    onCurrentKeyChange: (TitledNavKey?) -> Unit,
    eventViewModel: EventViewModel
) {
    val viewModel: GameDownloadViewModel =
        rememberGameDownloadViewModel(key)

    val context = LocalContext.current
    val backStack = key.backStack
    val stackTopKey = backStack.lastOrNull()

    LaunchedEffect(stackTopKey) {
        onCurrentKeyChange(stackTopKey)
    }

    GameInstallOperation(
        gameInstallOperation = viewModel.installOperation,

        updateOperation = {
            viewModel.installOperation = it
        },

        installer = viewModel.installer,

        onInstall = { info ->
            viewModel.install(
                context = context,
                info = info,
                onStart = {
                    eventViewModel.sendKeepScreen(true)
                },
                onStop = {
                    eventViewModel.sendKeepScreen(false)
                }
            )
        },

        onCancel = {
            viewModel.cancel()
            eventViewModel.sendKeepScreen(false)
        }
    )

    if (backStack.isNotEmpty()) {

        NavDisplay(
            backStack = backStack,

            modifier = Modifier
                .fillMaxSize()
                .background(AlfaaBlack),

            onBack = {
                onBack(backStack)
            },

            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),

            transitionSpec = rememberTransitionSpec(),
            popTransitionSpec = rememberTransitionSpec(),

            entryProvider = entryProvider {

                entry<NormalNavKey.DownloadGame.SelectGameVersion> {

                    SelectGameVersionScreen(
                        mainScreenKey = mainScreenKey,
                        downloadScreenKey = downloadScreenKey,
                        downloadGameScreenKey = downloadGameScreenKey,
                        eventViewModel = eventViewModel,

                        onVersionSelect = { versionString ->
                            backStack.navigateTo(
                                NormalNavKey.DownloadGame.Addons(
                                    versionString
                                )
                            )
                        }
                    )
                }

                entry<NormalNavKey.DownloadGame.Addons> { key ->

                    val context = LocalContext.current

                    DownloadGameWithAddonScreen(
                        mainScreenKey = mainScreenKey,
                        downloadScreenKey = downloadScreenKey,
                        downloadGameScreenKey = downloadGameScreenKey,
                        key = key,
                        refreshErrorCheck =
                            viewModel.versionNameErrorCheck
                    ) { info ->

                        if (
                            viewModel.installOperation !is
                            GameInstallOperation.None
                        ) {
                            return@DownloadGameWithAddonScreen
                        }

                        if (
                            !NotificationManager
                                .checkNotificationEnabled(context)
                        ) {
                            viewModel.installOperation =
                                GameInstallOperation
                                    .WarningForNotification(info)

                        } else if (
                            isUsingMobileData(context)
                        ) {
                            viewModel.installOperation =
                                GameInstallOperation
                                    .WarningForMobileData(info)

                        } else {

                            viewModel.install(
                                context = context,
                                info = info,
                                onStart = {
                                    eventViewModel
                                        .sendKeepScreen(true)
                                },
                                onStop = {
                                    eventViewModel
                                        .sendKeepScreen(false)
                                }
                            )
                        }
                    }
                }
            }
        )

    } else {

        Box(
            Modifier
                .fillMaxSize()
                .background(AlfaaBlack)
        )
    }
}

/* ============================================================
 * NEON INSTALL OPERATION UI
 * ============================================================ */

@Composable
private fun GameInstallOperation(
    gameInstallOperation: GameInstallOperation,
    updateOperation: (GameInstallOperation) -> Unit = {},
    installer: GameInstaller?,
    onInstall: (GameDownloadInfo) -> Unit,
    onCancel: () -> Unit
) {

    when (gameInstallOperation) {

        is GameInstallOperation.None -> Unit

        /* ----------------------------------------------------
         * NOTIFICATION WARNING
         * ---------------------------------------------------- */

        is GameInstallOperation.WarningForNotification -> {

            AlfaaWarningDialog(
                title = "NOTIFICATION REQUIRED",
                message = stringResource(
                    R.string.notification_data_jvm_service_message
                ),
                confirmText = "CONTINUE",
                dismissText = "CANCEL",

                onConfirm = {
                    onInstall(
                        gameInstallOperation.info
                    )
                },

                onDismiss = {
                    updateOperation(
                        GameInstallOperation.None
                    )
                }
            )
        }

        /* ----------------------------------------------------
         * MOBILE DATA WARNING
         * ---------------------------------------------------- */

        is GameInstallOperation.WarningForMobileData -> {

            AlfaaWarningDialog(
                title = stringResource(
                    R.string.generic_warning
                ),
                message = stringResource(
                    R.string.download_install_warning_mobile_data
                ),
                confirmText = stringResource(
                    R.string.generic_anyway
                ),
                dismissText = "CANCEL",

                onConfirm = {
                    onInstall(
                        gameInstallOperation.info
                    )
                },

                onDismiss = {
                    updateOperation(
                        GameInstallOperation.None
                    )
                }
            )
        }

        /* ----------------------------------------------------
         * INSTALLING
         * ---------------------------------------------------- */

        is GameInstallOperation.Install -> {

            if (installer != null) {

                val installGame =
                    installer.tasksFlow
                        .collectAsStateWithLifecycle()

                if (installGame.value.isNotEmpty()) {

                    val dialogTitle =
                        stringResource(
                            R.string.download_game_install_title
                        )

                    TitleTaskFlowDialog(
                        title = dialogTitle,
                        tasks = installGame.value,

                        onCancel = {
                            onCancel()

                            updateOperation(
                                GameInstallOperation.None
                            )
                        },

                        onMinimize = {

                            InstallerRestoreRegistry
                                .collapseTaskMenu()

                            val bgTask =
                                installer.createBackgroundTask(
                                    onCancelRequest = {
                                        onCancel()
                                    }
                                )

                            InstallerRestoreRegistry.register(
                                bgTask.id,

                                InstallerRestoreRegistry
                                    .RestorableInstaller(
                                        title = dialogTitle,
                                        tasksFlow =
                                            installer.tasksFlow,

                                        onCancel = {
                                            onCancel()

                                            updateOperation(
                                                GameInstallOperation.None
                                            )
                                        }
                                    )
                            )

                            TaskSystem.submitTask(
                                bgTask,
                                onEnded = {
                                    InstallerRestoreRegistry
                                        .unregister(bgTask.id)
                                }
                            )

                            updateOperation(
                                GameInstallOperation.None
                            )
                        }
                    )
                }
            }
        }

        /* ----------------------------------------------------
         * ERROR
         * ---------------------------------------------------- */

        is GameInstallOperation.Error -> {

            val th = gameInstallOperation.th

            Logger.error(
                "InstallGame",
                "Failed to download the game!",
                th
            )

            val message = when (th) {

                is HttpRequestTimeoutException,
                is SocketTimeoutException,
                is TimeoutException ->
                    stringResource(R.string.error_timeout)

                is UnknownHostException,
                is UnresolvedAddressException ->
                    stringResource(
                        R.string.error_network_unreachable
                    )

                is ConnectException ->
                    stringResource(
                        R.string.error_connection_failed
                    )

                is SerializationException,
                is JsonSyntaxException ->
                    stringResource(
                        R.string.error_parse_failed
                    )

                is CantFetchingOptiFineUrlException ->
                    stringResource(
                        R.string.download_install_error_cant_fetch_optifine_download_url
                    )

                is JvmCrashException ->
                    stringResource(
                        R.string.download_install_error_jvm_crash,
                        th.code
                    )

                is DownloadFailedException ->
                    stringResource(
                        R.string.download_install_error_download_failed
                    )

                else ->
                    th.localizedMessage
                        ?: th.message
                        ?: th::class.qualifiedName
                        ?: "Unknown error"
            }

            AlfaaErrorDialog(
                title = stringResource(
                    R.string.download_install_error_title
                ),

                messageTitle = stringResource(
                    R.string.download_install_error_message
                ),

                message = message,

                onDismiss = {
                    updateOperation(
                        GameInstallOperation.None
                    )
                }
            )
        }

        /* ----------------------------------------------------
         * SUCCESS
         * ---------------------------------------------------- */

        is GameInstallOperation.Success -> {

            AlfaaSuccessDialog(
                title = stringResource(
                    R.string.download_install_success_title
                ),

                message = stringResource(
                    R.string.download_install_success_message
                ),

                onDismiss = {
                    updateOperation(
                        GameInstallOperation.None
                    )
                }
            )
        }
    }
}

/* ============================================================
 * BASE NEON DIALOG
 * ============================================================ */

@Composable
private fun AlfaaDialogContainer(
    accent: Color,
    title: String,
    message: String,
    confirmText: String,
    dismissText: String? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    iconText: String
) {

    Dialog(
        onDismissRequest = onDismiss,

        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(22.dp),
                    ambientColor = accent.copy(alpha = 0.28f),
                    spotColor = accent.copy(alpha = 0.32f)
                )
                .clip(
                    RoundedCornerShape(22.dp)
                )
                .background(
                    Brush.verticalGradient(
                        listOf(
                            AlfaaSurface2,
                            AlfaaSurface,
                            AlfaaBlack
                        )
                    )
                )
                .border(
                    BorderStroke(
                        1.dp,
                        accent.copy(alpha = 0.7f)
                    ),
                    RoundedCornerShape(22.dp)
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(
                                RoundedCornerShape(14.dp)
                            )
                            .background(
                                accent.copy(alpha = 0.10f)
                            )
                            .border(
                                1.dp,
                                accent.copy(alpha = 0.55f),
                                RoundedCornerShape(14.dp)
                            ),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text = iconText,
                            color = accent,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(
                        Modifier.width(14.dp)
                    )

                    Text(
                        text = title.uppercase(),
                        color = AlfaaText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp
                    )
                }

                Spacer(
                    Modifier.height(18.dp)
                )

                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    accent.copy(alpha = 0.8f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Spacer(
                    Modifier.height(16.dp)
                )

                Text(
                    text = message,
                    color = AlfaaMuted,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )

                Spacer(
                    Modifier.height(22.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.End,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    if (dismissText != null) {

                        TextButton(
                            onClick = onDismiss
                        ) {

                            Text(
                                text = dismissText.uppercase(),
                                color = AlfaaMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.7.sp
                            )
                        }

                        Spacer(
                            Modifier.width(8.dp)
                        )
                    }

                    Button(
                        onClick = onConfirm,

                        colors = ButtonDefaults.buttonColors(
                            containerColor = accent,
                            contentColor = AlfaaBlack
                        ),

                        shape = RoundedCornerShape(12.dp)
                    ) {

                        Text(
                            text = confirmText.uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.6.sp
                        )
                    }
                }
            }
        }
    }
}

/* ============================================================
 * WARNING
 * ============================================================ */

@Composable
private fun AlfaaWarningDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {

    AlfaaDialogContainer(
        accent = AlfaaWarning,
        title = title,
        message = message,
        confirmText = confirmText,
        dismissText = dismissText,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        iconText = "!"
    )
}

/* ============================================================
 * ERROR
 * ============================================================ */

@Composable
private fun AlfaaErrorDialog(
    title: String,
    messageTitle: String,
    message: String,
    onDismiss: () -> Unit
) {

    Dialog(
        onDismissRequest = onDismiss
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    24.dp,
                    RoundedCornerShape(22.dp),
                    ambientColor =
                        AlfaaError.copy(alpha = 0.25f),
                    spotColor =
                        AlfaaError.copy(alpha = 0.3f)
                )
                .clip(
                    RoundedCornerShape(22.dp)
                )
                .background(
                    Brush.verticalGradient(
                        listOf(
                            AlfaaSurface2,
                            AlfaaSurface,
                            AlfaaBlack
                        )
                    )
                )
                .border(
                    1.dp,
                    AlfaaError.copy(alpha = 0.7f),
                    RoundedCornerShape(22.dp)
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(
                                RoundedCornerShape(14.dp)
                            )
                            .background(
                                AlfaaError.copy(alpha = 0.10f)
                            )
                            .border(
                                1.dp,
                                AlfaaError.copy(alpha = 0.55f),
                                RoundedCornerShape(14.dp)
                            ),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text = "×",
                            color = AlfaaError,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(
                        Modifier.width(14.dp)
                    )

                    Text(
                        text = title.uppercase(),
                        color = AlfaaText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(
                    Modifier.height(16.dp)
                )

                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            AlfaaError.copy(alpha = 0.45f)
                        )
                )

                Spacer(
                    Modifier.height(15.dp)
                )

                val scrollState =
                    rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = messageTitle,
                        color = AlfaaText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = message,
                        color = AlfaaMuted,
                        fontSize = 12.sp,
                        lineHeight = 19.sp
                    )
                }

                Spacer(
                    Modifier.height(20.dp)
                )

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),

                    onClick = onDismiss,

                    colors = ButtonDefaults.buttonColors(
                        containerColor = AlfaaError,
                        contentColor = AlfaaBlack
                    ),

                    shape = RoundedCornerShape(12.dp)
                ) {

                    Text(
                        text = stringResource(
                            R.string.generic_confirm
                        ).uppercase(),
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.7.sp
                    )
                }
            }
        }
    }
}

/* ============================================================
 * SUCCESS
 * ============================================================ */

@Composable
private fun AlfaaSuccessDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit
) {

    AlfaaDialogContainer(
        accent = AlfaaGreen,
        title = title,
        message = message,
        confirmText = stringResource(
            R.string.generic_confirm
        ),
        dismissText = null,
        onConfirm = onDismiss,
        onDismiss = onDismiss,
        iconText = "✓"
    )
}
