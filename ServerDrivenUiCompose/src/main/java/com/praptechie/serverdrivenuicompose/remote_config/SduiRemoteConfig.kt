package com.praptechie.serverdrivenuicompose.remote_config

import android.content.Context
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import org.json.JSONObject

class SduiRemoteConfig private constructor(
    private val screenKey: String,
    private val defaultJson: String?,
    private val fetchIntervalSeconds: Long,
    private val onUpdate: (String) -> Unit
) {

    private val remoteConfig = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = fetchIntervalSeconds
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        
        // Set defaults if provided
        defaultJson?.let {
            remoteConfig.setDefaultsAsync(mapOf(screenKey to it))
        }

        // Activate last fetched values immediately so they are available as "cached" values
        remoteConfig.activate()
    }

    /**
     * Fetches the latest configuration.
     * Emits the cached value immediately, then fetches and emits the fresh value
     * only if the version (or content) has changed.
     */
    fun fetch() {
        // 1. Get currently active (cached or default) JSON
        val cachedJson = remoteConfig.getString(screenKey)
        if (cachedJson.isNotBlank()) {
            onUpdate(cachedJson)
        }

        // 2. Fetch and activate fresh data from the server
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val freshJson = remoteConfig.getString(screenKey)
                    
                    if (freshJson.isNotBlank()) {
                        // 3. Only emit if it's different from what we just showed
                        if (isNewer(cachedJson, freshJson) || freshJson != cachedJson) {
                            onUpdate(freshJson)
                        }
                    }
                }
            }
    }

    /**
     * Compares two JSON strings to determine if the new one is actually an update.
     * Priority is given to the 'version' field if present.
     */
    private fun isNewer(oldJson: String, newJson: String): Boolean {
        if (oldJson == newJson) return false

        val oldVersion = try { JSONObject(oldJson).optString("version", null) } catch (e: Exception) { null }
        val newVersion = try { JSONObject(newJson).optString("version", null) } catch (e: Exception) { null }

        return if (oldVersion != null && newVersion != null) {
            oldVersion != newVersion
        } else {
            oldJson != newJson
        }
    }

    class Builder(private val context: Context) {
        private var screenKey: String = ""
        private var defaultJson: String? = null
        private var fetchIntervalSeconds: Long = 3600
        private var onUpdate: (String) -> Unit = {}

        fun screenKey(key: String) = apply { this.screenKey = key }
        fun defaultJson(json: String) = apply { this.defaultJson = json }
        fun fetchIntervalSeconds(seconds: Long) = apply { this.fetchIntervalSeconds = seconds }
        fun onUpdate(callback: (String) -> Unit) = apply { this.onUpdate = callback }

        fun build(): SduiRemoteConfig {
            if (screenKey.isEmpty()) throw IllegalStateException("screenKey must be set")
            return SduiRemoteConfig(screenKey, defaultJson, fetchIntervalSeconds, onUpdate)
        }
    }
}
