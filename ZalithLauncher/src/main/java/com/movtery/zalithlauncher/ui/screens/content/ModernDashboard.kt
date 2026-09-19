package com.movtery.zalithlauncher.ui.screens.content

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.account.AccountsManager
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.ui.screens.content.elements.AccountAvatar
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionIconImage

private val Accent = Color(0xFF20E0B2)
private val AccentBlue = Color(0xFF43C7FF)
private val AccentPurple = Color(0xFFA78BFA)
private val AccentOrange = Color(0xFFFFB86B)

@Composable
fun ModernDashboard(
    modifier: Modifier = Modifier,
    onLaunchGame: (Version?) -> Unit,
    onAccountClick: () -> Unit,
    onVersionsClick: () -> Unit,
    onVersionSettingsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onFileManagerClick: () -> Unit,
    onMultiplayerClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onRecordingsClick: () -> Unit,
    onFpsClick: () -> Unit,
    onAboutClick: () -> Unit,
) {
    val account by AccountsManager.currentAccountFlow.collectAsStateWithLifecycle()
    val version by VersionsManager.currentVersion.collectAsStateWithLifecycle()
    val versions by VersionsManager.versions.collectAsStateWithLifecycle()

    val panel = MaterialTheme.colorScheme.surface
    val panelAlt = MaterialTheme.colorScheme.surfaceVariant
    val border = MaterialTheme.colorScheme.outlineVariant
    val muted = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Header(
    version = version,
    hasAccount = account != null,
    onSettingsClick = onSettingsClick,
    onFileManagerClick = onFileManagerClick,
    onMultiplayerClick = onMultiplayerClick,
    onDownloadClick = onDownloadClick,
    onRecordingsClick = onRecordingsClick,
    onFpsClick = onFpsClick,
    onAboutClick = onAboutClick
)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {

            /*
             * LEFT / MAIN AREA
             */
            Column(
                modifier = Modifier.weight(1.62f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                HeroCard(
                    accountName = account?.username,
                    version = version,
                    onLaunchGame = onLaunchGame
                )

                QuickActions(
    panel = panelAlt,
    border = border,
    onVersionsClick = onVersionsClick,
    onAccountClick = onAccountClick,
    onVersionSettingsClick = onVersionSettingsClick,
    onSettingsClick = onSettingsClick
)
            }

            /*
             * RIGHT / SIDEBAR AREA
             */
            Column(
                modifier = Modifier.weight(.76f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                /*
                 * Hanya SATU area akun.
                 */
                AccountCard(
                    accountName = account?.username,
                    hasAccount = account != null,
                    panel = panel,
                    border = border,
                    muted = muted,
                    onAccountClick = onAccountClick
                ) {
                    AccountAvatar(
                        account = account,
                        avatarSize = 48.dp,
                        onClick = onAccountClick
                    )
                }

                InstancesCard(
                    versions = versions,
                    selected = version,
                    panel = panel,
                    border = border,
                    muted = muted,
                    onVersionsClick = onVersionsClick
                )

                SessionCard(
                    panel = panelAlt,
                    border = border,
                    muted = muted,
                    version = version,
                    onClick = onVersionSettingsClick
                )
            }
        }

        /*
         * FOOTER
         */
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "// PLAY  •  EXPLORE  •  CREATE",
                color = AccentBlue,
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                letterSpacing = .4.sp
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = "TikTok: @alfathgpp",
                color = muted,
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp
            )
        }
    }
}


/* ========================================================= */
/* HEADER */
/* ========================================================= */

