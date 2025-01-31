package app.vitune.android.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import app.vitune.android.R
import app.vitune.android.models.toUiMood
import app.vitune.android.preferences.UIStatePreferences
import app.vitune.android.ui.components.themed.Scaffold
import app.vitune.android.ui.screens.GlobalRoutes
import app.vitune.android.ui.screens.Route
import app.vitune.android.ui.screens.albumRoute
import app.vitune.android.ui.screens.artistRoute
import app.vitune.android.ui.screens.builtInPlaylistRoute
import app.vitune.android.ui.screens.builtinplaylist.BuiltInPlaylistScreen
import app.vitune.android.ui.screens.localPlaylistRoute
import app.vitune.android.ui.screens.localplaylist.LocalPlaylistScreen
import app.vitune.android.ui.screens.mood.MoodScreen
import app.vitune.android.ui.screens.mood.MoreAlbumsScreen
import app.vitune.android.ui.screens.mood.MoreMoodsScreen
import app.vitune.android.ui.screens.moodRoute
import app.vitune.android.ui.screens.pipedPlaylistRoute
import app.vitune.android.ui.screens.playlistRoute
import app.vitune.android.ui.screens.searchRoute
import app.vitune.android.ui.screens.settingsRoute
import app.vitune.compose.persist.PersistMapCleanup
import app.vitune.compose.routing.Route0
import app.vitune.compose.routing.RouteHandler

private val moreMoodsRoute = Route0("moreMoodsRoute")
private val moreAlbumsRoute = Route0("moreAlbumsRoute")

@Route
@Composable
fun HomeScreen() {
    val saveableStateHolder = rememberSaveableStateHolder()

    PersistMapCleanup("home/")

    // Route handler for various screens
    RouteHandler {
        GlobalRoutes()

        localPlaylistRoute { playlistId ->
            LocalPlaylistScreen(playlistId = playlistId)
        }

        builtInPlaylistRoute { builtInPlaylist ->
            BuiltInPlaylistScreen(builtInPlaylist = builtInPlaylist)
        }

        moodRoute { mood ->
            MoodScreen(mood = mood)
        }

        moreMoodsRoute {
            MoreMoodsScreen()
        }

        moreAlbumsRoute {
            MoreAlbumsScreen()
        }

        Content {
            Scaffold(
                key = "home",
                topIconButtonId = R.drawable.settings,
                onTopIconButtonClick = { settingsRoute() },
                bottomBar = {
                    BottomNavigation(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        BottomNavigationItem(
                            selected = UIStatePreferences.homeScreenTabIndex == 0,
                            onClick = { UIStatePreferences.homeScreenTabIndex = 0 },
                            label = { Text(stringResource(R.string.quick_picks)) },
                            icon = { Icon(painterResource(R.drawable.sparkles), contentDescription = null) }
                        )
                        BottomNavigationItem(
                            selected = UIStatePreferences.homeScreenTabIndex == 1,
                            onClick = { UIStatePreferences.homeScreenTabIndex = 1 },
                            label = { Text(stringResource(R.string.discover)) },
                            icon = { Icon(painterResource(R.drawable.globe), contentDescription = null) }
                        )
                        BottomNavigationItem(
                            selected = UIStatePreferences.homeScreenTabIndex == 2,
                            onClick = { UIStatePreferences.homeScreenTabIndex = 2 },
                            label = { Text(stringResource(R.string.songs)) },
                            icon = { Icon(painterResource(R.drawable.musical_notes), contentDescription = null) }
                        )
                        BottomNavigationItem(
                            selected = UIStatePreferences.homeScreenTabIndex == 3,
                            onClick = { UIStatePreferences.homeScreenTabIndex = 3 },
                            label = { Text(stringResource(R.string.playlists)) },
                            icon = { Icon(painterResource(R.drawable.playlist), contentDescription = null) }
                        )
                        BottomNavigationItem(
                            selected = UIStatePreferences.homeScreenTabIndex == 4,
                            onClick = { UIStatePreferences.homeScreenTabIndex = 4 },
                            label = { Text(stringResource(R.string.artists)) },
                            icon = { Icon(painterResource(R.drawable.person), contentDescription = null) }
                        )
                        BottomNavigationItem(
                            selected = UIStatePreferences.homeScreenTabIndex == 5,
                            onClick = { UIStatePreferences.homeScreenTabIndex = 5 },
                            label = { Text(stringResource(R.string.albums)) },
                            icon = { Icon(painterResource(R.drawable.disc), contentDescription = null) }
                        )
                        BottomNavigationItem(
                            selected = UIStatePreferences.homeScreenTabIndex == 6,
                            onClick = { UIStatePreferences.homeScreenTabIndex = 6 },
                            label = { Text(stringResource(R.string.local)) },
                            icon = { Icon(painterResource(R.drawable.download), contentDescription = null) }
                        )
                    }
                }
            ) { currentTabIndex ->
                saveableStateHolder.SaveableStateProvider(key = currentTabIndex) {
                    val onSearchClick = { searchRoute("") }
                    when (currentTabIndex) {
                        0 -> QuickPicks(
                            onAlbumClick = { albumRoute(it.key) },
                            onArtistClick = { artistRoute(it.key) },
                            onPlaylistClick = {
                                playlistRoute(
                                    p0 = it.key,
                                    p1 = null,
                                    p2 = null,
                                    p3 = it.channel?.name == "YouTube Music"
                                )
                            },
                            onSearchClick = onSearchClick
                        )

                        1 -> HomeDiscovery(
                            onMoodClick = { mood -> moodRoute(mood.toUiMood()) },
                            onNewReleaseAlbumClick = { albumRoute(it) },
                            onSearchClick = onSearchClick,
                            onMoreMoodsClick = { moreMoodsRoute() },
                            onMoreAlbumsClick = { moreAlbumsRoute() },
                            onPlaylistClick = { playlistRoute(it, null, null, true) }
                        )

                        2 -> HomeSongs(
                            onSearchClick = onSearchClick
                        )

                        3 -> HomePlaylists(
                            onBuiltInPlaylist = { builtInPlaylistRoute(it) },
                            onPlaylistClick = { localPlaylistRoute(it.id) },
                            onPipedPlaylistClick = { session, playlist ->
                                pipedPlaylistRoute(
                                    p0 = session.apiBaseUrl.toString(),
                                    p1 = session.token,
                                    p2 = playlist.id.toString()
                                )
                            },
                            onSearchClick = onSearchClick
                        )

                        4 -> HomeArtistList(
                            onArtistClick = { artistRoute(it.id) },
                            onSearchClick = onSearchClick
                        )

                        5 -> HomeAlbums(
                            onAlbumClick = { albumRoute(it.id) },
                            onSearchClick = onSearchClick
                        )

                        6 -> HomeLocalSongs(
                            onSearchClick = onSearchClick
                        )
                    }
                }
            }
        }
    }
}
