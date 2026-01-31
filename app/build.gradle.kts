import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.saiesh.tele"
    compileSdk {
        version = release(36)
    }

    val properties = Properties().apply {
        val localProperties = rootProject.file("local.properties")
        if (localProperties.exists()) {
            localProperties.inputStream().use { stream ->
                load(stream)
            }
        }
    }

    defaultConfig {
        applicationId = "com.saiesh.tele"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        ndk {
            abiFilters += listOf("armeabi-v7a")
        }

        val apiId = properties.getProperty("TELEGRAM_API_ID") ?: ""
        val apiHash = properties.getProperty("TELEGRAM_API_HASH") ?: ""
        buildConfigField("String", "TELEGRAM_API_ID", "\"$apiId\"")
        buildConfigField("String", "TELEGRAM_API_HASH", "\"$apiHash\"")

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        buildConfig = true
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.leanback)
    implementation(libs.glide)
}