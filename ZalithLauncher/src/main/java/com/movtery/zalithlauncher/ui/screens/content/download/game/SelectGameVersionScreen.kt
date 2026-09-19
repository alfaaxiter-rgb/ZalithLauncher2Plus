/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.movtery.zalithlauncher.ui.screens.content.download.game

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.scrollbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.versioninfo.MinecraftVersion
import com.movtery.zalithlauncher.game.versioninfo.MinecraftVersions
import com.movtery.zalithlauncher.game.versioninfo.models.isType
import com.movtery.zalithlauncher.ui.AndroidStringText
import com.movtery.zalithlauncher.ui.androidText
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.buildAppendedText
import com.movtery.zalithlauncher.ui.components.EdgeDirection
import com.movtery.zalithlauncher.ui.components.LittleTextLabel
import com.movtery.zalithlauncher.ui.components.ScalingLabel
import com.movtery.zalithlauncher.ui.components.SimpleTextInputField
import com.movtery.zalithlauncher.ui.components.fadeEdge
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.TitledNavKey
import com.movtery.zalithlauncher.utils.animation.getAnimateTween
import com.movtery.zalithlauncher.utils.animation.swapAnimateDpAsState
import com.movtery.zalithlauncher.utils.classes.Quadruple
import com.movtery.zalithlauncher.utils.formatDate
import com.movtery.zalithlauncher.utils.logging.Logger
import com.movtery.zalithlauncher.utils.network.toLocal
import com.movtery.zalithlauncher.utils.string.isEmptyOrBlank
import com.movtery.zalithlauncher.viewmodel.EventViewModel
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException

private const val TAG = "SelectGameVersion"

private val AlfaaBlack = Color(0xFF050608)
private val AlfaaPanel = Color(0xFF0A0D11)
private val AlfaaPanel2 = Color(0xFF10141A)
private val AlfaaBorder = Color(0xFF202832)
private val AlfaaText = Color(0xFFF1F5F9)
private val AlfaaMuted = Color(0xFF7D8996)
private val AlfaaCyan = Color(0xFF00E5FF)
private val AlfaaGreen = Color(0xFF00FF9D)

/** 版本列表加载状态 */
private sealed interface VersionState {
    data object Loading : VersionState

    data class None(
        val versions: List<MinecraftVersion>
    ) : VersionState

    data class Failure(
        val message: AndroidStringText
    ) : VersionState
}

/** 版本过滤条件 */
private data class VersionFilter(
    val release: Boolean = true,
    val snapshot: Boolean = false,
    val aprilFools: Boolean = false,
    val old: Boolean = false,
    val id: String = "",
)

private class VersionsViewModel : ViewModel() {

    var versionState by mutableStateOf<VersionState>(VersionState.Loading)
        private set

    var versionFilter by mutableStateOf(VersionFilter())
        private set

    fun filterWith(filter: VersionFilter) {
        versionFilter = filter

        viewModelScope.launch {
            val allVersions = MinecraftVersions.allVersions.value

            versionState = VersionState.None(
                versions = allVersions.filterVersions(versionFilter)
            )
        }
    }

    fun refresh(forceReload: Boolean = false) {
        viewModelScope.launch {
            versionState = VersionState.Loading

            versionState = runCatching {
                MinecraftVersions.refreshVersions(forceReload)

                val allVersions = MinecraftVersions.allVersions.value

                VersionState.None(
                    allVersions.filterVersions(versionFilter)
                )
            }.getOrElse { e ->
                Logger.warning(
                    TAG,
                    "Failed to get version manifest!",
                    e
                )

                val message: AndroidStringText = when (e) {
                    is HttpRequestTimeoutException ->
                        androidText(R.string.error_timeout)

                    is UnknownHostException,
                    is UnresolvedAddressException ->
                        androidText(R.string.error_network_unreachable)

                    is ConnectException ->
                        androidText(R.string.error_connection_failed)

                    is ResponseException ->
                        e.toLocal()

                    else -> {
                        Logger.error(
                            TAG,
                            "An unknown exception was caught!",
                            e
                        )

                        androidText(
                            e.localizedMessage
                                ?: e.message
                                ?: e::class.qualifiedName
                                ?: "Unknown error"
                        )
                    }
                }

                VersionState.Failure(message)
            }
        }
    }

    init {
        refresh()
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }
}

