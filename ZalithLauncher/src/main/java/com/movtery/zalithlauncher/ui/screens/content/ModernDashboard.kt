package com.movtery.zalithlauncher.ui.screens.content

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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

/* ========================================================= */
/* ALFAA NEON PALETTE */
/* ========================================================= */

private val AlfaaBlack = Color(0xFF03070B)
private val AlfaaPanel = Color(0xFF071019)
private val AlfaaPanel2 = Color(0xFF0B1620)
private val AlfaaPanel3 = Color(0xFF101E29)

private val AlfaaCyan = Color(0xFF43C7FF)
private val AlfaaGreen = Color(0xFF20E0B2)
private val AlfaaPurple = Color(0xFFA78BFA)
private val AlfaaOrange = Color(0xFFFFB454)
private val AlfaaRed = Color(0xFFFF5577)

private val AlfaaText = Color(0xFFE8FFF8)
private val AlfaaMuted = Color(0xFF829A98)
private val AlfaaBorder = Color(0xFF16483F)

/* ========================================================= */
/* MAIN DASHBOARD */
/* ========================================================= */

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

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val compact = maxWidth < 700.dp

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                AlfaaCyan.copy(alpha = .45f),
                                AlfaaGreen.copy(alpha = .65f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                DashboardTitleBar(version = version, hasAccount = account != null)

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(if (compact) 1.45f else 1.7f),
                        verticalArrangement = Arrangement.spacedBy(9.dp)
                    ) {
                        HeroCard(
                            modifier = Modifier.weight(1f),
                            accountName = account?.username,
                            version = version,
                            onLaunchGame = onLaunchGame
                        )
                        QuickActions(
                            panel = AlfaaPanel2,
                            border = AlfaaBorder,
                            onVersionsClick = onVersionsClick,
                            onAccountClick = onAccountClick,
                            onVersionSettingsClick = onVersionSettingsClick,
                            onSettingsClick = onSettingsClick
                        )
                    }

                    Column(
                        modifier = Modifier.weight(if (compact) .95f else .9f),
                        verticalArrangement = Arrangement.spacedBy(9.dp)
                    ) {
                        AccountCard(
                            accountName = account?.username,
                            hasAccount = account != null,
                            onAccountClick = onAccountClick,
                            avatar = {
                                AccountAvatar(
                                    account = account,
                                    avatarSize = 48.dp,
                                    onClick = onAccountClick
                                )
                            }
                        )
                        InstancesCard(
                            versions = versions,
                            selected = version,
                            muted = AlfaaMuted,
                            onVersionsClick = onVersionsClick
                        )
                        ResourceModsCard(
                            version = version,
                            onClick = onVersionSettingsClick
                        )
                    }
                }

                FooterBar()
            }
        }
    }
}

/* ========================================================= */
/* GENERIC NEON PANEL */
/* ========================================================= */

@Composable
private fun NeonPanel(
    modifier: Modifier = Modifier,
    accent: Color,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(19.dp)

    Box(
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                shape = shape,
                ambientColor = accent.copy(alpha = .16f),
                spotColor = accent.copy(alpha = .22f)
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        AlfaaPanel3,
                        AlfaaPanel2,
                        AlfaaPanel
                    )
                )
            )
            .border(
                width = 1.dp,
                color = accent.copy(alpha = .28f),
                shape = shape
            )
            .padding(11.dp)
    ) {
        content()
    }
}

/* ========================================================= */
/* TITLE BAR */
/* ========================================================= */

@Composable
private fun DashboardTitleBar(
    version: Version?,
    hasAccount: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 4.dp,
                vertical = 2.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(25.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    AlfaaCyan,
                                    AlfaaGreen
                                )
                            )
                        )
                )

                Spacer(Modifier.width(9.dp))

                Column {

                    Text(
                        text = "ALFAA // LAUNCH",
                        color = AlfaaText,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = version?.getVersionName()
                            ?: "NO INSTANCE SELECTED",
                        color = AlfaaMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        letterSpacing = .5.sp
                    )
                }
            }
        }

        NeonStatus(
            active = hasAccount
        )
    }
}

