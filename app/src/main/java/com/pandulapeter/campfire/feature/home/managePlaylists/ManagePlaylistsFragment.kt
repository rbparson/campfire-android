package com.pandulapeter.campfire.feature.home.managePlaylists

import com.pandulapeter.campfire.ManagePlaylistsBinding
import com.pandulapeter.campfire.R
import com.pandulapeter.campfire.feature.home.shared.homeChild.HomeChildFragment

/**
 * Allows the user to rearrange or delete playlists.
 *
 * Controlled by [ManagePlaylistsViewModel].
 */
class ManagePlaylistsFragment : HomeChildFragment<ManagePlaylistsBinding, ManagePlaylistsViewModel>(R.layout.fragment_manage_playlists) {

    override fun createViewModel() = ManagePlaylistsViewModel(analyticsManager)

    override fun getAppBarLayout() = binding.appBarLayout
}