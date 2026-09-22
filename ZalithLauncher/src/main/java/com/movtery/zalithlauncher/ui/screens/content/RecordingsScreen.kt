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

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.text.format.Formatter
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.components.RecordingPlayerOverlay
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.viewmodel.ScreenBackStackViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val AlfaaBlack = Color(0xFF05080D)
private val AlfaaPanel = Color(0xFF0B1118)
private val AlfaaPanel2 = Color(0xFF101923)
private val AlfaaCyan = Color(0xFF43C7FF)
private val AlfaaGreen = Color(0xFF20E0B2)
private val AlfaaText = Color(0xFFE8FFF8)
private val AlfaaMuted = Color(0xFF829A98)
private val AlfaaBorder = Color(0xFF16483F)

data class RecordingEntry(
    val uri: Uri,
    val id: Long,
    val displayName: String,
    val durationMs: Long,
    val sizeBytes: Long,
    val dateAddedSec: Long,
    val width: Int,
    val height: Int
)

@Composable
fun RecordingsScreen(backStackViewModel: ScreenBackStackViewModel) {
    BaseScreen(
        screenKey = NormalNavKey.Recordings,
        currentKey = backStackViewModel.mainScreen.currentKey
    ) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        var recordings by remember { mutableStateOf<List<RecordingEntry>>(emptyList()) }
        var loading by remember { mutableStateOf(true) }
        val thumbnails = remember { mutableStateMapOf<Long, Bitmap>() }
        var renameTarget  by remember { mutableStateOf<RecordingEntry?>(null) }
        var deleteTarget  by remember { mutableStateOf<RecordingEntry?>(null) }
        var playingEntry  by remember { mutableStateOf<RecordingEntry?>(null) }

        fun reload() {
            scope.launch {
                loading = true
                recordings = withContext(Dispatchers.IO) { queryRecordings(context) }
                loading = false
            }
        }

        LaunchedEffect(Unit) { reload() }

        Column(modifier = Modifier.fillMaxSize()) {
            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    BasicText(text = stringResource(R.string.generic_loading), style = TextStyle(color = AlfaaCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold))
                }
            } else if (recordings.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(R.drawable.ic_videocam_outlined),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = AlfaaCyan.copy(alpha = 0.4f)
                        )
                        Spacer(Modifier.height(12.dp))
                        BasicText(
                            text = stringResource(R.string.recordings_empty),
                            style = TextStyle(color = AlfaaText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recordings, key = { it.id }) { entry ->
                        LaunchedEffect(entry.id) {
                            if (!thumbnails.containsKey(entry.id)) {
                                val bmp = withContext(Dispatchers.IO) {
                                    loadThumbnail(context, entry.uri)
                                }
                                if (bmp != null) thumbnails[entry.id] = bmp
                            }
                        }
                        RecordingCard(
                            entry = entry,
                            thumbnail = thumbnails[entry.id],
                            onPlay = { playingEntry = entry },
                            onShare = { shareRecording(context, entry.uri) },
                            onRename = { renameTarget = entry },
                            onDelete = { deleteTarget = entry },
                            onReveal = {
                                val dir = android.os.Environment
                                    .getExternalStoragePublicDirectory(
                                        android.os.Environment.DIRECTORY_MOVIES
                                    )
                                val uri2 = Uri.parse("$dir/Zeryth Recordings")
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri2, "*/*")
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                runCatching { context.startActivity(intent) }
                            }
                        )
                    }
                }
            }
        }

        renameTarget?.let { entry ->
            var name by remember(entry.id) { mutableStateOf(entry.displayName.removeSuffix(".mp4")) }
            Dialog(onDismissRequest = { renameTarget = null }) {
                Column(
                    modifier = Modifier.width(340.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(18.dp))
                        .background(Brush.verticalGradient(listOf(AlfaaPanel2, AlfaaPanel)))
                        .border(1.dp, AlfaaBorder, androidx.compose.foundation.shape.RoundedCornerShape(18.dp))
                        .padding(20.dp)
                ) {
                    BasicText(stringResource(R.string.recordings_rename), style = TextStyle(color = AlfaaCyan, fontSize = 18.sp, fontWeight = FontWeight.Bold))
                    Spacer(Modifier.height(14.dp))
                    BasicTextField(
                        value = name,
                        onValueChange = { name = it },
                        singleLine = true,
                        textStyle = TextStyle(color = AlfaaText, fontSize = 14.sp),
                        modifier = Modifier.fillMaxWidth()
                            .background(AlfaaBlack, androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
                            .border(1.dp, AlfaaBorder, androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        decorationBox = { inner ->
                            Box {
                                if (name.isEmpty()) BasicText(stringResource(R.string.recordings_rename_hint), style = TextStyle(color = AlfaaMuted, fontSize = 14.sp))
                                inner()
                            }
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        BasicText(stringResource(R.string.generic_cancel), style = TextStyle(color = AlfaaMuted, fontSize = 13.sp, fontWeight = FontWeight.Bold), modifier = Modifier.clickable { renameTarget = null }.padding(12.dp))
                        BasicText(stringResource(R.string.generic_save), style = TextStyle(color = AlfaaGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold), modifier = Modifier.clickable {
                            val newName = name.trim() + ".mp4"
                            scope.launch(Dispatchers.IO) {
                                val values = ContentValues().apply { put(MediaStore.Video.Media.DISPLAY_NAME, newName) }
                                context.contentResolver.update(entry.uri, values, null, null)
                                withContext(Dispatchers.Main) { renameTarget = null; reload() }
                            }
                        }.padding(12.dp))
                    }
                }
            }
        }

        deleteTarget?.let { entry ->
            Dialog(onDismissRequest = { deleteTarget = null }) {
                Column(
                    modifier = Modifier.width(340.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(18.dp))
                        .background(Brush.verticalGradient(listOf(AlfaaPanel2, AlfaaPanel)))
                        .border(1.dp, AlfaaBorder, androidx.compose.foundation.shape.RoundedCornerShape(18.dp))
                        .padding(20.dp)
                ) {
                    BasicText(stringResource(R.string.recordings_delete_title), style = TextStyle(color = Color(0xFFFF5577), fontSize = 18.sp, fontWeight = FontWeight.Bold))
                    Spacer(Modifier.height(10.dp))
                    BasicText(stringResource(R.string.recordings_delete_message, entry.displayName), style = TextStyle(color = AlfaaText, fontSize = 13.sp))
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        BasicText(stringResource(R.string.generic_cancel), style = TextStyle(color = AlfaaMuted, fontSize = 13.sp, fontWeight = FontWeight.Bold), modifier = Modifier.clickable { deleteTarget = null }.padding(12.dp))
                        BasicText(stringResource(R.string.generic_delete), style = TextStyle(color = Color(0xFFFF5577), fontSize = 13.sp, fontWeight = FontWeight.Bold), modifier = Modifier.clickable {
                            scope.launch(Dispatchers.IO) {
                                runCatching { context.contentResolver.delete(entry.uri, null, null) }
                                withContext(Dispatchers.Main) { deleteTarget = null; reload() }
                            }
                        }.padding(12.dp))
                    }
                }
            }
        }

        playingEntry?.let { entry ->
            RecordingPlayerOverlay(
                uri   = entry.uri,
                title = entry.displayName.removeSuffix(".mp4"),
                onDismiss = { playingEntry = null }
            )
        }
    }
}

