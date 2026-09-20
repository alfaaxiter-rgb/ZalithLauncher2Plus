/*
 * Zalith Launcher 2 Plus
 *
 * Custom persistent navigation sidebar.
 */

package com.movtery.zalithlauncher.ui.screens.content.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.setting.enums.DarkMode
import com.movtery.zalithlauncher.setting.enums.isLauncherInDarkTheme
import com.movtery.zalithlauncher.ui.theme.cardColor
import com.movtery.zalithlauncher.ui.theme.onCardColor

private val CollapsedWidth = 58.dp
private val ExpandedWidth = 190.dp

@Composable
fun SideBar(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,

    onFpsClick: () -> Unit,
    onVersionsClick: () -> Unit,
    onRecordingsClick: () -> Unit,
    onFileManagerClick: () -> Unit,
    onMultiplayerClick: () -> Unit,
    onDownloadsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    if (!isVisible) return

    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    val sidebarWidth by animateDpAsState(
        targetValue = if (expanded) {
            ExpandedWidth
        } else {
            CollapsedWidth
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "sidebarWidth"
    )

    Box(
        modifier = modifier
            .width(sidebarWidth)
            .fillMaxHeight()
            .padding(
                start = 8.dp,
                top = 8.dp,
                bottom = 8.dp
            )
    ) {

        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = cardColor(),
                contentColor = onCardColor()
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 10.dp,
                        bottom = 8.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // =========================================================
                // BRAND
                // =========================================================

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = if (expanded) {
                                14.dp
                            } else {
                                8.dp
                            },
                            vertical = 5.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = if (expanded) {
                        Arrangement.Start
                    } else {
                        Arrangement.Center
                    }
                ) {

                    Text(
                        text = if (expanded) {
                            "ZALITH+"
                        } else {
                            "Z+"
                        },
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = if (expanded) {
                            16.sp
                        } else {
                            14.sp
                        },
                        maxLines = 1
                    )
                }

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                // =========================================================
                // NAVIGATION
                // =========================================================

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    SideBarShortcut(
                        icon = painterResource(
                            R.drawable.ic_video_settings
                        ),
                        label = stringResource(
                            R.string.game_menu_option_fps_settings
                        ),
                        expanded = expanded,
                        onClick = onFpsClick
                    )

                    SideBarShortcut(
                        icon = painterResource(
                            R.drawable.ic_assignment_filled
                        ),
                        label = stringResource(
                            R.string.page_title_version_manage
                        ),
                        expanded = expanded,
                        onClick = onVersionsClick
                    )

                    SideBarShortcut(
                        icon = painterResource(
                            R.drawable.ic_videocam_filled
                        ),
                        label = "Recordings",
                        expanded = expanded,
                        onClick = onRecordingsClick
                    )

                    SideBarShortcut(
                        icon = painterResource(
                            R.drawable.ic_folder_filled
                        ),
                        label = "File Manager",
                        expanded = expanded,
                        onClick = onFileManagerClick
                    )

                    SideBarShortcut(
                        icon = painterResource(
                            R.drawable.ic_group_filled
                        ),
                        label = "Multiplayer",
                        expanded = expanded,
                        onClick = onMultiplayerClick
                    )

                    SideBarShortcut(
                        icon = painterResource(
                            R.drawable.ic_download_2_filled
                        ),
                        label = "Downloads",
                        expanded = expanded,
                        onClick = onDownloadsClick
                    )

                    SideBarShortcut(
                        icon = painterResource(
                            R.drawable.ic_settings_filled
                        ),
                        label = "Settings",
                        expanded = expanded,
                        onClick = onSettingsClick
                    )

                    SideBarShortcut(
                        icon = painterResource(
                            R.drawable.ic_info_outlined
                        ),
                        label = stringResource(
                            R.string.about_launcher_title
                        ),
                        expanded = expanded,
                        onClick = onInfoClick
                    )
                }

                // =========================================================
                // THEME
                // =========================================================

                ThemeToggle(
                    expanded = expanded
                )

                // =========================================================
                // EXPAND / COLLAPSE
                // =========================================================

                SideBarToggle(
                    expanded = expanded,
                    onClick = {
                        expanded = !expanded
                    }
                )
            }
        }
    }
}


// ========================================================================
// SIDEBAR BUTTON
// ========================================================================

