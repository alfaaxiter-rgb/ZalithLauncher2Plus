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
 * along with this program. If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.ui.screens.content

import android.content.Context
import android.os.Environment

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoveDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel

import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.path.GamePathManager
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.game.version.installed.VersionComparator
import com.movtery.zalithlauncher.game.version.installed.VersionMover
import com.movtery.zalithlauncher.game.version.installed.VersionType
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.game.version.installed.cleanup.GameAssetCleaner
import com.movtery.zalithlauncher.ui.activities.MainActivity
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.content.elements.CleanupOperation
import com.movtery.zalithlauncher.ui.screens.content.elements.GameFolderOperation
import com.movtery.zalithlauncher.ui.screens.content.elements.GameFolderOperationDialog
import com.movtery.zalithlauncher.ui.screens.content.elements.GamePathItemLayout
import com.movtery.zalithlauncher.ui.screens.content.elements.GamePathOperation
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionCategory
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionCategoryItem
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionItemCallbacks
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionItemLayout
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionsOperation
import com.movtery.zalithlauncher.utils.ShortcutUtils
import com.movtery.zalithlauncher.utils.animation.swapAnimateDpAsState
import com.movtery.zalithlauncher.utils.canHandlePermission
import com.movtery.zalithlauncher.utils.checkStoragePermissions
import com.movtery.zalithlauncher.viewmodel.ErrorViewModel
import com.movtery.zalithlauncher.viewmodel.EventViewModel
import com.movtery.zalithlauncher.viewmodel.ScreenBackStackViewModel
import com.movtery.zalithlauncher.viewmodel.sendKeepScreen

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private val AlfaaVMBlack = Color(0xFF050608)
private val AlfaaVMPanel = Color(0xFF0A0D11)
private val AlfaaVMPanel2 = Color(0xFF10141A)
private val AlfaaVMBorder = Color(0xFF1D252E)
private val AlfaaVMCyan = Color(0xFF00E5FF)
private val AlfaaVMGreen = Color(0xFF00FF9C)
private val AlfaaVMPurple = Color(0xFF9B6CFF)
private val AlfaaVMText = Color(0xFFF3F7FA)
private val AlfaaVMMuted = Color(0xFF8B96A3)

private class VersionsScreenViewModel : ViewModel() {

    var versionCategory by mutableStateOf(VersionCategory.ALL)
        private set

    var resortKey by mutableIntStateOf(0)
        private set

    var gamePathOperation by mutableStateOf<GamePathOperation>(
        GamePathOperation.None
    )

    var allVersionsCount by mutableIntStateOf(0)
    var vanillaVersionsCount by mutableIntStateOf(0)
    var modloaderVersionsCount by mutableIntStateOf(0)

    var cleanupOperation by mutableStateOf<CleanupOperation>(
        CleanupOperation.None
    )

    var cleaner by mutableStateOf<GameAssetCleaner?>(null)

    var gameFolderOperation by mutableStateOf<GameFolderOperation>(
        GameFolderOperation.None
    )

    var mover by mutableStateOf<VersionMover?>(null)

    private var currentJob: Job? = null
    private val mutex = Mutex()

    fun startRefreshVersions() {
        if (!VersionsManager.isRefreshing.value) {
            VersionsManager.refresh(
                "VersionsScreenViewModel.startRefreshVersions"
            )
        }
    }

    fun changeCategory(category: VersionCategory) {
        currentJob?.cancel()

        currentJob = viewModelScope.launch {
            mutex.withLock {
                versionCategory = category
            }
        }
    }

    fun resortVersions() {
        resortKey++
    }

    fun startMoveVersions(
        context: Context,
        versions: List<Version>,
        targetPath: String,
        onStart: () -> Unit = {},
        onStop: () -> Unit = {},
        onComplete: ((List<String>, List<Pair<String, String>>) -> Unit)? = null
    ) {
        mover = VersionMover(
            context = context,
            scope = viewModelScope,
            versions = versions,
            sourceGameHome = GamePathManager.currentPath.value,
            targetGamePath = targetPath,
            changeState = {
                gameFolderOperation = it
            }
        ).also {
            gameFolderOperation =
                GameFolderOperation.MoveVersionsProgress(it)

            it.start(
                onEnd = { moved, failed ->
                    mover = null

                    gameFolderOperation =
                        GameFolderOperation.MoveVersionsResult(
                            moved,
                            failed
                        )

                    onComplete?.invoke(moved, failed)
                    onStop()
                },
                onThrowable = {
                    mover = null
                    gameFolderOperation =
                        GameFolderOperation.None
                    onStop()
                }
            )
        }

        onStart()
    }

