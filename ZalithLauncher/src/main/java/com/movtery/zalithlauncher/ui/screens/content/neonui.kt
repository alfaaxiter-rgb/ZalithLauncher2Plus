package com.movtery.zalithlauncher.ui.screens.content

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.Icons.Default.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.setting.enums.DarkMode
import com.movtery.zalithlauncher.setting.enums.isLauncherInDarkTheme

/*
 * ============================================================
 * ALFAA UI / NEON DESIGN SYSTEM
 * ============================================================
 *
 * UI baru launcher.
 *
 * IMPORTANT:
 * File ini hanya menangani VISUAL.
 * Jangan taruh logic launcher / game / account di sini.
 *
 * Semua logic tetap berada di manager/viewmodel Zalith.
 */

/* ============================================================
 * COLORS
 * ============================================================ */

object NeonColors {

    val Cyan = Color(0xFF20E0B2)
    val CyanBright = Color(0xFF39FFD0)

    val Blue = Color(0xFF43C7FF)
    val Purple = Color(0xFFA78BFA)
    val Orange = Color(0xFFFFB86B)
    val Red = Color(0xFFFF5C7A)

    val DarkBackground = Color(0xFF080B10)
    val DarkSurface = Color(0xFF0F141B)
    val DarkSurface2 = Color(0xFF141B23)
    val DarkSurface3 = Color(0xFF19222C)

    val LightBackground = Color(0xFFF3F6FA)
    val LightSurface = Color(0xFFFFFFFF)
    val LightSurface2 = Color(0xFFE9EEF5)

    val TextDark = Color(0xFFF4F7FA)
    val TextMutedDark = Color(0xFF8D99A8)

    val TextLight = Color(0xFF151A20)
    val TextMutedLight = Color(0xFF66717F)
}

/* ============================================================
 * HELPERS
 * ============================================================ */

@Composable
fun neonBackgroundColor(): Color {
    return if (isLauncherInDarkTheme()) {
        NeonColors.DarkBackground
    } else {
        NeonColors.LightBackground
    }
}

@Composable
fun neonSurfaceColor(): Color {
    return if (isLauncherInDarkTheme()) {
        NeonColors.DarkSurface
    } else {
        NeonColors.LightSurface
    }
}

@Composable
fun neonSurfaceSecondaryColor(): Color {
    return if (isLauncherInDarkTheme()) {
        NeonColors.DarkSurface2
    } else {
        NeonColors.LightSurface2
    }
}

@Composable
fun neonTextColor(): Color {
    return if (isLauncherInDarkTheme()) {
        NeonColors.TextDark
    } else {
        NeonColors.TextLight
    }
}

@Composable
fun neonMutedColor(): Color {
    return if (isLauncherInDarkTheme()) {
        NeonColors.TextMutedDark
    } else {
        NeonColors.TextMutedLight
    }
}

/* ============================================================
 * ROOT BACKGROUND
 * ============================================================ */

@Composable
fun NeonBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(neonBackgroundColor())
    ) {

        /*
         * Subtle neon atmosphere.
         *
         * Tidak terlalu terang supaya UI tetap nyaman.
         */
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            NeonColors.Cyan.copy(alpha = 0.055f),
                            Color.Transparent
                        ),
                        radius = 850f
                    )
                )
        )

        content()
    }
}

/* ============================================================
 * NEON CARD
 * ============================================================ */

@Composable
fun NeonCard(
    modifier: Modifier = Modifier,
    accent: Color = NeonColors.Cyan,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(22.dp)

    Surface(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        enabled = enabled,
                        interactionSource = remember {
                            MutableInteractionSource()
                        },
                        indication = null,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
            .shadow(
                elevation = if (enabled) 8.dp else 0.dp,
                shape = shape,
                ambientColor = accent.copy(alpha = 0.16f),
                spotColor = accent.copy(alpha = 0.20f)
            ),
        shape = shape,
        color = neonSurfaceColor(),
        border = BorderStroke(
            1.dp,
            accent.copy(
                alpha = if (enabled) 0.20f else 0.08f
            )
        )
    ) {
        content()
    }
}

/* ============================================================
 * NEON BUTTON
 * ============================================================ */

