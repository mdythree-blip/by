package top.linfshare.yun

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import top.linfshare.yun.ui.theme.YunTheme
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import androidx.compose.foundation.layout.padding

/**
 * 当别的 App「分享 APK 给星云盘」时进到这里。
 * 可以直接开始上传，省得用户再去文件管理器里翻。
 */
class ShareReceiverActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri: Uri? = if (intent?.action == Intent.ACTION_SEND) {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(Intent.EXTRA_STREAM)
        } else null

        setContent {
            YunTheme {
                Scaffold(topBar = { SmallTopAppBar(title = "分享到星云盘") }) { pad ->
                    Card(androidx.compose.ui.Modifier.padding(pad)) {
                        Text(
                            if (uri != null) "已收到文件，准备上传…\n$uri"
                            else "没有收到文件"
                        )
                    }
                }
            }
        }
    }
}