/* ========================================================= */
/* STATUS */
/* ========================================================= */

@Composable
private fun NeonStatus(
    active: Boolean
) {
    val accent = if (active) AlfaaGreen else AlfaaRed

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(13.dp))
            .background(accent.copy(alpha = .08f))
            .border(
                1.dp,
                accent.copy(alpha = .32f),
                RoundedCornerShape(13.dp)
            )
            .padding(
                horizontal = 10.dp,
                vertical = 7.dp
            )
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(RoundedCornerShape(50))
                    .background(accent)
            )

            Spacer(Modifier.width(7.dp))

            Text(
                text = if (active) "ONLINE" else "OFFLINE",
                color = accent,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 8.sp,
                letterSpacing = .7.sp
            )
        }
    }
}

/* ========================================================= */
/* HERO */
/* ========================================================= */

@Composable
private fun HeroCard(
    modifier: Modifier = Modifier,
    accountName: String?,
    version: Version?,
    onLaunchGame: (Version?) -> Unit
) {
    val shape = RoundedCornerShape(23.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 190.dp)
            .shadow(
                elevation = 15.dp,
                shape = shape,
                ambientColor = AlfaaCyan.copy(alpha = .18f),
                spotColor = AlfaaCyan.copy(alpha = .25f)
            )
            .clip(shape)
            .border(
                1.dp,
                AlfaaCyan.copy(alpha = .30f),
                shape
            )
    ) {

        Image(
            painter = painterResource(
                R.drawable.img_star1xr
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        /* dark overlay */
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xF903070B),
                            Color(0xE5070E15),
                            Color(0x990A131C),
                            Color(0x4008131A)
                        )
                    )
                )
        )

        /* cyan/green atmospheric glow */
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            AlfaaCyan.copy(alpha = .07f),
                            Color.Transparent,
                            AlfaaGreen.copy(alpha = .10f)
                        )
                    )
                )
        )

        /* top neon line */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            AlfaaCyan,
                            AlfaaGreen,
                            Color.Transparent
                        )
                    )
                )
        )

        /* content */
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(
                    start = 22.dp,
                    end = 205.dp
                ),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        AlfaaCyan.copy(alpha = .10f)
                    )
                    .border(
                        1.dp,
                        AlfaaCyan.copy(alpha = .38f),
                        RoundedCornerShape(50)
                    )
                    .padding(
                        horizontal = 9.dp,
                        vertical = 5.dp
                    )
            ) {
                Text(
                    text = "JAVA // ${
                        version?.getVersionSummary()
                            ?: "NO INSTANCE"
                    }",
                    color = AlfaaCyan,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp,
                    letterSpacing = .6.sp
                )
            }

            Text(
                text = "WELCOME BACK",
                color = AlfaaGreen,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                letterSpacing = 1.4.sp
            )

            Text(
                text = accountName ?: "PLAYER",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1
            )

            Text(
                text = version?.let {
                    "${it.getVersionName()}  •  ${it.getVersionSummary()}"
                } ?: "Pilih instance atau install versi Minecraft untuk mulai bermain.",
                color = Color.White.copy(alpha = .72f),
                fontSize = 10.sp,
                lineHeight = 15.sp,
                maxLines = 3
            )
        }

        /* play button */
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 15.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(18.dp),
                    ambientColor = AlfaaGreen.copy(alpha = .35f),
                    spotColor = AlfaaGreen.copy(alpha = .45f)
                )
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF39F2C2),
                            AlfaaGreen
                        )
                    )
                )
                .border(
                    1.dp,
                    Color.White.copy(alpha = .38f),
                    RoundedCornerShape(18.dp)
                )
                .clickable {
                    if (version != null) {
                        onLaunchGame(version)
                    }
                }
                .padding(
                    horizontal = 21.dp,
                    vertical = 17.dp
                )
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    painter = painterResource(
                        R.drawable.ic_play_arrow_filled
                    ),
                    contentDescription = null,
                    tint = Color(0xFF03100D),
                    modifier = Modifier.size(25.dp)
                )

                Spacer(Modifier.width(8.dp))

                Column {

                    Text(
                        text = "PLAY",
                        color = Color(0xFF03100D),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = .8.sp
                    )

                    Text(
                        text = if (version == null)
                            "SELECT INSTANCE"
                        else
                            "LAUNCH GAME",
                        color = Color(0xFF155447),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold
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
    onAccountClick: () -> Unit,
    avatar: @Composable () -> Unit
) {
    NeonPanel(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onAccountClick),
        accent = AlfaaGreen
    ) {

        SectionTitle(
            text = "ACTIVE ACCOUNT",
            accent = AlfaaGreen
        )

        Spacer(Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (hasAccount) {
                avatar()
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            AlfaaGreen.copy(alpha = .08f)
                        )
                        .border(
                            1.dp,
                            AlfaaGreen.copy(alpha = .30f),
                            RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        color = AlfaaGreen,
                        fontSize = 27.sp
                    )
                }
            }

            Spacer(Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = accountName ?: "ADD ACCOUNT",
                    color = AlfaaText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Text(
                    text = if (hasAccount)
                        "MICROSOFT // ACTIVE"
                    else
                        "SIGN IN // OFFLINE",
                    color = if (hasAccount)
                        AlfaaGreen
                    else
                        AlfaaMuted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 7.sp,
                    letterSpacing = .3.sp
                )
            }
        }
    }
}