@Composable
private fun Header(
    version: Version?,
    hasAccount: Boolean,
    onSettingsClick: () -> Unit,
    onFileManagerClick: () -> Unit,
    onMultiplayerClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onRecordingsClick: () -> Unit,
    onFpsClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    var menuExpanded by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        /*
         * NEON MENU BUTTON
         */
        Box {

            Surface(
                modifier = Modifier
                    .size(44.dp)
                    .clickable {
                        menuExpanded = !menuExpanded
                    },
                shape = RoundedCornerShape(15.dp),
                color = Accent.copy(alpha = .10f),
                border = BorderStroke(
                    1.dp,
                    Accent.copy(alpha = .40f)
                ),
                shadowElevation = 8.dp
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "☰",
                        color = Accent,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = {
                    menuExpanded = false
                }
            ) {

                NeonMenuItem(
                    "FPS / PERFORMANCE",
                    onClick = {
                        menuExpanded = false
                        onFpsClick()
                    }
                )

                NeonMenuItem(
                    "RECORDINGS",
                    onClick = {
                        menuExpanded = false
                        onRecordingsClick()
                    }
                )

                NeonMenuItem(
                    "FILE MANAGER",
                    onClick = {
                        menuExpanded = false
                        onFileManagerClick()
                    }
                )

                NeonMenuItem(
                    "MULTIPLAYER",
                    onClick = {
                        menuExpanded = false
                        onMultiplayerClick()
                    }
                )

                NeonMenuItem(
                    "DOWNLOADS",
                    onClick = {
                        menuExpanded = false
                        onDownloadClick()
                    }
                )

                NeonMenuItem(
                    "SETTINGS",
                    onClick = {
                        menuExpanded = false
                        onSettingsClick()
                    }
                )

                NeonMenuItem(
                    "ABOUT",
                    onClick = {
                        menuExpanded = false
                        onAboutClick()
                    }
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "DASHBOARD",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = .7.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Accent.copy(alpha = .10f),
                    border = BorderStroke(
                        1.dp,
                        Accent.copy(alpha = .35f)
                    )
                ) {

                    Text(
                        text = "ZL2+",
                        modifier = Modifier.padding(
                            horizontal = 9.dp,
                            vertical = 4.dp
                        ),
                        color = Accent,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp
                    )
                }

                Spacer(Modifier.width(7.dp))

                Text(
                    text = version?.getVersionName()
                        ?: "NO INSTANCE",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp
                )
            }
        }

        /*
         * STATUS
         */
        Surface(
            shape = RoundedCornerShape(15.dp),
            color = if (hasAccount)
                Accent.copy(alpha = .10f)
            else
                MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(
                1.dp,
                if (hasAccount)
                    Accent.copy(alpha = .35f)
                else
                    MaterialTheme.colorScheme.outlineVariant
            ),
            shadowElevation = 7.dp
        ) {

            Row(
                modifier = Modifier.padding(
                    horizontal = 11.dp,
                    vertical = 8.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (hasAccount)
                                Accent
                            else
                                MaterialTheme.colorScheme
                                    .onSurfaceVariant
                        )
                )

                Spacer(Modifier.width(7.dp))

                Text(
                    text = if (hasAccount)
                        "READY"
                    else
                        "OFFLINE",
                    color = if (hasAccount)
                        Accent
                    else
                        MaterialTheme.colorScheme
                            .onSurfaceVariant,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp
                )
            }
        }
    }
}


@Composable
private fun NeonMenuItem(
    text: String,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(
                text = text,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        onClick = onClick
    )
}


/* ========================================================= */
/* HERO */
/* ========================================================= */

@Composable
private fun HeroCard(
    accountName: String?,
    version: Version?,
    onLaunchGame: (Version?) -> Unit
) {
    val shape = RoundedCornerShape(28.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(245.dp)
            .clip(shape)
            .border(
                BorderStroke(
                    1.dp,
                    Color.White.copy(alpha = .08f)
                ),
                shape
            )
    ) {

        /*
         * BACKGROUND
         */
        Image(
            painter = painterResource(
                R.drawable.img_star1xr
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        /*
         * DARK GRADIENT
         */
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xF4070D12),
                            Color(0xD0070D12),
                            Color(0x66070D12),
                            Color(0x18070D12)
                        )
                    )
                )
        )

        /*
         * BOTTOM VIGNETTE
         */
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color(0x77000000)
                        )
                    )
                )
        )

        /*
         * HERO TEXT
         */
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(
                    start = 24.dp,
                    end = 220.dp
                ),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Surface(
                shape = RoundedCornerShape(50),
                color = Accent.copy(alpha = .12f),
                border = BorderStroke(
                    1.dp,
                    Accent.copy(alpha = .35f)
                )
            ) {

                Text(
                    text = "JAVA EDITION  •  ${
                        version?.getVersionSummary()
                            ?: "NO INSTANCE"
                    }",
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 5.dp
                    ),
                    color = Accent,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp,
                    letterSpacing = .5.sp
                )
            }

            Text(
                text = "WELCOME BACK",
                color = Color.White.copy(alpha = .64f),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 1.1.sp
            )

            Text(
                text = accountName ?: "PLAYER",
                color = Color.White,
                fontSize = 31.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = version?.let {
                    "${it.getVersionName()}  •  ${it.getVersionSummary()}"
                } ?: "Pilih atau install instance untuk mulai bermain.",
                color = Color.White.copy(alpha = .78f),
                fontSize = 11.sp,
                lineHeight = 16.sp,
                maxLines = 2
            )
        }

        /*
         * PLAY BUTTON
         */
        Surface(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 18.dp)
                .clickable {
    if (version != null) {
        onLaunchGame(version)
    }
                },
            shape = RoundedCornerShape(20.dp),
            color = Accent,
            shadowElevation = 12.dp
        ) {

            Row(
                modifier = Modifier.padding(
                    horizontal = 28.dp,
                    vertical = 17.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    painter = painterResource(
                        R.drawable.ic_play_arrow_filled
                    ),
                    contentDescription = null,
                    tint = Color(0xFF07100E),
                    modifier = Modifier.size(24.dp)
                )

                Spacer(Modifier.width(10.dp))

                Column {

                    Text(
                        text = "PLAY",
                        color = Color(0xFF07100E),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = .6.sp
                    )

                    Text(
                        text = if (version == null)
                            "Pilih instance"
                        else
                            "Launch instance",
                        color = Color(0xFF16453B),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp
                    )
                }
            }
        }
    }
}