@Composable
private fun SideBarShortcut(
    icon: Painter,
    label: String,
    expanded: Boolean,
    onClick: () -> Unit
) {

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isPressed by interactionSource
        .collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) {
            0.92f
        } else {
            1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "buttonScale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (expanded) {
                    7.dp
                } else {
                    6.dp
                }
            )
            .scale(scale)
            .shadow(
                elevation = if (isPressed) {
                    1.dp
                } else {
                    3.dp
                },
                shape = RoundedCornerShape(12.dp),
                ambientColor = MaterialTheme
                    .colorScheme
                    .primary
                    .copy(alpha = 0.08f),
                spotColor = MaterialTheme
                    .colorScheme
                    .primary
                    .copy(alpha = 0.12f)
            )
            .clip(
                RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),

        shape = RoundedCornerShape(12.dp),

        color = if (isPressed) {
            MaterialTheme
                .colorScheme
                .primary
                .copy(alpha = 0.16f)
        } else {
            MaterialTheme
                .colorScheme
                .surfaceVariant
                .copy(alpha = 0.30f)
        }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = if (expanded) {
                        11.dp
                    } else {
                        0.dp
                    },
                    vertical = 9.dp
                ),

            verticalAlignment = Alignment.CenterVertically,

            horizontalArrangement = if (expanded) {
                Arrangement.spacedBy(10.dp)
            } else {
                Arrangement.Center
            }
        ) {

            Icon(
                painter = icon,
                contentDescription = label,
                modifier = Modifier.size(21.dp),
                tint = if (isPressed) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme
                        .colorScheme
                        .onSurface
                        .copy(alpha = 0.72f)
                }
            )

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(
                    animationSpec = tween(180)
                ),
                exit = fadeOut(
                    animationSpec = tween(120)
                )
            ) {

                Text(
                    text = label,
                    style = MaterialTheme
                        .typography
                        .labelMedium,
                    color = MaterialTheme
                        .colorScheme
                        .onSurface
                        .copy(alpha = 0.80f),
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}


// ========================================================================
// TOGGLE BUTTON
// ========================================================================

@Composable
private fun SideBarToggle(
    expanded: Boolean,
    onClick: () -> Unit
) {

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isPressed by interactionSource
        .collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) {
            0.86f
        } else {
            1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "toggleScale"
    )

    Surface(
        modifier = Modifier
            .padding(top = 5.dp)
            .size(40.dp)
            .scale(scale)
            .clip(
                RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),

        shape = RoundedCornerShape(12.dp),

        color = MaterialTheme
            .colorScheme
            .surfaceVariant
            .copy(alpha = 0.35f)
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = if (expanded) {
                    "‹"
                } else {
                    "›"
                },
                fontSize = 28.sp,
                color = MaterialTheme
                    .colorScheme
                    .onSurface
                    .copy(alpha = 0.75f)
            )
        }
    }
}


// ========================================================================
// DARK / LIGHT TOGGLE
// ========================================================================

@Composable
private fun ThemeToggle(
    expanded: Boolean
) {

    val dark = isLauncherInDarkTheme()

    Surface(
        modifier = Modifier
            .padding(
                horizontal = if (expanded) {
                    8.dp
                } else {
                    6.dp
                },
                vertical = 6.dp
            )
            .clip(
                RoundedCornerShape(14.dp)
            )
            .clickable {

                AllSettings.launcherDarkMode.save(
                    if (dark) {
                        DarkMode.Disable
                    } else {
                        DarkMode.Enable
                    }
                )
            },

        shape = RoundedCornerShape(14.dp),

        color = MaterialTheme
            .colorScheme
            .surfaceVariant
            .copy(alpha = 0.45f)
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = if (expanded) {
                    10.dp
                } else {
                    8.dp
                },
                vertical = 8.dp
            ),

            verticalAlignment = Alignment.CenterVertically,

            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = if (dark) {
                    "☾"
                } else {
                    "☀"
                },
                fontSize = 14.sp
            )

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(
                    animationSpec = tween(150)
                ),
                exit = fadeOut(
                    animationSpec = tween(100)
                )
            ) {

                Text(
                    text = if (dark) {
                        "Dark"
                    } else {
                        "Light"
                    },
                    style = MaterialTheme
                        .typography
                        .labelSmall,
                    maxLines = 1
                )
            }
        }
    }
}
