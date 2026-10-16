package top.linfshare.yun.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

/**
 * MIUIX 默认主色就是 MIUI 蓝（浅色 0xFF3482FF / 深色 0xFF277AF7），
 * 直接用默认值即可，视觉上最"原生小米"。
 *
 * 想换成自己的品牌色就传 primary：
 *     lightColorScheme(primary = Color(0xFF2F7CF6))
 */
@Composable
fun YunTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MiuixTheme(
        colors = if (darkTheme) darkColorScheme() else lightColorScheme(),
        content = content,
    )
}