@Composable
private fun RecordingCard(
    entry: RecordingEntry,
    thumbnail: Bitmap?,
    onPlay: () -> Unit,
    onShare: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onReveal: () -> Unit,
) {
    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onLongClick = { menuExpanded = true }, onClick = onPlay),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .aspectRatio(16f / 9f)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                    .background(AlfaaBlack)
                    .border(1.dp, AlfaaBorder, androidx.compose.foundation.shape.RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (thumbnail != null) {
                    Image(
                        bitmap = thumbnail.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_videocam_outlined),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = AlfaaMuted.copy(alpha = 0.4f)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                BasicText(
                    text = entry.displayName.removeSuffix(".mp4"),
                    style = TextStyle(color = AlfaaText, fontSize = 13.sp, fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                val parts = buildList {
                    val dur = formatDuration(entry.durationMs)
                    if (dur.isNotEmpty()) add(dur)
                    if (entry.sizeBytes > 0)
                        add(Formatter.formatShortFileSize(context, entry.sizeBytes))
                    if (entry.width > 0 && entry.height > 0)
                        add("${entry.width}\u00d7${entry.height}")
                    add(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(Date(entry.dateAddedSec * 1000L)))
                }
                BasicText(
                    text = parts.joinToString(" \\u00b7 "),
                    style = TextStyle(color = AlfaaMuted, fontSize = 10.sp)
                )
            }

            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(painterResource(R.drawable.ic_more_vert), contentDescription = null)
                }
                Dialog(onDismissRequest = { menuExpanded = false }) {
                    Column(
                        modifier = Modifier.width(220.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                            .background(Brush.verticalGradient(listOf(AlfaaPanel2, AlfaaPanel)))
                            .border(1.dp, AlfaaBorder, androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                            .padding(vertical = 6.dp)
                    ) {
                        BasicText(stringResource(R.string.recordings_play), style = TextStyle(color = AlfaaText, fontSize = 13.sp, fontWeight = FontWeight.Bold), modifier = Modifier.fillMaxWidth().clickable { menuExpanded=false; onPlay() }.padding(14.dp))
                        BasicText(stringResource(R.string.recordings_share), style = TextStyle(color = AlfaaText, fontSize = 13.sp, fontWeight = FontWeight.Bold), modifier = Modifier.fillMaxWidth().clickable { menuExpanded=false; onShare() }.padding(14.dp))
                        BasicText(stringResource(R.string.recordings_rename), style = TextStyle(color = AlfaaText, fontSize = 13.sp, fontWeight = FontWeight.Bold), modifier = Modifier.fillMaxWidth().clickable { menuExpanded=false; onRename() }.padding(14.dp))
                        BasicText(stringResource(R.string.recordings_reveal), style = TextStyle(color = AlfaaText, fontSize = 13.sp, fontWeight = FontWeight.Bold), modifier = Modifier.fillMaxWidth().clickable { menuExpanded=false; onReveal() }.padding(14.dp))
                        BasicText(stringResource(R.string.generic_delete), style = TextStyle(color = Color(0xFFFF5577), fontSize = 13.sp, fontWeight = FontWeight.Bold), modifier = Modifier.fillMaxWidth().clickable { menuExpanded=false; onDelete() }.padding(14.dp))
                    }
                }            }
        }
    }
}

