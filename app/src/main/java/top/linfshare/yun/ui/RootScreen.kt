package top.linfshare.yun.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import top.yukonga.miuix.kmp.basic.FloatingActionButton
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Add
import top.yukonga.miuix.kmp.icon.extended.Folder
import top.yukonga.miuix.kmp.icon.extended.Search
import top.yukonga.miuix.kmp.icon.extended.Settings
import top.yukonga.miuix.kmp.icon.extended.UploadCloud

private data class Tab(val label: String, val icon: ImageVector)

@Composable
fun RootScreen() {
    var tab by rememberSaveable { mutableIntStateOf(0) }

    val tabs = listOf(
        Tab("文件", MiuixIcons.Folder),
        Tab("传输", MiuixIcons.UploadCloud),
        Tab("我的", MiuixIcons.Settings),
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SmallTopAppBar(
                title = "星云盘",
                actions = {
                    IconButton(onClick = { /* TODO 搜索 */ }) {
                        Icon(MiuixIcons.Search, contentDescription = "搜索")
                    }
                },
            )
        },
        bottomBar = {
            // NavigationBar 的 content 是 RowScope，所以 item 直接展开写
            NavigationBar {
                tabs.forEachIndexed { i, t ->
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { tab = i },
                        icon = t.icon,
                        label = t.label,
                    )
                }
            }
        },
        floatingActionButton = {
            if (tab == 0) {
                FloatingActionButton(onClick = { /* TODO 选文件上传 */ }) {
                    Icon(MiuixIcons.Add, contentDescription = "上传")
                }
            }
        },
    ) { innerPadding: PaddingValues ->
        when (tab) {
            0 -> FilesScreen(Modifier.padding(innerPadding))
            1 -> TransferScreen(Modifier.padding(innerPadding))
            else -> MineScreen(Modifier.padding(innerPadding))
        }
    }
}
