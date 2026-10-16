plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "top.linfshare.yun"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "top.linfshare.yun"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.compileSdk.get().toInt()
        versionCode = 1
        versionName = "0.1.0"
        // 注：AGP 9 里 resourceConfigurations 已移除，改用 androidResources.localeFilters。
        // 这里干脆不设，省得踩坑。
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // 用 Android Studio 的 Build → Generate Signed Bundle/APK 签自己的证书
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures { compose = true }

    packaging {
        resources.excludes += setOf(
            "/META-INF/{AL2.0,LGPL2.1}",
            "/META-INF/DEPENDENCIES",
            "/META-INF/INDEX.LIST",
        )
    }
}

// 注意：kotlin{} 必须放在 android{} 外面（它是 Kotlin 插件提供的 DSL，
// 放进 android{} 里会报 Unresolved reference: kotlin）
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.vm)
    implementation(libs.lifecycle.rt)
    implementation(libs.navigation)
    implementation(libs.datastore)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.preview)
    debugImplementation(libs.compose.tooling)

    // ★ MIUIX —— 小米设计系统
    implementation(libs.miuix.ui)
    implementation(libs.miuix.icons)
    implementation(libs.miuix.preference)

    implementation(libs.okhttp)
    implementation(libs.okhttp.log)
    implementation(libs.serialization)
    implementation(libs.coroutines)

    implementation(libs.coil)          // Coil 3 不需要 BOM
    implementation(libs.coil.net)

    // 只有走「App 直连 OneDrive」方案才需要；走「后端代理」可以删掉这行
    // implementation(libs.msal)
}
