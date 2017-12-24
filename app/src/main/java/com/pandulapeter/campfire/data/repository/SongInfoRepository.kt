package com.pandulapeter.campfire.data.repository

import com.pandulapeter.campfire.data.model.SongInfo

import com.pandulapeter.campfire.networking.NetworkManager
import com.pandulapeter.campfire.data.repository.shared.Repository
import com.pandulapeter.campfire.data.repository.shared.Subscriber
import com.pandulapeter.campfire.data.repository.shared.UpdateType
import com.pandulapeter.campfire.data.storage.DataStorageManager
import com.pandulapeter.campfire.data.storage.PreferenceStorageManager
import com.pandulapeter.campfire.util.enqueueCall
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 * Wraps caching and updating of [SongInfo] objects.
 */
class SongInfoRepository(
    private val preferenceStorageManager: PreferenceStorageManager,
    private val dataStorageManager: DataStorageManager,
    private val networkManager: NetworkManager,
    private val languageRepository: LanguageRepository) : Repository() {
    private var dataSet by Delegates.observable(dataStorageManager.songInfoCache) { _, _, new ->
        languageRepository.updateLanguages(getLibrarySongs())
        dataStorageManager.songInfoCache = new
    }
    var isLoading by Delegates.observable(false) { _: KProperty<*>, old: Boolean, new: Boolean -> if (old != new) notifySubscribers(UpdateType.LoadingStateChanged(new)) }

    init {
        if (!isLoading && System.currentTimeMillis() - preferenceStorageManager.lastUpdateTimestamp > CACHE_VALIDITY_LIMIT) {
            updateDataSet()
        }
        languageRepository.updateLanguages(getLibrarySongs())
    }

    override fun subscribe(subscriber: Subscriber) {
        super.subscribe(subscriber)
        subscriber.onUpdate(UpdateType.LibraryCacheUpdated(getLibrarySongs()))
        subscriber.onUpdate(UpdateType.LoadingStateChanged(isLoading))
    }

    fun getLibrarySongs(): List<SongInfo> = dataSet.values.toList()

    fun getSongInfo(id: String) = dataSet[id]

    fun updateDataSet(onError: () -> Unit = {}) {
        isLoading = true
        networkManager.service.getLibrary().enqueueCall(
            onSuccess = {
                isLoading = false
                dataSet = it.associateBy { it.id }
                preferenceStorageManager.lastUpdateTimestamp = System.currentTimeMillis()
                notifySubscribers(UpdateType.LibraryCacheUpdated(getLibrarySongs()))
            },
            onFailure = {
                isLoading = false
                onError()
            })
    }

    private companion object {
        private const val CACHE_VALIDITY_LIMIT = 1000 * 60 * 60 * 24
    }
}