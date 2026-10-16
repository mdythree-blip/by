pluginManagement {
    repositories {
        // 国内加速：阿里云镜像（Google 官方源在国内基本不可用）
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/public")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/public")
        google()          // 有代理的话保留，否则会被阿里云镜像接住
        mavenCentral()    // MIUIX (top.yukonga.miuix.kmp) 在这里
    }
}

rootProject.name = "YunDrive"
include(":app")
