import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
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
    }

    suspend fun checkForUpdates(currentVersion: String): String? {
        return try {
            // Sezginucelebi/Plaka_bilgisi repository'sini kontrol eder
            val release: GitHubRelease = client.get("https://api.github.com/repos/Sezginucelebi/Plaka_bilgisi/releases/latest").body()
            
            if (release.tag_name != currentVersion) {
                release.html_url // Yeni versiyon varsa indirme linkini döner
            } else {
                null // Versiyonlar aynı
            }
        } catch (e: Exception) {
            null // Hata durumunda (internet yok vb.) sessizce geç
        }
    }
}
