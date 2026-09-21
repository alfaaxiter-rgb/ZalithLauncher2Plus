/*
 * ZalithLauncher 2 Plus
 * Custom persistent left navigation sidebar.
 *
 * ALFAA NEON UI
 */

package com.movtery.zalithlauncher.ui.screens.content.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.setting.enums.DarkMode
import com.movtery.zalithlauncher.setting.enums.isLauncherInDarkTheme

/* ========================================================= */
/* ALFAA NEON PALETTE */
/* ========================================================= */

private val AlfaaBlack = Color(0xFF05080D)
private val AlfaaPanel = Color(0xFF0B1118)
private val AlfaaPanel2 = Color(0xFF101923)
private val AlfaaPanel3 = Color(0xFF14212B)

private val AlfaaCyan = Color(0xFF43C7FF)
private val AlfaaGreen = Color(0xFF20E0B2)
private val AlfaaPurple = Color(0xFFA78BFA)
private val AlfaaOrange = Color(0xFFFFB454)

private val AlfaaText = Color(0xFFE8FFF8)
private val AlfaaMuted = Color(0xFF829A98)
private val AlfaaBorder = Color(0xFF16483F)

private val AlfaaText = Color(0xFFE8FFF8)
private val AlfaaMuted = Color(0xFF829A98)
private val AlfaaBorder = Color(0xFF16483F)

/* ========================================================= */
/* SIDEBAR DIMENSIONS */
/* ========================================================= */

private val CollapsedWidth = 58.dp
private val ExpandedWidth = 188.dp

