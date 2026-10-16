package top.linfshare.yun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import top.linfshare.yun.ui.RootScreen
import top.linfshare.yun.ui.theme.YunTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            YunTheme {
                RootScreen()
            }
        }
    }
}