@Composable
fun NeonButton(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color = NeonColors.Cyan,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(17.dp)

    Surface(
        modifier = modifier
            .shadow(
                elevation = if (enabled) 9.dp else 0.dp,
                shape = shape,
                ambientColor = accent.copy(alpha = 0.25f),
                spotColor = accent.copy(alpha = 0.30f)
            )
            .clickable(
                enabled = enabled,
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null,
                onClick = onClick
            ),
        shape = shape,
        color = if (enabled) {
            accent.copy(alpha = 0.14f)
        } else {
            neonSurfaceSecondaryColor()
        },
        border = BorderStroke(
            1.dp,
            if (enabled) {
                accent.copy(alpha = 0.60f)
            } else {
                neonMutedColor().copy(alpha = 0.12f)
            }
        )
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 18.dp,
                vertical = 12.dp
            ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) {
                        accent
                    } else {
                        neonMutedColor()
                    },
                    modifier = Modifier.size(18.dp)
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )
            }

            Text(
                text = text,
                color = if (enabled) {
                    neonTextColor()
                } else {
                    neonMutedColor()
                },
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 0.4.sp
            )
        }
    }
}

/* ============================================================
 * NEON PRIMARY BUTTON
 * ============================================================ */

@Composable
fun NeonPrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)

    Surface(
        modifier = modifier
            .shadow(
                elevation = 14.dp,
                shape = shape,
                ambientColor = NeonColors.Cyan.copy(
                    alpha = if (enabled) 0.32f else 0f
                ),
                spotColor = NeonColors.Cyan.copy(
                    alpha = if (enabled) 0.42f else 0f
                )
            )
            .clickable(
                enabled = enabled,
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null,
                onClick = onClick
            ),
        shape = shape,
        color = if (enabled) {
            NeonColors.Cyan
        } else {
            neonSurfaceSecondaryColor()
        },
        border = BorderStroke(
            1.dp,
            if (enabled) {
                NeonColors.CyanBright.copy(alpha = 0.9f)
            } else {
                neonMutedColor().copy(alpha = 0.15f)
            }
        )
    ) {

        Box(
            modifier = Modifier
                .padding(
                    horizontal = 26.dp,
                    vertical = 15.dp
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = text,
                color = if (enabled) {
                    Color(0xFF06110E)
                } else {
                    neonMutedColor()
                },
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                letterSpacing = 0.8.sp
            )
        }
    }
}

/* ============================================================
 * NEON ICON BUTTON
 * ============================================================ */

@Composable
fun NeonIconButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    accent: Color = NeonColors.Cyan,
    contentDescription: String? = null,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(15.dp)

    Surface(
        modifier = modifier
            .size(44.dp)
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = accent.copy(alpha = 0.20f),
                spotColor = accent.copy(alpha = 0.24f)
            )
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null,
                onClick = onClick
            ),
        shape = shape,
        color = accent.copy(alpha = 0.09f),
        border = BorderStroke(
            1.dp,
            accent.copy(alpha = 0.42f)
        )
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = accent,
                modifier = Modifier.size(21.dp)
            )
        }
    }
}

/* ============================================================
 * NEON SECTION TITLE
 * ============================================================ */

@Composable
fun NeonSectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color = NeonColors.Cyan
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .width(3.dp)
                .height(17.dp)
                .clip(RoundedCornerShape(50))
                .background(accent)
        )

        Spacer(
            modifier = Modifier.width(9.dp)
        )

        Text(
            text = text.uppercase(),
            color = neonTextColor(),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp
        )
    }
}

/* ============================================================
 * NEON SETTING ROW
 * ============================================================ */

@Composable
fun NeonSettingRow(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    accent: Color = NeonColors.Cyan,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    NeonCard(
        modifier = modifier.fillMaxWidth(),
        accent = accent,
        onClick = onClick
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 14.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    color = neonTextColor(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                if (description != null) {

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = description,
                        color = neonMutedColor(),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        lineHeight = 13.sp
                    )
                }
            }

            if (trailing != null) {

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                trailing()
            }
        }
    }
}

/* ============================================================
 * NEON SWITCH
 * ============================================================ */

