package top.linfshare.yun.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * 登录令牌的本地存储。
 *
 * 只有「登录」没有「注册」——你自己的云盘，密码在服务器 .env 里配。
 * 令牌有效期 180 天，登录一次能用很久。
 *
 * 注：这里用的是普通 SharedPreferences（明文）。想更安全可以换成
 *     EncryptedSharedPreferences（需要加 androidx.security:security-crypto）。
 */
object TokenStore {

    private const val FILE = "yun_session"
    private const val KEY = "token"

    /** 全局可观察的登录态，Compose 直接读 */
    var token by mutableStateOf<String?>(null)
        private set

    private var prefs: android.content.SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        token = prefs?.getString(KEY, null)
    }

    fun save(t: String) {
        token = t
        prefs?.edit()?.putString(KEY, t)?.apply()
    }

    fun clear() {
        token = null
        prefs?.edit()?.remove(KEY)?.apply()
    }

    val isLoggedIn: Boolean get() = !token.isNullOrBlank()
}
