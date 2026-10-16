package top.linfshare.yun.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import top.linfshare.yun.data.DriveApi
import top.linfshare.yun.data.TokenStore
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Switch
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 「我的」页。
 * 只有登录，没有注册 —— 自己的云盘，密码在服务器 .env 里配。
 */
@Composable
fun MineScreen(modifier: Modifier = Modifier) {
    if (!TokenStore.isLoggedIn) {
        LoginCard(modifier)
    } else {
        SettingsList(modifier)
    }
}

/* ------------------------------------------------------------------ */
/* 未登录 → 登录表单                                                    */
/* ------------------------------------------------------------------ */
@Composable
private fun LoginCard(modifier: Modifier) {
    var pwd by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var err by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Spacer(Modifier.height(40.dp))

        Card(Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
            Text("登录星云盘", style = MiuixTheme.textStyles.title3)
            Spacer(Modifier.height(6.dp))
            Text(
                "输入你在服务器 .env 里设置的 ADMIN_PASSWORD",
                style = MiuixTheme.textStyles.footnote1,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )

            Spacer(Modifier.height(18.dp))

            TextField(
                value = pwd,
                onValueChange = { pwd = it; err = null },
                label = "密码",
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
            )

            if (err != null) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = err!!,
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.error,
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (pwd.isBlank() || busy) return@Button
                    busy = true
                    err = null
                    scope.launch {
                        runCatching { DriveApi.login(pwd) }
                            .onSuccess { TokenStore.save(it); pwd = "" }
                            .onFailure { err = it.message ?: "登录失败" }
                        busy = false
                    }
                },
                enabled = pwd.isNotBlank() && !busy,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (busy) "登录中…" else "登录")
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = "  · 没有注册功能，密码在服务器 .env 的 ADMIN_PASSWORD\n" +
                   "  · 登录一次有效期 180 天，不用天天输",
            style = MiuixTheme.textStyles.footnote1,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
    }
}

/* ------------------------------------------------------------------ */
/* 已登录 → 设置                                                        */
/* ------------------------------------------------------------------ */
@Composable
private fun SettingsList(modifier: Modifier) {
    var autoUpload by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        SmallTitle(text = "账号")
        Card(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 5.dp)) {
            Text("已登录", style = MiuixTheme.textStyles.body1)
            Spacer(Modifier.height(8.dp))
            TextButton(
                text = "退出登录",
                onClick = { scope.launch { TokenStore.clear() } },
            )
        }

        SmallTitle(text = "设置")
        Card(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 5.dp)) {
            ToggleRow("仅在 Wi-Fi 上传", autoUpload) { autoUpload = it }
        }

        SmallTitle(text = "关于")
        Card(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 5.dp)) {
            Text("星云盘 0.1.0", style = MiuixTheme.textStyles.body1)
            Spacer(Modifier.height(6.dp))
            Text(
                "存储：OneDrive · 后端：yun.linfshare.top",
                style = MiuixTheme.textStyles.footnote1,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
        }
        Spacer(Modifier.height(96.dp))
    }
}

@Composable
private fun ToggleRow(title: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(title, style = MiuixTheme.textStyles.body1)
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
