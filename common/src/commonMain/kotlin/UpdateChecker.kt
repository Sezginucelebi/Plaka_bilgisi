package com.sezgin.plaka_bilgisi.util

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class GitHubRelease(
    val tag_name: String,
    val html_url: String
)

class UpdateChecker {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        defaultRequest {
            header("User-Agent", "PlakaBilgisi-App")
        }
    }

    suspend fun checkForUpdates(currentVersion: String): String? {
        return try {
            val response: HttpResponse = client.get("https://api.github.com/repos/Sezginucelebi/Plaka_bilgisi/releases/latest")
            
            if (response.status.value == 200) {
                val release: GitHubRelease = response.body()
                // Gelen sürümü ve mevcut sürümü loglayalım (Hata mesajında görünecek)
                if (release.tag_name != currentVersion) {
                    release.html_url
                } else {
                    null
                }
            } else {
                throw Exception("GitHub Hatası: ${response.status.description} (${response.status.value})")
            }
        } catch (e: Exception) {
            throw Exception("Bağlantı Başarısız: ${e.message}")
        }
    }
}
