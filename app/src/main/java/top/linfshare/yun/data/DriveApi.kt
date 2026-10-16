package top.linfshare.yun.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * 云盘后端接口（yun.linfshare.top）
 *
 * 关键设计：**文件本体不经过你的服务器**
 *   1. App 调 /upload/session 拿到一个 OneDrive 的上传会话 URL
 *   2. App 直接 PUT 到 OneDrive（微软的 CDN，快）
 *   3. App 调 /upload/commit 告诉后端「传完了」，后端记录元数据
 *
 * 你的美国服务器只搬运几百字节的 JSON，一 KB 文件流量都不占。
 */
object DriveApi {

    const val BASE = "https://yun.linfshare.top"

    private val json = Json { ignoreUnknownKeys = true }

    private val http = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    @Serializable
    data class LoginResp(
        val token: String,
        @SerialName("expiresIn") val expiresIn: Long = 0,
    )

    // ---------------- 数据模型 ----------------

    @Serializable
    data class FileItem(
        val id: String,
        val name: String,
        val size: Long = 0,
        @SerialName("createdAt") val createdAt: Long = 0,
        @SerialName("shareUrl") val shareUrl: String? = null,
    )

    @Serializable
    data class UploadSession(
        @SerialName("uploadUrl") val uploadUrl: String,
        @SerialName("sessionId") val sessionId: String,
        @SerialName("chunkSize") val chunkSize: Int = 5 * 1024 * 1024,
    )

    @Serializable
    private data class ListResp(val files: List<FileItem> = emptyList())

    // ---------------- 接口 ----------------

    /**
     * 登录。没有注册 —— 密码在服务器的 .env 里配（ADMIN_PASSWORD）。
     * 成功返回的 token 有效期 180 天，存到 TokenStore 里就行。
     */
    suspend fun login(password: String): String = withContext(Dispatchers.IO) {
        val body = buildJson("password" to password).toRequestBody("application/json".toMediaType())
        val req = Request.Builder()
            .url("$BASE/api/login")
            .post(body)
            .build()
        http.newCall(req).execute().use { res ->
            val text = res.body?.string().orEmpty()
            if (res.code == 429) error("密码错误次数过多，请 15 分钟后再试")
            if (res.code == 503) error("服务器还没设置 ADMIN_PASSWORD")
            if (!res.isSuccessful) error("密码不正确")
            json.decodeFromString<LoginResp>(text).token
        }
    }

    /** 校验本地令牌还有没有效 */
    suspend fun checkSession(token: String): Boolean = withContext(Dispatchers.IO) {
        val req = Request.Builder()
            .url("$BASE/api/me")
            .header("Authorization", "Bearer $token")
            .build()
        runCatching {
            http.newCall(req).execute().use { it.isSuccessful }
        }.getOrDefault(false)
    }

    /** 文件列表 */
    suspend fun list(token: String?, parentId: String? = null): List<FileItem> =
        withContext(Dispatchers.IO) {
            val url = buildString {
                append(BASE).append("/api/files")
                parentId?.let { append("?parent=").append(it) }
            }
            val req = Request.Builder()
                .url(url)
                .apply { token?.let { header("Authorization", "Bearer $it") } }
                .build()
            http.newCall(req).execute().use { res ->
                if (!res.isSuccessful) error("HTTP ${res.code}")
                json.decodeFromString<ListResp>(res.body!!.string()).files
            }
        }

    /** 请求一个 OneDrive 上传会话 */
    suspend fun createUploadSession(
        token: String,
        name: String,
        size: Long,
        parentId: String? = null,
    ): UploadSession = withContext(Dispatchers.IO) {
        val body = buildJson(name, size, parentId).toRequestBody("application/json".toMediaType())
        val req = Request.Builder()
            .url("$BASE/api/upload/session")
            .header("Authorization", "Bearer $token")
            .post(body)
            .build()
        http.newCall(req).execute().use { res ->
            if (!res.isSuccessful) error("HTTP ${res.code}: ${res.body?.string().orEmpty()}")
            json.decodeFromString<UploadSession>(res.body!!.string())
        }
    }

    /** 通知后端：这个文件传完了，请记录元数据并生成分享链接 */
    suspend fun commit(token: String, sessionId: String, name: String, size: Long): FileItem =
        withContext(Dispatchers.IO) {
            val body = buildJson("sessionId" to sessionId, "name" to name, "size" to size)
                .toRequestBody("application/json".toMediaType())
            val req = Request.Builder()
                .url("$BASE/api/upload/commit")
                .header("Authorization", "Bearer $token")
                .post(body)
                .build()
            http.newCall(req).execute().use { res ->
                if (!res.isSuccessful) error("HTTP ${res.code}")
                json.decodeFromString<FileItem>(res.body!!.string())
            }
        }

    private fun buildJson(vararg pairs: Pair<String, Any?>): String =
        pairs.joinToString(",", "{", "}") { (k, v) ->
            val value = when (v) {
                null -> "null"
                is Number -> v.toString()
                else -> "\"${v.toString().replace("\"", "\\\"")}\""
            }
            "\"$k\":$value"
        }
}
