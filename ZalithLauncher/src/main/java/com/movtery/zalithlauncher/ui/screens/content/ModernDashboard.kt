package com.movtery.zalithlauncher.ui.screens.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.movtery.zalithlauncher.ui.screens.content.elements.PlayerFace
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionIconImage

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
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(154.dp)
                .clip(RoundedCornerShape(28.dp))
        ) {
            androidx.compose.foundation.Image(
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
                                Color.Black.copy(alpha = 0.78f),
                                Color.Black.copy(alpha = 0.35f),
                                Color.Black.copy(alpha = 0.10f)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "EXPLORE ENDLESS WORLDS",
                    color = Color.White.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = "Welcome back, ${account?.username ?: "Player"}",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Your Minecraft adventure starts here.",
                    color = Color.White.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardCard(modifier = Modifier.weight(1.15f), title = "CURRENT ACCOUNT") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AccountAvatar(
                        account = account,
                        avatarSize = 52.dp,
                        onClick = onAccountClick
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(account?.username ?: "No account", style = MaterialTheme.typography.titleMedium)
                        Text(
                            if (account == null) "Tap to add an account" else "Active account",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            DashboardCard(modifier = Modifier.weight(0.85f), title = "CURRENT SKIN") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (account != null) {
                        PlayerFace(account = account!!, avatarSize = 54.dp)
                    } else {
                        Box(
                            modifier = Modifier.size(54.dp),
                            contentAlignment = Alignment.Center
                        ) { Text("?") }
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Minecraft Skin", style = MaterialTheme.typography.titleSmall)
                        Text("Active profile", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardCard(modifier = Modifier.weight(1f), title = "SELECTED VERSION") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    VersionIconImage(version = version, modifier = Modifier.size(42.dp))
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(version?.getVersionName() ?: "No version", style = MaterialTheme.typography.titleSmall)
                        Text(
                            version?.getVersionSummary() ?: "Install a Minecraft version",
                            maxLines = 1,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text("${versions.size}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                }
            }

            Surface(
                modifier = Modifier
                    .weight(0.72f)
                    .height(82.dp),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("GAME PROFILE", style = MaterialTheme.typography.labelSmall)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Java Edition", style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.weight(1f))
                        Icon(
                            painter = painterResource(R.drawable.ic_settings_filled),
                            contentDescription = null,
                            modifier = Modifier
                                .size(22.dp)
                                .clickable(onClick = onVersionSettingsClick)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardCard(modifier = Modifier.weight(1f), title = "QUICK ACTIONS") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionChip("Versions", R.drawable.ic_assignment_filled, onVersionsClick)
                    ActionChip("Account", R.drawable.ic_person_outlined, onAccountClick)
                }
            }
            DashboardCard(modifier = Modifier.weight(1f), title = "LAUNCHER INFO") {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("${versions.size} installed versions", style = MaterialTheme.typography.bodyMedium)
                    Text("Ready for your next session", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            onClick = { onLaunchGame(null) },
            shape = RoundedCornerShape(22.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_play_arrow_filled),
                contentDescription = null,
                modifier = Modifier.size(26.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("PLAY MINECRAFT", fontSize = 17.sp)
        }
    }
}

@Composable
private fun DashboardCard(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}

@Composable
private fun ActionChip(text: String, icon: Int, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(icon), contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(text, style = MaterialTheme.typography.labelMedium)
        }
    }
}