    fun cancelMove() {
        mover?.cancel()
        mover = null
        gameFolderOperation = GameFolderOperation.None
    }

    fun cleanUnusedFiles(
        onStart: () -> Unit = {},
        onStop: () -> Unit = {}
    ) {
        cleaner = GameAssetCleaner(
            scope = viewModelScope
        ).also {
            cleanupOperation = CleanupOperation.Clean

            it.start(
                onEnd = { count, size ->
                    cleaner = null
                    cleanupOperation =
                        CleanupOperation.Success(count, size)
                    onStop()
                },
                onThrowable = { th ->
                    cleaner = null
                    cleanupOperation =
                        CleanupOperation.Error(th)
                    onStop()
                }
            )
        }

        onStart()
    }

    fun cancelCleaner() {
        cleaner?.cancel()
        cleaner = null
        cleanupOperation = CleanupOperation.None
    }

    override fun onCleared() {
        cancelCleaner()
        cancelMove()
        currentJob?.cancel()
    }
}

@Composable
private fun rememberVersionViewModel(): VersionsScreenViewModel {
    return viewModel(
        key = NormalNavKey.VersionsManager.toString()
    ) {
        VersionsScreenViewModel()
    }
}

@Composable
private fun rememberVersions(
    versions: StateFlow<List<Version>>,
    viewModel: VersionsScreenViewModel
): State<List<Version>> {

    val vers by versions.collectAsStateWithLifecycle()
    val category = viewModel.versionCategory
    val resortKey = viewModel.resortKey

    return remember(
        vers,
        category,
        resortKey
    ) {
        derivedStateOf {

            viewModel.allVersionsCount = vers.size

            val vanillaVersions =
                vers.filter {
                    it.versionType == VersionType.VANILLA
                }.also {
                    viewModel.vanillaVersionsCount = it.size
                }

            val modloaderVersions =
                vers.filter {
                    it.versionType == VersionType.MODLOADERS
                }.also {
                    viewModel.modloaderVersionsCount = it.size
                }

            when (category) {

                VersionCategory.ALL ->
                    vers

                VersionCategory.VANILLA ->
                    vanillaVersions

                VersionCategory.MODLOADER ->
                    modloaderVersions
            }.sortedWith(VersionComparator)
        }
    }
}