/* ========================================================= */
/* ACCOUNT */
/* ========================================================= */

@Composable
private fun AccountCard(
    accountName: String?,
    hasAccount: Boolean,
    panel: Color,
    border: Color,
    muted: Color,
    onAccountClick: () -> Unit,
    avatar: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onAccountClick
            ),
        shape = RoundedCornerShape(24.dp),
        color = panel,
        border = BorderStroke(
            1.dp,
            border.copy(alpha = .85f)
        ),
        shadowElevation = 6.dp
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            SectionTitle("ACTIVE ACCOUNT")

            Spacer(Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (hasAccount) {

                    avatar()

                } else {

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Accent.copy(alpha = .10f),
                        border = BorderStroke(
                            1.dp,
                            Accent.copy(alpha = .28f)
                        )
                    ) {

                        Box(
                            modifier = Modifier.size(48.dp),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = "+",
                                color = Accent,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Light
                            )
                        }
                    }
                }

                Spacer(Modifier.width(11.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = accountName ?: "Tambahkan Akun",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (hasAccount)
                            "Microsoft / active"
                        else
                            "Sign in / offline profile",
                        color = if (hasAccount)
                            Accent
                        else
                            muted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp
                    )
                }
            }
        }
    }
}


/* ========================================================= */
/* INSTANCES */
/* ========================================================= */

@Composable
private fun InstancesCard(
    versions: List<Version>,
    selected: Version?,
    panel: Color,
    border: Color,
    muted: Color,
    onVersionsClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = panel,
        border = BorderStroke(
            1.dp,
            border.copy(alpha = .85f)
        )
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                SectionTitle("INSTANCES")

                Spacer(Modifier.weight(1f))

                Surface(
                    modifier = Modifier.clickable(
                        onClick = onVersionsClick
                    ),
                    shape = RoundedCornerShape(50),
                    color = Accent.copy(alpha = .10f),
                    border = BorderStroke(
                        1.dp,
                        Accent.copy(alpha = .28f)
                    )
                ) {

                    Text(
                        text = "+ NEW",
                        modifier = Modifier.padding(
                            horizontal = 9.dp,
                            vertical = 5.dp
                        ),
                        color = Accent,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            /*
             * HORIZONTAL SCROLL
             */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(
                        rememberScrollState()
                    ),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                versions
                    .take(8)
                    .forEach { item ->

                        InstanceChip(
                            version = item,
                            selected = item == selected,
                            muted = muted,
                            onClick = onVersionsClick
                        )
                    }

                AddInstanceChip(
                    onClick = onVersionsClick
                )
            }
        }
    }
}


@Composable
private fun InstanceChip(
    version: Version,
    selected: Boolean,
    muted: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(17.dp)

    Surface(
        modifier = Modifier
            .width(170.dp)
            .height(74.dp)
            .clickable(
                onClick = onClick
            ),
        shape = shape,
        color = if (selected)
            Accent.copy(alpha = .09f)
        else
            Color(0xFF172128),
        border = BorderStroke(
            1.dp,
            if (selected)
                Accent.copy(alpha = .42f)
            else
                Color.White.copy(alpha = .06f)
        )
    ) {

        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            VersionIconImage(
                version = version,
                modifier = Modifier.size(48.dp)
            )

            Spacer(Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = version.getVersionName(),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = version.getVersionSummary(),
                    color = if (selected)
                        Accent
                    else
                        muted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.sp,
                    maxLines = 1
                )
            }
        }
    }
}