/* ========================================================= */
/* INSTANCES */
/* ========================================================= */

@Composable
private fun InstancesCard(
    modifier: Modifier = Modifier,
    versions: List<Version>,
    selected: Version?,
    muted: Color,
    onVersionsClick: () -> Unit
) {
    NeonPanel(
        modifier = modifier.fillMaxWidth(),
        accent = AlfaaCyan
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            SectionTitle(
                text = "INSTANCES",
                accent = AlfaaCyan
            )

            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        AlfaaCyan.copy(alpha = .09f)
                    )
                    .border(
                        1.dp,
                        AlfaaCyan.copy(alpha = .28f),
                        RoundedCornerShape(50)
                    )
                    .clickable(onClick = onVersionsClick)
                    .padding(
                        horizontal = 8.dp,
                        vertical = 5.dp
                    )
            ) {
                Text(
                    text = "+ NEW",
                    color = AlfaaCyan,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 7.sp
                )
            }
        }

        Spacer(Modifier.height(9.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(
                    rememberScrollState()
                ),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
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

@Composable
private fun InstanceChip(
    version: Version,
    selected: Boolean,
    muted: Color,
    onClick: () -> Unit
) {
    val accent = if (selected) AlfaaGreen else AlfaaCyan
    val shape = RoundedCornerShape(15.dp)

    Box(
        modifier = Modifier
            .width(165.dp)
            .height(70.dp)
            .clip(shape)
            .background(
                if (selected)
                    accent.copy(alpha = .09f)
                else
                    AlfaaPanel3
            )
            .border(
                1.dp,
                if (selected)
                    accent.copy(alpha = .48f)
                else
                    Color.White.copy(alpha = .07f),
                shape
            )
            .clickable(onClick = onClick)
            .padding(9.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            VersionIconImage(
                version = version,
                modifier = Modifier.size(46.dp)
            )

            Spacer(Modifier.width(9.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = version.getVersionName(),
                    color = AlfaaText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Text(
                    text = version.getVersionSummary(),
                    color = if (selected)
                        accent
                    else
                        muted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 7.sp,
                    maxLines = 1
                )

                Spacer(Modifier.height(5.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            accent.copy(
                                alpha = if (selected) .55f else .14f
                            )
                        )
                )
            }
        }
    }
}

@Composable
private fun AddInstanceChip(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(105.dp)
            .height(70.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(
                AlfaaPanel3
            )
            .border(
                1.dp,
                AlfaaPurple.copy(alpha = .25f),
                RoundedCornerShape(15.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "+",
                color = AlfaaPurple,
                fontSize = 24.sp,
                fontWeight = FontWeight.Light
            )

            Text(
                text = "NEW INSTANCE",
                color = AlfaaText,
                fontFamily = FontFamily.Monospace,
                fontSize = 7.sp,
                letterSpacing = .3.sp
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
    NeonPanel(
        modifier = Modifier.fillMaxWidth(),
        accent = AlfaaPurple
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            SectionTitle(
                text = "QUICK ACTIONS",
                accent = AlfaaPurple
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = "SWIPE →",
                color = AlfaaPurple,
                fontFamily = FontFamily.Monospace,
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(9.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(
                    rememberScrollState()
                ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            ActionCard(
                title = "VERSIONS",
                subtitle = "MANAGE INSTANCES",
                icon = R.drawable.ic_assignment_filled,
                accent = AlfaaGreen,
                onClick = onVersionsClick
            )

            ActionCard(
                title = "ACCOUNT",
                subtitle = "MANAGE PROFILE",
                icon = R.drawable.ic_person_outlined,
                accent = AlfaaCyan,
                onClick = onAccountClick
            )

            ActionCard(
                title = "INSTANCE",
                subtitle = "EDIT SELECTED",
                icon = R.drawable.ic_settings_filled,
                accent = AlfaaPurple,
                onClick = onVersionSettingsClick
            )

            ActionCard(
                title = "SETTINGS",
                subtitle = "LAUNCHER OPTIONS",
                icon = R.drawable.ic_settings_filled,
                accent = AlfaaOrange,
                onClick = onSettingsClick
            )
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: Int,
    accent: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .width(148.dp)
            .height(88.dp)
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = accent.copy(alpha = .12f),
                spotColor = accent.copy(alpha = .18f)
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        AlfaaPanel3,
                        AlfaaPanel2
                    )
                )
            )
            .border(
                1.dp,
                accent.copy(alpha = .22f),
                shape
            )
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(39.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        accent.copy(alpha = .09f)
                    )
                    .border(
                        1.dp,
                        accent.copy(alpha = .25f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(Modifier.width(9.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    color = AlfaaText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    maxLines = 1
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    color = AlfaaMuted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 6.sp,
                    maxLines = 2
                )
            }
        }
    }
}

/* ========================================================= */
/* SESSION */
/* ========================================================= */

@Composable
private fun ResourceModsCard(
    modifier: Modifier = Modifier,
    version: Version?,
    onClick: () -> Unit
) {
    NeonPanel(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        accent = AlfaaOrange
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(AlfaaOrange.copy(alpha = .08f))
                    .border(1.dp, AlfaaOrange.copy(alpha = .25f), RoundedCornerShape(11.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_extension_outlined),
                    contentDescription = null,
                    tint = AlfaaOrange,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(9.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "RESOURCES / MODS",
                    color = AlfaaText,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    letterSpacing = .5.sp,
                    maxLines = 1
                )
                Text(
                    text = version?.getVersionName() ?: "NO ACTIVE VERSION",
                    color = AlfaaMuted,
                    fontSize = 8.sp,
                    maxLines = 1
                )
                Text(
                    text = "CHECK INSTALLED CONTENT  ›",
                    color = AlfaaOrange,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 6.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(
    text: String,
    accent: Color = AlfaaCyan
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .width(3.dp)
                .height(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            accent,
                            accent.copy(alpha = .45f)
                        )
                    )
                )
        )

        Spacer(Modifier.width(7.dp))

        Text(
            text = text,
            color = AlfaaText,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            letterSpacing = 1.sp
        )
    }
}

/* ========================================================= */
/* FOOTER */
/* ========================================================= */

@Composable
private fun FooterBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 5.dp,
                vertical = 2.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "// PLAY  •  EXPLORE  •  CREATE",
            color = AlfaaCyan.copy(alpha = .75f),
            fontFamily = FontFamily.Monospace,
            fontSize = 7.sp,
            letterSpacing = .35.sp
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = "ALFAA // @alfathgpp",
            color = AlfaaMuted,
            fontFamily = FontFamily.Monospace,
            fontSize = 7.sp
        )
    }
}
