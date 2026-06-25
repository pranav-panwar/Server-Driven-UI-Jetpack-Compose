package com.praptechie.serverdrivenuicompose.remote_config

import android.content.Context
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class SduiRemoteConfig private constructor(
    private val context: Context,
    private val screenKey: String,
    private val defaultJson: String?,
    private val fetchIntervalSeconds: Long,
    private val cohortContext: Map<String, String>,
    private val onUpdate: (String) -> Unit,
    private val onVariantResolved: ((String, String?) -> Unit)?
) {

    private val remoteConfig = Firebase.remoteConfig
    private val prefs = context.getSharedPreferences("SduiCache", Context.MODE_PRIVATE)

    companion object {
        private var appContext: Context? = null

        @JvmStatic
        fun init(context: Context) {
            appContext = context.applicationContext
        }

        @JvmStatic
        fun clearCachedVersion(screenKey: String) {
            appContext?.getSharedPreferences("SduiCache", Context.MODE_PRIVATE)?.edit()?.remove(screenKey)?.apply()
        }
        
        fun markLastKnownGood(context: Context, screenKey: String, json: String) {
            context.getSharedPreferences("SduiCache", Context.MODE_PRIVATE).edit().putString(screenKey, json).apply()
        }
    }

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = fetchIntervalSeconds
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        
        defaultJson?.let {
            remoteConfig.setDefaultsAsync(mapOf(screenKey to it))
        }

        remoteConfig.activate()
    }

    fun fetchWithVariantResolution(variantResolverUrl: String? = null) {
        val lastKnownGood = prefs.getString(screenKey, null)
        val cachedRcJson = remoteConfig.getString(screenKey)

        // Try to show something immediately
        if (!lastKnownGood.isNullOrBlank()) {
            onUpdate(lastKnownGood)
        } else if (cachedRcJson.isNotBlank()) {
            onUpdate(cachedRcJson)
        }

        if (variantResolverUrl != null) {
            thread {
                try {
                    val url = URL(variantResolverUrl)
                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.doOutput = true

                    val payload = JSONObject().apply {
                        put("screenKey", screenKey)
                        val cohortObj = JSONObject()
                        cohortContext.forEach { (k, v) -> cohortObj.put(k, v) }
                        put("cohortContext", cohortObj)
                    }

                    OutputStreamWriter(conn.outputStream).use { it.write(payload.toString()) }

                    if (conn.responseCode == 200) {
                        val responseJson = conn.inputStream.bufferedReader().readText()
                        val jsonObj = JSONObject(responseJson)
                        val resolvedUiJson = jsonObj.optString("uiJson", null)
                        val variant = jsonObj.optString("variant", null)

                        if (!resolvedUiJson.isNullOrBlank()) {
                            onUpdate(resolvedUiJson)
                            if (variant != null) {
                                onVariantResolved?.invoke(resolvedUiJson, variant)
                            }
                            return@thread
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SduiRemoteConfig", "Variant resolution failed", e)
                }
                // Fallback to regular fetch
                fetchRegular(lastKnownGood ?: cachedRcJson)
            }
        } else {
            fetchRegular(lastKnownGood ?: cachedRcJson)
        }
    }

    fun fetch() {
        fetchWithVariantResolution(null)
    }

    private fun fetchRegular(currentlyShowingJson: String) {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val freshJson = remoteConfig.getString(screenKey)
                    if (freshJson.isNotBlank()) {
                        if (isNewer(currentlyShowingJson, freshJson) || freshJson != currentlyShowingJson) {
                            onUpdate(freshJson)
                        }
                    }
                }
            }
    }

    private fun isNewer(oldJson: String?, newJson: String): Boolean {
        if (oldJson == null) return true
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
        private var cohortContext: Map<String, String> = emptyMap()
        private var onUpdate: (String) -> Unit = {}
        private var onVariantResolved: ((String, String?) -> Unit)? = null

        fun screenKey(key: String) = apply { this.screenKey = key }
        fun defaultJson(json: String) = apply { this.defaultJson = json }
        fun fetchIntervalSeconds(seconds: Long) = apply { this.fetchIntervalSeconds = seconds }
        fun cohortContext(context: Map<String, String>) = apply { this.cohortContext = context }
        fun onUpdate(callback: (String) -> Unit) = apply { this.onUpdate = callback }
        fun onVariantResolved(callback: (String, String?) -> Unit) = apply { this.onVariantResolved = callback }

        fun build(): SduiRemoteConfig {
            if (screenKey.isEmpty()) throw IllegalStateException("screenKey must be set")
            init(context) // Ensure static context is set for clearCachedVersion
            return SduiRemoteConfig(context, screenKey, defaultJson, fetchIntervalSeconds, cohortContext, onUpdate, onVariantResolved)
        }
    }
}
