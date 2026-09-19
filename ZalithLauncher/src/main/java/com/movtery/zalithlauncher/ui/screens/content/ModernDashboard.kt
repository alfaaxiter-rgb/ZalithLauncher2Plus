package com.movtery.zalithlauncher.ui.screens.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.account.AccountsManager
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.ui.screens.content.elements.AccountAvatar
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionIconImage

private val Neon = Color(0xFF20E0B2)
private val NeonBlue = Color(0xFF39C9FF)
private val Panel = Color(0xFF11161B)
private val Panel2 = Color(0xFF151D23)

@Composable
fun ModernDashboard(
    modifier: Modifier = Modifier,
    onLaunchGame: (Version?) -> Unit,
    onAccountClick: () -> Unit,
    onVersionsClick: () -> Unit,
    onVersionSettingsClick: () -> Unit,
) {
    val account by AccountsManager.currentAccountFlow.collectAsStateWithLifecycle()
    val version by VersionsManager.currentVersion.collectAsStateWithLifecycle()
    val versions by VersionsManager.versions.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "ZALITHLAUNCHER 2+",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 19.sp
                )

                Text(
                    text = "DASHBOARD  •  ${version?.getVersionName() ?: "NO INSTANCE"}",
                    color = Neon,
                    fontSize = 11.sp
                )
            }

            Surface(
                modifier = Modifier.clickable(onClick = onAccountClick),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 9.dp,
                        vertical = 6.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AccountAvatar(
                        account = account,
                        avatarSize = 32.dp,
                        onClick = onAccountClick
                    )

                    Spacer(Modifier.width(8.dp))

                    Column {
                        Text(
                            account?.username ?: "Guest",
                            fontSize = 12.sp
                        )

                        Text(
                            "ACTIVE ACCOUNT",
                            fontSize = 8.sp,
                            color = Neon
                        )
                    }
                }
            }
        }

        // MAIN AREA
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // HERO
            Box(
                modifier = Modifier
                    .weight(1.65f)
                    .height(255.dp)
                    .clip(RoundedCornerShape(28.dp))
            ) {

                Image(
                    painter = painterResource(R.drawable.img_star1xr),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xF5071014),
                                    Color(0xB5071014),
                                    Color(0x30071014)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(
                            start = 24.dp,
                            end = 12.dp
                        )
                ) {

                    Surface(
                        shape = RoundedCornerShape(30.dp),
                        color = Neon.copy(alpha = 0.13f)
                    ) {
                        Text(
                            text = "MINECRAFT • JAVA EDITION",
                            modifier = Modifier.padding(
                                horizontal = 11.dp,
                                vertical = 6.dp
                            ),
                            color = Neon,
                            fontSize = 9.sp
                        )
                    }

                    Spacer(Modifier.height(9.dp))

                    Text(
                        "SELAMAT DATANG,",
                        color = Color.White.copy(alpha = .70f),
                        fontSize = 12.sp
                    )

                    Text(
                        account?.username ?: "PLAYER",
                        color = Color.White,
                        fontSize = 30.sp
                    )

                    Text(
                        "Main bebas. Atur instance sendiri.\n" +
                                "Semua fitur launcher tetap di tempatnya.",
                        color = Color.White.copy(alpha = .78f),
                        fontSize = 12.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HudChip("FPS", "60")
                        HudChip("RAM", "—")
                        HudChip(
                            "VERSI",
                            version?.getVersionName() ?: "—"
                        )
                    }
                }

                // PLAY BUTTON
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .clickable {
                            onLaunchGame(version)
                        },
                    shape = RoundedCornerShape(20.dp),
                    color = Neon
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 25.dp,
                            vertical = 15.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            painter = painterResource(
                                R.drawable.ic_play_arrow_filled
                            ),
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(25.dp)
                        )

                        Spacer(Modifier.width(9.dp))

                        Column {
                            Text(
                                "MAIN",
                                color = Color.Black,
                                fontSize = 19.sp
                            )

                            Text(
                                "Mulai petualanganmu",
                                color = Color.Black.copy(alpha = .65f),
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }

            // RIGHT CONTROL PANEL
            Column(
                modifier = Modifier.weight(.72f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                RailCard("ACTIVE ACCOUNT") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        AccountAvatar(
                            account = account,
                            avatarSize = 44.dp,
                            onClick = onAccountClick
                        )

                        Spacer(Modifier.width(10.dp))

                        Column(
                            Modifier.weight(1f)
                        ) {
                            Text(
                                account?.username ?: "No account",
                                fontSize = 13.sp
                            )

                            Text(
                                if (account == null)
                                    "Add account"
                                else
                                    "Microsoft / active",
                                fontSize = 9.sp,
                                color = Neon
                            )
                        }
                    }
                }

                RailCard("SELECTED VERSION") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        VersionIconImage(
                            version = version,
                            modifier = Modifier.size(42.dp)
                        )

                        Spacer(Modifier.width(10.dp))

                        Column(
                            Modifier.weight(1f)
                        ) {

                            Text(
                                version?.getVersionName()
                                    ?: "No version",
                                fontSize = 13.sp
                            )

                            Text(
                                version?.getVersionSummary()
                                    ?: "Install a version",
                                fontSize = 9.sp,
                                color = MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                            )
                        }

                        Icon(
                            painter = painterResource(
                                R.drawable.ic_settings_filled
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable(
                                    onClick =
                                        onVersionSettingsClick
                                )
                        )
                    }
                }

                RailCard("PROFILE / GAME MODE") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            painter = painterResource(
                                R.drawable.ic_person_outlined
                            ),
                            contentDescription = null,
                            tint = Neon,
                            modifier = Modifier.size(22.dp)
                        )

                        Spacer(Modifier.width(9.dp))

                        Column {
                            Text(
                                "Java Edition",
                                fontSize = 13.sp
                            )

                            Text(
                                "Default profile",
                                fontSize = 9.sp,
                                color = MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // INSTANCES + QUICK ACTIONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Surface(
                modifier = Modifier.weight(1.5f),
                shape = RoundedCornerShape(22.dp),
                color = Panel
            ) {

                Column(
                    Modifier.padding(14.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            "MINECRAFT INSTANCES",
                            fontSize = 12.sp,
                            color = Color.White
                        )

                        Spacer(Modifier.weight(1f))

                        Text(
                            "${versions.size} INSTALLED",
                            fontSize = 9.sp,
                            color = Neon
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.horizontalScroll(
                            rememberScrollState()
                        ),
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        versions
                            .take(6)
                            .forEach { item ->
                                InstanceTile(
                                    version = item,
                                    selected = item == version,
                                    onClick = onVersionsClick
                                )
                            }

                        AddTile(onVersionsClick)
                    }
                }
            }

            Surface(
                modifier = Modifier.weight(.75f),
                shape = RoundedCornerShape(22.dp),
                color = Panel2
            ) {

                Column(
                    Modifier.padding(14.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        "QUICK ACTIONS",
                        fontSize = 12.sp,
                        color = Color.White
                    )

                    QuickAction(
                        "VERSIONS",
                        R.drawable.ic_assignment_filled,
                        onVersionsClick
                    )

                    QuickAction(
                        "ACCOUNT",
                        R.drawable.ic_person_outlined,
                        onAccountClick
                    )

                    QuickAction(
                        "SETTINGS",
                        R.drawable.ic_settings_filled,
                        onVersionSettingsClick
                    )
                }
            }
        }

        // FOOTER / WATERMARK
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                "// PLAY  •  EXPLORE  •  CREATE",
                color = NeonBlue,
                fontSize = 9.sp
            )

            Spacer(Modifier.weight(1f))

            Text(
                "TikTok: @alfathgpp",
                color = MaterialTheme
                    .colorScheme
                    .onSurfaceVariant,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
private fun HudChip(
    label: String,
    value: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.Black.copy(alpha = .42f)
    ) {

        Column(
            Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            )
        ) {

            Text(
                label,
                color = Neon,
                fontSize = 7.sp
            )

            Text(
                value,
                color = Color.White,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun RailCard(
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(21.dp),
        color = Panel
    ) {

        Column(
            Modifier.padding(13.dp),
            verticalArrangement =
                Arrangement.spacedBy(9.dp)
        ) {

            Text(
                title,
                color = Neon,
                fontSize = 9.sp
            )

            content()
        }
    }
}

@Composable
private fun InstanceTile(
    version: Version,
    selected: Boolean,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .width(108.dp)
            .height(105.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color =
            if (selected)
                Neon.copy(alpha = .12f)
            else
                Color(0xFF18222A)
    ) {

        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(4.dp)
        ) {

            VersionIconImage(
                version = version,
                modifier = Modifier.size(45.dp)
            )

            Text(
                version.getVersionName(),
                color = Color.White,
                fontSize = 11.sp
            )

            Text(
                version.getVersionSummary(),
                color =
                    if (selected)
                        Neon
                    else
                        Color.White.copy(alpha = .55f),
                fontSize = 8.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun AddTile(
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .width(108.dp)
            .height(105.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF18222A)
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.Center
        ) {

            Text(
                "+",
                color = Neon,
                fontSize = 28.sp
            )

            Text(
                "TAMBAH VERSI",
                color = Color.White,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
private fun QuickAction(
    text: String,
    icon: Int,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(13.dp),
        color = Color(0xFF1B252D)
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 9.dp,
                vertical = 8.dp
            ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = Neon,
                modifier = Modifier.size(17.dp)
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text,
                color = Color.White,
                fontSize = 9.sp
            )
        }
    }
}
