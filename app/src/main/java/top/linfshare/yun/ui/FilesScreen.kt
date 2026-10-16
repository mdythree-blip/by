package top.linfshare.yun.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.File
import top.yukonga.miuix.kmp.theme.MiuixTheme

/** 一个文件条目（先用假数据，接上接口后换成真实数据） */
data class DriveFile(
    val id: String,
    val name: String,
    val sizeText: String,
    val timeText: String,
)

private val demoFiles = listOf(
    DriveFile("1", "Fold Craft Launcher 1.2.8.6.apk", "120 MB", "10-14 18:22"),
    DriveFile("2", "星云笔记 v3.2.1.apk", "24.6 MB", "10-12 14:33"),
    DriveFile("3", "设计稿打包.zip", "86.2 MB", "10-09 09:12"),
)

@Composable
fun FilesScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValuesForList,
    ) {
        item {
            SmallTitle(text = "全部文件")
        }
        items(demoFiles, key = { it.id }) { f ->
            FileCard(f)
        }
        item {
            Spacer(Modifier.height(96.dp))   // 给 FAB 和底部导航留位置
        }
    }
}

private val PaddingValuesForList =
    androidx.compose.foundation.layout.PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp)

@Composable
private fun FileCard(f: DriveFile) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 5.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = MiuixIcons.File,
                contentDescription = null,
                tint = MiuixTheme.colorScheme.primary,
                modifier = Modifier.size(26.dp),
            )
            Spacer(Modifier.size(14.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = f.name,
                    style = MiuixTheme.textStyles.body1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${f.sizeText} · ${f.timeText}",
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
            }
        }
    }
}