@Composable
fun SelectGameVersionScreen(
    mainScreenKey: TitledNavKey?,
    downloadScreenKey: TitledNavKey?,
    downloadGameScreenKey: TitledNavKey?,
    eventViewModel: EventViewModel,
    onVersionSelect: (String) -> Unit = {}
) {
    val viewModel = viewModel(
        key = NormalNavKey.DownloadGame.SelectGameVersion.toString()
    ) {
        VersionsViewModel()
    }

    BaseScreen(
        levels1 = listOf(
            Pair(
                NestedNavKey.Download::class.java,
                mainScreenKey
            ),
            Pair(
                NestedNavKey.DownloadGame::class.java,
                downloadScreenKey
            )
        ),
        Triple(
            NormalNavKey.DownloadGame.SelectGameVersion,
            downloadGameScreenKey,
            false
        )
    ) { isVisible ->

        val yOffset by swapAnimateDpAsState(
            targetValue = (-40).dp,
            swapIn = isVisible
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AlfaaBlack)
                .offset {
                    IntOffset(
                        x = 0,
                        y = yOffset.roundToPx()
                    )
                }
        ) {

            when (val state = viewModel.versionState) {

                is VersionState.Loading -> {
                    VersionLoading()
                }

                is VersionState.Failure -> {
                    VersionError(
                        message = state.message,
                        onRetry = {
                            viewModel.refresh(true)
                        }
                    )
                }

                is VersionState.None -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {

                        VersionHeader(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 14.dp,
                                    bottom = 8.dp
                                ),
                            versionFilter = viewModel.versionFilter,
                            onVersionFilterChange = {
                                viewModel.filterWith(it)
                            },
                            onRefreshClick = {
                                viewModel.refresh(true)
                            }
                        )

                        VersionList(
                            modifier = Modifier.weight(1f),
                            versions = state.versions,
                            onVersionSelect = onVersionSelect,
                            openLink = { url ->
                                eventViewModel.sendEvent(
                                    EventViewModel.Event.OpenLink(url)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Filter versi.
 */
private fun List<MinecraftVersion>.filterVersions(
    versionFilter: VersionFilter
): List<MinecraftVersion> {

    return this
        .filter { version ->
            version.isType(
                release = versionFilter.release,
                snapshot = versionFilter.snapshot,
                aprilFools = versionFilter.aprilFools,
                old = versionFilter.old
            )
        }
        .filter { version ->
            val versionId = versionFilter.id

            versionId.isEmptyOrBlank() ||
                    version.version.id.contains(versionId)
        }
}

@Composable
private fun VersionLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AlfaaBlack),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(AlfaaPanel2),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A",
                    color = AlfaaCyan,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Text(
                text = "LOADING VERSIONS",
                color = AlfaaMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            LinearProgressIndicator(
                modifier = Modifier
                    .width(180.dp)
                    .clip(RoundedCornerShape(50))
            )
        }
    }
}

@Composable
private fun VersionError(
    message: AndroidStringText,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AlfaaBlack),
        contentAlignment = Alignment.Center
    ) {

        Surface(
            modifier = Modifier
                .padding(24.dp)
                .widthIn(max = 520.dp),
            shape = RoundedCornerShape(22.dp),
            color = AlfaaPanel,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                AlfaaBorder
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "DOWNLOAD ERROR",
                    color = AlfaaText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                AndroidStringText(
                    text = buildAppendedText {
                        append(
                            R.string.download_game_failed_to_get_versions
                        )
                        append(message)
                    }
                )

                TextButton(
                    onClick = onRetry
                ) {
                    Text(
                        text = stringResource(
                            R.string.generic_refresh
                        ),
                        color = AlfaaCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun VersionHeader(
    modifier: Modifier = Modifier,
    versionFilter: VersionFilter,
    onVersionFilterChange: (VersionFilter) -> Unit,
    onRefreshClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {

        Text(
            text = "MINECRAFT VERSIONS",
            color = AlfaaText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Choose a version to install",
            color = AlfaaMuted,
            fontSize = 12.sp
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                val scrollState = rememberScrollState()

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fadeEdge(
                            state = scrollState,
                            direction = EdgeDirection.Horizontal
                        )
                        .horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    VersionFilterChip(
                        selected = versionFilter.release,
                        text = stringResource(
                            R.string.download_game_type_release
                        ),
                        onClick = {
                            onVersionFilterChange(
                                versionFilter.copy(
                                    release = !versionFilter.release
                                )
                            )
                        }
                    )

                    VersionFilterChip(
                        selected = versionFilter.snapshot,
                        text = stringResource(
                            R.string.download_game_type_snapshot
                        ),
                        onClick = {
                            onVersionFilterChange(
                                versionFilter.copy(
                                    snapshot = !versionFilter.snapshot
                                )
                            )
                        }
                    )

                    VersionFilterChip(
                        selected = versionFilter.aprilFools,
                        text = stringResource(
                            R.string.download_game_type_april_fools
                        ),
                        onClick = {
                            onVersionFilterChange(
                                versionFilter.copy(
                                    aprilFools = !versionFilter.aprilFools
                                )
                            )
                        }
                    )

                    VersionFilterChip(
                        selected = versionFilter.old,
                        text = stringResource(
                            R.string.download_game_type_old
                        ),
                        onClick = {
                            onVersionFilterChange(
                                versionFilter.copy(
                                    old = !versionFilter.old
                                )
                            )
                        }
                    )
                }

                SearchBox(
                    value = versionFilter.id,
                    onValueChange = {
                        onVersionFilterChange(
                            versionFilter.copy(id = it)
                        )
                    },
                    onRefreshClick = onRefreshClick
                )
            }
        }
    }
}

@Composable
private fun VersionFilterChip(
    selected: Boolean,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) {
            AlfaaCyan.copy(alpha = 0.14f)
        } else {
            AlfaaPanel
        },
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) {
                AlfaaCyan.copy(alpha = 0.7f)
            } else {
                AlfaaBorder
            }
        )
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 13.dp,
                vertical = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (selected) {
                            AlfaaCyan
                        } else {
                            AlfaaMuted
                        }
                    )
            )

            Spacer(
                modifier = Modifier.width(7.dp)
            )

            Text(
                text = text,
                color = if (selected) {
                    AlfaaCyan
                } else {
                    AlfaaMuted
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SearchBox(
    value: String,
    onValueChange: (String) -> Unit,
    onRefreshClick: () -> Unit
) {
    Row(
        modifier = Modifier.widthIn(
            min = 180.dp,
            max = 300.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(13.dp),
            color = AlfaaPanel,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                AlfaaBorder
            )
        ) {

            Row(
                modifier = Modifier.padding(
                    start = 10.dp,
                    end = 4.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = AlfaaMuted,
                    modifier = Modifier.size(17.dp)
                )

                SimpleTextInputField(
                    modifier = Modifier.weight(1f),
                    value = value,
                    onValueChange = onValueChange,
                    color = Color.Transparent,
                    contentColor = AlfaaText,
                    singleLine = true,
                    hint = {
                        Text(
                            text = stringResource(
                                R.string.generic_search
                            ),
                            color = AlfaaMuted,
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }

        Surface(
            onClick = onRefreshClick,
            shape = RoundedCornerShape(13.dp),
            color = AlfaaPanel,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                AlfaaBorder
            )
        ) {
            Icon(
                painter = painterResource(
                    R.drawable.ic_refresh
                ),
                contentDescription = stringResource(
                    R.string.generic_refresh
                ),
                tint = AlfaaCyan,
                modifier = Modifier
                    .padding(10.dp)
                    .size(19.dp)
            )
        }
    }
}

@Composable
private fun VersionList(
    modifier: Modifier = Modifier,
    versions: List<MinecraftVersion>,
    onVersionSelect: (String) -> Unit,
    openLink: (url: String) -> Unit
) {

    val scrollState = rememberLazyListState()

    LazyColumn(
    modifier = modifier,
    contentPadding = PaddingValues(
        horizontal = 16.dp,
        vertical = 8.dp
    ),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    state = scrollState
) {

        items(
            items = versions,
            key = {
                it.version.id
            }
        ) { version ->

            VersionItemLayout(
                modifier = Modifier.fillMaxWidth(),
                version = version,
                onClick = {
                    onVersionSelect(
                        version.version.id
                    )
                },
                onAccessWiki = openLink
            )
        }
    }
}

@Composable
private fun VersionItemLayout(
    modifier: Modifier = Modifier,
    version: MinecraftVersion,
    onClick: () -> Unit = {},
    onAccessWiki: (String) -> Unit = {},
    shape: Shape = RoundedCornerShape(18.dp)
) {

    val scale = remember {
        Animatable(0.96f)
    }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = getAnimateTween()
        )
    }

    val (
        icon,
        versionType,
        wikiUrl,
        summary
    ) = getVersionComponents(version)

    Surface(
        modifier = modifier.graphicsLayer(
            scaleX = scale.value,
            scaleY = scale.value
        ),
        onClick = onClick,
        shape = shape,
        color = AlfaaPanel,
        contentColor = AlfaaText,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            AlfaaBorder
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        AlfaaPanel2
                    ),
                contentAlignment = Alignment.Center
            ) {

                icon?.let { versionIcon ->

                    Image(
                        modifier = Modifier.size(32.dp),
                        painter = versionIcon,
                        contentDescription = null
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = version.version.id,
                        color = AlfaaText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    VersionTypeBadge(
                        text = versionType
                    )
                }

                summary?.let { text ->

                    Text(
                        modifier = Modifier.alpha(0.65f),
                        text = text,
                        color = AlfaaText,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }

                Text(
                    text = formatDate(
                        input = version.version.releaseTime,
                        pattern = stringResource(
                            R.string.date_format
                        )
                    ),
                    color = AlfaaMuted,
                    fontSize = 10.sp
                )
            }

            if (wikiUrl != null) {

                Surface(
                    onClick = {
                        onAccessWiki(wikiUrl)
                    },
                    shape = RoundedCornerShape(11.dp),
                    color = AlfaaPanel2,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        AlfaaBorder
                    )
                ) {

                    Icon(
                        painter = painterResource(
                            R.drawable.ic_link
                        ),
                        contentDescription = "Wiki",
                        tint = AlfaaCyan,
                        modifier = Modifier
                            .padding(9.dp)
                            .size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun VersionTypeBadge(
    text: String
) {

    val isRelease = text ==
            stringResource(
                R.string.download_game_type_release
            )

    Surface(
        shape = RoundedCornerShape(7.dp),
        color = if (isRelease) {
            AlfaaGreen.copy(alpha = 0.12f)
        } else {
            AlfaaCyan.copy(alpha = 0.10f)
        }
    ) {

        Text(
            modifier = Modifier.padding(
                horizontal = 7.dp,
                vertical = 3.dp
            ),
            text = text,
            color = if (isRelease) {
                AlfaaGreen
            } else {
                AlfaaCyan
            },
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun getVersionComponents(
    version: MinecraftVersion
): Quadruple<Painter?, String, String?, String?> {

    val vmVer = version.version

    val summary = version.summary?.let {
        stringResource(it)
    }

    val urlSuffix = version.urlSuffix ?: vmVer.id

    return when (version.type) {

        MinecraftVersion.Type.Release -> {
            Quadruple(
                painterResource(
                    R.drawable.img_minecraft
                ),
                stringResource(
                    R.string.download_game_type_release
                ),
                stringResource(
                    R.string.url_wiki_minecraft_game_release,
                    urlSuffix
                ),
                summary
            )
        }

        MinecraftVersion.Type.Snapshot -> {
            Quadruple(
                painterResource(
                    R.drawable.img_command_block
                ),
                stringResource(
                    R.string.download_game_type_snapshot
                ),
                stringResource(
                    R.string.url_wiki_minecraft_game_snapshot,
                    urlSuffix
                ),
                summary
            )
        }

        MinecraftVersion.Type.AprilFools -> {
            Quadruple(
                painterResource(
                    R.drawable.img_diamond_block
                ),
                stringResource(
                    R.string.download_game_type_april_fools
                ),
                stringResource(
                    R.string.url_wiki_minecraft_game_snapshot,
                    urlSuffix
                ),
                summary
            )
        }

        MinecraftVersion.Type.OldBeta -> {
            Quadruple(
                painterResource(
                    R.drawable.img_old_cobblestone
                ),
                stringResource(
                    R.string.download_game_type_old_beta
                ),
                null,
                summary
            )
        }

        MinecraftVersion.Type.OldAlpha -> {
            Quadruple(
                painterResource(
                    R.drawable.img_old_grass_block
                ),
                stringResource(
                    R.string.download_game_type_old_alpha
                ),
                null,
                summary
            )
        }

        else -> {
            Quadruple(
                null,
                stringResource(
                    R.string.generic_unknown
                ),
                null,
                version.summary?.let {
                    stringResource(it)
                }
            )
        }
    }
}
