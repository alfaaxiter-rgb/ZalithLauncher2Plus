/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.movtery.zalithlauncher.ui.screens.content.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.movtery.zalithlauncher.BuildConfig
import com.movtery.zalithlauncher.BuildKeys
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.plugin.ApkPlugin
import com.movtery.zalithlauncher.game.plugin.PluginLoader
import com.movtery.zalithlauncher.game.plugin.appCacheIcon
import com.movtery.zalithlauncher.library.LibraryInfo
import com.movtery.zalithlauncher.library.libraryData
import com.movtery.zalithlauncher.path.URL_COMMUNITY
import com.movtery.zalithlauncher.path.URL_MCMOD
import com.movtery.zalithlauncher.path.URL_PROJECT
import com.movtery.zalithlauncher.path.URL_STAR1XR
import com.movtery.zalithlauncher.path.URL_SUPPORT
import com.movtery.zalithlauncher.path.URL_WEBLATE
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.components.AnimatedLazyColumn
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.TitledNavKey
import com.movtery.zalithlauncher.ui.screens.content.NeonCard
import com.movtery.zalithlauncher.ui.screens.content.NeonColors
import com.movtery.zalithlauncher.ui.screens.content.NeonIconButton
import com.movtery.zalithlauncher.ui.screens.content.NeonPrimaryButton
import com.movtery.zalithlauncher.ui.screens.content.NeonButton
import com.movtery.zalithlauncher.ui.screens.content.neonMutedColor
import com.movtery.zalithlauncher.ui.screens.content.neonTextColor

