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

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.scrollbar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.contract.MediaPickerContract
import com.movtery.zalithlauncher.coroutine.Task
import com.movtery.zalithlauncher.coroutine.TaskSystem
import com.movtery.zalithlauncher.path.PathManager
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.setting.enums.AppLanguage
import com.movtery.zalithlauncher.setting.enums.BackgroundBlur
import com.movtery.zalithlauncher.setting.enums.DarkMode
import com.movtery.zalithlauncher.setting.enums.HomePageType
import com.movtery.zalithlauncher.setting.enums.MirrorSourceType
import com.movtery.zalithlauncher.setting.enums.applyLanguage
import com.movtery.zalithlauncher.setting.unit.floatRange
import com.movtery.zalithlauncher.ui.androidText
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.components.AnimatedColumn
import com.movtery.zalithlauncher.ui.components.IconTextButton
import com.movtery.zalithlauncher.ui.components.OwnOutlinedTextField
import com.movtery.zalithlauncher.ui.components.SimpleAlertDialog
import com.movtery.zalithlauncher.ui.components.TitleAndSummary
import com.movtery.zalithlauncher.ui.components.WarningCard
import com.movtery.zalithlauncher.ui.components.fadeEdge
import com.movtery.zalithlauncher.ui.components.verticalScrollWithBar
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.TitledNavKey
import com.movtery.zalithlauncher.ui.screens.content.elements.DisabledAlpha
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.CardPosition
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.EnumSettingsCard
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.IntSliderSettingsCard
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.ListSettingsCard
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.SettingsCard
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.SettingsCardColumn
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.SwitchSettingsCard
import com.movtery.zalithlauncher.ui.theme.cardColor
import com.movtery.zalithlauncher.ui.theme.onCardColor
import com.movtery.zalithlauncher.utils.animation.TransitionAnimationType
import com.movtery.zalithlauncher.utils.file.shareFile
import com.movtery.zalithlauncher.utils.checkStoragePermissions
import com.movtery.zalithlauncher.utils.isChinaMainland
import com.movtery.zalithlauncher.utils.logging.Logger
import com.movtery.zalithlauncher.utils.settings.SettingsTransferUtils
import com.movtery.zalithlauncher.utils.string.getMessageOrToString
import com.movtery.zalithlauncher.viewmodel.BackgroundViewModel
import com.movtery.zalithlauncher.viewmodel.ErrorViewModel
import com.movtery.zalithlauncher.viewmodel.EventViewModel
import com.movtery.zalithlauncher.viewmodel.LocalBackgroundViewModel
import com.movtery.zalithlauncher.viewmodel.LocalHomePageViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private const val TAG = "LauncherSettingsScreen"

private sealed interface BackgroundOperation {
    data object None : BackgroundOperation
    data object PreReset : BackgroundOperation
    data object Reset : BackgroundOperation
}

@Composable
private fun CustomBackground(
    backgroundViewModel: BackgroundViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var operation by remember { mutableStateOf<BackgroundOperation>(BackgroundOperation.None) }

    BackgroundOperation(
        operation = operation,
        changeOperation = { operation = it },
        backgroundViewModel = backgroundViewModel
    )

    val importErrorText = stringResource(R.string.error_import_image)
    val filePicker = rememberLauncherForActivityResult(
        contract = MediaPickerContract(
            allowImages = true,
            allowVideos = true,
            allowMultiple = false
        )
    ) { result ->
        if (result != null) {
            TaskSystem.submitTask(
                Task.runTask(
                    dispatcher = Dispatchers.IO,
                    task = { task ->
                        task.updateMessage(androidText(R.string.settings_launcher_background_importing))
                        backgroundViewModel.import(context, result[0] /* 取决于上面的allowMultiple，此处一定会是单个元素的列表 */)
                    },
                    onError = { th ->
                        backgroundViewModel.delete()
                        submitError(
                            ErrorViewModel.ThrowableMessage(
                                title = androidText(importErrorText),
                                message = androidText(th.getMessageOrToString())
                            )
                        )
                    }
                )
            )
        }
    }

    Row(
        modifier = modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { filePicker.launch(Unit) }
                .padding(horizontal = 4.dp, vertical = 8.dp),
        ) {
            TitleAndSummary(
                title = stringResource(R.string.settings_launcher_background_title),
                summary = stringResource(R.string.settings_launcher_background_summary),
            )

            if (backgroundViewModel.isValid) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (backgroundViewModel.isVideo) {
                        "VIDEO // ACTIVE"
                    } else {
                        "IMAGE // ACTIVE"
                    },
                    color = Color(0xFF20E0B2),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp,
                    letterSpacing = 0.8.sp
                )
            } else {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "PHOTO / VIDEO // TAP TO SELECT",
                    color = Color(0xFF43C7FF),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp,
                    letterSpacing = 0.7.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF20E0B2).copy(alpha = 0.08f))
                .border(
                    width = 1.dp,
                    color = Color(0xFF20E0B2).copy(alpha = 0.35f),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { filePicker.launch(Unit) }
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = if (backgroundViewModel.isValid) "CHANGE" else "SELECT",
                color = Color(0xFF20E0B2),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 8.sp,
                letterSpacing = 0.8.sp
            )
        }

        AnimatedVisibility(
            modifier = Modifier.padding(start = 2.dp),
            visible = backgroundViewModel.isValid
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFF5577).copy(alpha = 0.08f))
                    .border(
                        width = 1.dp,
                        color = Color(0xFFFF5577).copy(alpha = 0.30f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        if (operation == BackgroundOperation.None) {
                            operation = BackgroundOperation.PreReset
                        }
                    }
                    .padding(horizontal = 10.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "RESET",
                    color = Color(0xFFFF5577),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp,
                    letterSpacing = 0.7.sp
                )
            }
        }
    }
}

@Composable
private fun BackgroundOperation(
    operation: BackgroundOperation,
    changeOperation: (BackgroundOperation) -> Unit,
    backgroundViewModel: BackgroundViewModel
) {
    when (operation) {
        is BackgroundOperation.None -> {}
        is BackgroundOperation.PreReset -> {
            SimpleAlertDialog(
                title = stringResource(R.string.generic_reset),
                text = stringResource(R.string.settings_launcher_background_reset_message),
                onConfirm = {
                    changeOperation(BackgroundOperation.Reset)
                },
                onDismiss = {
                    changeOperation(BackgroundOperation.None)
                }
            )
        }
        is BackgroundOperation.Reset -> {
            LaunchedEffect(Unit) {
                backgroundViewModel.delete()
                changeOperation(BackgroundOperation.None)
            }
        }
    }
}