private fun queryRecordings(context: Context): List<RecordingEntry> {
    val projection = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.DURATION,
        MediaStore.Video.Media.SIZE,
        MediaStore.Video.Media.DATE_ADDED,
        MediaStore.Video.Media.WIDTH,
        MediaStore.Video.Media.HEIGHT
    )
    val selection = "${MediaStore.Video.Media.RELATIVE_PATH} LIKE ? AND " +
            "${MediaStore.Video.Media.IS_PENDING} = 0"
    val selectionArgs = arrayOf("%Zeryth Recordings%")
    val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

    val results = mutableListOf<RecordingEntry>()
    context.contentResolver.query(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        projection, selection, selectionArgs, sortOrder
    )?.use { cursor ->
        val idCol   = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
        val durCol  = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
        val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
        val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
        val wCol    = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)
        val hCol    = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idCol)
            results += RecordingEntry(
                uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id
                ),
                id = id,
                displayName = cursor.getString(nameCol) ?: "recording.mp4",
                durationMs  = cursor.getLong(durCol),
                sizeBytes   = cursor.getLong(sizeCol),
                dateAddedSec = cursor.getLong(dateCol),
                width  = cursor.getInt(wCol),
                height = cursor.getInt(hCol)
            )
        }
    }
    return results
}

private fun loadThumbnail(context: Context, uri: Uri): Bitmap? = runCatching {
    MediaMetadataRetriever().use { r ->
        r.setDataSource(context, uri)
        r.getFrameAtTime(0L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
    }
}.getOrNull()

private fun formatDuration(ms: Long): String {
    if (ms <= 0L) return ""
    val sec = ms / 1000L
    val h = sec / 3600; val m = (sec % 3600) / 60; val s = sec % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}

private fun playRecording(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "video/mp4")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { context.startActivity(Intent.createChooser(intent, null).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }) }
}

private fun shareRecording(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "video/mp4"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    runCatching { context.startActivity(Intent.createChooser(intent, null).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }) }
}