@Composable
fun NeonSwitch(
    checked: Boolean,
    modifier: Modifier = Modifier,
    accent: Color = NeonColors.Cyan,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color(0xFF07100E),
            checkedTrackColor = accent,
            checkedBorderColor = accent.copy(alpha = 0.85f),
            uncheckedThumbColor = neonMutedColor(),
            uncheckedTrackColor = neonSurfaceSecondaryColor(),
            uncheckedBorderColor = neonMutedColor().copy(
                alpha = 0.35f
            )
        )
    )
}

/* ============================================================
 * NEON SLIDER
 * ============================================================ */

@Composable
fun NeonSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = NeonColors.Cyan,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        modifier = modifier.fillMaxWidth()
    )
}

/* ============================================================
 * NEON VALUE CHIP
 * ============================================================ */

@Composable
fun NeonValueChip(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color = NeonColors.Cyan
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = accent.copy(alpha = 0.10f),
        border = BorderStroke(
            1.dp,
            accent.copy(alpha = 0.34f)
        )
    ) {

        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 11.dp,
                vertical = 6.dp
            ),
            color = accent,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 8.sp
        )
    }
}

/* ============================================================
 * NEON DIVIDER
 * ============================================================ */

@Composable
fun NeonDivider(
    modifier: Modifier = Modifier,
    accent: Color = NeonColors.Cyan
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        accent.copy(alpha = 0.45f),
                        Color.Transparent
                    )
                )
            )
    )
}

/* ============================================================
 * THEME TOGGLE
 * ============================================================ */

@Composable
fun NeonThemeToggle(
    modifier: Modifier = Modifier
) {
    val dark = isLauncherInDarkTheme()

    var changing by remember {
        mutableStateOf(false)
    }

    NeonCard(
        modifier = modifier,
        accent = if (dark) {
            NeonColors.Blue
        } else {
            NeonColors.Orange
        },
        onClick = {
            if (!changing) {

                changing = true

                AllSettings.launcherDarkMode.save(
                    if (dark) {
                        DarkMode.Disable
                    } else {
                        DarkMode.Enable
                    }
                )

                changing = false
            }
        }
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 13.dp,
                vertical = 10.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = if (dark) "☾" else "☀",
                color = if (dark) {
                    NeonColors.Blue
                } else {
                    NeonColors.Orange
                },
                fontSize = 17.sp
            )

            Column {

                Text(
                    text = if (dark) {
                        "DARK MODE"
                    } else {
                        "LIGHT MODE"
                    },
                    color = neonTextColor(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )

                Text(
                    text = "Tap to switch",
                    color = neonMutedColor(),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 7.sp
                )
            }
        }
    }
}

/* ============================================================
 * NEON TOP BAR
 * ============================================================ */

@Composable
fun NeonTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    accent: Color = NeonColors.Cyan,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (onBack != null) {

            NeonIconButton(
                icon = androidx.compose.material.icons.Icons.Default.ArrowBack,
                accent = accent,
                contentDescription = "Back",
                onClick = onBack
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                color = neonTextColor(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 21.sp,
                letterSpacing = 0.5.sp
            )

            if (subtitle != null) {

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = subtitle,
                    color = neonMutedColor(),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.sp
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = actions
        )
    }
}

/* ============================================================
 * NEON STATUS DOT
 * ============================================================ */

@Composable
fun NeonStatusDot(
    active: Boolean,
    modifier: Modifier = Modifier,
    activeColor: Color = NeonColors.Cyan
) {
    Box(
        modifier = modifier
            .size(9.dp)
            .clip(CircleShape)
            .background(
                if (active) {
                    activeColor
                } else {
                    neonMutedColor().copy(alpha = 0.45f)
                }
            )
    )
}

/* ============================================================
 * NEON EMPTY STATE
 * ============================================================ */

@Composable
fun NeonEmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    accent: Color = NeonColors.Cyan,
    action: (@Composable () -> Unit)? = null
) {
    NeonCard(
        modifier = modifier.fillMaxWidth(),
        accent = accent
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        accent.copy(alpha = 0.10f)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "＋",
                    color = accent,
                    fontSize = 25.sp
                )
            }

            Text(
                text = title,
                color = neonTextColor(),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Text(
                text = description,
                color = neonMutedColor(),
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp
            )

            if (action != null) {

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                action()
            }
        }
    }
}