@Composable
fun VersionsManageScreen(
    backScreenViewModel: ScreenBackStackViewModel,
    navigateToVersions: (Version) -> Unit,
    navigateToExport: (Version) -> Unit,
    eventViewModel: EventViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {

    val viewModel = rememberVersionViewModel()
    val context = LocalContext.current

    val versions by rememberVersions(
        VersionsManager.versions,
        viewModel
    )

    val currentVersion by
        VersionsManager.currentVersion.collectAsStateWithLifecycle()

    val isRefreshing by
        VersionsManager.isRefreshing.collectAsStateWithLifecycle()

    GamePathOperation(
        gamePathOperation = viewModel.gamePathOperation,
        changeState = {
            viewModel.gamePathOperation = it
        },
        submitError = submitError
    )

    GameFolderOperationDialog(
        operation = viewModel.gameFolderOperation,
        changeState = {
            viewModel.gameFolderOperation = it
        },
        versions = versions,
        onStartMove = { selectedVersions, targetPath ->

            viewModel.startMoveVersions(
                context = context,
                versions = selectedVersions,
                targetPath = targetPath,
                onStart = {
                    eventViewModel.sendKeepScreen(true)
                },
                onStop = {
                    eventViewModel.sendKeepScreen(false)
                }
            )
        }
    )

    BaseScreen(
        screenKey = NormalNavKey.VersionsManager,
        currentKey = backScreenViewModel.mainScreen.currentKey
    ) { isVisible ->

        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(AlfaaVMBlack)
        ) {

            AlfaaVersionsSidebar(
                isVisible = isVisible,
                isRefreshing = isRefreshing,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(220.dp),
                onAddPath = {

                    (context as? MainActivity)?.let { activity ->

                        checkStoragePermissions(
                            activity = activity,
                            message = activity.getString(
                                R.string.versions_manage_game_path_storage_permissions
                            ),
                            messageSdk30 = activity.getString(
                                R.string.versions_manage_game_path_storage_permissions_sdk30
                            ),
                            hasPermission = {

                                backScreenViewModel
                                    .mainScreen
                                    .backStack
                                    .navigateToFileSelector(
                                        startPath = Environment
                                            .getExternalStorageDirectory()
                                            .absolutePath,
                                        selectFile = false,
                                        saveKey = NormalNavKey.VersionsManager
                                    ) { path ->

                                        viewModel.gamePathOperation =
                                            GamePathOperation.AddNewPath(
                                                path
                                            )
                                    }
                            }
                        )
                    }
                },
                onMoveVersions = {
                    viewModel.gameFolderOperation =
                        GameFolderOperation.MoveVersionsSelect
                },
                onCleanup = {

                    if (
                        viewModel.cleanupOperation ==
                        CleanupOperation.None
                    ) {
                        viewModel.cleanupOperation =
                            CleanupOperation.Tip
                    }
                },
                changePathOperation = {
                    viewModel.gamePathOperation = it
                }
            )

            AlfaaVersionsContent(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(
                        top = 12.dp,
                        bottom = 12.dp,
                        end = 12.dp
                    ),
                isRefreshing = isRefreshing,
                versions = versions,
                currentVersion = currentVersion,
                versionCategory = viewModel.versionCategory,
                allVersionsCount = viewModel.allVersionsCount,
                vanillaVersionsCount =
                    viewModel.vanillaVersionsCount,
                modloaderVersionsCount =
                    viewModel.modloaderVersionsCount,
                onCategoryChange = {
                    viewModel.changeCategory(it)
                },
                onRefresh = {
                    viewModel.startRefreshVersions()
                },
                onInstall = {
                    backScreenViewModel.navigateToDownload()
                },
                navigateToVersions = navigateToVersions,
                navigateToExport = navigateToExport,
                submitError = submitError,
                onVersionPinned = {
                    viewModel.resortVersions()
                }
            )

            CleanupOperation(
                operation = viewModel.cleanupOperation,
                changeOperation = {
                    viewModel.cleanupOperation = it
                },
                cleaner = viewModel.cleaner,
                onClean = {
                    viewModel.cleanUnusedFiles(
                        onStart = {
                            eventViewModel.sendKeepScreen(true)
                        },
                        onStop = {
                            eventViewModel.sendKeepScreen(false)
                        }
                    )
                },
                onCancel = {
                    viewModel.cancelCleaner()
                    eventViewModel.sendKeepScreen(false)
                },
                submitError = submitError
            )
        }
    }
}

/*
 * ============================================================
 * ALFAA SIDEBAR
 * ============================================================
 */