@Composable
fun AboutInfoScreen(
    key: NestedNavKey.Settings,
    settingsScreenKey: TitledNavKey?,
    mainScreenKey: TitledNavKey?,
    checkUpdate: () -> Unit,
    openLicense: (raw: Int) -> Unit,
    openLink: (url: String) -> Unit
) {
    BaseScreen(
        Triple(key, mainScreenKey, false),
        Triple(NormalNavKey.Settings.AboutInfo, settingsScreenKey, false)
    ) { isVisible ->

        AnimatedLazyColumn(
            modifier = Modifier.fillMaxSize(),
            isVisible = isVisible,
            contentPadding = PaddingValues(
                horizontal = 14.dp,
                vertical = 14.dp
            )
        ) { scope ->

            /*
             * =================================================
             * HEADER
             * =================================================
             */

            animatedItem(scope) { yOffset ->

                NeonCard(
                    modifier = Modifier.offset {
                        IntOffset(
                            x = 0,
                            y = yOffset.roundToPx()
                        )
                    },
                    accent = NeonColors.CyanBright
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 20.dp,
                                vertical = 18.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Image(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(
                                    RoundedCornerShape(16.dp)
                                ),
                            painter = painterResource(
                                R.drawable.img_launcher
                            ),
                            contentDescription = null,
                            contentScale = ContentScale.Fit
                        )

                        Spacer(
                            modifier = Modifier.width(14.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "ALFAA LAUNCHER",
                                color = NeonColors.CyanBright,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 9.sp,
                                letterSpacing = 1.8.sp
                            )

                            Spacer(
                                modifier = Modifier.height(3.dp)
                            )

                            Text(
                                text = BuildKeys.LAUNCHER_NAME,
                                color = neonTextColor(),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 23.sp
                            )

                            Text(
                                text = "CUSTOM UI • VERSION ${BuildConfig.VERSION_NAME}",
                                color = neonMutedColor(),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.sp,
                                letterSpacing = 0.7.sp
                            )
                        }
                    }
                }
            }

            /*
             * =================================================
             * LAUNCHER
             * =================================================
             */

            animatedItem(scope) { yOffset ->

                AboutSection(
                    modifier = Modifier.offset {
                        IntOffset(
                            x = 0,
                            y = yOffset.roundToPx()
                        )
                    },
                    title = stringResource(
                        R.string.about_launcher_title
                    ),
                    subtitle = "LAUNCHER INFORMATION"
                ) {

                    AboutActionItem(
                        icon = painterResource(
                            R.drawable.img_launcher
                        ),
                        title = BuildKeys.LAUNCHER_NAME,
                        text = stringResource(
                            R.string.about_launcher_version,
                            BuildConfig.VERSION_NAME
                        ),
                        accent = NeonColors.CyanBright,
                        actions = {
                            NeonPrimaryButton(
                                text = stringResource(
                                    R.string.upgrade_title
                                ),
                                onClick = checkUpdate
                            )

                            NeonButton(
                                text = "PROJECT",
                                onClick = {
                                    openLink(URL_PROJECT)
                                }
                            )
                        }
                    )

                    AboutActionItem(
                        icon = painterResource(
                            R.drawable.img_avatar_movtery
                        ),
                        title = stringResource(
                            R.string.about_launcher_author_movtery_title
                        ),
                        text = stringResource(
                            R.string.about_launcher_author_movtery_text,
                            BuildKeys.LAUNCHER_NAME
                        ),
                        accent = NeonColors.Purple,
                        actions = {
                            NeonButton(
                                text = stringResource(
                                    R.string.about_sponsor
                                ),
                                onClick = {
                                    openLink(URL_SUPPORT)
                                }
                            )
                        }
                    )

                    AboutActionItem(
                        icon = painterResource(
                            R.drawable.img_star1xr
                        ),
                        title = stringResource(
                            R.string.about_launcher_author_star1xr_title
                        ),
                        text = stringResource(
                            R.string.about_launcher_author_star1xr_text,
                            BuildKeys.LAUNCHER_NAME
                        ),
                        accent = NeonColors.Blue,
                        actions = {
                            NeonButton(
                                text = "PROJECT",
                                onClick = {
                                    openLink(URL_STAR1XR)
                                }
                            )
                        }
                    )
                }
            }

            /*
             * =================================================
             * ALFAA ACKNOWLEDGEMENT
             * =================================================
             */

            animatedItem(scope) { yOffset ->

                AboutSection(
                    modifier = Modifier.offset {
                        IntOffset(
                            x = 0,
                            y = yOffset.roundToPx()
                        )
                    },
                    title = stringResource(
                        R.string.about_acknowledgements_title
                    ),
                    subtitle = "PEOPLE • PROJECTS • COMMUNITY"
                ) {

                    AboutActionItem(
                        icon = painterResource(
                            R.drawable.img_avatar_alfaa_bejirr
                        ),
                        title = "AlfaaBEJIRR",
                        text = "Terima kasih sudah membantu remake UI bagian depan launcher ini.",
                        accent = NeonColors.CyanBright,
                        actions = {
                            NeonPrimaryButton(
                                text = "TIKTOK",
                                onClick = {
                                    openLink(
                                        "https://www.tiktok.com/@alfathgpp?_r=1&_t=ZS-99rojVeF26n"
                                    )
                                }
                            )
                        }
                    )

                    AboutActionItem(
                        icon = painterResource(
                            R.drawable.img_avatar_bangbang93
                        ),
                        title = "bangbang93",
                        text = stringResource(
                            R.string.about_acknowledgements_bangbang93_text,
                            BuildKeys.LAUNCHER_SHORT_NAME
                        ),
                        accent = NeonColors.Orange,
                        actions = {
                            NeonButton(
                                text = stringResource(
                                    R.string.about_sponsor
                                ),
                                onClick = {
                                    openLink(
                                        "https://ifdian.net/a/bangbang93"
                                    )
                                }
                            )
                        }
                    )

                    AboutLinkItem(
                        icon = painterResource(
                            R.drawable.img_launcher_fcl
                        ),
                        title = "Fold Craft Launcher",
                        text = stringResource(
                            R.string.about_acknowledgements_fcl_text,
                            BuildKeys.LAUNCHER_SHORT_NAME
                        ),
                        accent = NeonColors.Blue,
                        openLicense = {
                            openLicense(R.raw.fcl_license)
                        },
                        openLink = {
                            openLink(
                                "https://github.com/FCL-Team/FoldCraftLauncher"
                            )
                        }
                    )

                    AboutLinkItem(
                        icon = painterResource(
                            R.drawable.img_launcher_hmcl
                        ),
                        title = "Hello Minecraft! Launcher",
                        text = stringResource(
                            R.string.about_acknowledgements_hmcl_text,
                            BuildKeys.LAUNCHER_SHORT_NAME
                        ),
                        accent = NeonColors.Purple,
                        openLicense = {
                            openLicense(R.raw.hmcl_license)
                        },
                        openLink = {
                            openLink(
                                "https://github.com/HMCL-dev/HMCL"
                            )
                        }
                    )

                    AboutLinkItem(
                        icon = painterResource(
                            R.drawable.img_platform_mcmod
                        ),
                        title = stringResource(
                            R.string.about_acknowledgements_mcmod
                        ),
                        text = stringResource(
                            R.string.about_acknowledgements_mcmod_text,
                            BuildKeys.LAUNCHER_SHORT_NAME
                        ),
                        accent = NeonColors.CyanBright,
                        openLink = {
                            openLink(URL_MCMOD)
                        }
                    )

                    AboutActionItem(
                        icon = painterResource(
                            R.drawable.img_avatar_mcim
                        ),
                        title = "mcmod-info-mirror",
                        text = stringResource(
                            R.string.about_acknowledgements_mcim_text,
                            BuildKeys.LAUNCHER_SHORT_NAME
                        ),
                        accent = NeonColors.Orange,
                        actions = {
                            NeonButton(
                                text = stringResource(
                                    R.string.about_sponsor
                                ),
                                onClick = {
                                    openLink(
                                        "https://www.mcimirror.top/sponsor"
                                    )
                                }
                            )
                        }
                    )

                    AboutLinkItem(
                        icon = painterResource(
                            R.drawable.img_launcher_pcl2
                        ),
                        title = "Plain Craft Launcher 2",
                        text = stringResource(
                            R.string.about_acknowledgements_pcl_text,
                            BuildKeys.LAUNCHER_SHORT_NAME
                        ),
                        accent = NeonColors.Cyan,
                        openLink = {
                            openLink(
                                "https://github.com/Meloong-Git/PCL"
                            )
                        }
                    )

                    AboutLinkItem(
                        icon = painterResource(
                            R.drawable.img_launcher_pojav
                        ),
                        title = "PojavLauncher",
                        text = stringResource(
                            R.string.about_acknowledgements_pojav_text,
                            BuildKeys.LAUNCHER_SHORT_NAME
                        ),
                        accent = NeonColors.Blue,
                        openLicense = {
                            openLicense(R.raw.lgpl_3_license)
                        },
                        openLink = {
                            openLink(
                                "https://github.com/PojavLauncherTeam/PojavLauncher"
                            )
                        }
                    )

                    AboutLinkItem(
                        icon = painterResource(
                            R.drawable.ic_github
                        ),
                        title = stringResource(
                            R.string.about_acknowledgements_github_community
                        ),
                        text = stringResource(
                            R.string.about_acknowledgements_github_community_text
                        ),
                        accent = NeonColors.Purple,
                        openLink = {
                            openLink(URL_COMMUNITY)
                        }
                    )

                    AboutLinkItem(
                        icon = painterResource(
                            R.drawable.img_platform_weblate
                        ),
                        title = stringResource(
                            R.string.about_acknowledgements_weblate_community
                        ),
                        text = stringResource(
                            R.string.about_acknowledgements_weblate_community_text
                        ),
                        accent = NeonColors.CyanBright,
                        openLink = {
                            openLink(URL_WEBLATE)
                        }
                    )
                }
            }

            /*
             * =================================================
             * LIBRARIES
             * =================================================
             */

            animatedItem(scope) { yOffset ->

                AboutSection(
                    modifier = Modifier.offset {
                        IntOffset(
                            x = 0,
                            y = yOffset.roundToPx()
                        )
                    },
                    title = stringResource(
                        R.string.about_library_title
                    ),
                    subtitle = "DEPENDENCIES • LICENSES"
                ) {

                    libraryData.forEach { info ->

                        LibraryInfoItem(
                            info = info,
                            openLicense = openLicense,
                            openLink = openLink
                        )
                    }
                }
            }

            /*
             * =================================================
             * PLUGINS
             * =================================================
             */

            PluginLoader.allPlugins
                .takeIf { it.isNotEmpty() }
                ?.let { allPlugins ->

                    animatedItem(scope) { yOffset ->

                        AboutSection(
                            modifier = Modifier.offset {
                                IntOffset(
                                    x = 0,
                                    y = yOffset.roundToPx()
                                )
                            },
                            title = stringResource(
                                R.string.about_plugin_title
                            ),
                            subtitle = "${allPlugins.size} PLUGIN(S) LOADED"
                        ) {

                            allPlugins.forEach { apkPlugin ->

                                PluginInfoItem(
                                    apkPlugin = apkPlugin
                                )
                            }
                        }
                    }
                }
        }
    }
}