/* ========================================================= */
/* SIDEBAR */
/* ========================================================= */

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

    /*
     * Satu animasi utama untuk lebar sidebar.
     *
     * Tidak memakai spring bertingkat supaya perpindahan
     * tidak terasa patah/bergetar di device.
     */
    val sidebarWidth by animateDpAsState(
        targetValue = if (expanded) {
            ExpandedWidth
        } else {
            CollapsedWidth
        },
        animationSpec = tween(
            durationMillis = 220,
            easing = FastOutSlowInEasing
        ),
        label = "alfaaSidebarWidth"
    )

    Surface(
        modifier = modifier
            .width(sidebarWidth)
            .fillMaxHeight()
            .padding(
                start = 6.dp,
                top = 6.dp,
                bottom = 6.dp
            ),
        shape = RoundedCornerShape(20.dp),
        color = AlfaaBlack,
        contentColor = AlfaaText,
        border = BorderStroke(
            1.dp,
            AlfaaCyan.copy(alpha = 0.30f)
        ),
        shadowElevation = 0.dp
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            AlfaaPanel2,
                            AlfaaPanel,
                            AlfaaBlack
                        )
                    )
                )
        ) {

            /*
             * NEON ACCENT LINE
             */
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                AlfaaCyan.copy(alpha = 0.15f),
                                AlfaaGreen.copy(alpha = 0.90f),
                                AlfaaCyan.copy(alpha = 0.15f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        vertical = 10.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                /* ================================================= */
                /* BRAND */
                /* ================================================= */

                SidebarBrand(
                    expanded = expanded
                )

                Spacer(
                    modifier = Modifier.height(9.dp)
                )

                NeonDivider()

                Spacer(
                    modifier = Modifier.height(9.dp)
                )

                /* ================================================= */
                /* NAVIGATION */
                /* ================================================= */

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {

                    SideBarShortcut(
                        icon = painterResource(
                            R.drawable.ic_video_settings
                        ),
                        label = stringResource(
                            R.string.game_menu_option_fps_settings
                        ),
                        expanded = expanded,
                        accent = AlfaaCyan,
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
                        accent = AlfaaGreen,
                        onClick = onVersionsClick
                    )

                    SideBarShortcut(
                        icon = painterResource(
                            R.drawable.ic_videocam_filled
                        ),
                        label = "Recordings",
                        expanded = expanded,
                        accent = AlfaaPurple,
                        onClick = onRecordingsClick
                    )

                    SideBarShortcut(
                        icon = painterResource(
                            R.drawable.ic_folder_filled
                        ),
                        label = "File Manager",
                        expanded = expanded,
                        accent = AlfaaCyan,
                        onClick = onFileManagerClick
                    )

                    SideBarShortcut(
                        icon = painterResource(
                            R.drawable.ic_group_filled
                        ),
                        label = "Multiplayer",
                        expanded = expanded,
                        accent = AlfaaGreen,
                        onClick = onMultiplayerClick
                    )

                    SideBarShortcut(
                        icon = painterResource(
                            R.drawable.ic_download_2_filled
                        ),
                        label = "Downloads",
                        expanded = expanded,
                        accent = AlfaaCyan,
                        onClick = onDownloadsClick
                    )

                    SideBarShortcut(
                        icon = painterResource(
                            R.drawable.ic_settings_filled
                        ),
                        label = "Settings",
                        expanded = expanded,
                        accent = AlfaaPurple,
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
                        accent = AlfaaGreen,
                        onClick = onInfoClick
                    )
                }

                /* ================================================= */
                /* THEME */
                /* ================================================= */

                ThemeToggle(
                    expanded = expanded
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                /* ================================================= */
                /* EXPAND / COLLAPSE */
                /* ================================================= */

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

/* ========================================================= */
/* BRAND */
/* ========================================================= */

@Composable
private fun SidebarBrand(
    expanded: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (expanded) {
                    13.dp
                } else {
                    8.dp
                }
            ),
        horizontalArrangement = if (expanded) {
            Arrangement.Start
        } else {
            Arrangement.Center
        },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            modifier = Modifier.size(36.dp),
            shape = RoundedCornerShape(11.dp),
            color = AlfaaCyan.copy(alpha = 0.08f),
            border = BorderStroke(
                1.dp,
                AlfaaCyan.copy(alpha = 0.40f)
            )
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A",
                    color = AlfaaCyan,
                    fontSize = 17.sp
                )
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(
                animationSpec = tween(150)
            ),
            exit = fadeOut(
                animationSpec = tween(100)
            )
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Spacer(
                    modifier = Modifier.width(9.dp)
                )

                Column {

                    Text(
                        text = "ALFAA",
                        color = AlfaaText,
                        fontSize = 13.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    )

                    Text(
                        text = "NEON LAUNCHER",
                        color = AlfaaGreen,
                        fontSize = 6.sp,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }
    }
}

/* ========================================================= */
/* DIVIDER */
/* ========================================================= */

@Composable
private fun NeonDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        AlfaaCyan.copy(alpha = 0.45f),
                        AlfaaGreen.copy(alpha = 0.65f),
                        Color.Transparent
                    )
                )
            )
    )
}

/* ========================================================= */
/* NAVIGATION ITEM */
/* ========================================================= */

@Composable
private fun SideBarShortcut(
    icon: Painter,
    label: String,
    expanded: Boolean,
    accent: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) {
            0.975f
        } else {
            1f
        },
        animationSpec = tween(
            durationMillis = 90
        ),
        label = "sidebarItemScale"
    )

    val background = if (pressed) {
        accent.copy(alpha = 0.15f)
    } else {
        AlfaaPanel2.copy(alpha = 0.72f)
    }

    val borderColor = if (pressed) {
        accent.copy(alpha = 0.75f)
    } else {
        AlfaaBorder.copy(alpha = 0.72f)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (expanded) {
                    7.dp
                } else {
                    5.dp
                }
            )
            .scale(scale)
            .clip(
                RoundedCornerShape(13.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(13.dp),
        color = background,
        contentColor = AlfaaText,
        border = BorderStroke(
            1.dp,
            borderColor
        ),
        shadowElevation = 0.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = if (expanded) {
                        10.dp
                    } else {
                        0.dp
                    },
                    vertical = 9.dp
                ),
            horizontalArrangement = if (expanded) {
                Arrangement.Start
            } else {
                Arrangement.Center
            },
            verticalAlignment = Alignment.CenterVertically
        ) {

            /*
             * ICON BOX
             */
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(
                        RoundedCornerShape(10.dp)
                    )
                    .background(
                        accent.copy(
                            alpha = if (pressed) {
                                0.18f
                            } else {
                                0.07f
                            }
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    painter = icon,
                    contentDescription = label,
                    modifier = Modifier.size(19.dp),
                    tint = if (pressed) {
                        Color.White
                    } else {
                        accent.copy(alpha = 0.90f)
                    }
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = 150
                    )
                ),
                exit = fadeOut(
                    animationSpec = tween(
                        durationMillis = 100
                    )
                )
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    Column {

                        Text(
                            text = label,
                            color = AlfaaText,
                            fontSize = 10.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                            maxLines = 1
                        )

                        Text(
                            text = "OPEN",
                            color = accent.copy(alpha = 0.75f),
                            fontSize = 6.sp,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }
        }
    }
}