@Composable
private fun AlfaaVersionsSidebar(
    isVisible: Boolean,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    onAddPath: () -> Unit,
    onMoveVersions: () -> Unit,
    onCleanup: () -> Unit,
    changePathOperation: (GamePathOperation) -> Unit
) {

    val context = LocalContext.current

    val paths by
        GamePathManager.gamePathData.collectAsStateWithLifecycle()

    val currentPath by
        GamePathManager.currentPath.collectAsStateWithLifecycle()

    val xOffset by swapAnimateDpAsState(
        targetValue = (-35).dp,
        swapIn = isVisible,
        isHorizontal = true
    )

    Column(
        modifier = modifier
            .offset {
                IntOffset(
                    x = xOffset.roundToPx(),
                    y = 0
                )
            }
            .padding(
                start = 12.dp,
                top = 12.dp,
                bottom = 12.dp
            )
    ) {

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = AlfaaVMPanel,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                AlfaaVMBorder
            )
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "ALFAA",
                    color = AlfaaVMCyan,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    letterSpacing = 2.sp
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "VERSIONS",
                    color = AlfaaVMText,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )

                Text(
                    text = "MINECRAFT INSTALLATIONS",
                    color = AlfaaVMMuted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 7.sp,
                    letterSpacing = 0.8.sp
                )
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(20.dp),
            color = AlfaaVMPanel,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                AlfaaVMBorder
            )
        ) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement =
                    Arrangement.spacedBy(6.dp)
            ) {

                items(
                    paths,
                    key = { it.id }
                ) { pathItem ->

                    GamePathItemLayout(
                        item = pathItem,
                        selected =
                            currentPath == pathItem.path,
                        enabled = canHandlePermission,
                        onClick = {

                            if (!isRefreshing) {

                                if (
                                    pathItem.id ==
                                    GamePathManager.DEFAULT_ID
                                ) {

                                    GamePathManager
                                        .saveDefaultPath()

                                } else {

                                    (context as? MainActivity)
                                        ?.let { activity ->

                                            checkStoragePermissions(
                                                activity = activity,
                                                message =
                                                    activity.getString(
                                                        R.string.versions_manage_game_storage_permissions
                                                    ),
                                                messageSdk30 =
                                                    activity.getString(
                                                        R.string.versions_manage_game_storage_permissions_sdk30
                                                    ),
                                                hasPermission = {

                                                    GamePathManager
                                                        .saveCurrentPath(
                                                            pathItem.id
                                                        )
                                                }
                                            )
                                        }
                                }
                            }
                        },
                        onDelete = {

                            changePathOperation(
                                GamePathOperation.DeletePath(
                                    pathItem
                                )
                            )
                        },
                        onRename = {

                            changePathOperation(
                                GamePathOperation.RenamePath(
                                    pathItem
                                )
                            )
                        }
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        AlfaaSideButton(
            icon = Icons.Default.Add,
            text = stringResource(
                R.string.versions_manage_game_path_add_new
            ),
            accent = AlfaaVMCyan,
            enabled = canHandlePermission,
            onClick = onAddPath
        )

        AlfaaSideButton(
            icon = Icons.Default.MoveDown,
            text = stringResource(
                R.string.versions_manage_move_versions
            ),
            accent = AlfaaVMPurple,
            onClick = onMoveVersions
        )

        AlfaaSideButton(
            icon = Icons.Default.CleaningServices,
            text = stringResource(
                R.string.versions_manage_cleanup
            ),
            accent = AlfaaVMGreen,
            onClick = onCleanup
        )
    }
}

@Composable
private fun AlfaaSideButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    accent: Color,
    enabled: Boolean = true,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        enabled = enabled,
        onClick = onClick,
        shape = RoundedCornerShape(15.dp),
        color = AlfaaVMPanel2,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            accent.copy(alpha = 0.28f)
        )
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 11.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                modifier = Modifier.size(19.dp),
                imageVector = icon,
                contentDescription = null,
                tint = accent
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Text(
                text = text,
                color = AlfaaVMText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/*
 * ============================================================
 * MAIN VERSION CONTENT
 * ============================================================
 */

@Composable
private fun AlfaaVersionsContent(
    modifier: Modifier,
    isRefreshing: Boolean,
    versions: List<Version>,
    currentVersion: Version?,
    versionCategory: VersionCategory,
    allVersionsCount: Int,
    vanillaVersionsCount: Int,
    modloaderVersionsCount: Int,
    onCategoryChange: (VersionCategory) -> Unit,
    onRefresh: () -> Unit,
    onInstall: () -> Unit,
    navigateToVersions: (Version) -> Unit,
    navigateToExport: (Version) -> Unit,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit,
    onVersionPinned: () -> Unit
) {

    val context = LocalContext.current

    var versionsOperation by remember {
        mutableStateOf<VersionsOperation>(
            VersionsOperation.None
        )
    }

    VersionsOperation(
        versionsOperation = versionsOperation,
        updateVersionsOperation = {
            versionsOperation = it
        },
        submitError = submitError
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = AlfaaVMPanel,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            AlfaaVMBorder
        )
    ) {

        if (isRefreshing) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally,
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    CircularProgressIndicator(
                        color = AlfaaVMCyan
                    )

                    Text(
                        text = "REFRESHING VERSIONS",
                        color = AlfaaVMMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp
                    )
                }
            }

        } else {

            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                /*
                 * HEADER
                 */

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 20.dp,
                            end = 12.dp,
                            top = 18.dp,
                            bottom = 12.dp
                        ),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "INSTALLED MINECRAFT",
                            color = AlfaaVMText,
                            fontWeight =
                                FontWeight.ExtraBold,
                            fontSize = 19.sp
                        )

                        Text(
                            text =
                                "${versions.size} VERSION${if (versions.size == 1) "" else "S"} AVAILABLE",
                            color = AlfaaVMMuted,
                            fontFamily =
                                FontFamily.Monospace,
                            fontSize = 8.sp,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Surface(
                        onClick = onRefresh,
                        shape =
                            RoundedCornerShape(13.dp),
                        color = AlfaaVMPanel2,
                        border =
                            androidx.compose.foundation.BorderStroke(
                                1.dp,
                                AlfaaVMBorder
                            )
                    ) {

                        Row(
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 9.dp
                            ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Refresh,
                                contentDescription =
                                    stringResource(
                                        R.string.generic_refresh
                                    ),
                                tint = AlfaaVMCyan,
                                modifier =
                                    Modifier.size(17.dp)
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(6.dp)
                            )

                            Text(
                                text = "REFRESH",
                                color = AlfaaVMText,
                                fontSize = 9.sp,
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.width(7.dp)
                    )

                    Surface(
                        onClick = onInstall,
                        shape =
                            RoundedCornerShape(13.dp),
                        color = AlfaaVMCyan
                    ) {

                        Row(
                            modifier = Modifier.padding(
                                horizontal = 13.dp,
                                vertical = 10.dp
                            ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier =
                                    Modifier.size(17.dp)
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(5.dp)
                            )

                            Text(
                                text = "INSTALL",
                                color = Color.Black,
                                fontSize = 9.sp,
                                fontWeight =
                                    FontWeight.ExtraBold
                            )
                        }
                    }
                }

                /*
                 * CATEGORY FILTER
                 */

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(
                            rememberScrollState()
                        )
                        .padding(
                            horizontal = 16.dp,
                            vertical = 4.dp
                        ),
                    horizontalArrangement =
                        Arrangement.spacedBy(7.dp)
                ) {

                    AlfaaCategory(
                        title = "ALL",
                        count = allVersionsCount,
                        selected =
                            versionCategory ==
                                    VersionCategory.ALL,
                        accent = AlfaaVMCyan,
                        onClick = {
                            onCategoryChange(
                                VersionCategory.ALL
                            )
                        }
                    )

                    AlfaaCategory(
                        title = "VANILLA",
                        count = vanillaVersionsCount,
                        selected =
                            versionCategory ==
                                    VersionCategory.VANILLA,
                        accent = AlfaaVMGreen,
                        onClick = {
                            onCategoryChange(
                                VersionCategory.VANILLA
                            )
                        }
                    )

                    AlfaaCategory(
                        title = "MODLOADER",
                        count = modloaderVersionsCount,
                        selected =
                            versionCategory ==
                                    VersionCategory.MODLOADER,
                        accent = AlfaaVMPurple,
                        onClick = {
                            onCategoryChange(
                                VersionCategory.MODLOADER
                            )
                        }
                    )
                }

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                /*
                 * VERSION LIST
                 */

                if (versions.isNotEmpty()) {

                    val listState =
                        rememberLazyListState()

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clipToBounds(),
                        state = listState,
                        contentPadding =
                            PaddingValues(
                                horizontal = 14.dp,
                                vertical = 8.dp
                            ),
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        items(
                            items = versions,
                            key = {
                                it.toString()
                            }
                        ) { version ->

                            val callbacks =
                                remember(version) {

                                    VersionItemCallbacks(

                                        submitError =
                                            submitError,

                                        onSelected = {

                                            if (
                                                version ==
                                                currentVersion
                                            ) {
                                                return@VersionItemCallbacks
                                            }

                                            if (
                                                !VersionsManager
                                                    .saveVersion(
                                                        version
                                                    )
                                            ) {

                                                versionsOperation =
                                                    VersionsOperation
                                                        .InvalidDelete(
                                                            version
                                                        )
                                            }
                                        },

                                        onSettingsClick = {
                                            navigateToVersions(
                                                version
                                            )
                                        },

                                        onRenameClick = {
                                            versionsOperation =
                                                VersionsOperation
                                                    .Rename(
                                                        version
                                                    )
                                        },

                                        onCopyClick = {
                                            versionsOperation =
                                                VersionsOperation
                                                    .Copy(
                                                        version
                                                    )
                                        },

                                        onExportClick = {
                                            navigateToExport(
                                                version
                                            )
                                        },

                                        onDeleteClick = {
                                            versionsOperation =
                                                VersionsOperation
                                                    .Delete(
                                                        version
                                                    )
                                        },

                                        onPinned =
                                            onVersionPinned,

                                        onAddShortcutClick = {
    ShortcutUtils.pinVersion(
        context = context,
        version = version
    )
                                        }
                                    )
                                }

                            AlfaaVersionCard(
                                version = version,
                                selected =
                                    version ==
                                            currentVersion,
                                callbacks = callbacks
                            )
                        }
                    }

                } else {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Surface(
                                shape =
                                    RoundedCornerShape(20.dp),
                                color = AlfaaVMPanel2,
                                border =
                                    androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        AlfaaVMBorder
                                    )
                            ) {

                                Column(
                                    modifier =
                                        Modifier.padding(
                                            horizontal = 30.dp,
                                            vertical = 25.dp
                                        ),
                                    horizontalAlignment =
                                        Alignment.CenterHorizontally
                                ) {

                                    Icon(
                                        imageVector =
                                            Icons.Default.Folder,
                                        contentDescription =
                                            null,
                                        tint = AlfaaVMMuted,
                                        modifier =
                                            Modifier.size(35.dp)
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.height(10.dp)
                                    )

                                    Text(
                                        text = "NO VERSIONS",
                                        color = AlfaaVMText,
                                        fontWeight =
                                            FontWeight.ExtraBold,
                                        fontSize = 13.sp
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.height(4.dp)
                                    )

                                    Text(
                                        text =
                                            "Install a Minecraft version to get started.",
                                        color = AlfaaVMMuted,
                                        fontSize = 9.sp
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

/*
 * ============================================================
 * CATEGORY CHIP
 * ============================================================
 */

@Composable
private fun AlfaaCategory(
    title: String,
    count: Int,
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit
) {

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) {
            accent.copy(alpha = 0.14f)
        } else {
            AlfaaVMPanel2
        },
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) {
                accent.copy(alpha = 0.7f)
            } else {
                AlfaaVMBorder
            }
        )
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 8.dp
            ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = title,
                color = if (selected) {
                    accent
                } else {
                    AlfaaVMMuted
                },
                fontFamily =
                    FontFamily.Monospace,
                fontWeight =
                    FontWeight.Bold,
                fontSize = 8.sp
            )

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            Text(
                text = count.toString(),
                color = if (selected) {
                    AlfaaVMText
                } else {
                    AlfaaVMMuted
                },
                fontSize = 8.sp,
                fontWeight =
                    FontWeight.ExtraBold
            )
        }
    }
}