/*
 * =============================================================
 * SECTION
 * =============================================================
 */

@Composable
private fun AboutSection(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    NeonCard(
        modifier = modifier.fillMaxWidth(),
        accent = NeonColors.Cyan.copy(alpha = 0.55f)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = title.uppercase(),
                    color = neonTextColor(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )

                Text(
                    text = subtitle,
                    color = NeonColors.CyanBright,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 7.sp,
                    letterSpacing = 1.sp
                )
            }

            content()
        }
    }
}

/*
 * =============================================================
 * ACTION ITEM
 * =============================================================
 */

@Composable
private fun AboutActionItem(
    icon: Painter,
    title: String,
    text: String,
    accent: androidx.compose.ui.graphics.Color,
    actions: @Composable RowScope.() -> Unit
) {
    NeonCard(
        modifier = Modifier.fillMaxWidth(),
        accent = accent
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 11.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Image(
                modifier = Modifier
                    .size(44.dp)
                    .clip(
                        RoundedCornerShape(12.dp)
                    ),
                painter = icon,
                contentDescription = null,
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    color = neonTextColor(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = text,
                    color = neonMutedColor(),
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = actions
            )
        }
    }
}

/*
 * =============================================================
 * LINK ITEM
 * =============================================================
 */

@Composable
private fun AboutLinkItem(
    icon: Painter,
    title: String,
    text: String,
    accent: androidx.compose.ui.graphics.Color,
    openLicense: (() -> Unit)? = null,
    openLink: (() -> Unit)? = null
) {
    NeonCard(
        modifier = Modifier.fillMaxWidth(),
        accent = accent
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 11.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Image(
                modifier = Modifier
                    .size(42.dp)
                    .clip(
                        RoundedCornerShape(11.dp)
                    ),
                painter = icon,
                contentDescription = null,
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    color = neonTextColor(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                Text(
                    text = text,
                    color = neonMutedColor(),
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {

                if (openLicense != null) {

                    IconButton(
    onClick = openLicense
) {
    Icon(
        painter = painterResource(R.drawable.ic_copyright_outlined),
        contentDescription = "License",
        tint = NeonColors.Cyan
    )
                    }

                if (openLink != null) {

                    IconButton(
    onClick = openLink
) {
    Icon(
        painter = painterResource(R.drawable.ic_link),
        contentDescription = stringResource(
            R.string.generic_open_link
        ),
        tint = NeonColors.Cyan
    )
                    }
            }
        }
    }
}

/*
 * =============================================================
 * PLUGIN ITEM
 * =============================================================
 */

@Composable
private fun PluginInfoItem(
    apkPlugin: ApkPlugin,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    NeonCard(
        modifier = modifier.fillMaxWidth(),
        accent = NeonColors.Purple
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            val iconFile = appCacheIcon(
                apkPlugin.packageName
            )

            if (iconFile.exists()) {

                val model = remember(
                    context,
                    iconFile
                ) {
                    ImageRequest.Builder(context)
                        .data(iconFile)
                        .build()
                }

                AsyncImage(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(
                            RoundedCornerShape(10.dp)
                        ),
                    model = model,
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )

            } else {

                Image(
                    modifier = Modifier.size(42.dp),
                    painter = painterResource(
                        R.drawable.ic_unknown_icon
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = apkPlugin.appName,
                    color = neonTextColor(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    Text(
                        text = apkPlugin.packageName,
                        color = neonMutedColor(),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp
                    )

                    if (apkPlugin.appVersion.isNotEmpty()) {

                        Text(
                            text = apkPlugin.appVersion,
                            color = NeonColors.Purple,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp
                        )
                    }
                }
            }
        }
    }
}

/*
 * =============================================================
 * LIBRARY ITEM
 * =============================================================
 */

@Composable
private fun LibraryInfoItem(
    info: LibraryInfo,
    modifier: Modifier = Modifier,
    openLicense: (Int) -> Unit,
    openLink: (String) -> Unit
) {
    NeonCard(
        modifier = modifier.fillMaxWidth(),
        accent = NeonColors.Blue.copy(alpha = 0.8f)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 11.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = info.name,
                    color = neonTextColor(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                info.copyrightInfo?.let { copyright ->

                    Text(
                        text = copyright,
                        modifier = Modifier.alpha(0.72f),
                        color = neonMutedColor(),
                        fontSize = 9.sp
                    )
                }

                Text(
                    modifier = Modifier.clickable {
                        openLicense(
                            info.license.raw
                        )
                    },
                    text = "Licensed under the ${info.license.name}",
                    color = NeonColors.CyanBright,
                    fontSize = 9.sp,
                    textDecoration = TextDecoration.Underline
                )
            }

            IconButton(
    onClick = {
        openLink(info.webUrl)
    }
) {
    Icon(
        painter = painterResource(R.drawable.ic_link),
        contentDescription = stringResource(
            R.string.generic_open_link
        ),
        tint = NeonColors.Cyan
    )
            }
        }
    }
}