@Composable
private fun AddInstanceChip(
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(110.dp)
            .height(74.dp)
            .clickable(
                onClick = onClick
            ),
        shape = RoundedCornerShape(17.dp),
        color = Color(0xFF172128),
        border = BorderStroke(
            1.dp,
            Accent.copy(alpha = .20f)
        )
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "+",
                color = Accent,
                fontSize = 26.sp,
                fontWeight = FontWeight.Light
            )

            Text(
                text = "NEW INSTANCE",
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontSize = 7.sp,
                letterSpacing = .4.sp
            )
        }
    }
}


/* ========================================================= */
/* QUICK ACTIONS */
/* ========================================================= */

@Composable
private fun QuickActions(
    panel: Color,
    border: Color,
    onVersionsClick: () -> Unit,
    onAccountClick: () -> Unit,
    onVersionSettingsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = panel,
        border = BorderStroke(
            1.dp,
            border.copy(alpha = .85f)
        )
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                SectionTitle("QUICK ACTIONS")

                Spacer(Modifier.weight(1f))

                Text(
                    text = "SWIPE →",
                    color = Accent,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(10.dp))

            /*
             * INI YANG MEMPERBAIKI BUG QUICK ACTION
             */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(
                        rememberScrollState()
                    ),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                ActionCard(
                    title = "VERSIONS",
                    subtitle = "Manage instances",
                    icon = R.drawable.ic_assignment_filled,
                    accent = Accent,
                    onClick = onVersionsClick
                )

                ActionCard(
                    title = "ACCOUNT",
                    subtitle = "Manage profile",
                    icon = R.drawable.ic_person_outlined,
                    accent = AccentBlue,
                    onClick = onAccountClick
                )

                ActionCard(
                    title = "INSTANCE",
                    subtitle = "Edit selected",
                    icon = R.drawable.ic_settings_filled,
                    accent = AccentPurple,
                    onClick = onVersionSettingsClick
                )

                ActionCard(
                    title = "SETTINGS",
                    subtitle = "Launcher options",
                    icon = R.drawable.ic_settings_filled,
                    accent = AccentOrange,
                    onClick = onVersionSettingsClick
                )
            }
        }
    }
}


@Composable
ActionCard(
    title = "SETTINGS",
    subtitle = "Launcher options",
    icon = R.drawable.ic_settings_filled,
    accent = AccentOrange,
    onClick = onSettingsClick
) {
    Surface(
        modifier = Modifier
            .width(150.dp)
            .height(96.dp)
            .clickable(
                onClick = onClick
            ),
        shape = RoundedCornerShape(19.dp),
        color = Color(0xFF141D24),
        border = BorderStroke(
            1.dp,
            accent.copy(alpha = .16f)
        ),
        shadowElevation = 4.dp
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = accent.copy(alpha = .10f),
                border = BorderStroke(
                    1.dp,
                    accent.copy(alpha = .22f)
                )
            ) {

                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(17.dp)
                )
            }

            Spacer(Modifier.height(9.dp))

            Text(
                text = title,
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                letterSpacing = .5.sp
            )

            Text(
                text = subtitle,
                color = Color.White.copy(alpha = .55f),
                fontSize = 8.sp
            )
        }
    }
}


/* ========================================================= */
/* SESSION */
/* ========================================================= */

@Composable
private fun SessionCard(
    panel: Color,
    border: Color,
    muted: Color,
    version: Version?,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            ),
        shape = RoundedCornerShape(24.dp),
        color = panel,
        border = BorderStroke(
            1.dp,
            border.copy(alpha = .75f)
        )
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 13.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = painterResource(
                    R.drawable.ic_assignment_filled
                ),
                contentDescription = null,
                tint = Accent,
                modifier = Modifier.size(18.dp)
            )

            Spacer(Modifier.width(9.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "SESSION",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    letterSpacing = .7.sp
                )

                Text(
                    text = version?.getVersionName()
                        ?: "No active instance",
                    color = muted,
                    fontSize = 8.sp
                )
            }

            Text(
                text = "›",
                color = muted,
                fontSize = 20.sp
            )
        }
    }
}


/* ========================================================= */
/* SECTION TITLE */
/* ========================================================= */

@Composable
private fun SectionTitle(
    text: String
) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurface,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        letterSpacing = 1.1.sp
    )
}