/*
 * ============================================================
 * VERSION CARD
 *
 * VersionItemLayout tetap digunakan supaya seluruh aksi lama
 * tetap tersedia.
 * ============================================================
 */

@Composable
private fun AlfaaVersionCard(
    version: Version,
    selected: Boolean,
    callbacks: VersionItemCallbacks
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = if (selected) {
            Color(0xFF0D1718)
        } else {
            AlfaaVMPanel2
        },
        border = androidx.compose.foundation.BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) {
                AlfaaVMCyan.copy(alpha = 0.8f)
            } else {
                AlfaaVMBorder
            }
        )
    ) {

        Column(
            modifier = Modifier.padding(7.dp)
        ) {

            if (selected) {

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 7.dp,
                            vertical = 4.dp
                        ),
                    shape =
                        RoundedCornerShape(10.dp),
                    color =
                        AlfaaVMCyan.copy(alpha = 0.08f)
                ) {

                    Row(
                        modifier =
                            Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            ),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(
                                    AlfaaVMGreen,
                                    RoundedCornerShape(50)
                                )
                        )

                        Spacer(
                            modifier =
                                Modifier.width(7.dp)
                        )

                        Text(
                            text = "ACTIVE VERSION",
                            color = AlfaaVMCyan,
                            fontFamily =
                                FontFamily.Monospace,
                            fontWeight =
                                FontWeight.Bold,
                            fontSize = 7.sp,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }

            VersionItemLayout(
                version = version,
                selected = selected,
                callbacks = callbacks,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