/* ========================================================= */
/* TOGGLE */
/* ========================================================= */

@Composable
private fun SideBarToggle(
    expanded: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) {
            0.94f
        } else {
            1f
        },
        animationSpec = tween(
            durationMillis = 90
        ),
        label = "sidebarToggleScale"
    )

    Surface(
        modifier = Modifier
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
        color = if (pressed) {
            AlfaaCyan.copy(alpha = 0.18f)
        } else {
            AlfaaPanel2
        },
        border = BorderStroke(
            1.dp,
            if (pressed) {
                AlfaaCyan.copy(alpha = 0.75f)
            } else {
                AlfaaCyan.copy(alpha = 0.25f)
            }
        )
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
                color = if (pressed) {
                    Color.White
                } else {
                    AlfaaCyan
                },
                fontSize = 27.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Light
            )
        }
    }
}

/* ========================================================= */
/* THEME TOGGLE */
/* ========================================================= */

@Composable
private fun ThemeToggle(
    expanded: Boolean
) {
    val dark = isLauncherInDarkTheme()

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val pressed by interactionSource.collectIsPressedAsState()

    val accent = if (dark) {
        AlfaaCyan
    } else {
        AlfaaOrange
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (expanded) {
                    8.dp
                } else {
                    5.dp
                }
            )
            .clip(
                RoundedCornerShape(13.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {

                AllSettings.launcherDarkMode.save(
                    if (dark) {
                        DarkMode.Disable
                    } else {
                        DarkMode.Enable
                    }
                )
            },
        shape = RoundedCornerShape(13.dp),
        color = if (pressed) {
            accent.copy(alpha = 0.15f)
        } else {
            AlfaaPanel2
        },
        border = BorderStroke(
            1.dp,
            accent.copy(
                alpha = if (pressed) {
                    0.70f
                } else {
                    0.22f
                }
            )
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = if (expanded) {
                        10.dp
                    } else {
                        8.dp
                    },
                    vertical = 8.dp
                ),
            horizontalArrangement = if (expanded) {
                Arrangement.Start
            } else {
                Arrangement.Center
            },
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(
                        RoundedCornerShape(9.dp)
                    )
                    .background(
                        accent.copy(alpha = 0.10f)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = if (dark) {
                        "☾"
                    } else {
                        "☀"
                    },
                    color = accent,
                    fontSize = 15.sp
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(
                    animationSpec = tween(150)
                ),
                exit = fadeOut(
                    animationSpec = tween(100)
                )
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Spacer(
                        modifier = Modifier.width(9.dp)
                    )

                    Column {

                        Text(
                            text = if (dark) {
                                "DARK MODE"
                            } else {
                                "LIGHT MODE"
                            },
                            color = AlfaaText,
                            fontSize = 9.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )

                        Text(
                            text = "THEME",
                            color = accent.copy(alpha = 0.75f),
                            fontSize = 6.sp,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }
        }
    }
}
