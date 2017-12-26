package com.pandulapeter.campfire.feature

import android.databinding.ObservableField
import com.pandulapeter.campfire.data.repository.UserPreferenceRepository
import com.pandulapeter.campfire.feature.home.HomeViewModel

/**
 * Handles events and logic for [MainActivity].
 */
class MainViewModel(userPreferenceRepository: UserPreferenceRepository, overrideMainNavigationItem: MainNavigationItem?) {
    val mainNavigationItem = ObservableField<MainNavigationItem>(overrideMainNavigationItem ?: MainNavigationItem.Home(userPreferenceRepository.navigationItem))

    sealed class MainNavigationItem(val stringValue: String) {
        class Home(val homeNavigationItem: HomeViewModel.HomeNavigationItem) : MainNavigationItem(VALUE_HOME + homeNavigationItem.stringValue)
        class Detail(val songId: String, val playlistId: Int? = null) : MainNavigationItem(VALUE_DETAIL)

        companion object {
            private const val VALUE_HOME = "home_"
            private const val VALUE_DETAIL = "detail_"

            fun fromStringValue(string: String?) = when {
                string == null || string.isEmpty() -> null
                string.startsWith(VALUE_HOME) -> Home(HomeViewModel.HomeNavigationItem.fromStringValue(string.removePrefix(VALUE_HOME)))
                string.startsWith(VALUE_DETAIL) -> Detail(string.removePrefix(VALUE_DETAIL), null)//TODO: Implement playlist ID parsing.
                else -> Home(HomeViewModel.HomeNavigationItem.Library)
            }
        }
    }
}