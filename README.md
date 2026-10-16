# 星云盘 Android 客户端（MIUIX）

Kotlin + Jetpack Compose + **MIUIX 0.9.4**（小米设计系统 Compose 版）

---

## 出 APK 的两条路

### 🅰 用 GitHub 自动编译（推荐，不用装任何东西）

1. GitHub 新建仓库（**public**，Actions 对公开仓库免费无限量）
2. 把本目录整个推上去 —— **注意 `.github` 是隐藏目录，必须一起推**

```bash
cd yun-android
git init
git add -A
git commit -m "init"
git branch -M main
git remote add origin https://github.com/<你的用户名>/<仓库名>.git
git push -u origin main
```

3. 打开仓库页 → **Actions** 标签 → 等 3~5 分钟
4. 点进那次运行 → 页面最下面 **Artifacts** → 下载 `yun-drive-debug`（里面就是 APK）

> 也可以在 Actions 页面点 **Run workflow** 手动触发。

### 🅱 本地用 Android Studio

1. Android Studio → **Open**（不是 Import）→ 选 `yun-android` 目录
2. 等 Gradle Sync 完成（走阿里云镜像，几分钟）
3. 点 **Run ▶** 直接装到手机

---

## 工程结构

```
yun-android/
├── .github/workflows/build-apk.yml   ← GitHub 自动编译
├── gradle/libs.versions.toml         ← 依赖版本
├── settings.gradle.kts               ← 仓库源（阿里云镜像加速）
└── app/src/main/java/top/linfshare/yun/
    ├── MainActivity.kt
    ├── ShareReceiverActivity.kt      ← 接收别的 App 分享过来的 APK
    ├── data/DriveApi.kt              ← 调 yun.linfshare.top
    └── ui/
        ├── theme/Theme.kt            ← MiuixTheme 接线
        ├── RootScreen.kt             ← Scaffold + NavigationBar + FAB
        ├── FilesScreen.kt
        ├── TransferScreen.kt
        └── MineScreen.kt
```

## 依赖版本（全部实查于 2026-10）

| 库 | 版本 |
|---|---|
| AGP | 9.4.1 |
| Kotlin | 2.4.21 |
| Gradle | 9.8.1 |
| Compose BOM | 2026.09.00 |
| **MIUIX** | **0.9.4** |
| OkHttp | 5.5.0 |
| kotlinx.serialization | 1.11.0 |
| Coil | 3.6.3 |
| compileSdk / minSdk | 36 / 26 |

## 和后端怎么配合

**文件本体不经过你的服务器**：

```
上传  App ──► yun.linfshare.top/api/upload/session   （几百字节 JSON）
          └► 直接 PUT 分片到 OneDrive                 （微软 CDN，快）

下载  用户 ──► yun.linfshare.top/api/shares/:token/download
          └► 302 跳到 OneDrive 预签名直链 ──► 用户
```

`DriveApi.kt` 里的三个方法对应这条链路：
`createUploadSession()` → 自己 PUT → `commit()`。

## 已知待办

- [ ] 登录态（现在是 `token: String?` 占位，接上你的 `/api/login`）
- [ ] `FilesScreen` 现在是假数据，接 `DriveApi.list()`
- [ ] 上传用 WorkManager 做断点续传 + 前台服务通知
- [ ] 下载完 APK 后走 FileProvider 拉起系统安装器（Manifest 已配好 provider）
