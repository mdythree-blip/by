package top.linfshare.yun

import android.app.Application
import top.linfshare.yun.data.TokenStore

class YunApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // 把上次登录的令牌读出来，Compose 里直接能看到
        TokenStore.init(this)
    }
}
